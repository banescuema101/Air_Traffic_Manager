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
            // antetul fisierelor pe care le voi folosi ca intrare/iesire.
            String antetResources = "src/main/resources/";
            String fisierIntrare = antetResources + args[0] + "/" + "input.in";
            String fisierExceptii = antetResources + args[0] + "/"+ "board_exceptions.out";
            String fisierOut = antetResources + args[0] + "/" + "flight_info.out";
            try {
                // fisierul in care voi SCRIE exceptiile de tip IncorrectRunway si UnavailableRunway line by line.
                FileWriter fw = new FileWriter(fisierExceptii, true);
                PrintWriter pwExceptii = new PrintWriter(fw);
                // file ul de intrare, din care CITESC informatiile.
                FileReader fr = new FileReader(fisierIntrare);
                BufferedReader br = new BufferedReader(fr);
                // fisierul de iesire in care scriu outputul comenzii flights_info: flights_info.out.
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
                    // creez un abiect de tip LocalTime, din Stringul timestamp primit de la input,
                    // formatat corespunzator.
                    LocalTime timestamp = LocalTime.parse(sirCuTimpul, formatter);
                    switch (comanda) {
                        case "add_runway_in_use":
                            // prieau parametrii.
                            String id = textLinie[2];
                            String utilizare = textLinie[3];
                            String tipAvionAcceptat = textLinie[4];
                            // conteaza pentru mine in primul rand daca adaug pista in mapul de piste cu avioane
                            // de tip wide body sau narrow body, si pentru fiecare din aceste doua cazuri, va conta
                            // si daca pista este de decolare sau de aterizare, d.p.d.v al comparatorului folosit
                            // pt a mentine prioritatea corecta in cadrul cozii de avioane.
                            if (tipAvionAcceptat.equals("wide body")) {
                                if (utilizare.equals("landing")) {
                                    Runway<WideBodyAirplane> runway = new Runway<>(id, utilizare, tipAvionAcceptat, new RunwayComparatorLanding<>());
                                    gestiunePiste.addToWideList(runway);
                                } else if(utilizare.equals("takeoff")) {
                                    Runway<WideBodyAirplane> runway = new Runway<>(id, utilizare, tipAvionAcceptat, new RunwayComparatorTakeoff<>());
                                    gestiunePiste.addToWideList(runway);
                                }
                            } else if (tipAvionAcceptat.equals("narrow body")) {
                                if (utilizare.equals("landing")) {
                                    Runway<NarrowBodyAirplane> runway = new Runway<>(id, utilizare, tipAvionAcceptat, new RunwayComparatorLanding<>());
                                    gestiunePiste.addToNarrowList(runway);
                                } else if(utilizare.equals("takeoff")) {
                                    Runway<NarrowBodyAirplane> runway = new Runway<>(id, utilizare, tipAvionAcceptat, new RunwayComparatorTakeoff<>());
                                    gestiunePiste.addToNarrowList(runway);
                                }
                            }
                            break;
                        case "allocate_plane":
                            // comanda de alocare a unui avion unei piste cu id-ul id. ( nu mi se spune daca
                            // contine avioane de tip narrow sau wide, deci voi incerca separat mai intai sa o gasesc
                            // in mapul cu piste cu avioane de tip wide si apoi de tip narrow. Vad in care o gasesc
                            // si adaug corespunzator apeland metoda adaugaAvion din clasa Runway, cat si din clasa
                            // GestiunePiste.
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
                                    WideBodyAirplane avion = new WideBodyAirplane(model, idZbor, plecare, destinatie, timpDorit, urgent);
                                    runwayFoundWide.adaugaAvion(avion, timestamp);
                                    // adaug si in lista de zboruri din clasa GestiunePiste,
                                    // unde memorez in hashmap-ul ID_ZBOR <-> avion
                                    gestiunePiste.adaugaZbor(avion);
                                } else {
                                    Runway<NarrowBodyAirplane> runwayFoundNarrow = gestiunePiste.findNarrowRunway(idPista);
                                    NarrowBodyAirplane avion = new NarrowBodyAirplane(model, idZbor, plecare, destinatie, timpDorit, urgent);
                                    runwayFoundNarrow.adaugaAvion(avion, timestamp);
                                    gestiunePiste.adaugaZbor(avion);
                                }
                            } catch (IncorrectRunwayException e) {
                                pwExceptii.println(e.getMessage());
                            }
                            break;
                        case "flight_info":
                            // Comanda de afisare in fisierul flight_info a informatiilor despre un zbor ( un anume
                            // avion) cu un anume id. Apelez metoda gasireZborDupaId din instanta clasei
                            // GestiunePiste.
                            LocalTime timestampFormatat = LocalTime.parse(textLinie[0], formatter);
                            String idZborDeAfisat = textLinie[2];
                            String timestampFormatatCuSecunde = timestampFormatat.format(formatter);
                            pwOut.println(timestampFormatatCuSecunde + " | " + gestiunePiste.gasireZborDupaId(idZborDeAfisat));
                            break;
                        case "runway_info":
                            // Comanda de afisare in fisier a informatiilor unei piste cu un anume id.
                            // apeland metoda afisareRunwayInfo din clasa GestiunePiste.
                            String idRunway = textLinie[2];
                            // creez acum un DateTimeFormatter pentru a crea numele fisierului
                            // delimitat de "-" intre ora-minut-secunde.
                            DateTimeFormatter formatterCuLinii = DateTimeFormatter.ofPattern("HH-mm-ss");
                            LocalTime timestampFormatat1 = LocalTime.parse(textLinie[0], formatter);
                            // pentru a crea fisierul runway_info_name_ora-minut-secunde.
                            String timestampFormatatCuSecunde1 = timestampFormatat1.format(formatterCuLinii);
                            String fisierRunwayInfo = antetResources + args[0] + "/" + "runway_info_" + idRunway + "_" + timestampFormatatCuSecunde1 + ".out";
                            FileWriter fwRunwayInfo = new FileWriter(fisierRunwayInfo, true);
                            PrintWriter pwRunwayInfo = new PrintWriter(fwRunwayInfo);
                            gestiunePiste.afisareRunwayInfo(idRunway, timestamp, pwRunwayInfo);
                            pwRunwayInfo.close();
                            break;
                        case "permission_for_maneuver":
                            // Comanda care executa manevra de aterizare, respectiv decolare, a avionului
                            // extras cu ajutorul metodei extrageAvion din clasa GestiunePiste.
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
                        // comanda noua pt partea de bonus, cu ajutorul careia putem:
                        // (1) amana timpul de decolare/aterizare a unui anume avion de pe o anume pista.
                        // (2) anula un anume zbor din cadrul cozii de avioane dintr-o anume pista.
                        // (3) sa mutam un avion cu un anume id, de pe o o pista cu un anume id, pe alta pista cu alt id.
                        case "change_flight":
                            // poate fi data sub 3 forme:
                            // timestamp change_flight delay id_zbor id_pista timestamp
                            // timestamp change_flight cancel id_zbor id_pista
                            // timestamp change_flight move id_zbor id_pista id_pista_noua
                            String actiune = textLinie[2];
                            String idZborDeSchimbat = textLinie[3];
                            idPista = textLinie[4];
                            switch(actiune) {
                                case "delay":
                                    String sirTimestampSchimbat = textLinie[5];
                                    LocalTime timestampSchimbat = LocalTime.parse(sirTimestampSchimbat, formatter);
                                    System.out.println("DELAY FLIGHT");
                                    gestiunePiste.amanareZbor(idZborDeSchimbat, idPista, timestampSchimbat);
                                    break;
                                case "cancel":
                                    System.out.println("CANCEL FLIGHT");
                                    gestiunePiste.eliminareZbor(idZborDeSchimbat, idPista);
                                    break;
                                case "move":
                                    System.out.println("MOVE FLIGHT");
                                    String idPistaNoua = textLinie[5];
                                    gestiunePiste.mutareZbor(idZborDeSchimbat, idPista, idPistaNoua, timestamp, pwExceptii);
                                    break;
                            }
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