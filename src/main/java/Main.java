import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length > 0) {
            String fisierDorit = "..\\resources\\" + args[0] + "\\input.in";
                try {
                    FileReader fr = new FileReader(fisierDorit);
                    BufferedReader br = new BufferedReader(fr);
                    String linie;
                    // un formatter de tip ora:minut:secunde
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                    GestiunePiste gestiunePiste = new GestiunePiste();
                    while ((linie = br.readLine()) != null) {
                        String[] textLinie = linie.split(" - ");
                        String comanda = textLinie[1];
                        String sirCuTimpul = textLinie[0];
                        LocalTime timestamp = LocalTime.parse(sirCuTimpul, formatter);
                        switch (comanda) {
                            case "add_runway_in_use":
                                System.out.println("testul 1:");
                                String id = textLinie[2];
                                String utilizare = textLinie[3];
                                String tipAvionAcceptat = textLinie[4];
                                if (tipAvionAcceptat.equals("wide body")) {
                                    if (utilizare.equals("landing")) {
                                        Runway<WideBodyAirplane> runway = new Runway<>(timestamp, id, utilizare, tipAvionAcceptat, new RunwayComparatorLanding<WideBodyAirplane>());
                                        gestiunePiste.addToWideList(runway);
                                    } else if(utilizare.equals("takeoff")) {
                                        Runway<WideBodyAirplane> runway = new Runway<>(timestamp, id, utilizare, tipAvionAcceptat, new RunwayComparatorTakeoff<>());
                                        gestiunePiste.addToWideList(runway);
                                    }
                                } else if (tipAvionAcceptat.equals("narrow body")) {
                                    if (utilizare.equals("landing")) {
                                        Runway<NarrowBodyAirplane> runway = new Runway<>(timestamp, id, utilizare, tipAvionAcceptat, new RunwayComparatorLanding<>());
                                        gestiunePiste.addToNarrowList(runway);
                                    } else if(utilizare.equals("takeoff")) {
                                        Runway<NarrowBodyAirplane> runway = new Runway<>(timestamp, id, utilizare, tipAvionAcceptat, new RunwayComparatorTakeoff<>());
                                        gestiunePiste.addToNarrowList(runway);
                                    }
                                }
                                // daca nu puneam formatorul de tip DateTimeFormater, nu mi se afisau secundele
                                // de fiecare data ( de ex: la 00:00:00, aveam doar 00:00
                                // System.out.println(timestamp.format(formatter));
                                gestiunePiste.listareNarrowRunway();
                                gestiunePiste.listareWideRunway();
                                break;
                            case "allocate_plane":
                                    String tipAvion = textLinie[2];
                                    String model = textLinie[3];
                                    String idZbor = textLinie[4];
                                    String plecare = textLinie[5];
                                    String destinatie = textLinie[6];
                                    LocalTime timpDorit = LocalTime.parse(textLinie[7], formatter);
                                    String idPista = textLinie[8];
                                    // urgent poate fi null sau poate exista Stringul "urgent:
                                    // acest lucru este gestionat de constructorul clasei abstracta Airplane.
                                    String urgent = null;
                                    if (textLinie.length == 10) {
                                        urgent = textLinie[9];
                                    }
                                    if (tipAvion.equals("wide body")) {
                                        Runway<WideBodyAirplane> runwayFoundWide = gestiunePiste.findWideRunway(idPista);
                                        runwayFoundWide.adaugaAvion(new WideBodyAirplane(model, idZbor, plecare, destinatie, timpDorit, urgent));
                                    }
                                    else {
                                        Runway<NarrowBodyAirplane> runwayFoundNarrow = gestiunePiste.findNarrowRunway(idPista);
                                        runwayFoundNarrow.adaugaAvion(new NarrowBodyAirplane(model, idZbor, plecare, destinatie, timpDorit, urgent));
                                    }
                                    gestiunePiste.listareWideRunway();
                                    gestiunePiste.listareNarrowRunway();
                                break;

                            default:
                                System.out.println("to be continued...");
                        }
                    }
            } catch (FileNotFoundException e) {
                throw new FileNotFoundException(e.getMessage());
            }
        } else {
            System.out.println("Tema2");
        }
    }
}