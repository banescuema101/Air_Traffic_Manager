import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length > 0) {
            String antetResources = "../resources/";
            String fisierIntrare = antetResources + args[0] + "/" + "input.in";
            String fisierExceptii = antetResources + args[0] + "/"+ "board_exceptions.out";
            String fisierOut = antetResources + args[0] + "/" + "flights_info.out";
            String fisierBoardExceptions = antetResources + args[0] + "/"+ "board_exceptions.out";
            try {
                // fisierul in care voi scrie exceptiile de tip IncorrectRunway line by line.
                FileWriter fw = new FileWriter(fisierExceptii, true);
                PrintWriter pw = new PrintWriter(fw);
                // fisierul in care voi scrie exceptiile de tipul unavailable runway exceptions.
                FileWriter fwBoardExceptions = new FileWriter(fisierBoardExceptions, true);
                PrintWriter pwBoardExceptions = new PrintWriter(fwBoardExceptions);
                // file ul de intrare, din care citesc informatiile
                FileReader fr = new FileReader(fisierIntrare);
                BufferedReader br = new BufferedReader(fr);
                // fisierul de flights_info.out
                FileWriter fwOut = new FileWriter(fisierOut, true);
                PrintWriter pwOut = new PrintWriter(fwOut);
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
                            try {
                                if (tipAvion.equals("wide body")) {
                                    Runway<WideBodyAirplane> runwayFoundWide = gestiunePiste.findWideRunway(idPista);
                                    runwayFoundWide.adaugaAvion(new WideBodyAirplane(model, idZbor, plecare, destinatie, timpDorit, urgent), timestamp);
                                } else {
                                    Runway<NarrowBodyAirplane> runwayFoundNarrow = gestiunePiste.findNarrowRunway(idPista);
                                    runwayFoundNarrow.adaugaAvion(new NarrowBodyAirplane(model, idZbor, plecare, destinatie, timpDorit, urgent), timestamp);
                                }
                            } catch (IncorrectRunwayException e) {
                                pw.println(e.getMessage());
                            }
                            gestiunePiste.listareWideRunway();
                            gestiunePiste.listareNarrowRunway();
                            break;
                        case "flight_info":
                            LocalTime timestampFormatat = LocalTime.parse(textLinie[0], formatter);
                            String idZborDeAfisat = textLinie[2];
                            String timestampFormatatCuSecunde = timestampFormatat.format(formatter);
                            pwOut.println(timestampFormatatCuSecunde + " | " + gestiunePiste.afiseazaZbor(idZborDeAfisat));
                            break;
                        case "runway_info":
                            String idRunway = textLinie[2];
                            DateTimeFormatter formatterDouaPuncte = DateTimeFormatter.ofPattern("HH:mm:ss");
                            DateTimeFormatter formatterCuLinii = DateTimeFormatter.ofPattern("HH-mm-ss");
                            LocalTime timestampFormatat1 = LocalTime.parse(textLinie[0], formatterDouaPuncte);
                            String timestampFormatatCuSecunde1 = timestampFormatat1.format(formatterCuLinii);
                            String fisierRunwayInfo = antetResources + args[0] + "/" + "runway_info_" + idRunway + "_" + timestampFormatatCuSecunde1 + ".out";
                            FileWriter fwRunwayInfo = new FileWriter(fisierRunwayInfo, true);
                            PrintWriter pwRunwayInfo = new PrintWriter(fwRunwayInfo);
                            gestiunePiste.afisareRunwayInfo(idRunway, timestamp, pwRunwayInfo);
                            pwRunwayInfo.close();
                            break;
                        case "permission_for_maneuver":
                            String idPistaManeuver = textLinie[2];
                            try {
                                Runway<WideBodyAirplane> runwayWide = gestiunePiste.findWideRunway(idPistaManeuver);
                                if (runwayWide != null) {
                                    runwayWide.extrageAvion(timestamp);
                                } else {
                                    Runway<NarrowBodyAirplane> runwayNarrow = gestiunePiste.findNarrowRunway(idPistaManeuver);
                                    runwayNarrow.extrageAvion(timestamp);
                                }
                            } catch (UnavailableRunwayException e) {
                                pwBoardExceptions.println(e.getMessage());
                            }
                            break;
                        default:
                            break;
                    }
                }
                pw.close();
                pwBoardExceptions.close();
                pwOut.close();
            } catch (FileNotFoundException e) {
                throw new FileNotFoundException(e.getMessage());
            } catch (IOException e) {
                throw new IOException(e.getMessage());
            }
        } else {
            System.out.println("Tema2");
        }
    }
}