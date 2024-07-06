package fail.stderr.usb

import fail.stderr.usb.common.CommonConstants
import fail.stderr.usb.kerbol.buildKerbinOrbit
import fail.stderr.usb.kerbol.buildMunOrbit
import fail.stderr.usb.kerbol.createKerbinCenteredFrame
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

      // Create Keplerian propagator
      val kerbinPropagator = KeplerianPropagator(buildKerbinOrbit())
      val munPropagator = KeplerianPropagator(buildMunOrbit())

      val kerbinFrame = createKerbinCenteredFrame()

      var lastPropagatedDate: AbsoluteDate = CommonConstants.REF_DATE

      fun buildAdaptiveStepHandler(body: String, vecSetter: (value: Vector3D) -> Unit): OrekitStepHandler {
        var step = 0L

        val stepHandler = OrekitStepHandler { interpolator ->
          val p = interpolator.currentState.pvCoordinates.position

          val vec = Vector3D(p.x, p.z, p.y)

          vecSetter(vec)

          log.debug("${body} [${step}] propagated last=${lastPropagatedDate} to next=${interpolator.currentState.date} to ${vec}")
          lastPropagatedDate = interpolator.currentState.date

          if (body == "mun") {
            val transformed = CommonConstants.REF_INERTIAL_FRAME
              .getTransformTo(kerbinFrame, interpolator.currentState.date)
              .transformPVCoordinates(interpolator.currentState.pvCoordinates)
            val xVec = Vector3D(transformed.position.x, transformed.position.z, transformed.position.y)

            log.debug("${body} [${step}] propagated last=${lastPropagatedDate} to next=${interpolator.currentState.date} to ${vec} x ${xVec}")


          }

          step++
        }
        return buildStepHandler@ stepHandler
      }

      kerbinPropagator.setStepHandler(buildAdaptiveStepHandler("kerbin") {})
      munPropagator.setStepHandler(buildAdaptiveStepHandler("mun") {})


      for (i in 1..52) {
        val nextDate = CommonConstants.REF_DATE.shiftedBy(i.toLong() * 7L, TimeUnit.DAYS)
        kerbinPropagator.propagate(nextDate)
      }

      for (i in 1..52) {
        val nextDate = CommonConstants.REF_DATE.shiftedBy(i.toLong() * 7L, TimeUnit.DAYS)
        munPropagator.propagate(nextDate)

      }

    } catch (oe: OrekitException) {
      oe.printStackTrace()
    }

  }
}