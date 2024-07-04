package fail.stderr.usb

import fail.stderr.usb.solarsystem.SolarSystemSim
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

  lateinit var sim: SolarSystemSim

  var time: Double = 0.0

  @RegisterFunction
  override fun _ready() {
    GD.print("ready")
    sun = getNode("Sun".asNodePath()) as CSGSphere3D
    earth = getNode("Sun/Earth".asNodePath()) as CSGSphere3D
    try {
      sim = SolarSystemSim()
      sim.prep()
      GD.print("ready done")
    } catch (e: Exception) {
      GD.printErr(e)
    }
  }

  @RegisterFunction
  override fun _process(delta: Double) {
//    GD.print("process")
    time += delta

    sim.next(time, 3600.0 * 24.0 * 7 * 4) // 1h per second

    sim.earthVec?.let {
      val modifiedVec = it.div(1000000000.0)
//      GD.print("moving earth to ${modifiedVec}")
      earth.position = earth.position.lerp(modifiedVec, 1.0 )
    }


  }

  @RegisterFunction
  fun speedChanged(value: Int) {
    GD.print("kt speed changed: ${value}")
  }

}