package fail.stderr.usb.model.result;

public record ResultOk<R>(
  R value
) implements Result<R, Void> {

}
