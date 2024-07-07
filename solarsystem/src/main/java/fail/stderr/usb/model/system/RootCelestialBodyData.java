package fail.stderr.usb.model.system;

public record RootCelestialBodyData(
  String name,
  double mu,
  CelestialBodyData[] children
) {
}
