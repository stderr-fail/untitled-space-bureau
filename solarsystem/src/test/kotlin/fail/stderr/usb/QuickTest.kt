package fail.stderr.usb

import fail.stderr.usb.kerbol.KerbolSystem
import org.hipparchus.geometry.euclidean.threed.Vector3D
import org.junit.jupiter.api.Test
import org.orekit.errors.OrekitException
import org.orekit.propagation.analytical.KeplerianPropagator
import org.orekit.propagation.sampling.OrekitStepHandler
import org.orekit.time.AbsoluteDate
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit


class QuickTest {

  // ksp orbit references https://github.com/Arrowstar/ksptot/blob/master/bodies.ini

  val log = LoggerFactory.getLogger(QuickTest::class.java)

  @Test
  fun abc() {

    try {

      val k = KerbolSystem()


      // Create Keplerian propagator
      val kerbinPropagator = KeplerianPropagator(k.kerbinOrbit)
      val munPropagator = KeplerianPropagator(k.munOrbit)

      var lastPropagatedDate: AbsoluteDate = KerbolSystem.REF_DATE

      fun buildAdaptiveStepHandler(body: String, vecSetter: (value: Vector3D) -> Unit): OrekitStepHandler {
        var step = 0L

        val stepHandler = OrekitStepHandler { interpolator ->
          val p = interpolator.currentState.pvCoordinates.position

          val vec = Vector3D(p.x, p.z, p.y)

          vecSetter(vec)

          log.debug("${body} [${step}] propagated last=${lastPropagatedDate} to next=${interpolator.currentState.date} to ${vec} d=${vec.norm}")
          lastPropagatedDate = interpolator.currentState.date

          step++
        }
        return buildStepHandler@ stepHandler
      }

      kerbinPropagator.setStepHandler(buildAdaptiveStepHandler("kerbin") {})
      munPropagator.setStepHandler(buildAdaptiveStepHandler("mun") {})


      for (i in 1..52) {
        val nextDate = KerbolSystem.REF_DATE.shiftedBy(i.toLong() * 7L, TimeUnit.DAYS)
        kerbinPropagator.propagate(nextDate)
      }

      for (i in 1..52) {
        val nextDate = KerbolSystem.REF_DATE.shiftedBy(i.toLong() * 7L, TimeUnit.DAYS)
        munPropagator.propagate(nextDate)

      }

    } catch (oe: OrekitException) {
      oe.printStackTrace()
    }

  }
}