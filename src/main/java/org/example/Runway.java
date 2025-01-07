package org.example;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.PriorityQueue;

// statusul Pistei.
enum StatusRunway{
    FREE,
    OCCUPIED
}

/**
 * Clasa care reprezinta pista ce contine o coada de avioane, in cadrul pistei avand voie
 * sa lucrez doar cu parametrul T.
 * @param <T> Este un tip de date care extinde clasa {@link Airplane} (adica poate fi in cazul nostru:
 *           {@link WideBodyAirplane} sau {@link NarrowBodyAirplane})
 */
public class Runway<T extends Airplane> {
    private String id;
    private String utilizare;
    private PriorityQueue<T> coadaAvioane;
    private StatusRunway statusPista;
    // timpul PANA LA CARE este pista ocupata.
    private LocalTime timpPistaOcupata;

    /**
     * Constructorul pistei de avioane
     * @param id id-ul asociat pistei
     * @param utilizare pentru ce este aceasta utilizata: landing / takeoff.
     * @param tipAvionAcceptat tipul de avion pe care il accepta.
     * @param comparator Un comparator, de tip Comparator<T>, eu ulterior facand doua clase
     *                   {@link RunwayComparatorLanding} cu criteriile de comparare pe baza careia voi mentine
     *                   elementele in coada de avioane pentru o pista de aterizare, respectiv
     *                   {@link RunwayComparatorTakeoff} cu criterii pt pistele de decolare.
     * Asignez cu FREE statusul pistei, pentru inceput.
     */
    public Runway(String id, String utilizare, String tipAvionAcceptat, Comparator<T> comparator) {
        this.id = id;
        this.utilizare = utilizare;
        this.coadaAvioane = new PriorityQueue<>(comparator);
        this.statusPista = StatusRunway.FREE;
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

    public LocalTime getTimpPistaOcupata() {
        return timpPistaOcupata;
    }

    public void setTimpPistaOcupata(LocalTime timpPistaOcupata) {
        this.timpPistaOcupata = timpPistaOcupata;
    }

    /**
     * Metoda care adauga un avion in coada de avioane a pistei, verificand conditiile de apartenenta
     * a avionului la tipul de utilizare a pistei. Daca nu se verifica => se arunca exceptie.
     * @param avion Avionul de tip Airplane pe care doresc sa il adaug il coada pistei.
     * @param timestamp Timestampul la care a fost executata comanda, pentru a o afisa in caz ca mi se
     *                  arunca exceptie (daca doreste aterizarea dar pista este una de decolare)
     * @throws IncorrectRunwayException exceptie in caz ca utilizarea pistei (decolare/aterizare) nu coincide
     *                                  cu utilizarea practica a avionului (daca acesta aterizeaza/decoleaza).
     */
    public void adaugaAvion(T avion, LocalTime timestamp) throws IncorrectRunwayException {
        if ("Bucharest".equals(avion.getDestinatie()) && "takeoff".equals(this.getUtilizare())
            || !("Bucharest".equals(avion.getDestinatie())) && "landing".equals(this.getUtilizare())) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            String timestampFormatat = timestamp.format(formatter);
            throw new IncorrectRunwayException(timestampFormatat + " | The chosen runway for allocating the plane is incorrect");
        } else {
            coadaAvioane.offer(avion);
        }
    }

    // pt BONUS.
    /**
     * Metoda care modifica timestampul unui avion cu un anume id, din cadrul cozii de avioane
     * din cadrul pistei
     * @param idAvion id-ul avionului caruia ii fac actualizarea timestampului.
     * @param newTimestamp noul timestamp dorit.
     */
    public void modificaTimestampAvion(String idAvion, LocalTime newTimestamp){
        PriorityQueue<T> auxQueue = new PriorityQueue<>(coadaAvioane);
        while(!auxQueue.isEmpty()) {
            // potentialul avion dorit.
            T avion = auxQueue.poll();
            if (avion.getId().equals(idAvion)) {
                // il elimin
                coadaAvioane.remove(avion);
                // actualizez statusul avionului
                avion.setTimpDorit(newTimestamp);
                // il introduc la loc in coada pistei Runway.
                coadaAvioane.offer(avion);
            }
        }
    }

