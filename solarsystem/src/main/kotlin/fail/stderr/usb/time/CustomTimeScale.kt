package fail.stderr.usb.time

import org.orekit.time.ConstantOffsetTimeScale

class CustomTimeScale(name: String, offset: Double) : ConstantOffsetTimeScale(name, offset)