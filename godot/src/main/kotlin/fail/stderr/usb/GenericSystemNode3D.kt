package fail.stderr.usb

import fail.stderr.usb.system.KeplerianSystem
import fail.stderr.usb.system.SystemDataUtils
import fail.stderr.usb.system.createGenericSystem
import godot.*
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.core.Vector3
import godot.core.asNodePath
import godot.extensions.godotStatic
import godot.global.GD
import org.orekit.time.AbsoluteDate
import java.lang.Thread
import java.util.concurrent.TimeUnit
import javax.management.Query.div


@RegisterClass
class GenericSystemNode3D : Node3D() {

  lateinit var systemRoot: Node3D
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

      GD.print("ready")

//      templatePlanet = GD.load<SceneTree>("res://template_planet.tscn")!!

      system = buildSystem()

      systemRoot = GodotStatic.templateStar.instantiate() as Node3D
      addChild(systemRoot)

      system.nonRootCelestialBodies.forEach { simBody ->

        val planetNode = GodotStatic.templatePlanet.instantiate() as Node3D

      var parentNode = systemRoot

        if (simBody.parent !== system.rootBody) {
          // is a moon, lookup parent node
          parentNode = systemBodies.get(simBody.parent.name)!!

          // change radius of moon to be smaller
          val sphere = planetNode.getNode("Sphere".asNodePath()) as CSGSphere3D
          sphere.radius = 1.0f
        }

        systemBodies.put(simBody.name, planetNode)
        parentNode.addChild(planetNode)
      }


      dateLabel = getNode("UI/ColorRect/MarginContainer/VBoxContainer/DateContainer/DateLabel".asNodePath()) as Label

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
      GD.print("speedChanged")
      speed = value
    } catch (e: Exception) {
      GD.printErr(e)
    }
  }

  @RegisterFunction
  override fun _process(delta: Double) {
    try {
//      GD.print("process")
      time += delta

//      val deltaMillis = delta * 1000.0
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

      system.nonRootCelestialBodies.forEach { simBody ->
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
          GD.print("moving1 ${simBody.name} to ${scaledVec} scaledVec.d=${scaledVec.length()}")
        }
      }


    } catch (e: Exception) {
      GD.printErr(e)
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
