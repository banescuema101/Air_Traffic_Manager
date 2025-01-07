package org.example;
import java.io.PrintWriter;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class GestiunePiste {
    // Cele doua hashMap-uri, facand asocierea:  idPista - Pista in sine
    private Map<String, Runway<WideBodyAirplane>> wideRunwayMap;
    private Map<String, Runway<NarrowBodyAirplane>> narrowRunwayMap;

    // pentru a retine o lista de zboruri, pentru a imi fi mai usor si optim sa caut
    // un zbor dupa ID-ul lui, pentru comanda flight_info.
    private Map<String, Airplane> zboruriMap;

    /**
     * Constructorul clasei in care execut cateva operatiuni asupra pistelor/zborurilor
     * Aici initializez si hashMap-urile.
     */
    public GestiunePiste() {
        zboruriMap = new HashMap<>();
        wideRunwayMap = new HashMap<>();
        narrowRunwayMap = new HashMap<>();
    }
    // adaug un zbor ( adica un obiect de tipul Avion) la dictionarul de zboruri.
    public void adaugaZbor(Airplane avion) {
        zboruriMap.put(avion.getId(), avion);
    }
    // metoda cu care extrag si returnez avionul cu id-ul id.
    public Airplane gasireZborDupaId(String id) {
        return zboruriMap.get(id);
    }

    // PT BONUS.
    /**
     * Metoda care ma ajuta sa elimin un avion din coada de avioane a unei anume piste.
     * @param idAvion id-ului avionului pe care vreau sa il elimin.
     * @param idPista id-ului pistei din cadrul careia elimin un avion.
     */
    public void eliminareZbor(String idAvion, String idPista) {
        Airplane avion = zboruriMap.get(idAvion);
        if (avion != null) {
            Runway<WideBodyAirplane> runwayWide = wideRunwayMap.get(idPista);
            // am gasit pista.
            if (runwayWide == null) {
                Runway<NarrowBodyAirplane> runwayNarrow = narrowRunwayMap.get(idPista);
                runwayNarrow.stergeAvion(idAvion);
            } else {
                runwayWide.stergeAvion(idAvion);
            }
        }
        zboruriMap.remove(idAvion);
    }
    //pt BONUS

    /**
     * Metoda prin care mut un zbor dintr-o anume pista cu un anume id in alta pista cu alt id.
     * Totul ramane la fel la acel zbor, doar ca va apartine altei piste. Ma asigur ca tipul si utilizarea
     * avionului coincid cu tipul si utilizarea pistei, astfel, afisez mesajul exceptiei pe care o arunca
     * metoda adaugaAvion.
     * @param idAvion id-ului avionului pe care vreau sa il mut.
     * @param idPista id-ul pistei pe care sta acum avionul cu idAvion.
     * @param idPistaNoua id-ul pistei pe care vreau sa pun avionul cu idAvion.
     * @param pw //optional// un abiect de tip PrintWriter in care sa afisez, daca este cazul, exceptia
     *           generata la adaugarea avionului in noua pista.
     */
    public void mutareZbor(String idAvion, String idPista, String idPistaNoua, LocalTime timestamp, PrintWriter pw){
        Airplane avion = zboruriMap.get(idAvion);
        eliminareZbor(idAvion, idPista);
        Runway<WideBodyAirplane> runwayWide = wideRunwayMap.get(idPistaNoua);
        if (runwayWide != null) {
            try {
                // pentru a ma asigura ca este de tip wide. Posibila discrepanta intre avion ce decoleaza
                // pista ce aterizeaza sau invers s-ar finaliza cu prinderea erorii, si printarea mesajului
                // de eroare.
                if (avion.getTip().equals("wide")) {
                    runwayWide.adaugaAvion((WideBodyAirplane) avion, timestamp);
                }
            } catch (IncorrectRunwayException e) {
                pw.println(e.getMessage());
            }
        } else {
            try {
                Runway<NarrowBodyAirplane> runwayNarrow = narrowRunwayMap.get(idPista);
                if (avion.getTip().equals("narrow")) {
                    runwayNarrow.adaugaAvion((NarrowBodyAirplane) avion, timestamp);
                }
            } catch (IncorrectRunwayException e) {
                pw.println(e.getMessage());
            }
        }
        adaugaZbor(avion);
    }

    // pt BONUS.

    /**
     * Metoda cu ajutorul caruia aman un avion. Poate ca operatorul a fost instiintat ca avionul cu idAvion
     * are o intarziere. Atfel, timpul la care se estimeaza ca va ateriza/ decola acel avion pe / de pe
     * pista aferenta va trebui modificat.
     * @param idAvion id-ulu avionului la care schimb timpul dorit.
     * @param idPista id-ul pistei pe care se afla avionul.
     * @param newTimestamp noul timestamp la care voi seta avionul intarziat.
     */
    public void amanareZbor(String idAvion, String idPista, LocalTime newTimestamp){
        Airplane avion = zboruriMap.get(idAvion);
        if (avion != null && (avion.getStatus().equals(Status.WAITING_FOR_LANDING) || avion.getStatus().equals(Status.WAITING_FOR_TAKEOFF))) {
            Runway<WideBodyAirplane> runwayWide = wideRunwayMap.get(idPista);
            if (runwayWide != null) {
                runwayWide.modificaTimestampAvion(idAvion, newTimestamp);
                zboruriMap.get(idAvion).setTimpDorit(newTimestamp);
                // dar trebuie sa modific si din id-zboruri.
            } else {
                Runway<NarrowBodyAirplane> narrowRunway = narrowRunwayMap.get(idPista);
                narrowRunway.modificaTimestampAvion(idAvion, newTimestamp);
            }
        }
    }

    /**
     * Metoda cu care adaug in dictionarul wideRunwayMap o anume pista ca valoare pt cheia aferenta -> id-ului pistei.
     * @param runway1 o anume pista - ce contine doar avioane de tip WideBodyAirplane.
     */
    public void addToWideList(Runway<WideBodyAirplane> runway1) {
        wideRunwayMap.put(runway1.getId(), runway1);
    }

    /**
     * Metoda in care adaug in dictionarul narrowRunwayMap: pe pozitia idPista -> pista.
     * @param runway2 o anume pista - ce contine doar avioane de tip NarrowBodyAirplane
     */
    public void addToNarrowList(Runway<NarrowBodyAirplane> runway2) {
        narrowRunwayMap.put(runway2.getId(), runway2);
    }

    /**
     * Metoda de cautare a unei piste cu avioane de tip WideBody dupa id-ul pistei
     * @param idPista id-ul pistei pe care doresc sa o gasesc.
     * @return valoarea cu cheia idPista.
     */
    public Runway<WideBodyAirplane> findWideRunway(String idPista) {
        return wideRunwayMap.get(idPista);
    }

    /**
     * Metoda de cautare a unei piste cu avioane de tip NarrowBody dupa id-ul pistei
     * @param idPista id-ul pistei dorite.
     * @return valoarea cu cheia idPista.
     */
    public Runway<NarrowBodyAirplane> findNarrowRunway(String idPista) {
        return narrowRunwayMap.get(idPista);
    }

    /**
     * Metoda cu care afisez fiecare o pista cu un anume id(fara sa stiu in acesta metoda excat cu ce
     * avioane este acea pista (wide sau narrow).
     * Ma ajut de metoda helperAfisareRunwayInfo.
     * @param idPista id-ul pistei de afisat.
     * @param timestamp timestampul la care se executa comanda runway_info.
     * @param pw o instanta de tip PrintWriter (careia ii atribui un filedescriptor in clasa Main)
     *          unde voi afisa outputul.
     */
    public void afisareRunwayInfo(String idPista, LocalTime timestamp, PrintWriter pw) {
        helperAfisareRunwayInfo(findNarrowRunway(idPista), timestamp, pw);
        helperAfisareRunwayInfo(findWideRunway(idPista), timestamp, pw);
    }

    /**
     * Metoda care afiseaza id-ul pistei, cat si toate avioanele din coada de avioane a pistei.
     * @param runway pista dorita. Practic aceasta imi va lua valoarea returnata de functia findNarrowRunway
     *               si astfel va fi o pista cu avioane de tip NarrowBody, daca functia returneaza != null, altfel,
     *               voi apela aceasta metoda cu parametrul acesta runway luat din findWideRunway.
     * @param timestamp timestampul la care se exeucta comanda runway_info.
     * @param pw PrintWriterul.
     * @param <T> Plec de la premiza ca pista in aceasta functie poate lua tipul Runway<WideBodyAirplane> sau
     *      * Runway<NarrowBodyAirplane>.
     */
    public <T extends Airplane> void helperAfisareRunwayInfo(Runway<T> runway, LocalTime timestamp, PrintWriter pw) {
        if (runway != null) {
            // actualizez si aici statusul pistei, pentru ca mi ar ramane pe ocupat,
            // de la maneuvers !! eu in cadrul metodei de extragere avion din org.example.Runway,
            // il resetez pe FREE, facand acea comparatie cu timestampul la care se vrea sa se faca urmatoarea
            // extragere, dar aici la afisari, trebuie resetatat din nou!
            if (runway.getStatusPista().equals(StatusRunway.OCCUPIED) && runway.getTimpPistaOcupata().isBefore(timestamp)) {
                runway.setStatusPista(StatusRunway.FREE);
            }
            pw.println(runway.getId() + " - " + runway.getStatusPista());
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
