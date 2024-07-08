package fail.stderr.usb.ex

class DataDeserializationException : RuntimeException {

  constructor(e: String) : super(e)

  constructor(e: String, cause: Exception) : super(e, cause)

}