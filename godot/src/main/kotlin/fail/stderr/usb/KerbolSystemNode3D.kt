package fail.stderr.usb

import fail.stderr.usb.sim.OrbitalParams
import fail.stderr.usb.sim.SolarSystemSim
import godot.CSGSphere3D
import godot.Label
import godot.Node3D
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.core.asNodePath
import godot.global.GD
import org.orekit.time.AbsoluteDate
import java.util.concurrent.TimeUnit

@RegisterClass
class KerbolSystemNode3D : Node3D() {


  lateinit var kerbin: CSGSphere3D
  lateinit var mun: CSGSphere3D

  lateinit var dateLabel: Label

  lateinit var sim: SolarSystemSim

  var time: Double = 0.0
  var rollingTime: Double = 0.0
  var speed: Int = 1

  lateinit var endDate: AbsoluteDate
  lateinit var accumulatingDate: AbsoluteDate
  lateinit var initialDate: AbsoluteDate
  var accumulatedSeconds: Double = 0.0


  @RegisterFunction
  override fun _ready() {
    try {

      GD.print("ready")
      kerbin = getNode("Kerbin".asNodePath()) as CSGSphere3D
      mun = getNode("Kerbin/Mun".asNodePath()) as CSGSphere3D
      dateLabel = getNode("UI/ColorRect/MarginContainer/VBoxContainer/DateContainer/DateLabel".asNodePath()) as Label

      sim = SolarSystemSim(speed = speed)
      sim.prep()

      initialDate = OrbitalParams.COMMON_START_DATE.shiftedBy(2L, TimeUnit.DAYS)
      endDate = initialDate
      accumulatingDate = endDate

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

//      val deltaMillis = delta * 1000.0
      var deltaSeconds = delta
      deltaSeconds *= speed

      accumulatedSeconds += deltaSeconds
      if (accumulatedSeconds >= 1.0) {

        val shiftBySeconds = Math.floor(accumulatedSeconds)

        accumulatedSeconds %= 1.0

        endDate = endDate.shiftedBy(shiftBySeconds.toLong(), TimeUnit.SECONDS)
        dateLabel.text = "Date: ${endDate}"
        sim.next(endDate)


      }



//
//      rollingTime += delta
//      if (rollingTime > 1.0) {
//        rollingTime = rollingTime % 1
//        GD.print("rolled over rollingTime=${rollingTime}")
//      }
//
////      val deltaMillis = Math.floor(delta * 1000).toLong()
////      var scaledSeconds = Math.floor(rollingTime * speed).toLong()
////      var scaledSeconds = (rollingTime * speed).toLong()
////      var scaledMillis = deltaMillis * speed
//
//      accumulatedMillis += scaledMillis
//
//      if (accumulatedMillis >= 1000) {
//
//        var nextEndDate = endDate.shiftedBy(accumulatedMillis, TimeUnit.MILLISECONDS)
//        if (nextEndDate.isAfter(endDate)) {
////        GD.print("${Instant.now()} :: [t=${time}] [nDays=${numDays}] moving endDate ${endDate} to ${nextEndDate}")
//          endDate = nextEndDate
//          accumulatedMillis = (accumulatedMillis % 1000) * speed
//          sim.next(endDate) // 1h per second
//          dateLabel.text = "Date: ${endDate}"
//
//        }
//
//      }


//      val lerpWeight = 1 / 120.0 * delta
      val lerpWeight = delta / (1 / 120.0)

//      sim.earthVec?.let {
//        val modifiedVec = it.div(1000000000.0)
////      GD.print("moving earth to ${modifiedVec}")
//        earth.position = earth.position.lerp(modifiedVec, lerpWeight)
//      }
//
//      sim.moonVec?.let {
//        val modifiedVec = it.div(1000000000.0) * 10
////      GD.print("moving earth to ${modifiedVec}")
//        moon.position = moon.position.lerp(modifiedVec, lerpWeight)
//      }
//
//      sim.mercuryVec?.let {
//        val modifiedVec = it.div(1000000000.0)
////      GD.print("moving mercury to ${modifiedVec}")
//        mercury.position = mercury.position.lerp(modifiedVec, lerpWeight)
//      }
//
//      sim.venusVec?.let {
//        val modifiedVec = it.div(1000000000.0)
////      GD.print("moving venus to ${modifiedVec}")
//        venus.position = venus.position.lerp(modifiedVec, lerpWeight)
//      }
//
//      sim.marsVec?.let {
//        val modifiedVec = it.div(1000000000.0)
////      GD.print("moving mars to ${modifiedVec}")
//        mars.position = mars.position.lerp(modifiedVec, lerpWeight)
//      }

    } catch (e: Exception) {
      GD.printErr(e)
    }

  }

}