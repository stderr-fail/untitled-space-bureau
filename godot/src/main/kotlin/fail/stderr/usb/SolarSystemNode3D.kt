package fail.stderr.usb

import fail.stderr.usb.sim.SolarSystemSim
import godot.CSGSphere3D
import godot.Node3D
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.core.asNodePath
import godot.global.GD

@RegisterClass
class SolarSystemNode3D : Node3D() {


  lateinit var sun: CSGSphere3D
  lateinit var earth: CSGSphere3D
  lateinit var mercury: CSGSphere3D

  lateinit var sim: SolarSystemSim

  var time: Double = 0.0
  var speed: Int = 1

  @RegisterFunction
  override fun _ready() {
    GD.print("ready")
    sun = getNode("Sun".asNodePath()) as CSGSphere3D
    earth = getNode("Sun/Earth".asNodePath()) as CSGSphere3D
    mercury = getNode("Sun/Mercury".asNodePath()) as CSGSphere3D
    try {
      sim = SolarSystemSim(speed = speed)
      sim.prep()
      GD.print("ready done3")
    } catch (e: Exception) {
      GD.printErr(e)
    }
  }

  // GDScript code will connect the speed_changed signal to this function
  @RegisterFunction
  fun speedChanged(value: Int) {
    speed = value
  }

  @RegisterFunction
  override fun _process(delta: Double) {
//    GD.print("process")
    time += delta

    sim.next(time, 3600.0 * 24.0 * 7 * 4) // 1h per second

    sim.earthVec?.let {
      val modifiedVec = it.div(1000000000.0)
//      GD.print("moving earth to ${modifiedVec}")
      earth.position = earth.position.lerp(modifiedVec, 1.0)
    }

    sim.mercuryVec?.let {
      val modifiedVec = it.div(1000000000.0)
//      GD.print("moving mercury to ${modifiedVec}")
      mercury.position = mercury.position.lerp(modifiedVec, 1.0)
    }


  }

}