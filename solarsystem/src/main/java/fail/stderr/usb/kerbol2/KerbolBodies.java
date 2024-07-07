package fail.stderr.usb.kerbol2;

import fail.stderr.usb.common2.FrameFunctions;
import fail.stderr.usb.common2.KeplerianOrbitParameters;
import fail.stderr.usb.common2.OrbitFunctions;
import org.orekit.frames.Frame;
import org.orekit.orbits.KeplerianOrbit;

public class KerbolBodies {

  public static class Constants {
    public static final double KERBOL_MU = 1.1723328e18;
    public static final double KERBIN_MU = 3.5316e12;
    public static final double MUN_MU = 6.5138398e10;
  }

  public static Frame createKerbolFrame() {
    return FrameFunctions.createRootFrame("Kerbol");
  }

  public static KeplerianOrbit createKerbinOrbit(Frame parentFrame) {
    return OrbitFunctions.createKeplerianOrbit(
      new KeplerianOrbitParameters(
        13599840256.0, // semiMajorAxis
        0.0, // eccentricity
        0.0, // inclination
        0.0, // pa
        0.0, // raan
        3.14 // anomaly
      ),
      parentFrame,
      KerbolSystemConstants.REF_DATE,
      KerbolBodies.Constants.KERBOL_MU
    );
  }

  public static Frame createKerbinFrame(Frame parentFrame, KeplerianOrbit bodyOrbit) {
    return FrameFunctions.createBodyFrame("Kerbin", bodyOrbit, parentFrame);
  }

  public static KeplerianOrbit createMunOrbit(Frame parentFrame) {
    return OrbitFunctions.createKeplerianOrbit(
      new KeplerianOrbitParameters(
        12000000.0, // semiMajorAxis
        0.0, // eccentricity
        0.0, // inclination
        0.0, // pa
        0.0, // raan
        1.7 // anomaly
      ),
      parentFrame,
      KerbolSystemConstants.REF_DATE,
      KerbolBodies.Constants.KERBIN_MU
    );
  }

  public static Frame createMunFrame(Frame parentFrame, KeplerianOrbit bodyOrbit) {
    return FrameFunctions.createBodyFrame("Mun", bodyOrbit, parentFrame);
  }

}
