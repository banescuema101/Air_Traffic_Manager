import java.time.LocalTime;

public class IncorrectRunwayException extends Exception {
    public IncorrectRunwayException() {
        super("IncorrectRunwayException");
    }
    public IncorrectRunwayException(String message) {
        super(message);
    }
}
