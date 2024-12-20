import java.util.Comparator;

public class RunwayComparatorTakeoff<T extends Airplane> implements Comparator<T> {
    @Override
    public int compare(T airplane1, T airplane2) {
        if (airplane1.getTimpDorit().equals(airplane2.getTimpDorit())) {
            return 0;
        } else if (airplane1.getTimpDorit().isAfter(airplane2.getTimpDorit())) {
            return 1;
        } else {
            return -1;
        }
    }
}
