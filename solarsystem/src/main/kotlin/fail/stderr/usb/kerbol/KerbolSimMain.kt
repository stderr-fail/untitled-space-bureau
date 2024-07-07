package fail.stderr.usb.kerbol

import java.util.concurrent.TimeUnit

fun main() {

  val k = KerbolSystem()

  val testDates = arrayOf(
    KerbolSystem.REF_DATE.shiftedBy(0.0),
    KerbolSystem.REF_DATE.shiftedBy(1L, TimeUnit.DAYS),
    KerbolSystem.REF_DATE.shiftedBy(7L, TimeUnit.DAYS),
    KerbolSystem.REF_DATE.shiftedBy(28L, TimeUnit.DAYS),
    KerbolSystem.REF_DATE.shiftedBy(365L, TimeUnit.DAYS)
  )

  testDates.forEach { date ->

    val kerbinPV = k.kerbin.getPVCoordinates(date, k.kerbolFrame)
    println("Date: $date - Position (kerbol): vec=${kerbinPV.position} d=${kerbinPV.position.norm} m")

    val munPV = k.mun.getPVCoordinates(date, k.kerbinFrame)
    println("Date: $date - Position (mun): vec=${munPV.position} d=${munPV.position.norm} m")

  }

}
