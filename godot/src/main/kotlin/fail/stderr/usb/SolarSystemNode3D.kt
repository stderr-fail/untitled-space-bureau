package fail.stderr.usb

import fail.stderr.usb.sim.OrbitalParams
import fail.stderr.usb.sim.SolarSystemSim
import godot.CSGSphere3D
import godot.Node3D
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.core.asNodePath
import godot.global.GD
import org.orekit.time.AbsoluteDate
import java.util.concurrent.TimeUnit

@RegisterClass
class SolarSystemNode3D : Node3D() {


  lateinit var sun: CSGSphere3D
  lateinit var earth: CSGSphere3D
  lateinit var moon: CSGSphere3D
  lateinit var mercury: CSGSphere3D

  lateinit var sim: SolarSystemSim

  var time: Double = 0.0
  var speed: Int = 1

  lateinit var endDate: AbsoluteDate
  lateinit var initialDate: AbsoluteDate


  @RegisterFunction
  override fun _ready() {
    try {

      GD.print("ready")
      sun = getNode("Sun".asNodePath()) as CSGSphere3D
      earth = getNode("Sun/Earth".asNodePath()) as CSGSphere3D
      moon = getNode("Sun/Earth/Moon".asNodePath()) as CSGSphere3D
      mercury = getNode("Sun/Mercury".asNodePath()) as CSGSphere3D

      sim = SolarSystemSim(speed = speed)
      sim.prep()

      initialDate = OrbitalParams.COMMON_START_DATE.shiftedBy(2L, TimeUnit.DAYS)
      endDate = initialDate

      GD.print("ready done3")
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

      var numDays = Math.floor(time * 14L).toLong()

      var nextEndDate = initialDate.shiftedBy(numDays, TimeUnit.DAYS)
      if (nextEndDate.isAfter(endDate)) {
//        GD.print("${Instant.now()} :: [t=${time}] [nDays=${numDays}] moving endDate ${endDate} to ${nextEndDate}")
        endDate = nextEndDate
        sim.next(endDate) // 1h per second

      }

      sim.earthVec?.let {
        val modifiedVec = it.div(1000000000.0)
//      GD.print("moving earth to ${modifiedVec}")
        earth.position = earth.position.lerp(modifiedVec, 1 / 120.0)
      }

      sim.moonVec?.let {
        val modifiedVec = it.div(1000000000.0) * 20
//      GD.print("moving earth to ${modifiedVec}")
        moon.position = moon.position.lerp(modifiedVec, 1 / 120.0)
      }

      sim.mercuryVec?.let {
        val modifiedVec = it.div(1000000000.0)
//      GD.print("moving mercury to ${modifiedVec}")
        mercury.position = mercury.position.lerp(modifiedVec, 1 / 120.0)
      }
    } catch (e: Exception) {
      GD.printErr(e)
    }

  }

}