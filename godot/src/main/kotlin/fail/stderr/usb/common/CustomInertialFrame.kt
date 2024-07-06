package fail.stderr.usb.common

import org.hipparchus.CalculusFieldElement
import org.orekit.frames.FieldTransform
import org.orekit.frames.Frame
import org.orekit.frames.Transform
import org.orekit.frames.TransformProvider
import org.orekit.time.AbsoluteDate
import org.orekit.time.FieldAbsoluteDate


object CustomInertialFrame {
  fun create(): Frame {

    val transformProvider: TransformProvider = object : TransformProvider {
      override fun getTransform(date: AbsoluteDate): Transform {
        return Transform.IDENTITY
      }

      override fun <T : CalculusFieldElement<T>?> getTransform(date: FieldAbsoluteDate<T>): FieldTransform<T> {
        return FieldTransform(date.field.zero!!.field, Transform.IDENTITY)
      }
    }

    // Create a custom inertial frame
    val customFrame = Frame(Frame.getRoot(), transformProvider, "CustomInertialFrame", true)
    return customFrame
  }
}