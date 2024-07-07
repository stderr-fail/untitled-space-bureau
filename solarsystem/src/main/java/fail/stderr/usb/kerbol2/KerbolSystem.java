package fail.stderr.usb.kerbol2;

import org.orekit.bodies.CelestialBody;
import org.orekit.frames.Frame;
import org.orekit.orbits.KeplerianOrbit;

public class KerbolSystem {

  public final Frame kerbolFrame = KerbolBodies.createKerbolFrame();

  public final KeplerianOrbit kerbinOrbit = KerbolBodies.createKerbinOrbit(kerbolFrame);
  public final Frame kerbinFrame = KerbolBodies.createKerbinFrame(kerbolFrame, kerbinOrbit);
  public final CelestialBody kerbin = KerbolBodies.createKerbin(kerbinFrame, kerbinOrbit);

  public final KeplerianOrbit munOrbit = KerbolBodies.createMunOrbit(kerbinFrame);
  public final Frame munFrame = KerbolBodies.createMunFrame(kerbinFrame, munOrbit);
  public final CelestialBody mun = KerbolBodies.createMun(munFrame, munOrbit);

  public final KeplerianOrbit minmusOrbit = KerbolBodies.createMinmusOrbit(kerbinFrame);
  public final Frame minmusFrame = KerbolBodies.createMinmusFrame(kerbinFrame, minmusOrbit);
  public final CelestialBody minmus = KerbolBodies.createMinmus(minmusFrame, minmusOrbit);

}
