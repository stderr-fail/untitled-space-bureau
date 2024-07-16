package fail.stderr.usb.sandbox

import fail.stderr.usb.godot.FakeGodotCamera
import fail.stderr.usb.system.KeplerianSystem
import fail.stderr.usb.system.SystemDataUtils
import fail.stderr.usb.system.createGenericSystem
import godot.*
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.core.*
import godot.extensions.godotStatic
import godot.global.GD
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

  @RegisterProperty
  lateinit var systemRoot: Node3D

  @RegisterProperty
  var bodiesDict: Dictionary<String, Node3D> = Dictionary()

  var lineCache = mutableMapOf<String, Line2D>()

  var systemBodies: MutableMap<String, Node3D> = mutableMapOf()

  lateinit var dateLabel: Label

  lateinit var system: KeplerianSystem

  var time: Double = 0.0
  var speed: Int = 1

  lateinit var currentDate: AbsoluteDate
  lateinit var accumulatingDate: AbsoluteDate
  lateinit var initialDate: AbsoluteDate
  var accumulatedSeconds: Double = 0.0

  var iteration = 0L


  @RegisterFunction
  override fun _ready() {
    try {

      GD.print("ready5")

//      templatePlanet = GD.load<SceneTree>("res://template_planet.tscn")!!

      system = buildSystem()

      systemRoot = GodotStatic.templateStar.instantiate() as Node3D
//      systemRoot.name = "Star".asStringName()
      systemRoot.name = system.rootBody.name.asStringName()
      addChild(systemRoot)
      systemRoot.owner = this

      bodiesDict.put(system.rootBody.name, systemRoot)

      GD.print("added Star")


      system.nonRootCelestialBodies.forEach { simBody ->

        val planetNode = GodotStatic.templatePlanet.instantiate() as Node3D
        planetNode.name = simBody.name.asStringName()

        var parentNode = systemRoot

        if (simBody.parent !== system.rootBody) {
          // is a moon, lookup parent node
          parentNode = systemBodies.get(simBody.parent.name)!!

          // change radius of moon to be smaller
          val sphere = planetNode.getNode("Sphere".asNodePath()) as CSGSphere3D
          sphere.radius = 1.0f
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
  override fun _process(delta: Double) {
    try {
      time += delta
      renderPlanets(delta)
      renderOrbitLines(delta)
    } catch (e: Exception) {
      GD.printErr(e)
    }
  }

  fun renderOrbitLines(delta: Double) {

    var fakeCamera = FakeGodotCamera(
      cameraProjection = camera.getCameraProjection(),
      cameraTransform = camera.getCameraTransform(),
      globalTransform = camera.globalTransform,
      viewportSize = camera.getWindow()!!.size,
    )

    val segmentCount = 128
    val startDate = system.refDate
    val frame = system.refFrame

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
        val pv = body.orbit.getPVCoordinates(date, frame)

        // flip y/z for godot 3d coordinates
        var v3 = Vector3(pv.position.x, pv.position.z, pv.position.y)

        var v3scaled = v3.div(1_000_000_000)

        val pos2d = camera.unprojectPosition(v3scaled)
        val pos2d2 = fakeCamera.unprojectPosition(v3scaled)

        vec2s.add(pos2d)
        line.addPoint(pos2d)
      }


      if (!lineExisted) {
        line.width = 3.0f
        line.closed = true
        line.defaultColor = Color(Color.darkGray, 0.5)
        line.visible = true
        lines.addChild(line)

      }

//      line.points = vec2s

//      GD.print("adding Line2D for body ${body.name}")
//      for (vec2 in vec2s) {
//        GD.print("    ${vec2}")
//      }




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
      val pv = simBody.orbit.getPVCoordinates(currentDate, simBody.parent.frame)
      val p = pv.position

      val vec = Vector3(p.x, p.z, p.y)
      var scaledVec = vec.div(1000000000.0)

      if (simBody.parent !== system.rootBody) {
        // is a moon
        scaledVec *= 200
      }

      val bodyNode = systemBodies.get(simBody.name)!!

      bodyNode.position = scaledVec
      if (iteration % 1000 === 0L) {
//        GD.print("moving1 ${simBody.name} to ${scaledVec} scaledVec.d=${scaledVec.length()}")
      }
    }
  }

}


private fun buildSystem(): KeplerianSystem {
  val result = SystemDataUtils.fromYAML {
    Thread.currentThread().contextClassLoader.getResourceAsStream("systems/dwarla/system.yaml")!!
  }

  val system = createGenericSystem("Dwarlis", result)
  return system
}

object GodotStatic {

  var templatePlanet by godotStatic {
    ResourceLoader.load("res://template_planet.tscn") as PackedScene
  }
  var templateStar by godotStatic {
    ResourceLoader.load("res://template_star.tscn") as PackedScene
  }
}
