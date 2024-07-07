package fail.stderr.usb;

import fail.stderr.usb.kerbol2.KerbolSystem;
import fail.stderr.usb.kerbol2.KerbolSystemConstants;
import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.junit.jupiter.api.Test;
import org.orekit.propagation.analytical.KeplerianPropagator;
import org.orekit.propagation.sampling.OrekitStepHandler;
import org.orekit.time.AbsoluteDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public class QuickTest {

  private static Logger log = LoggerFactory.getLogger(QuickTest.class);

  @Test
  public void test() throws Exception {

    var k = new KerbolSystem();


    // Create Keplerian propagator
    var kerbinPropagator = new KeplerianPropagator(k.kerbinOrbit);
    var munPropagator = new KeplerianPropagator(k.munOrbit);

    var lastPropagatedDate = new AtomicReference<AbsoluteDate>(KerbolSystemConstants.REF_DATE);

    Function<String, OrekitStepHandler> buildAdaptiveStepHandler = body -> {
      var step = new AtomicLong(0L);

      OrekitStepHandler stepHandler = interpolator -> {

        var p = interpolator.getCurrentState().getPVCoordinates().getPosition();

        var vec = new Vector3D(p.getX(), p.getZ(), p.getY());

        log.debug("{} [{}] propagated last={} to next={} to {} d={}", body, step, lastPropagatedDate, interpolator.getCurrentState().getDate(), vec, vec.getNorm());
        lastPropagatedDate.set(interpolator.getCurrentState().getDate());

        step.incrementAndGet();
      };

      return stepHandler;
    };

    kerbinPropagator.setStepHandler(buildAdaptiveStepHandler.apply("kerbin"));
    munPropagator.setStepHandler(buildAdaptiveStepHandler.apply("mun"));

    for (int i = 1; i <= 52; i++) {
      var nextDate = KerbolSystemConstants.REF_DATE.shiftedBy(i * 7L, TimeUnit.DAYS);
      kerbinPropagator.propagate(nextDate);
    }

    for (int i = 1; i <= 52; i++) {
      var nextDate = KerbolSystemConstants.REF_DATE.shiftedBy(i * 7L, TimeUnit.DAYS);
      munPropagator.propagate(nextDate);

    }

  }
}
