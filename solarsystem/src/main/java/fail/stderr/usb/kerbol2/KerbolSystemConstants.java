package fail.stderr.usb.kerbol2;

import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScale;
import org.orekit.time.TimeScaleFunctions;

public final class KerbolSystemConstants {

  public static final TimeScale TIME_SCALE = TimeScaleFunctions.createConstantOffsetTimeScale("KERBOLTIME", 0.0);

  public static final AbsoluteDate REF_DATE = new AbsoluteDate(1983, 7, 14, TIME_SCALE);

}
