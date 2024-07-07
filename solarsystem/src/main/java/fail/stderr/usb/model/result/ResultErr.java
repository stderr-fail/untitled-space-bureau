package fail.stderr.usb.model.result;

public record ResultErr<E>(
  E err
) implements Result<Void, E> {
}
