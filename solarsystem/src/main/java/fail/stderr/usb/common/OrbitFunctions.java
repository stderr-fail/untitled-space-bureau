package fail.stderr.usb.common;

import org.orekit.frames.Frame;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.PositionAngleType;
import org.orekit.time.AbsoluteDate;

public class OrbitFunctions {

  public static KeplerianOrbit createKeplerianOrbit(
    KeplerianOrbitParameters params,
    Frame parentFrame,
    AbsoluteDate refDate,
    double bodyMU
  ) {
    return new KeplerianOrbit(
      params.semiMajorAxis(),
      params.eccentricity(),
      params.inclination(),
      params.pa(),
      params.raan(),
      params.anomaly(),
      PositionAngleType.MEAN,
      parentFrame,
      refDate,
      bodyMU
    );
  }

}
