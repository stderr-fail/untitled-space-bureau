package org.orekit.time;

public final class TimeScaleFunctions {

  public static  TimeScale createConstantOffsetTimeScale(String name, double offset) {
    return new ConstantOffsetTimeScale(name, offset);
  }

}
