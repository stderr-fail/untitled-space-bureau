package fail.stderr.usb.kerbol2;

import org.orekit.time.AbsoluteDate;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class KerbolSimMain {

  public static void main(String[] args) throws Exception {


    var k = new KerbolSystem();

    final AbsoluteDate[] testDates = {
      KerbolSystemConstants.REF_DATE.shiftedBy(0.0),
      KerbolSystemConstants.REF_DATE.shiftedBy(1L, TimeUnit.DAYS),
      KerbolSystemConstants.REF_DATE.shiftedBy(7L, TimeUnit.DAYS),
      KerbolSystemConstants.REF_DATE.shiftedBy(28L, TimeUnit.DAYS),
      KerbolSystemConstants.REF_DATE.shiftedBy(365L, TimeUnit.DAYS)
    };

    Arrays.stream(testDates).toList().forEach(date -> {
      var kerbinPV = k.kerbin.getPVCoordinates(date, k.kerbolFrame);
      System.out.println("Date: %s - Position (kerbol): vec=%s d=%s m".formatted(date, kerbinPV.getPosition(), kerbinPV.getPosition().getNorm()));

      var munPV = k.mun.getPVCoordinates(date, k.kerbinFrame);
      System.out.println("Date: %s - Position (mun): vec=%s d=%s m".formatted(date, munPV.getPosition(), munPV.getPosition().getNorm()));
    });

  }

}
