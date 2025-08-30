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
            // The header of the files that I will use as input/output.
            String resourcesHeader = "src/main/resources/";
            String inputFile = resourcesHeader + args[0] + "/" + "input.in";
            String exceptionsFile = resourcesHeader + args[0] + "/"+ "board_exceptions.out";
            String outputFile = resourcesHeader + args[0] + "/" + "flight_info.out";
            try {
                // The file in which I will WRITE exceptions of type IncorrectRunway and UnavailableRunway line by line.
                FileWriter fw = new FileWriter(exceptionsFile, true);
                PrintWriter pwForExceptions = new PrintWriter(fw);
                // The input file, from which I READ the information.
                FileReader fr = new FileReader(inputFile);
                BufferedReader br = new BufferedReader(fr);
                // The output file where I write the output of the flights_info command: flights_info.out.
                FileWriter fwOut = new FileWriter(outputFile, true);
                PrintWriter pwOut = new PrintWriter(fwOut);
                String line;
                // a formatter of type hour:minute:seconds
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                RunwayManagement runwayManagement = new RunwayManagement();
                // I check that I can read the line and that it is NOT EMPTY, only with trailing spaces...
                while ((line = br.readLine()) != null && !line.isEmpty()) {
                    String[] lineStringsList = line.split(" - ");
                    String command = lineStringsList[1];
                    String timeString = lineStringsList[0];
                    // I create a LocalTime object from the timestamp String received as input,
                    // formatted accordingly.
                    LocalTime timestamp = LocalTime.parse(timeString, formatter);
                    switch (command) {
                        case "add_runway_in_use":
                            // I extract the parameters of the command.
                            String id = lineStringsList[2];
                            String usage = lineStringsList[3];
                            String acceptedAirplaneType = lineStringsList[4];
                            // It is important for me, first of all, to add the runway to the map of runways with airplanes
                            // of type wide body or narrow body, and for each of these two cases, it will also matter
                            // whether the runway is for takeoff or landing, from the perspective of the comparator used
                            // to maintain the correct priority within the airplane queue.
                            if (acceptedAirplaneType.equals("wide body")) {
                                if (usage.equals("landing")) {
                                    Runway<WideBodyAirplane> runway = new Runway<>(id, usage, acceptedAirplaneType, new RunwayComparatorLanding<>());
                                    runwayManagement.addToWideList(runway);
                                } else if(usage.equals("takeoff")) {
                                    Runway<WideBodyAirplane> runway = new Runway<>(id, usage, acceptedAirplaneType, new RunwayComparatorTakeoff<>());
                                    runwayManagement.addToWideList(runway);
                                }
                            } else if (acceptedAirplaneType.equals("narrow body")) {
                                if (usage.equals("landing")) {
                                    Runway<NarrowBodyAirplane> runway = new Runway<>(id, usage, acceptedAirplaneType, new RunwayComparatorLanding<>());
                                    runwayManagement.addToNarrowList(runway);
                                } else if(usage.equals("takeoff")) {
                                    Runway<NarrowBodyAirplane> runway = new Runway<>(id, usage, acceptedAirplaneType, new RunwayComparatorTakeoff<>());
                                    runwayManagement.addToNarrowList(runway);
                                }
                            }
                            break;
                        case "allocate_plane":
                            // Command to allocate an airplane to a runway with the given id. (It is not specified
                            // whether it contains narrow or wide-body airplanes, so I will first try to find it
                            // in the map of runways with wide-body airplanes and then in the map of runways with
                            // narrow-body airplanes. I will see where it is found and add it accordingly by calling
                            // the addAirplane method from the Runway class as well as from the RunwayManagement class.
                            String airplaneType = lineStringsList[2];
                            String model = lineStringsList[3];
                            String flightId = lineStringsList[4];
                            String departure = lineStringsList[5];
                            String destination = lineStringsList[6];
                            LocalTime desiredTime = LocalTime.parse(lineStringsList[7], formatter);
                            String runwayId = lineStringsList[8];
                            // urgentFlag can be null or can contain the String "urgentFlag":
                            // this is handled by the constructor of the abstract class org.example.Airplane.
                            String urgentFlag = null;
                            if (lineStringsList.length == 10) {
                                urgentFlag = lineStringsList[9];
                            }
                            try {
                                if (airplaneType.equals("wide body")) {
                                    Runway<WideBodyAirplane> runwayFoundWide = runwayManagement.findWideRunway(runwayId);
                                    WideBodyAirplane airplane = new WideBodyAirplane(model, flightId, departure, destination, desiredTime, urgentFlag);
                                    runwayFoundWide.addAirplane(airplane, timestamp);
                                    // add to the list of flights in the RunwayManagement class,
                                    // where I store in the hashmap ID_FLIGHT <-> airplane
                                    runwayManagement.addFlight(airplane);
                                } else {
                                    Runway<NarrowBodyAirplane> runwayFoundNarrow = runwayManagement.findNarrowRunway(runwayId);
                                    NarrowBodyAirplane airplane = new NarrowBodyAirplane(model, flightId, departure, destination, desiredTime, urgentFlag);
                                    runwayFoundNarrow.addAirplane(airplane, timestamp);
                                    runwayManagement.addFlight(airplane);
                                }
                            } catch (IncorrectRunwayException e) {
                                pwForExceptions.println(e.getMessage());
                            }
                            break;
                        case "flight_info":
                            // Command to write flight information about a specific flight (a specific airplane) with a specific id to the flight_info file.
                            // I call the findFlightById method from the instance of the RunwayManagement class.
                            LocalTime formattedTimestamp = LocalTime.parse(lineStringsList[0], formatter);
                            String displayedFlightId = lineStringsList[2];
                            String timestampFormattedWithSeconds = formattedTimestamp.format(formatter);
                            pwOut.println(timestampFormattedWithSeconds + " | " + runwayManagement.findFlightById(displayedFlightId));
                            break;
                        case "runway_info":
                            // Command to display information about a runway with a specific id in a file.
                            // by calling the displayRunwayInfo method from the RunwayManagement class.
                            String idRunway = lineStringsList[2];
                            // I am now creating a DateTimeFormatter to create the file name
                            // delimited by "-" between hour-minute-seconds.
                            DateTimeFormatter formatterWithDashes = DateTimeFormatter.ofPattern("HH-mm-ss");
                            LocalTime formattedTimestamp1 = LocalTime.parse(lineStringsList[0], formatter);
                            // to create the file runway_info_name_hour-minute-seconds.
                            String timestampFormattedWithSeconds1 = formattedTimestamp1.format(formatterWithDashes);
                            String runwayInfoFile = resourcesHeader + args[0] + "/" + "runway_info_" + idRunway + "_" + timestampFormattedWithSeconds1 + ".out";
                            FileWriter fwRunwayInfo = new FileWriter(runwayInfoFile, true);
                            PrintWriter pwRunwayInfo = new PrintWriter(fwRunwayInfo);
                            runwayManagement.displayRunwayInfo(idRunway, timestamp, pwRunwayInfo);
                            pwRunwayInfo.close();
                            break;
                        case "permission_for_maneuver":
                            // Command that executes the landing or takeoff maneuver of the airplane
                            // extracted using the airplaneExtraction method from the RunwayManagement class.
                            String runwayIdManeuver = lineStringsList[2];
                            try {
                                Runway<WideBodyAirplane> runwayWide = runwayManagement.findWideRunway(runwayIdManeuver);
                                if (runwayWide != null) {
                                    runwayWide.airplaneExtraction(timestamp);
                                } else {
                                    Runway<NarrowBodyAirplane> runwayNarrow = runwayManagement.findNarrowRunway(runwayIdManeuver);
                                    runwayNarrow.airplaneExtraction(timestamp);
                                }
                            } catch (UnavailableRunwayException e) {
                                pwForExceptions.println(e.getMessage());
                            }
                            break;
                            // New command for the bonus part, with which we can:
                            // (1) delay the takeoff/landing time of a specific airplane on a specific runway.
                            // (2) cancel a specific flight from the queue of airplanes on a specific runway.
                            // (3) move an airplane with a specific id from one runway with a specific id to another runway with a different id.
                        case "change_flight":
                            // It can be given in 3 forms:
                            // timestamp change_flight delay flight_id runway_id timestamp
                            // timestamp change_flight cancel flight_id runway_id
                            // timestamp change_flight move flight_id runway_id new_runway_id
                            String action = lineStringsList[2];
                            String flightIdToChange = lineStringsList[3];
                            runwayId = lineStringsList[4];
                            switch(action) {
                                case "delay":
                                    String changedTimestampString = lineStringsList[5];
                                    LocalTime changedTimestamp = LocalTime.parse(changedTimestampString, formatter);
                                    System.out.println("DELAY FLIGHT");
                                    runwayManagement.delayFlight(flightIdToChange, runwayId, changedTimestamp);
                                    break;
                                case "cancel":
                                    System.out.println("CANCEL FLIGHT");
                                    runwayManagement.eliminateFlight(flightIdToChange, runwayId);
                                    break;
                                case "move":
                                    System.out.println("MOVE FLIGHT");
                                    String newRunwayId = lineStringsList[5];
                                    runwayManagement.moveFlight(flightIdToChange, runwayId, newRunwayId, timestamp, pwForExceptions);
                                    break;
                            }
                        case "exit":
                            break;
                    }
                }
                pwForExceptions.close();
                pwOut.close();
            } catch (FileNotFoundException e) {
                throw new FileNotFoundException(e.getMessage());
            } catch (IOException e) {
                throw new IOException(e.getMessage());
            }
        }
    }
}