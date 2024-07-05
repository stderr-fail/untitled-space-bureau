package fail.stderr.usb.sim

import godot.core.Vector3
import org.orekit.data.ClasspathCrawler
import org.orekit.data.DataContext
import org.orekit.propagation.analytical.KeplerianPropagator
import org.orekit.propagation.sampling.OrekitStepHandler
import org.orekit.time.AbsoluteDate
import org.orekit.time.TimeScale
import org.orekit.time.TimeScalesFactory
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

class SolarSystemSim(var speed: Int) {

  val log = LoggerFactory.getLogger("usb.SolarSystemSim")

  var earthVec: Vector3? = null
  var moonVec: Vector3? = null
  var mercuryVec: Vector3? = null

  lateinit var earthPropagator: KeplerianPropagator
  lateinit var moonPropagator: KeplerianPropagator
  lateinit var mercuryPropagator: KeplerianPropagator

  lateinit var utc: TimeScale
  lateinit var startDate: AbsoluteDate
  lateinit var lastPropagatedDate: AbsoluteDate

  fun next(endDate: AbsoluteDate) {

    try {
//    GD.print("next start")

//      val targetTimeSeconds = Math.floor(time * timeScale).toLong()
//
//      val endDate = startDate.shiftedBy(targetTimeSeconds, TimeUnit.SECONDS)

      earthPropagator.propagate(endDate)
      moonPropagator.propagate(endDate)
      mercuryPropagator.propagate(endDate)
//    GD.print("next end")
    } catch (e: Exception) {
      log.error(e.message, e)
      throw e
//      GD.printErr(e.message)
//      GD.printErr(e.stackTraceToString())
    }
  }

  fun prep() {
//    GD.print("init start")


    // utc time scale
    DataContext.getDefault().dataProvidersManager.addProvider(ClasspathCrawler("tai-utc.dat"))
    // jpl ephemeris
    DataContext.getDefault().dataProvidersManager.addProvider(ClasspathCrawler("DE-440-ephemerides/lnxp1990.440"))

    utc = TimeScalesFactory.getUTC()

    startDate = AbsoluteDate(2024, 6, 25, 0, 0, 0.0, utc)
    lastPropagatedDate = startDate

    val printEvery = 1000L

    earthPropagator = buildEarthPropagator()
    moonPropagator = buildMoonPropagator()
    mercuryPropagator = buildMercuryPropagator()


    fun buildAdaptiveStepHandler(
      body: String,
      vecSetter: (value: Vector3) -> Unit
    ): OrekitStepHandler {
      var step = 0L

      val stepHandler = OrekitStepHandler { interpolator ->
        val p = interpolator.currentState.pvCoordinates.position
//        val currentDate = currentState.date
//        currentState.getPVCoordinates(currentState.date)
//        val p = currentState.pvCoordinates.position
        val vec = Vector3(p.x, p.z, p.y)
//        GD.print("${body}: ${vec}")
        vecSetter(vec)



        if (step % printEvery == 0L) {

          log.debug("${body} [${step}] propagated last=${lastPropagatedDate} to next=${interpolator.currentState.date} to ${vec}")

        }
        lastPropagatedDate = interpolator.currentState.date
        step++
      }
      return buildStepHandler@ stepHandler
    }


//    earthPropagator.setStepHandler(settings.minStep, buildFixedStepHandler("earth") { earthVec = it })
//    moonPropagator.setStepHandler(settings.minStep, buildFixedStepHandler("moon") { moonVec = it })
//    mercuryPropagator.setStepHandler(settings.minStep, buildFixedStepHandler("mercury") { mercuryVec = it })

    earthPropagator.setStepHandler(buildAdaptiveStepHandler("earth") { earthVec = it })
    moonPropagator.setStepHandler(buildAdaptiveStepHandler("moon") { moonVec = it })
    mercuryPropagator.setStepHandler(buildAdaptiveStepHandler("mercury") { mercuryVec = it })

    val shiftBy = 1L
//    val shiftBy = 168L

    earthPropagator.propagate(startDate, startDate.shiftedBy(shiftBy, TimeUnit.DAYS))
    moonPropagator.propagate(startDate, startDate.shiftedBy(shiftBy, TimeUnit.DAYS))
    mercuryPropagator.propagate(startDate, startDate.shiftedBy(shiftBy, TimeUnit.DAYS))

//    GD.print("init done")
  }


}