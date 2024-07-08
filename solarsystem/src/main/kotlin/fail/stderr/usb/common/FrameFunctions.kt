package fail.stderr.usb.common;

import org.orekit.frames.Frame;
import org.orekit.orbits.KeplerianOrbit;

import java.lang.reflect.InvocationTargetException;

public class FrameFunctions {

  public static Frame createRootFrame(String name) {
    try {
      var c = Frame.class.getDeclaredConstructor(String.class, boolean.class);
      c.setAccessible(true);
      var frame = c.newInstance(name, true);
      return frame;
    } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  public static Frame createBodyFrame(String bodyName, KeplerianOrbit bodyOrbit, Frame parentFrame) {
    var transformProvider = new BodyToParentTransformProvider(bodyOrbit, parentFrame);
    return new Frame(parentFrame, transformProvider, "%sCenteredFrame".formatted(bodyName), true);
  }

}
