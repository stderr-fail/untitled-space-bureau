package fail.stderr.usb.model.data;

public record DataDeserializationError(String message, Exception cause) {
}
