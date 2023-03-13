package org.example;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;

public class Main {
    public static void main(String[] args) {

        String fileName = "tickets.json";
        String originCity = "VVO";
        String destinationCity = "TLV";

        try (FileReader fileReader = new FileReader(fileName)) {
            // чтение данных из файла
            JSONParser parser = new JSONParser();
            JSONObject jsonData = (JSONObject) parser.parse(fileReader);
            JSONArray tickets = (JSONArray) jsonData.get("tickets");

            // подсчет среднего времени полета и 90-го процентиля времени полета
            ArrayList<Long> flightTimes = new ArrayList<>();
            for (Object ticket : tickets) {
                JSONObject ticketData = (JSONObject) ticket;
                String origin = (String) ticketData.get("origin");
                String destination = (String) ticketData.get("destination");
                if (origin.equals(originCity) && destination.equals(destinationCity)) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy'T'H:mm");
                    LocalDateTime departureDateTime = LocalDateTime.parse(
                            (String) ticketData.get("departure_date") + "T" + (String) ticketData.get("departure_time"), formatter);
                    LocalDateTime arrivalDateTime = LocalDateTime.parse(
                            (String) ticketData.get("arrival_date") + "T" + (String) ticketData.get("arrival_time"),formatter);
                    Duration flightDuration = Duration.between(departureDateTime, arrivalDateTime);
                    flightTimes.add(flightDuration.toMinutes());
                }
            }
            Collections.sort(flightTimes);
            double averageFlightTime = flightTimes.stream().mapToLong(val -> val).average().orElse(0.0);
            int percentileIndex = (int) Math.ceil(flightTimes.size() * 0.9);
            long percentileFlightTime = flightTimes.get(percentileIndex - 1);

            // вывод результатов
            System.out.println("Среднее время полета между Владивастоком и Тель-Авивом равно " + averageFlightTime + " минут.");
            System.out.println("90-й процентиль времени полета между Владивастоком и Тель-Авивом равен " + percentileFlightTime + " минут.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
