import java.time.LocalTime;
import java.util.*;

enum StatusRunway{
    FREE,
    OCCUPIED
}
public class Runway<T extends Airplane> {
    private LocalTime timestamp;
    private String id;
    private String utilizare;
    private PriorityQueue<T> coadaAvioane;
    private StatusRunway statusPista;
    // timpul PANA LA CARE este pista ocupata. !!
    private LocalTime timeOccupied;
    public Runway() {

    }
    public Runway(LocalTime timestamp, String id, String utilizare, String tipAvionAcceptat, Comparator<T> comparator) {
        this.timestamp = timestamp;
        this.id = id;
        this.utilizare = utilizare;
        this.coadaAvioane = new PriorityQueue<>(comparator);
        this.statusPista = StatusRunway.FREE;
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

    public StatusRunway getStatusPista() {
        return statusPista;
    }

    public void setStatusPista(StatusRunway statusPista) {
        this.statusPista = statusPista;
    }

    public LocalTime getTimeOccupied() {
        return timeOccupied;
    }

    public void setTimeOccupied(LocalTime timeOccupied) {
        this.timeOccupied = timeOccupied;
    }

    public void adaugaAvion(T avion, LocalTime timestamp) throws IncorrectRunwayException {
        if ("Bucharest".equals(avion.getDestinatie()) && "takeoff".equals(this.getUtilizare())
            || !("Bucharest".equals(avion.getDestinatie())) && "landing".equals(this.getUtilizare())) {
            // daca doreste aterizarea dar pista este una de decolare = > arunc exceptie.
            throw new IncorrectRunwayException(timestamp.toString() + " | The chosen runway for allocating the plane is incorrect");
        } else {
            coadaAvioane.offer(avion);
        }
    }
    public String toString() {
        StringBuilder sir = new StringBuilder();
        sir.append(this.id);
        sir.append("\n");
        PriorityQueue<T> auxQueue = new PriorityQueue<>(coadaAvioane);
        while (!auxQueue.isEmpty()) {
            sir.append(auxQueue.poll().toString());
            sir.append("\n");
        }
        return sir.toString();
    }

    /**
     * Metoda de cautare si returnare a unui avion din colectia de zboruri a pistei, care
     * are un anumit id dorit si transmis ca parametru.
     * @param idZbor id-ului unui zbor.
     * @return
     */
    public T cautaAirplane(String idZbor) {
        Iterator<T> valIterator = coadaAvioane.iterator();
        while (valIterator.hasNext()) {
            T avionFound = valIterator.next();
            if (idZbor.equals(avionFound.getId())) {
                return avionFound;
            }
        }
        return null;
    }
    public T extrageAvion(LocalTime timestamp) throws UnavailableRunwayException {
        // nu vreau sa il sterg, doar sa il extrag.
        LocalTime timestampAux = timestamp.plusMinutes(1);
        if (this.getStatusPista().equals(StatusRunway.OCCUPIED) && this.getTimeOccupied().isBefore(timestampAux)) {
            this.setStatusPista(StatusRunway.FREE);
        }
        if (this.getStatusPista().equals(StatusRunway.FREE)) {
            PriorityQueue<T> auxQueue = new PriorityQueue<>(coadaAvioane);
            T avionExtras = null;
            boolean gasit = false;
            while(!auxQueue.isEmpty()) {
                T airplane = auxQueue.poll();
                System.out.println("testare-extragere-avion:" + airplane);
                if (!airplane.getStatus().equals(Status.LANDED) && !airplane.getStatus().equals(Status.DEPARTED)) {
                    avionExtras = airplane;
                    gasit = true;
                    break;
                }
            }
            if (gasit) {
                if (avionExtras != null && avionExtras.getStatus().equals(Status.WAITING_FOR_LANDING)) {
                    avionExtras.setStatus(Status.LANDED);
                } else if (avionExtras != null && avionExtras.getStatus().equals(Status.WAITING_FOR_TAKEOFF)) {
                    avionExtras.setStatus(Status.DEPARTED);
                }

                this.setStatusPista(StatusRunway.OCCUPIED);
                // actaulizarea statusului si a timpului pana la care va fi ocupata pista.
                if (this.getUtilizare().equals("landing")) {
                    LocalTime timestampDelay1 = timestamp.plusMinutes(10);
                    this.setTimeOccupied(timestampDelay1);
                } else if (this.getUtilizare().equals("takeoff")) {
                    LocalTime timestampDelay2 = timestamp.plusMinutes(5);
                    this.setTimeOccupied(timestampDelay2);
                }
                return avionExtras;
            }
        } else {
            // daca practic au trecut cele 5/10 minute in care pista a fost ocupata
            // ii vom actualiza statusul.
            throw new UnavailableRunwayException(timestamp);
        }
        return null;
    }
}
