package fail.stderr.usb.sandbox

import com.codahale.metrics.MetricRegistry
import com.codahale.metrics.Timer
import fail.stderr.usb.data.system.CelestialBodyType
import fail.stderr.usb.extensions.*
import fail.stderr.usb.godot.FakeGodotCamera
import fail.stderr.usb.metrics.GodotReporter
import fail.stderr.usb.system.KeplerianSystem
import fail.stderr.usb.system.SystemDataUtils
import fail.stderr.usb.system.createGenericSystem
import fail.stderr.usb.system.model.DefaultCelestialBodyHolder
import godot.*
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.core.*
import godot.extensions.godotStatic
import godot.global.GD
import org.hipparchus.geometry.euclidean.threed.Vector3D
import org.orekit.time.AbsoluteDate
import java.lang.Thread
import java.util.concurrent.TimeUnit
import kotlin.math.floor


@RegisterClass(className = "GenericSystemNode3D")
class GenericSystemNode3D : Node3D() {

  @Export
  @RegisterProperty
  lateinit var camera: Camera3D

  @Export
  @RegisterProperty
  lateinit var lines: Control

  @Export
  @RegisterProperty
  lateinit var planetBoxes: Control

  @Export
  @RegisterProperty
  var lineColor = Color(Color.aquamarine, 0.2)

  @Export
  @RegisterProperty
  var maneuverLineColor = Color(Color.yellow, 0.3)

  @RegisterProperty
  @Export
  var maneuverPoints: PackedVector2Array = PackedVector2Array()

  @RegisterProperty
  lateinit var systemRoot: Node3D

  @RegisterProperty
  var bodiesDict: Dictionary<String, Node3D> = Dictionary()

  var lineCache = mutableMapOf<String, Line2D>()

  var systemBodies: MutableMap<String, Node3D> = mutableMapOf()

  lateinit var dateLabel: Label

  lateinit var system: KeplerianSystem
  lateinit var maneuverPropagator: ManeuverPropagator

  var fakeCameraInited = false
  lateinit var fakeCamera: FakeGodotCamera

//  var systemDistanceScaleReduction = 1_000_000_000.0
//  var moonDistanceScaleIncrease = 400.0
//  var satelliteDistanceScaleIncrease = 10000.0

  var systemDistanceScaleReduction = 1.0
  var moonDistanceScaleIncrease = 1.0
  var satelliteDistanceScaleIncrease = 1.0

  var time: Double = 0.0
  var speed: Int = 1
  var maneuver: ManeuverData = ManeuverData(Vector3D.ZERO, 100.0)

  lateinit var currentDate: AbsoluteDate
  lateinit var accumulatingDate: AbsoluteDate
  lateinit var initialDate: AbsoluteDate

  var accumulatedSeconds: Double = 0.0

  var iteration = 0L

  val registry = MetricRegistry()

  var maneuverPositions = emptyList<Vector3D>()

  val reporter = GodotReporter.forRegistry(registry)
    .convertRatesTo(TimeUnit.SECONDS)
    .convertDurationsTo(TimeUnit.MILLISECONDS)
    .build()

  //  val gdCameraTimer: Timer = registry.timer("gd-camera")
//  val fakeCameraTimer: Timer = registry.timer("fake-camera")
  val linesJvmDataTimer: Timer = registry.timer("lines-jvm-data")
//  val linesGodotDataTimer: Timer = registry.timer("lines-godot-data")

