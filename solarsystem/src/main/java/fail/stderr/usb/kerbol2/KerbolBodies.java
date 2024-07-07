package fail.stderr.usb.kerbol2;

import fail.stderr.usb.common.CustomCelestialBody;
import fail.stderr.usb.common.FrameFunctions;
import fail.stderr.usb.common.KeplerianOrbitParameters;
import fail.stderr.usb.common.OrbitFunctions;
import org.orekit.bodies.CelestialBody;
import org.orekit.frames.Frame;
import org.orekit.orbits.KeplerianOrbit;

// see https://wiki.kerbalspaceprogram.com/ for full reference
// see https://github.com/Arrowstar/ksptot/blob/master/bodies.ini for quick ref
public class KerbolBodies {

  public static class Constants {

    public static final double KERBOL_MU = 1.1723328e18;
    public static final double KERBIN_MU = 3.5316e12;
    public static final double MUN_MU = 6.5138398e10;
    public static final double MINMUS_MU = 1.7658e9;

    public static final String KERBOL_NAME = "Kerbol";
    public static final String KERBIN_NAME = "Kerbin";
    public static final String MUN_NAME = "Mun";
    public static final String MINMUS_NAME = "Minmus";

  }

  public static Frame createKerbolFrame() {
    return FrameFunctions.createRootFrame(Constants.KERBOL_NAME);
  }

  public static KeplerianOrbitParameters createKerbinOrbitParameters() {
    return new KeplerianOrbitParameters(
      13599840256.0, // semiMajorAxis
      0.0, // eccentricity
      0.0, // inclination
      0.0, // pa
      0.0, // raan
      3.14 // anomaly
    );
  }

  public static KeplerianOrbit createKerbinOrbit(Frame parentFrame) {
    return OrbitFunctions.createKeplerianOrbit(
      createKerbinOrbitParameters(),
      parentFrame,
      KerbolSystemConstants.REF_DATE,
      KerbolBodies.Constants.KERBOL_MU
    );
  }

  public static Frame createKerbinFrame(Frame parentFrame, KeplerianOrbit bodyOrbit) {
    return FrameFunctions.createBodyFrame(Constants.KERBIN_NAME, bodyOrbit, parentFrame);
  }

  public static CelestialBody createKerbin(Frame frame, KeplerianOrbit bodyOrbit) {
    return new CustomCelestialBody(Constants.KERBIN_NAME, KerbolBodies.Constants.KERBIN_MU, frame, bodyOrbit);
  }

  public static KeplerianOrbitParameters createMunOrbitParameters() {
    return new KeplerianOrbitParameters(
      12000000.0, // semiMajorAxis
      0.0, // eccentricity
      0.0, // inclination
      0.0, // pa
      0.0, // raan
      1.7 // anomaly
    );
  }

  public static KeplerianOrbit createMunOrbit(Frame parentFrame) {
    return OrbitFunctions.createKeplerianOrbit(
      createMunOrbitParameters(),
      parentFrame,
      KerbolSystemConstants.REF_DATE,
      KerbolBodies.Constants.KERBIN_MU
    );
  }

  public static Frame createMunFrame(Frame parentFrame, KeplerianOrbit bodyOrbit) {
    return FrameFunctions.createBodyFrame(Constants.MUN_NAME, bodyOrbit, parentFrame);
  }

  public static CelestialBody createMun(Frame frame, KeplerianOrbit bodyOrbit) {
    return new CustomCelestialBody(Constants.MUN_NAME, KerbolBodies.Constants.MUN_MU, frame, bodyOrbit);
  }

  public static KeplerianOrbitParameters createMinmusOrbitParameters() {
    return new KeplerianOrbitParameters(
      47000000, // semiMajorAxis
      0.0, // eccentricity
      Math.toRadians(6.0), // inclination
      Math.toRadians(38.0), // pa
      Math.toRadians(78), // raan
      0.9 // anomaly
    );
  }

  public static KeplerianOrbit createMinmusOrbit(Frame parentFrame) {
    return OrbitFunctions.createKeplerianOrbit(
      createMinmusOrbitParameters(),
      parentFrame,
      KerbolSystemConstants.REF_DATE,
      KerbolBodies.Constants.KERBIN_MU
    );
  }

  public static Frame createMinmusFrame(Frame parentFrame, KeplerianOrbit bodyOrbit) {
    return FrameFunctions.createBodyFrame(Constants.MINMUS_NAME, bodyOrbit, parentFrame);
  }

  public static CelestialBody createMinmus(Frame frame, KeplerianOrbit bodyOrbit) {
    return new CustomCelestialBody(Constants.MINMUS_NAME, Constants.MINMUS_MU, frame, bodyOrbit);
  }

}
