package fail.stderr.usb;

import fail.stderr.usb.kerbol2.KerbolSystem;
import fail.stderr.usb.kerbol2.KerbolSystemConstants;
import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.junit.jupiter.api.Test;
import org.orekit.propagation.Propagator;
import org.orekit.propagation.PropagatorsParallelizer;
import org.orekit.propagation.analytical.KeplerianPropagator;
import org.orekit.propagation.sampling.MultiSatStepHandler;
import org.orekit.propagation.sampling.OrekitStepInterpolator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class QuickTest {

  private static Logger log = LoggerFactory.getLogger(QuickTest.class);

  @Test
  public void test() throws Exception {

    var k = new KerbolSystem();

    final ArrayList<Propagator> propagators = new ArrayList<>();
    propagators.add(new KeplerianPropagator(k.kerbinOrbit));
    propagators.add(new KeplerianPropagator(k.munOrbit));
    propagators.add(new KeplerianPropagator(k.minmusOrbit));

    var step = new AtomicLong(0L);
    var lastPropagatedDate = new AtomicReference<>(KerbolSystemConstants.REF_DATE);


    final MultiSatStepHandler handler = interpolators -> {
      for (OrekitStepInterpolator interpolator : interpolators) {

        var p = interpolator.getCurrentState().getPVCoordinates().getPosition();

        var vec = new Vector3D(p.getX(), p.getZ(), p.getY());

        log.debug("{} [{}] propagated last={} to next={} to {} d={}", "??", step, lastPropagatedDate, interpolator.getCurrentState().getDate(), vec, vec.getNorm());
        lastPropagatedDate.set(interpolator.getCurrentState().getDate());

      }
    };

    var propagator = new PropagatorsParallelizer(propagators, handler);

    var current = KerbolSystemConstants.REF_DATE;

    for (int i = 1; i <= 52; i++) {
      var next = current.shiftedBy(i * 7L, TimeUnit.DAYS);
      propagator.propagate(current, next);
      current = next;
    }

  }
}
