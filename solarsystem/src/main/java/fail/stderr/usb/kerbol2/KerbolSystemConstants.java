package fail.stderr.usb.kerbol2;

import fail.stderr.usb.time.CustomTimeScale;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScale;

public final class KerbolSystemConstants {

  public static final TimeScale TIME_SCALE = new CustomTimeScale("KERBOLTIME", 0.0);

  public static final AbsoluteDate REF_DATE = new AbsoluteDate(1983, 7, 14, TIME_SCALE);

}
