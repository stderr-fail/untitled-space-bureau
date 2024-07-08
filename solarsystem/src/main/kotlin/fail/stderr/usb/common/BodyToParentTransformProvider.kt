package fail.stderr.usb.common;

import org.hipparchus.CalculusFieldElement;
import org.hipparchus.geometry.euclidean.threed.FieldVector3D;
import org.jetbrains.annotations.NotNull;
import org.orekit.frames.FieldTransform;
import org.orekit.frames.Frame;
import org.orekit.frames.Transform;
import org.orekit.frames.TransformProvider;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.FieldAbsoluteDate;
import org.orekit.utils.FieldPVCoordinates;

public record BodyToParentTransformProvider(
  @NotNull KeplerianOrbit bodyOrbit,
  @NotNull Frame parentFrame
) implements TransformProvider {

  @Override
  public Transform getTransform(AbsoluteDate date) {
    final var bodyPV = bodyOrbit.getPVCoordinates(date, parentFrame);
    return new Transform(date, bodyPV);
  }

  @Override
  public <T extends CalculusFieldElement<T>> FieldTransform<T> getTransform(FieldAbsoluteDate<T> date) {
    final var bodyPV = bodyOrbit.getPVCoordinates(date.toAbsoluteDate(), parentFrame);
    final var ft = new FieldTransform(
      date,
      new FieldPVCoordinates(
        new FieldVector3D(date.getField(), bodyPV.getPosition()),
        new FieldVector3D(date.getField(), bodyPV.getVelocity())
      )
    );
    return ft;
  }

}
