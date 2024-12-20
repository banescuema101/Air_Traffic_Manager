import java.util.ArrayList;
import java.util.List;

public class GestiunePiste {
    private List<Runway<WideBodyAirplane>> wideRunwayList = new ArrayList<>();
    private List<Runway<NarrowBodyAirplane>> narrowRunwayList = new ArrayList<>();
    public void addToWideList(Runway<WideBodyAirplane> runway1) {
        wideRunwayList.add(runway1);
    }
    public void addToNarrowList(Runway<NarrowBodyAirplane> runway2) {
        narrowRunwayList.add(runway2);
    }
    public Runway<WideBodyAirplane> findWideRunway(String id) {
        for (Runway<WideBodyAirplane> runway1 : wideRunwayList) {
            if ((runway1.getId()).equals(id)) {
                return runway1;
            }
        }
        return null;
    }
    public Runway<NarrowBodyAirplane> findNarrowRunway(String id) {
        for (Runway<NarrowBodyAirplane> runway1 : narrowRunwayList) {
            if ((runway1.getId()).equals(id)) {
                return runway1;
            }
        }
        return null;
    }
    public void listareWideRunway() {
        for (Runway<WideBodyAirplane> runway : this.wideRunwayList) {
            System.out.println(runway.toString());
        }
    }
    public void listareNarrowRunway() {
        for (Runway<NarrowBodyAirplane> runway : narrowRunwayList) {
            System.out.println(runway.toString());
        }
    }
}