  @RegisterFunction
  override fun _ready() {
    try {

      GD.print("ready5")

      maneuverPoints.resize(0)

//      maybeUpdateFakeCamera()

//      reporter.start(10L, TimeUnit.SECONDS);


      system = buildSystem()
      maneuverPropagator = ManeuverPropagator(system)

      systemRoot = GodotStatic.templateStar.instantiate() as Node3D
      systemRoot.name = system.rootBody.name.asStringName()
      addChild(systemRoot)
      systemRoot.owner = this

      bodiesDict.put(system.rootBody.name, systemRoot)

      GD.print("added Star")

      system.nonRootCelestialBodies.forEach { simBody ->

        val planetNode = when (simBody.data.type) {
          CelestialBodyType.PLANET -> GodotStatic.templatePlanet.instantiate() as Node3D
          CelestialBodyType.MOON -> GodotStatic.templateMoon.instantiate() as Node3D
          CelestialBodyType.SATELLITE -> GodotStatic.templateSatellite.instantiate() as Node3D
          CelestialBodyType.STAR -> GodotStatic.templateStar.instantiate() as Node3D
          else -> GodotStatic.templateMoon.instantiate() as Node3D
        }

        planetNode.name = simBody.name.asStringName()

        var parentNode = systemRoot

        if (simBody.parent !== system.rootBody) {
          // is a moon or satellite, lookup parent node
          parentNode = systemBodies.get(simBody.parent.name)!!

          // change radius of moon to be smaller
//          val sphere = planetNode.getNode("Sphere".asNodePath()) as CSGSphere3D
//          sphere.radius = 1.0f
        }

        systemBodies.put(simBody.name, planetNode)
        bodiesDict.put(simBody.name, planetNode)
        parentNode.addChild(planetNode)
        planetNode.owner = parentNode
      }

      dateLabel = getNode("../UI/ColorRect/MarginContainer/VBoxContainer/DateContainer/DateLabel".asNodePath()) as Label

      initialDate = system.refDate
      currentDate = initialDate
      accumulatingDate = currentDate

      GD.print("ready done")
    } catch (e: Exception) {
      GD.printErr(e)
    }
  }

  // GDScript code will connect the speed_changed signal to this function
  @RegisterFunction
  fun speedChanged(value: Int) {
    try {
      speed = value
    } catch (e: Exception) {
      GD.printErr(e)
    }
  }

  @RegisterFunction
  fun maneuverChanged(vec: Vector3, isp: Float) {
    try {

      maneuver = ManeuverData(vec = vec.toJVM(), isp = isp.toDouble())
      GD.print("got new maneuver ${maneuver}")


    } catch (e: Exception) {
      GD.printErr(e)
    }
  }

  fun regenManeuverPositions() {
    val newPositions = maneuverPropagator.propagate(maneuver, currentDate, "S01")

    maneuverPositions = newPositions

  }

  @RegisterFunction
  override fun _process(delta: Double) {
    try {
      time += delta

      maybeUpdateFakeCamera()

      renderPlanets(delta)

      val context1 = linesJvmDataTimer.time()
      renderOrbitLinesUsingJvmData(delta)
      context1.stop()

      renderManeuverLines(delta)

//      val context2 = linesGodotDataTimer.time()
//      renderOrbitLinesUsingGodotData(delta)
//      context2.stop()


    } catch (e: Exception) {
      GD.printErr(e)
    }
  }

//  fun hasCameraChanged(): Boolean = true

  fun hasCameraChanged(): Boolean {
    if (
      !fakeCameraInited
      || null == fakeCamera
      || camera.getCameraProjection() != fakeCamera.cameraProjection
      || camera.getCameraTransform() != fakeCamera.cameraTransform
      || camera.globalTransform != fakeCamera.globalTransform
    ) {
      fakeCameraInited = true
      return true
    }
    return false
  }

  fun maybeUpdateFakeCamera() {
    if (hasCameraChanged()) {
      fakeCamera = FakeGodotCamera(
        cameraProjection = camera.getCameraProjection(),
        cameraTransform = camera.getCameraTransform(),
        globalTransform = camera.globalTransform,
        viewportSize = camera.getWindow()!!.size,
      )
    }
  }

  val SEGMENT_COUNT = 128


  fun getParentPositions(
    body: DefaultCelestialBodyHolder,
    aggregator: MutableList<Vector3D> = mutableListOf()
  ): List<Vector3D> {

    if (body.parent is DefaultCelestialBodyHolder) {
      systemBodies.get(body.parent.name)?.let { parentNode ->
        aggregator.add(parentNode.position.toJVM())
        getParentPositions(body.parent as DefaultCelestialBodyHolder, aggregator)
      }
    }

    return aggregator
  }

  fun isParentSystemRoot(body: DefaultCelestialBodyHolder): Boolean {
    return body.parent === system.rootBody
  }

  var orbitLineCache: MutableMap<String, List<Vector3D>> = mutableMapOf()

