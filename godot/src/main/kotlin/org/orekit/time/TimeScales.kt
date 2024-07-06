package org.orekit.time

fun createConstantOffsetTimeScale(name: String, offset: Double): TimeScale {
  return ConstantOffsetTimeScale(name, offset)
}