import java.io.PrintWriter;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class GestiunePiste {
    private List<Runway<WideBodyAirplane>> wideRunwayList = new ArrayList<>();
    private List<Runway<NarrowBodyAirplane>> narrowRunwayList = new ArrayList<>();
    public void addToWideList(Runway<WideBodyAirplane> runway1) {
        wideRunwayList.add(runway1);
    }
    public void addToNarrowList(Runway<NarrowBodyAirplane> runway2) {
        narrowRunwayList.add(runway2);
    }
    public Runway<WideBodyAirplane> findWideRunway(String idPista) {
        for (Runway<WideBodyAirplane> runway1 : wideRunwayList) {
            if ((runway1.getId()).equals(idPista)) {
                return runway1;
            }
        }
        return null;
    }
    // cautare a unei piste dupa id-ul pistei
    public Runway<NarrowBodyAirplane> findNarrowRunway(String idPista) {
        for (Runway<NarrowBodyAirplane> runway1 : narrowRunwayList) {
            if ((runway1.getId()).equals(idPista)) {
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
        for (Runway<NarrowBodyAirplane> runway : this.narrowRunwayList) {
            System.out.println(runway.toString());
        }
    }
    // cautare pista dupa id-ul unui avion.
    // si vreau sa imi returneze descrierea acelui zbor.
    public String afiseazaZbor(String idZbor) {
        String rezultat = "";
        WideBodyAirplane airplane;
        boolean wideAirplaneGasit = false;
        for (Runway<WideBodyAirplane> runway : wideRunwayList) {
            airplane = runway.cautaAirplane(idZbor);
            // daca avionul nu este in lista de piste cu avioane de tip widw
            // caut in cea cu avioane de tip narrow.
            if (airplane != null) {
                wideAirplaneGasit = true;
                rezultat = rezultat.concat(airplane.toString());
                break;
            }
        }
        if (!wideAirplaneGasit) {
            NarrowBodyAirplane narrowAirplane;
            for (Runway<NarrowBodyAirplane> runway : narrowRunwayList) {
                narrowAirplane = runway.cautaAirplane(idZbor);
                if (narrowAirplane != null) {
                    rezultat = rezultat.concat(narrowAirplane.toString());
                    break;
                }
            }
        }
        return rezultat;
    }

    public void afisareRunwayInfo(String idPista, LocalTime timestamp, PrintWriter pw) {
        helperAfisareRunwayInfo(findNarrowRunway(idPista), timestamp, pw);
        helperAfisareRunwayInfo(findWideRunway(idPista), timestamp, pw);
    }
    // cumva trebuie acum sa mai fac logica in functie de timestamp, cumva sa gestionez
    // decolarea si aterizarea propriu zisa, sa modific statusul avioanelor + timpul concret daca e cazul.
    public <T extends Airplane> void helperAfisareRunwayInfo(Runway<T> runway, LocalTime timestamp, PrintWriter pw) {
        if (runway != null) {
            // actualizez si aici statusul pistei, pentru ca mi ar ramane pe ocupat,
            // de la meneuvers !! eu in cadrul metodei de extragere avion din Runway,
            // il resetez pe FREE, facand acea comparatie cu timestampul la care se vrea sa se faca urmatoarea
            // extragere, dar aici la afisari, trebuie resetatat din nou!!
            LocalTime timestampAux = timestamp.plusMinutes(1);
            if (runway.getStatusPista().equals(StatusRunway.OCCUPIED) && runway.getTimeOccupied().isBefore(timestampAux)) {
                runway.setStatusPista(StatusRunway.FREE);
            }
            pw.println(runway.getId() + " - " + runway.getStatusPista());
            System.out.println(runway.getId() + " - " + runway.getStatusPista());
            PriorityQueue<T> auxQueue = new PriorityQueue<>(runway.getCoadaAvioane());
            while (!auxQueue.isEmpty()) {
                T airplane = auxQueue.poll();
                if (!airplane.getStatus().equals(Status.DEPARTED) && !airplane.getStatus().equals(Status.LANDED)) {
                    pw.println(airplane);
                }
            }
        }
    }
}
