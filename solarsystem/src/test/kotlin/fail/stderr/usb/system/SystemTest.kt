package fail.stderr.usb.system

import fail.stderr.usb.QuickTest
import fail.stderr.usb.system.model.DefaultCelestialBodyHolder
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

class SystemTest {

  var log: Logger = LoggerFactory.getLogger(QuickTest::class.java)

  @Test
  fun test() {

    val result = SystemDataUtils.fromYAML {
      Thread.currentThread().contextClassLoader.getResourceAsStream("systems/dwarla/system.yaml")!!
    }

    val system = createGenericSystem("Dwarlis", result)

    for (i in 1..365) {
      val toDate = system.refDate.shiftedBy(i.toLong(), TimeUnit.DAYS)

//      val dwarlin = system.allCelestialBodies.get("Dwarlin") !! as DefaultCelestialBodyHolder
//      val dweeb = system.allCelestialBodies.get("Dweeb") !! as DefaultCelestialBodyHolder
//
//      val dwarlinPV = dwarlin.orbit.getPVCoordinates(toDate, dwarlin.parent.frame)
//      val dweebpV = dweeb.orbit.getPVCoordinates(toDate, dweeb.parent.frame)
//
//
//      log.info("position=${dwarlinPV.position} d=${dwarlinPV.position.norm}")
//      log.info("position=${dweebpV.position} d=${dweebpV.position.norm}")

      system.nonRootCelestialBodies.forEach { body ->
        val pv = body.orbit.getPVCoordinates(toDate, body.parent.frame)
        log.info("position=${pv.position} d=${pv.position.norm}")

      }

    }

    println("here!")


  }
}