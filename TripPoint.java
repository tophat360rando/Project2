import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
 
public class TripPoint {
 
    private static final double EARTH_RADIUS_KM = 6371.0;
 
    private int time;     // minutes
    private double lat;   // degrees
    private double lon;   // degrees
 
    private static ArrayList<TripPoint> trip = new ArrayList<>();
 
    // Constructor
    public TripPoint(int time, double lat, double lon) {
        this.time = time;
        this.lat = lat;
        this.lon = lon;
    }
 
    // Getters
    public int getTime() {
        return time;
    }
 
    public double getLat() {
        return lat;
    }
 
    public double getLon() {
        return lon;
    }
 
    // Returns a copy of the trip ArrayList
    public static ArrayList<TripPoint> getTrip() {
        return new ArrayList<>(trip);
    }
 
    // Reads Time,Latitude,Longitude rows into the trip ArrayList
    public static void readFile(String filename) throws FileNotFoundException, IOException {
        trip.clear();
 
        try (Scanner scan = new Scanner(new File(filename))) {
            // Skip the header line
            if (scan.hasNextLine()) {
                scan.nextLine();
            }
 
            while (scan.hasNextLine()) {
                String line = scan.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                }
 
                String[] parts = line.split(",");
                int time = Integer.parseInt(parts[0].trim());
                double lat = Double.parseDouble(parts[1].trim());
                double lon = Double.parseDouble(parts[2].trim());
 
                trip.add(new TripPoint(time, lat, lon));
            }
        }
    }
 
    // Total time of the trip in hours (Time column is in minutes)
    public static double totalTime() {
        if (trip.size() < 2) {
            return 0;
        }
        int minutes = trip.get(trip.size() - 1).getTime() - trip.get(0).getTime();
        return minutes / 60.0;
    }
 
    // distance between two point in kilometers
    public static double haversineDistance(TripPoint a, TripPoint b) {
        double lat1 = Math.toRadians(a.getLat());
        double lat2 = Math.toRadians(b.getLat());
        double dLat = lat2 - lat1;
        double dLon = Math.toRadians(b.getLon() - a.getLon());
 
        double h = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                 + Math.cos(lat1) * Math.cos(lat2)
                 * Math.sin(dLon / 2) * Math.sin(dLon / 2);
 
        return 2 * EARTH_RADIUS_KM * Math.asin(Math.sqrt(h));
    }
 
    // Total distance of the trip in kilometers
    public static double totalDistance() {
        double total = 0;
        for (int i = 1; i < trip.size(); i++) {
            total += haversineDistance(trip.get(i - 1), trip.get(i));
        }
        return total;
    }
 
    // Average speed between two points in km/h
    public static double avgSpeed(TripPoint a, TripPoint b) {
        double hours = Math.abs(a.getTime() - b.getTime()) / 60.0;
        if (hours == 0) {
            return 0;
        }
        return haversineDistance(a, b) / hours;
    }
}
 