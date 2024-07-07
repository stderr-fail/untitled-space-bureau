package fail.stderr.usb.common2;

import org.hipparchus.CalculusFieldElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.orekit.bodies.CelestialBody;
import org.orekit.frames.Frame;
import org.orekit.orbits.Orbit;
import org.orekit.time.FieldAbsoluteDate;
import org.orekit.utils.PVCoordinates;
import org.orekit.utils.TimeStampedFieldPVCoordinates;
import org.orekit.utils.TimeStampedPVCoordinates;

public record CustomCelestialBody(
  @NotNull String name,
  @NotNull double mu,
  @NotNull Frame frame,
  @Nullable Orbit orbit
) implements CelestialBody {

  @Override
  public Frame getInertiallyOrientedFrame() {
    return frame;
  }

  @Override
  public Frame getBodyOrientedFrame() {
    return frame;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public double getGM() {
    return mu;
  }

  @Override
  public <T extends CalculusFieldElement<T>> TimeStampedFieldPVCoordinates<T> getPVCoordinates(FieldAbsoluteDate<T> date, Frame frame) {
    switch (orbit) {
      case null -> {
        return new TimeStampedFieldPVCoordinates(
          date.getField(),
          new TimeStampedPVCoordinates(date.toAbsoluteDate(), PVCoordinates.ZERO)
        );
      }
      default -> {
        final var pv = orbit.getPVCoordinates(date.toAbsoluteDate(), frame);
        return new TimeStampedFieldPVCoordinates(date.getField(), pv);
      }
    }
  }

}
