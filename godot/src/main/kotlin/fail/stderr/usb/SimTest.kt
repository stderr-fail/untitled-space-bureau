package fail.stderr.usb

import fail.stderr.usb.sim.SolarSystemSim
import org.slf4j.LoggerFactory
import java.time.Duration


fun main() {

  val log = LoggerFactory.getLogger("usb")

  log.info("test!")



  var sim = SolarSystemSim(speed = 1)
  sim.prep()

  val secondsInDay = Duration.ofDays(1L).toSeconds().toDouble()
  val secondsInWeek = Duration.ofDays(7L).toSeconds().toDouble()
  val secondsIn4Weeks = secondsInWeek * 4

  for (day in 1..365 * 100) {
    sim.next(secondsInDay * day, 1.0) // 1h per second
  }

//  for (week in 1..52 * 100) {
//    sim.next(secondsInWeek * week, 1.0) // 1h per second
//  }

//  for (weeks4 in 1..13 * 10) {
//    sim.next(secondsIn4Weeks * weeks4, 1.0) // 1h per second
//  }

//  sim.next(1.0, 3600.0 * 24.0 * 7 * 4) // 1h per second

  println("here!")


}

