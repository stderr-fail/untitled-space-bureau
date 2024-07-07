package fail.stderr.usb.model.result;

public sealed interface Result<R, E> permits ResultOk, ResultErr {
}