  fun buildOrbitLinesForBody(body: DefaultCelestialBodyHolder): List<Vector3D> {

    val unshiftedLines = orbitLineCache.getOrPut(body.name) { buildOrbitLinesForBodyNoShift(body) }

    val shiftedLines = when {
      body.parent === system.rootBody -> unshiftedLines
      else -> {
        val parentPositions = getParentPositions(body)

        unshiftedLines.map { unshiftedPos ->
          var newVec = unshiftedPos
          parentPositions.forEach { parentPosition ->
            newVec += parentPosition
          }
          newVec
        }
      }

    }

    return shiftedLines
  }


  fun buildOrbitLinesForBodyNoShift(body: DefaultCelestialBodyHolder): List<Vector3D> {

    val vec3s = mutableListOf<Vector3D>()

    val segmentTimeIncrement = floor(body.orbit.keplerianPeriod / SEGMENT_COUNT).toLong()

    for (segmentIndex in 0..<SEGMENT_COUNT) {

      val date = system.refDate.shiftedBy(segmentTimeIncrement * segmentIndex, TimeUnit.SECONDS)

      val parentRelativePosition = body.orbit.getPosition(date, body.parent.frame)

      var scaledParentRelativePosition = Vector3D(
        parentRelativePosition.x / systemDistanceScaleReduction,
        parentRelativePosition.y / systemDistanceScaleReduction,
        parentRelativePosition.z / systemDistanceScaleReduction,
      )

      // if moon...
      if (body.data.type == CelestialBodyType.MOON) {

        // scale up moon distance
        scaledParentRelativePosition = Vector3D(
          scaledParentRelativePosition.x * moonDistanceScaleIncrease,
          scaledParentRelativePosition.y * moonDistanceScaleIncrease,
          scaledParentRelativePosition.z * moonDistanceScaleIncrease,
        )

      }

      // if satellite...
      if (body.data.type == CelestialBodyType.SATELLITE) {

        // scale up moon distance
        scaledParentRelativePosition = Vector3D(
          scaledParentRelativePosition.x * satelliteDistanceScaleIncrease,
          scaledParentRelativePosition.y * satelliteDistanceScaleIncrease,
          scaledParentRelativePosition.z * satelliteDistanceScaleIncrease,
        )

      }

      vec3s.add(scaledParentRelativePosition)
    }

    return vec3s
  }

  fun renderOrbitLinesUsingJvmData(delta: Double) {

//    maybeUpdateFakeCamera()

    for (bodyIndex in 0..<system.nonRootCelestialBodies.size) {

      val body = system.nonRootCelestialBodies[bodyIndex]
      val vec3s = buildOrbitLinesForBody(body)

      val lineExisted = lineCache.containsKey(body.name)

      val line = lineCache.getOrPut(body.name) { Line2D() }

      if (lineExisted) {
        line.clearPoints();
      }

      val vec2s = vec3s
        .map(Vector3D::toGodot)
        .map(fakeCamera::unprojectPosition)

      vec2s.forEach(line::addPoint)

      if (!lineExisted) {
        line.width = 3.0f
        line.closed = true
        line.defaultColor = lineColor
        line.visible = true
        lines.addChild(line)
      } else {
        line.defaultColor = lineColor
      }

    }

  }

  fun renderManeuverLines(delta: Double) {

    regenManeuverPositions()


//    val bodyName = "S01man"

//    val lineExisted = lineCache.containsKey(bodyName)

//    val line = lineCache.getOrPut(bodyName) { Line2D() }

//    if (lineExisted) {
//      lines.removeChild(line)
//      line.clearPoints()
//      lines.addChild(line)
//    }

//    val satellite = system.allCelestialBodies["S01"]!! as DefaultCelestialBodyHolder

    val scaled = maneuverPositions.map { it / systemDistanceScaleReduction * satelliteDistanceScaleIncrease }

    // already in system coordinates
    val shiftedVectors = scaled

//    val shiftedVectors = when {
//      satellite.parent === system.rootBody -> scaled
//      else -> {
//        val parentPositions = getParentPositions(satellite)
//
//        scaled.map { unshiftedPos ->
//          var newVec = unshiftedPos
//          parentPositions.forEach { parentPosition ->
//            newVec += parentPosition
//          }
//          newVec
//        }
//      }
//
//    }

    if (maneuverPoints.size != shiftedVectors.size) {
      maneuverPoints.resize(shiftedVectors.size)
    }

    for (i in shiftedVectors.indices) {
      val vec = shiftedVectors[i]
      maneuverPoints[i] = fakeCamera.unprojectPosition(vec.toGodot()) * Vector2(-1, 1)

    }

//    val vec2s = shiftedVectors
//      .map(Vector3D::toGodot)
//      .map(fakeCamera::unprojectPosition)

//    vec2s.forEach(line::addPoint)

//    val a = PackedVector2Array()

//    if (!lineExisted) {
//      line.width = 3.0f
//      line.closed = false
//      line.defaultColor = maneuverLineColor
//      line.visible = true
//      lines.addChild(line)
//    } else {
//      line.defaultColor = maneuverLineColor
//    }

  }


