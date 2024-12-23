import java.time.LocalTime;

public class UnavailableRunwayException extends Exception {
    public UnavailableRunwayException() {
    }
    public UnavailableRunwayException(LocalTime timestamp) {
        super(timestamp.toString() + " | " + "The chosen runway for maneuver is currently occupied");
    }
}
