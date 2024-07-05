package fail.stderr.usb.sim

import godot.core.Vector3
import org.orekit.bodies.CelestialBodyFactory
import org.orekit.data.ClasspathCrawler
import org.orekit.data.DataContext
import org.orekit.orbits.OrbitType
import org.orekit.propagation.SpacecraftState
import org.orekit.propagation.numerical.NumericalPropagator
import org.orekit.propagation.sampling.OrekitFixedStepHandler
import org.orekit.propagation.sampling.OrekitStepHandler
import org.orekit.time.AbsoluteDate
import org.orekit.time.TimeScale
import org.orekit.time.TimeScalesFactory
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.concurrent.TimeUnit

class SolarSystemSim(var speed: Int) {

  val log = LoggerFactory.getLogger("usb.SolarSystemSim")

  var earthVec: Vector3? = null
  var moonVec: Vector3? = null
  var mercuryVec: Vector3? = null

  //  lateinit var earthPropagator: NumericalPropagator
  lateinit var moonPropagator: NumericalPropagator
//  lateinit var mercuryPropagator: NumericalPropagator

  lateinit var utc: TimeScale
  lateinit var startDate: AbsoluteDate
  lateinit var lastPropagatedDate: AbsoluteDate

  fun next(time: Double, timeScale: Double) {

    try {
//    GD.print("next start")

      val targetTimeSeconds = Math.floor(time * timeScale).toLong()

      val endDate = startDate.shiftedBy(targetTimeSeconds, TimeUnit.SECONDS)

//      earthPropagator.propagate(endDate)
      moonPropagator.propagate(endDate)
//      mercuryPropagator.propagate(endDate)
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

    val settings = PropagationSettings(
//      positionTolerance = 100.0,
//      positionTolerance = 10.0,
//      positionTolerance = 1.0,
//      positionTolerance = 1.0E-6,
//      positionTolerance = 1.0E-4,
//      positionTolerance = 10.0,
//      positionTolerance = 1.0,
//      positionTolerance = 1.0E-2,
//      positionTolerance = 1.0E-4,
      positionTolerance = 1.0E-6,
//      minStep = Duration.ofDays(7L).toSeconds().toDouble(),
//      minStep = 0.0001,
      minStep = 1E-10,
//      minStep = 0.01,
//      maxStep = Duration.ofDays(28L).toSeconds().toDouble(),
//      minStep = 1E-10,
//      minStep = 1E-3,
//      maxStep = Duration.ofHours(1L).toSeconds().toDouble(),
      maxStep = Duration.ofHours(6L).toSeconds().toDouble(),
//      maxStep = Duration.ofMinutes(5L).toSeconds().toDouble(),
//      maxStep = Duration.ofMinutes(1L).toSeconds().toDouble(),
//      propagationType = OrbitType.CARTESIAN,
      propagationType = OrbitType.KEPLERIAN,
    )

    val printEvery = 1000L

//    earthPropagator = buildEarthPropagator(startDate, settings)
    moonPropagator = buildMoonPropagator(startDate, settings)
//    mercuryPropagator = buildMercuryPropagator(startDate, settings)


    fun buildFixedStepHandler(body: String, vecSetter: (value: Vector3) -> Unit): OrekitFixedStepHandler {
      val stepHandler = OrekitFixedStepHandler { currentState ->
//        val currentDate = currentState.date
        val p = currentState.pvCoordinates.position
        val vec = Vector3(p.x, p.z, p.y)
//        GD.print("${body}: ${vec}")
        vecSetter(vec)
      }
      return buildStepHandler@ stepHandler
    }

    val semiMajorAxisMoon = 384.4e6 // Semi-major axis in meters
    val eccentricityMoon = 0.0549 // Orbital eccentricity


    fun buildAdaptiveStepHandler(numericalPropagator: NumericalPropagator, body: String, vecSetter: (value: Vector3) -> Unit): OrekitStepHandler {
      var step = 0L
      var resetCount = 0L
      val earthMu = CelestialBodyFactory.getEarth().gm

      val stepHandler = OrekitStepHandler { interpolator ->
        val p = interpolator.currentState.pvCoordinates.position
//        val currentDate = currentState.date
//        currentState.getPVCoordinates(currentState.date)
//        val p = currentState.pvCoordinates.position
        val vec = Vector3(p.x, p.z, p.y)
//        GD.print("${body}: ${vec}")
        vecSetter(vec)


        val semiMajorAxisDiff = Math.abs(semiMajorAxisMoon - interpolator.currentState.a)
        val eccentricityDiff = Math.abs(interpolator.currentState.e - eccentricityMoon)

        if (eccentricityDiff >= 0.9) {
//          log.debug("${body} [${step}] eDiff=${eccentricityDiff}, resetting state")

//          val orbit = buildMoonInitialOrbit(interpolator.currentState.date, earthMu)
          numericalPropagator.resetInitialState(numericalPropagator.initialState)
          resetCount++
        }

        if (++step % printEvery == 0L) {


          val semiMajorAxisDiff = Math.abs(semiMajorAxisMoon - interpolator.currentState.a)
          val eccentricityDiff = Math.abs(interpolator.currentState.e - eccentricityMoon)

          log.debug("${body} [${step}] [resets=${resetCount}] propagated last=${lastPropagatedDate} to next=${interpolator.currentState.date} aDiff=${semiMajorAxisDiff} eDiff=${eccentricityDiff} to ${vec}")

        }
        lastPropagatedDate = interpolator.currentState.date
      }
      return buildStepHandler@ stepHandler
    }


//    earthPropagator.setStepHandler(settings.minStep, buildFixedStepHandler("earth") { earthVec = it })
//    moonPropagator.setStepHandler(settings.minStep, buildFixedStepHandler("moon") { moonVec = it })
//    mercuryPropagator.setStepHandler(settings.minStep, buildFixedStepHandler("mercury") { mercuryVec = it })

//    earthPropagator.setStepHandler(buildAdaptiveStepHandler("earth") { earthVec = it })
    moonPropagator.setStepHandler(buildAdaptiveStepHandler(moonPropagator, "moon") { moonVec = it })
//    mercuryPropagator.setStepHandler(buildAdaptiveStepHandler("mercury") { mercuryVec = it })

    val shiftBy = 84L
//    val shiftBy = 168L

//    earthPropagator.propagate(startDate, startDate.shiftedBy(shiftBy, TimeUnit.HOURS))
    moonPropagator.propagate(startDate, startDate.shiftedBy(shiftBy, TimeUnit.HOURS))

//    mercuryPropagator.propagate(startDate, startDate.shiftedBy(shiftBy, TimeUnit.HOURS))

//    GD.print("init done")
  }


}