  fun renderOrbitLinesUsingGodotData(delta: Double) {

    var fakeCamera = FakeGodotCamera(
      cameraProjection = camera.getCameraProjection(),
      cameraTransform = camera.getCameraTransform(),
      globalTransform = camera.globalTransform,
      viewportSize = camera.getWindow()!!.size,
    )

    val segmentCount = 256
    val startDate = system.refDate

    for (bodyIndex in 0..<system.nonRootCelestialBodies.size) {

      val body = system.nonRootCelestialBodies[bodyIndex]

      val lineExisted = lineCache.containsKey(body.name)

      val line = lineCache.getOrPut(body.name) { Line2D() }

      if (lineExisted) {
        line.clearPoints();
      }

      val vec2s = mutableListOf<Vector2>()

      val segmentTimeIncrement = floor(body.orbit.keplerianPeriod / segmentCount).toLong()

      for (segmentIndex in 0..<segmentCount) {

        val date = startDate.shiftedBy(segmentTimeIncrement * segmentIndex, TimeUnit.SECONDS)

        val parentRelativePosition = body.orbit.getPosition(date, body.parent.frame)
        var scaledParentRelativePosition = Vector3(
          parentRelativePosition.x / systemDistanceScaleReduction,
          parentRelativePosition.y / systemDistanceScaleReduction,
          parentRelativePosition.z / systemDistanceScaleReduction,
        )

        // if moon...
        if (body.data.type == CelestialBodyType.MOON) {

          // scale up moon distance
          scaledParentRelativePosition = Vector3(
            scaledParentRelativePosition.x * moonDistanceScaleIncrease,
            scaledParentRelativePosition.y * moonDistanceScaleIncrease,
            scaledParentRelativePosition.z * moonDistanceScaleIncrease,
          )

        }

        // if moon...
        if (body.data.type == CelestialBodyType.SATELLITE) {

          // scale up moon distance
          scaledParentRelativePosition = Vector3(
            scaledParentRelativePosition.x * satelliteDistanceScaleIncrease,
            scaledParentRelativePosition.y * satelliteDistanceScaleIncrease,
            scaledParentRelativePosition.z * satelliteDistanceScaleIncrease,
          )

        }

        fun getParentPositions(
          body: DefaultCelestialBodyHolder,
          aggregator: MutableList<Vector3> = mutableListOf()
        ): List<Vector3> {

          if (body.parent is DefaultCelestialBodyHolder) {
            systemBodies.get(body.parent.name)?.let { parentNode ->
              aggregator.add(parentNode.position)
              getParentPositions(body.parent as DefaultCelestialBodyHolder, aggregator)
            }
          }

          return aggregator
        }

        // flip y/z for Godot
        val systemRelativePosition = when {
          // if the parent, use as-is
          body.parent === system.rootBody -> Vector3(
            scaledParentRelativePosition.x,
            scaledParentRelativePosition.z,
            scaledParentRelativePosition.y
          )

          else -> {
            // need add to all the parent position vectors
            var vec =
              Vector3(scaledParentRelativePosition.x, scaledParentRelativePosition.z, scaledParentRelativePosition.y)

            val parentPositions = getParentPositions(body)
            parentPositions.forEach {
              vec += it
            }
            vec
          }
        }

        // TODO: benchmark Camera3D vs FakeCamera unprojectPosition perf

//        val context1 = gdCameraTimer.time()
//        val pos2d = camera.unprojectPosition(systemRelativePosition)
//        context1.stop()

//        val context2 = fakeCameraTimer.time()
        val pos2d = fakeCamera.unprojectPosition(systemRelativePosition)
//        context2.stop()

        vec2s.add(pos2d)
      }

      vec2s.forEach(line::addPoint)

      if (!lineExisted) {
        line.width = 3.0f
        line.closed = true
        line.defaultColor = lineColor
        line.visible = true
        lines.addChild(line)
      } else {
        line.defaultColor = lineColor
      }

    }

  }

