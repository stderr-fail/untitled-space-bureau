package fail.stderr.usb.kerbol2;

import fail.stderr.usb.common2.CustomCelestialBody;
import org.orekit.bodies.CelestialBody;
import org.orekit.frames.Frame;
import org.orekit.orbits.KeplerianOrbit;

public class KerbolSystem {

  public final Frame kerbolFrame = KerbolBodies.createKerbolFrame();

  public final KeplerianOrbit kerbinOrbit = KerbolBodies.createKerbinOrbit(kerbolFrame);
  public final Frame kerbinFrame = KerbolBodies.createKerbinFrame(kerbolFrame, kerbinOrbit);
  public final CelestialBody kerbin = new CustomCelestialBody("Kerbin", KerbolBodies.Constants.KERBIN_MU, kerbinFrame, kerbinOrbit);

  public final KeplerianOrbit munOrbit = KerbolBodies.createMunOrbit(kerbinFrame);
  public final Frame munFrame = KerbolBodies.createMunFrame(kerbinFrame, munOrbit);
  public final CelestialBody mun = new CustomCelestialBody("Mun", KerbolBodies.Constants.MUN_MU, munFrame, munOrbit);

}
