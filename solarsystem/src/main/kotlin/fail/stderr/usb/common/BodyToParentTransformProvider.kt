package fail.stderr.usb.common

import org.hipparchus.CalculusFieldElement
import org.hipparchus.geometry.euclidean.threed.FieldVector3D
import org.orekit.frames.FieldTransform
import org.orekit.frames.Frame
import org.orekit.frames.Transform
import org.orekit.frames.TransformProvider
import org.orekit.orbits.KeplerianOrbit
import org.orekit.time.AbsoluteDate
import org.orekit.time.FieldAbsoluteDate
import org.orekit.utils.FieldPVCoordinates

class BodyToParentTransformProvider(
  val bodyOrbit: KeplerianOrbit,
  val parentFrame: Frame
) : TransformProvider {

  override fun getTransform(date: AbsoluteDate): Transform {
    val bodyPV = bodyOrbit.getPVCoordinates(date, parentFrame)
    return Transform(date, bodyPV)
  }

  override fun <T : CalculusFieldElement<T>> getTransform(date: FieldAbsoluteDate<T>): FieldTransform<T> {
    val bodyPV = bodyOrbit.getPVCoordinates(date.toAbsoluteDate(), parentFrame)
    val ft: FieldTransform<T> = FieldTransform(
      date,
      FieldPVCoordinates(
        FieldVector3D(date.field, bodyPV.position),
        FieldVector3D(date.field, bodyPV.velocity)
      )
    )
    return ft
  }
}