  fun renderPlanets(delta: Double) {

    var deltaSeconds = delta
    deltaSeconds *= speed

    accumulatedSeconds += deltaSeconds

    if (accumulatedSeconds >= 1.0) {

      val shiftBySeconds = Math.floor(accumulatedSeconds)

      accumulatedSeconds %= 1.0

      currentDate = currentDate.shiftedBy(shiftBySeconds.toLong(), TimeUnit.SECONDS)
      dateLabel.text = "Date: ${currentDate}"

    }

    iteration++

    for (simBody in system.nonRootCelestialBodies) {
      val p = simBody.orbit.getPosition(currentDate, simBody.parent.frame)

      val vec = Vector3(p.x, p.z, p.y)
      var scaledVec = vec.div(systemDistanceScaleReduction)

      if (simBody.parent !== system.rootBody) {
        // is a moon
        when (simBody.data.type) {
          CelestialBodyType.MOON -> scaledVec *= moonDistanceScaleIncrease
          CelestialBodyType.SATELLITE -> scaledVec *= satelliteDistanceScaleIncrease
          else -> {}
        }

      }

      val bodyNode = systemBodies.get(simBody.name)!!

      bodyNode.position = scaledVec

      //
      // START: render boxes
      //

      if (simBody.data.type === CelestialBodyType.PLANET || simBody.data.type === CelestialBodyType.SATELLITE) {
        val systemPV = simBody.orbit.getPosition(currentDate, system.refFrame)

        val body2d = fakeCamera.unprojectPosition(systemPV.toGodot())

        val lineName = "${bodyNode.name}Box"

        val lineExisted = lineCache.containsKey(lineName)

        val line = lineCache.getOrPut(lineName) {
          val l = Line2D()
          l.width = 4.0f
          l.closed = false
          l.defaultColor = when (simBody.data.type) {
            CelestialBodyType.PLANET -> Color(Color.lightSkyBlue, 0.25)
            CelestialBodyType.SATELLITE -> Color(Color.yellow, 0.25)
            else -> Color(Color.red, 0.25)
          }
          l.visible = true
          planetBoxes.addChild(l)
          l
        }

        if (lineExisted) {
          line.clearPoints();
        }

        val point1 = body2d + Vector2(10, 10)
        val point2 = point1 - Vector2(20, 0)
        val point3 = point2 - Vector2(0, 20)
        val point4 = point3 + Vector2(20, 0)
        val point5 = point4 + Vector2(0, 20)

        line.addPoint(point1)
        line.addPoint(point2)
        line.addPoint(point3)
        line.addPoint(point4)
        line.addPoint(point5)
      }

      //
      // END: render boxes
      //

      if (iteration % 1000 === 0L) {
//        GD.print("moving1 ${simBody.name} to ${scaledVec} scaledVec.d=${scaledVec.length()}")
      }
    }
  }

}


private fun buildSystem(): KeplerianSystem {
  val result = SystemDataUtils.fromYAML {
//    Thread.currentThread().contextClassLoader.getResourceAsStream("systems/dwarla/system.yaml")!!
//    Thread.currentThread().contextClassLoader.getResourceAsStream("systems/maneuver/maneuver01.yaml")!!
    Thread.currentThread().contextClassLoader.getResourceAsStream("systems/maneuver/maneuver02.yaml")!!
  }

  val system = createGenericSystem(result.name, result)
  return system
}

object GodotStatic {

  var templateSatellite by godotStatic {
    ResourceLoader.load("res://template_satellite.tscn") as PackedScene
  }
  var templateMoon by godotStatic {
    ResourceLoader.load("res://template_moon.tscn") as PackedScene
  }
  var templatePlanet by godotStatic {
    ResourceLoader.load("res://template_planet.tscn") as PackedScene
  }
  var templateStar by godotStatic {
    ResourceLoader.load("res://template_star.tscn") as PackedScene
  }
}