    /**
     * Metoda are rolul de a modifica corespunzator statusul avionului care merge sa fie manevrat
     * in functie de criteriile urmatoare:
     * 1) Daca statusul pistei este pe moment OCUPPIED, insa timpul pana la care este ocupata a expirat,
     * adica timpul "timpPistaOcupata" este inaintea timestampul la care s-a executat comanda permission_for_maneuver
     * voi asigna statusul FREE pistei.
     * 2) Daca pista e FREE:
     *      Extrag din coada de avioane (folosind o coada auxiliara, din care tot scot elementele) primul avion
     *      care ARE STATUSUL WAITING_FOR_LANDING SAU WAITING_FOR_TAKEOFF. Daca il exista, atunci ii modific
     *      statusul ( LANDED/DEPARTED) si setez si timpulConcret cu timpul la care s-a executat manevra.
     *      De asemenea, actualizez si timpul pana la care pista va fi ocupata ( 5/10 minute in functie de
     *      ce tip de pista e.)
     * 3)   In caz ca pista are statusul OCCUPIED arunc exceptia {@link UnavailableRunwayException}, care va contine ca
     * mesaj timestampul la care s-a incercat executarea manevrei cat si mesajul de eroare ca pista este
     * ocupata.
     * @param timestamp Ora la care se executa comanda de permission_for_maneuver
     * @throws UnavailableRunwayException daca pista este ocupata in acel moment.
     */
    public void extrageAvion(LocalTime timestamp) throws UnavailableRunwayException {
        // nu vreau sa il sterg, doar sa il "extrag". (sa ii schimb statusul avionului, statusul pistei
        // si timpul cat aceasta va fi sau nu ocupata pentru alte manevre.
        if (this.getStatusPista().equals(StatusRunway.OCCUPIED) && this.getTimpPistaOcupata().isBefore(timestamp)) {
            this.setStatusPista(StatusRunway.FREE);
        }
        if (this.getStatusPista().equals(StatusRunway.FREE)) {
            // creez o coada auxiliara din care tot voi extrage elementele in ordinea prioritatii
            // aferente cozii "coadaAvioane".
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
                if (avionExtras.getStatus().equals(Status.WAITING_FOR_LANDING)) {
                    avionExtras.setStatus(Status.LANDED);
                    avionExtras.setTimpConcret(timestamp);
                } else if (avionExtras.getStatus().equals(Status.WAITING_FOR_TAKEOFF)) {
                    avionExtras.setStatus(Status.DEPARTED);
                    avionExtras.setTimpConcret(timestamp);
                }

                this.setStatusPista(StatusRunway.OCCUPIED);
                // actualizarea statusului si a timpului pana la care va fi ocupata pista.
                if (this.getUtilizare().equals("landing")) {
                    LocalTime timestampDelay1 = timestamp.plusMinutes(10);
                    this.setTimpPistaOcupata(timestampDelay1);
                    System.out.println("timpul pana la care e ocupata pista de aterizare este: " + timestampDelay1);
                } else if (this.getUtilizare().equals("takeoff")) {
                    LocalTime timestampDelay2 = timestamp.plusMinutes(5);
                    this.setTimpPistaOcupata(timestampDelay2);
                    System.out.println("timpul pana la care e ocupata pista de decolare este: " + timestampDelay2);
                }
            }
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            String timestampFormatatCuSecunde =  timestamp.format(formatter);
            throw new UnavailableRunwayException(timestampFormatatCuSecunde  + " | " + "The chosen runway for maneuver is currently occupied");
        }
    }

    /**
     * PT. BONUS:
     * Metoda care imi sterge un avion cu id-ul primit ca parametru.
     * Creez coada auxiliara cu elementele din coada {@link Runway#coadaAvioane} si extrag unul cate unul
     * pana cand ajung la avionul cu id-ul dorit. Atunci il voi elimina din coada de avioane
     * @param idAvion id-ul avionului de sters.
     */
    public void stergeAvion(String idAvion) {
        PriorityQueue<T> auxQueue = new PriorityQueue<>(coadaAvioane);
        while (!auxQueue.isEmpty()) {
            T avion = auxQueue.poll();
            if (avion.getId().equals(idAvion)) {
                this.coadaAvioane.remove(avion);
            }
        }
    }

    /**
     * Metoda toString in care afisez:
     * @return id-ul pistei (pe linie separata), si apoi pe fiecare linie, avioanele din coada
     * coada fiind deja sortata, intrucat e de prioritate, cu comparatorul specificat la creare.
     */
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
}

