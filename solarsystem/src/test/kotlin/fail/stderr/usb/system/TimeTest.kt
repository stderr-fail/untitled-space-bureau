package fail.stderr.usb.system

import fail.stderr.usb.QuickTest
import fail.stderr.usb.time.CustomTimeScale
import org.junit.jupiter.api.Test
import org.orekit.time.AbsoluteDate
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

class TimeTest {

  var log: Logger = LoggerFactory.getLogger(QuickTest::class.java)

  @Test
  fun propagateOneYear() {
    val ts = CustomTimeScale("40", 0.0)
    val d1 = AbsoluteDate(2024, 7, 12, 0, 51, 0.0, ts)

    val d2 = d1.shiftedBy(49L, TimeUnit.SECONDS)
    val d3 = d1.shiftedBy(50L, TimeUnit.SECONDS)
    val d4 = d1.shiftedBy(51L, TimeUnit.SECONDS)
    println("here!")
  }
}