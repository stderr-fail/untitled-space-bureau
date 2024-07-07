package fail.stderr.usb.model.system;

public record CelestialBodyData(
  String name,
  double mu,
  KeplerianOrbitalParametersData initialOrbitalParameters,
  CelestialBodyData[] children
) {
}
