package fail.stderr.usb

import fail.stderr.usb.sim.SolarSystemSim
import org.orekit.time.AbsoluteDate
import org.orekit.time.TimeScalesFactory
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit


fun main() {

  val log = LoggerFactory.getLogger("usb")

  log.info("test!")


  var sim = SolarSystemSim(speed = 1)
  sim.prep()

  val utc = TimeScalesFactory.getUTC()

  val startDate = AbsoluteDate(2024, 6, 25, 0, 0, 0.0, utc)
  var endDate = startDate

  for (day in 1..365 * 100) {
    endDate = endDate.shiftedBy(1L, TimeUnit.DAYS)
    sim.next(endDate)
  }

//  for (week in 1..52 * 10_000) {
//    sim.next(secondsInWeek * week, 1.0) // 1h per second
//  }

//  for (weeks4 in 1..13 * 10) {
//    sim.next(secondsIn4Weeks * weeks4, 1.0) // 1h per second
//  }

//  sim.next(1.0, 3600.0 * 24.0 * 7 * 4) // 1h per second

  println("here!")


}

