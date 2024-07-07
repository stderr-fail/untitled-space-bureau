package fail.stderr.usb.model.system;

public record KeplerianOrbitalParametersData(
  double semiMajorAxis,
  double eccentricity,
  double inclination,
  double perigreeArgument,
  double raan,
  double anomaly
) {
}
