package fail.stderr.usb.solarsystem

import godot.core.Vector3
import godot.global.GD
import org.orekit.bodies.CelestialBodyFactory
import org.orekit.data.ClasspathCrawler
import org.orekit.data.DataContext
import org.orekit.orbits.OrbitType
import org.orekit.propagation.numerical.NumericalPropagator
import org.orekit.propagation.sampling.OrekitFixedStepHandler
import org.orekit.time.AbsoluteDate
import org.orekit.time.TimeScale
import org.orekit.time.TimeScalesFactory
import java.time.Duration
import java.util.concurrent.TimeUnit

class SolarSystemSim {

  var earthVec: Vector3? = null

  lateinit var earthPropagator: NumericalPropagator

  lateinit var utc: TimeScale

  lateinit var startDate: AbsoluteDate

  fun next(time: Double, timeScale: Double) {

//    GD.print("next start")

    val targetTimeSeconds = Math.floor(time * timeScale).toLong()

    val endDate = startDate.shiftedBy(targetTimeSeconds, TimeUnit.SECONDS)

    earthPropagator.propagate(endDate)
//    GD.print("next end")
  }

  fun prep() {
    GD.print("init start")



    // utc time scale
    DataContext.getDefault().getDataProvidersManager().addProvider(ClasspathCrawler("tai-utc.dat"))
    // jpl ephemeris
    DataContext.getDefault().getDataProvidersManager().addProvider(ClasspathCrawler("DE-440-ephemerides/lnxp1990.440"))

    utc = TimeScalesFactory.getUTC()

    startDate = AbsoluteDate(2024, 6, 25, 0, 0, 0.0, utc)

    val settings = PropagationSettings(
      positionTolerance = 10.0,
      minStep = Duration.ofDays(7L).toSeconds().toDouble(),
      maxStep = Duration.ofDays(7L).toSeconds().toDouble(),
//      propagationType = OrbitType.CARTESIAN,
      propagationType = OrbitType.CARTESIAN,
    )

    earthPropagator = buildEarthPropagator(startDate, settings)

    val sun = CelestialBodyFactory.getSun()

    fun buildStepHandler(body: String, out: MutableList<String>): OrekitFixedStepHandler {
      val stepHandler = OrekitFixedStepHandler { currentState ->

//        GD.print("in step handler!!");

//        if (steps++ % printEvery == 0L) {
//          val currentDate = currentState.date
////          log.info("${body} at ${currentDate}")
//          val p = currentState.pvCoordinates.position
//          // cartesian z up/down
////          out.add("Vector3(${formatter.format(p.x)}, ${formatter.format(p.y)}, ${formatter.format(p.z)})")
//          // godot y up/down
//          out.add("Vector3(${formatter.format(p.x)}, ${formatter.format(p.z)}, ${formatter.format(p.y)})")
//        }
          val p = currentState.pvCoordinates.position

        earthVec = Vector3(p.x, p.z, p.y)

      }
      return buildStepHandler@ stepHandler
    }

    val earthVectors = mutableListOf<String>()

    earthPropagator.setStepHandler(settings.minStep, buildStepHandler("earth", earthVectors))

    earthPropagator.propagate(startDate, startDate.shiftedBy(168L, TimeUnit.HOURS))


    GD.print("init done")
  }



}