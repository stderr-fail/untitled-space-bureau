package fail.stderr.usb.system

import fail.stderr.usb.QuickTest
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

class KeplerianSystemTest {

  var log: Logger = LoggerFactory.getLogger(QuickTest::class.java)

  @Test
  fun propagateOneYear() {

    val result = SystemDataUtils.fromYAML {
      Thread.currentThread().contextClassLoader.getResourceAsStream("systems/dwarla/system.yaml")!!
    }

    val system = createGenericSystem("Dwarlis", result)

    val numWarmupDays = 7L
    val numWarmupSeconds = TimeUnit.DAYS.toSeconds(numWarmupDays)

//    val numFullDays = 365L
    val numFullDays = 7L
    val numFullSeconds = TimeUnit.DAYS.toSeconds(numFullDays)

    log.info("starting warmpup")

    for (i in 1..numWarmupSeconds) {
      val toDate = system.refDate.shiftedBy(i, TimeUnit.SECONDS)

      system.nonRootCelestialBodies.forEach { body ->
        val pv = body.orbit.getPVCoordinates(toDate, body.parent.frame)
//        log.info("position=${pv.position} d=${pv.position.norm}")

      }

    }

    log.info("starting full")

    var numPropagations = 0L

    val start = System.currentTimeMillis()

    for (i in 1..numFullSeconds) {
      val toDate = system.refDate.shiftedBy(i, TimeUnit.SECONDS)

      system.nonRootCelestialBodies.forEach { body ->
        val pv = body.orbit.getPVCoordinates(toDate, body.parent.frame)
//        log.info("position=${pv.position} d=${pv.position.norm}")
        numPropagations++
      }

    }


    val end = System.currentTimeMillis()

    val elapsed = end - start
    println("here, timed ${numPropagations} propagations in ${elapsed} millis")


  }
}