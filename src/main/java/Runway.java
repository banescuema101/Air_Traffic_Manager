import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Runway<T extends Airplane> {
    private LocalTime timestamp;
    private String id;
    private String utilizare;
    private PriorityQueue<T> coadaAvioane;
    public Runway() {

    }
    public Runway(LocalTime timestamp, String id, String utilizare, String tipAvionAcceptat, Comparator<T> comparator) {
        this.timestamp = timestamp;
        this.id = id;
        this.utilizare = utilizare;
        this.coadaAvioane = new PriorityQueue<>(comparator);
    }

    public LocalTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUtilizare() {
        return utilizare;
    }

    public void setUtilizare(String utilizare) {
        this.utilizare = utilizare;
    }

    public PriorityQueue<T> getCoadaAvioane() {
        return coadaAvioane;
    }

    public void setCoadaAvioane(PriorityQueue<T> coadaAvioane) {
        this.coadaAvioane = coadaAvioane;
    }

    public void adaugaAvion(T avion) {
        if ("Bucharest".equals(avion.getDestinatie()) && "takeoff".equals(this.getUtilizare())) {
            // daca doreste aterizarea dar pista este una de decolare = > arunc exceptie.
//            throw IncorrectRunwayException();

        }
        coadaAvioane.offer(avion);
    }
    public String toString() {
        StringBuilder sir = new StringBuilder();
        sir.append(this.id);
        sir.append("\n");
        Iterator<T> valIterator = coadaAvioane.iterator();
        while (valIterator.hasNext()) {
            sir.append(valIterator.next().toString());
            sir.append("\n");
        }
        return sir.toString();
    }
    public static void main(String[] args) {
        // pentru testarea de inceput.
        // !! SA NU uit sa o sterg.
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalTime timp1 = LocalTime.parse("12:01:29", formatter);
        LocalTime timp2 = LocalTime.parse("10:10:25", formatter);
        LocalTime timp3 = LocalTime.parse("09:01:29", formatter);
        WideBodyAirplane avion1 = new WideBodyAirplane("militart", "id1", "Germania", "Bucharest", timp1, "urgent");
        WideBodyAirplane avion2 = new WideBodyAirplane("comercial", "id2", "Bucharest", "Germania", timp2, "urgent");
        WideBodyAirplane avion3 = new WideBodyAirplane("fain", "id3", "Australia", "Bucharest", timp3, null);

//        Runway<WideBodyAirplane> runway1 = new Runway<WideBodyAirplane>(timp1, "pista1", "landing", "wideBody");
//        Runway<NarrowBodyAirplane> runway2 = new Runway<NarrowBodyAirplane>(timp1, "pista1", "landing", "wideBody");
//        runway1.adaugaAvion(avion1);
//        runway1.adaugaAvion(avion2);
//        runway1.adaugaAvion(avion3);
//        System.out.println(runway1.toString());
    }
}
