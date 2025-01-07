package org.example;
import java.time.format.DateTimeFormatter;
import java.time.LocalTime;

/**
 * Statusurile pe care le poate lua un avion.
 */
enum Status {
    WAITING_FOR_TAKEOFF,
    DEPARTED,
    WAITING_FOR_LANDING,
    LANDED
}
public abstract class Airplane {
    private String tip; // poate fi "narrow" sau "wide", initializat in clasele mai specifice.;
    private String model;
    private String urgent;
    private String id;
    private String locatiePlecare;
    private String destinatie;
    // orele de tip LocalTime, pe care le-am formatat mai apoi, cu ajutorul DateTimeFormatter,
    // respectiv le-am parsat din clasele Main si Runway cu ajutorul metodei
    // LocalTime.parse(String..., DateTimeFormatter ...)
    private LocalTime timpDorit;
    // timpul concret se va asigna in clasa Runway, atunci cand fac extragerea avionului
    // din coada de avioane a unei piste. Momentan el nu este asignat in constructorul lui Airplane.
    private LocalTime timpConcret;
    private Status status;

    /**
     * Contructorul pentru un avion
     * @param tip tip-ul avionului wide sau narrow.
     * @param model modelul avionului pe care il atribui campului aferent.
     * @param id id-ul asociat avioanului.
     * @param locatiePlecare locatia de placare, de tip String. Daca este "Bucharest" => este avion
     *                       ce doreste sa decoleze. Statusul il asignez corespunzator.
     * @param destinatie    destinatia zborului. Daca este "Bucharest" => este avion ce doreste sa
     *                      aterizeze. Statusul il asignez corespunzator.
     * @param timpDorit timpul la care se anticipeaza
     * @param urgent camp care poate fi null => atributul urgent al claei avion ramane tot pe null => nu are
     *               nicio urgenta.
     *               camp care poate fi stringul "urgent" => inseamna ca avionul are o urgenta si va fi tratat
     *               ca atare ulterior.
     */
    public Airplane(String tip, String model, String id, String locatiePlecare, String destinatie, LocalTime timpDorit, String urgent) {
        if (urgent != null) {
            this.urgent = urgent;
        }
        this.tip = tip;
        this.model = model;
        this.id = id;
        this.locatiePlecare = locatiePlecare;
        this.destinatie = destinatie;
        this.timpDorit = timpDorit;
        if (locatiePlecare.equals("Bucharest")) {
            status = Status.WAITING_FOR_TAKEOFF;
        } else if (destinatie.equals("Bucharest")) {
            status = Status.WAITING_FOR_LANDING;
        }
    }
    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
    public String getTip() {
        return tip;
    }
    public void setTip(String tip) {
        this.tip = tip;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocatiePlecare() {
        return locatiePlecare;
    }

    public void setLocatiePlecare(String locatiePlecare) {
        this.locatiePlecare = locatiePlecare;
    }

    public String getDestinatie() {
        return destinatie;
    }

    public void setDestinatie(String destinatie) {
        this.destinatie = destinatie;
    }

    public LocalTime getTimpDorit() {
        return timpDorit;
    }

    public void setTimpDorit(LocalTime timpDorit) {
        this.timpDorit = timpDorit;
    }

    public LocalTime getTimpConcret() {
        return timpConcret;
    }

    public void setTimpConcret(LocalTime timpConcret) {
        this.timpConcret = timpConcret;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
    public String getUrgent() {
        return urgent;
    }

    public void setUrgent(String urgent) {
        this.urgent = urgent;
    }

    /**
     * Metoda toString, care returneaza reprezentarea avionului, in doua cazuri:
     * @return Caz 1) daca timpul concret este null => avionul aterizeaza/decoleaza la ora dorita
     * data initial in fisierul de input din Main. Atunci voi afisa toate atributele clasei acestea, in afara
     * de atributul timpConcret.
     *         Caz 2) daca timpulConret este asignat => voi adauga la retur si valoarea acestuia
     *
     * In ambele cazuri, am formatat data pentru a o afisa cu delimitatorul ":" intre ora:minut:secunde
     * Si de asemenea, daca nu o formatam asa, nu imi afisa partea de secunde mereu ( ex: daca ora era:
     * 13:45:00 => mi se afisa 13:45)
     */
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String timpDoritFormatatCuSecunde = timpDorit.format(formatter);
        if (timpConcret == null) {
            return this.model + " - " + this.id + " - " + this.locatiePlecare + " - " + this.destinatie + " - " + this.status + " - " + timpDoritFormatatCuSecunde;
        }
        String timpConcretFormatatCuSecunde = timpConcret.format(formatter);
        return this.model + " - " + this.id + " - " + this.locatiePlecare + " - " + this.destinatie + " - " + this.status + " - " + timpDoritFormatatCuSecunde + " - " + timpConcretFormatatCuSecunde;
    }
}

