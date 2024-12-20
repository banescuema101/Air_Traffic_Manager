import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

enum Status {
    WAITING_FOR_TAKEOFF,
    DEPARTED,
    WAITING_FOR_LANDING,
    LANDED
}
public abstract class Airplane {
    private String model;
    private String urgent;  // Nu stiu daca am pus -o bine aici.
    private String id;
    private String locatiePlecare;
    private String destinatie;
    private LocalTime timpDorit;
    private LocalTime timpConcret;
    private Status status;
    public Airplane(String model, String id, String locatiePlecare, String destinatie, LocalTime timpDorit, String urgent) {
        if (urgent != null) {
            this.urgent = urgent;
        }
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

    public String toString() {
        return this.model + " - " + this.id + " - " + this.locatiePlecare + " - " + this.destinatie + " - " + this.status + " - " + this.timpDorit;
    }
}
