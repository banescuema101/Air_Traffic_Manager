package org.example;

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
            String antetResources = "src/main/resources/";
            String fisierIntrare = antetResources + args[0] + "/" + "input.in";
            String fisierExceptii = antetResources + args[0] + "/"+ "board_exceptions.out";
            String fisierOut = antetResources + args[0] + "/" + "flight_info.out";
            try {
                // fisierul in care voi scrie exceptiile de tip IncorrectRunway si UnavailableRunway line by line.
                FileWriter fw = new FileWriter(fisierExceptii, true);
                PrintWriter pwExceptii = new PrintWriter(fw);
                // file ul de intrare, din care citesc informatiile
                FileReader fr = new FileReader(fisierIntrare);
                BufferedReader br = new BufferedReader(fr);
                // fisierul de iesire: flights_info.out
                FileWriter fwOut = new FileWriter(fisierOut, true);
                PrintWriter pwOut = new PrintWriter(fwOut);
                String linie;
                // un formatter de tip ora:minut:secunde
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                GestiunePiste gestiunePiste = new GestiunePiste();
                // verific ca reusesc sa fac citirea liniei, cat si faptul CA NU E GOAALA doar cu
                // trailing space-uri...
                while ((linie = br.readLine()) != null && !linie.isEmpty()) {
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
                            // acest lucru este gestionat de constructorul clasei abstracta org.example.Airplane.
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
                                pwExceptii.println(e.getMessage());
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
                            DateTimeFormatter formatterCuLinii = DateTimeFormatter.ofPattern("HH-mm-ss");
                            LocalTime timestampFormatat1 = LocalTime.parse(textLinie[0], formatter);
                            // pentru a crea fisierul runway_info name_ora-minut-secunde
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
                                pwExceptii.println(e.getMessage());
                            }
                            break;
                        case "exit":
                            break;
                    }
                }
                pwExceptii.close();
                pwOut.close();
            } catch (FileNotFoundException e) {
                throw new FileNotFoundException(e.getMessage());
            } catch (IOException e) {
                throw new IOException(e.getMessage());
            }
        }
    }
}