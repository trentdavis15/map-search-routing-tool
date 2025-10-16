/* Date: 12/11/2024
   @author Trent Davis
   Description: This program creates a map storing cities at certain vertexes and then finding the shortest path from point to point
*/
import java.util.*;
import java.io.*;

public class TicketToRideBoard {

    // A map to store the cities and their neighboring cities with route weights
    private Map<String, List<Route>> citiesMap;

    // Constructor to read data from the map file and build the graph
    public TicketToRideBoard(String fileName) {
        citiesMap = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            int numCities = Integer.parseInt(reader.readLine().trim());
            Map<Integer, String> idToCity = new HashMap<>();

            // Read the city names and add them to the map
            for (int i = 0; i < numCities; i++) {
                String line = reader.readLine().trim();
                int spaceIndex = line.indexOf(" ");
                int cityId = Integer.parseInt(line.substring(0, spaceIndex));
                String cityName = line.substring(spaceIndex + 1).toLowerCase().trim(); // Handle multi-word city names
                idToCity.put(cityId, cityName);
                citiesMap.put(cityName, new ArrayList<>());  // Initialize empty routes for each city
            }

            // Read the routes between cities and add them to the map
            String routeLine;
            while ((routeLine = reader.readLine()) != null) {
                String[] parts = routeLine.split(" ");
                int startCityId = Integer.parseInt(parts[0]);
                int endCityId = Integer.parseInt(parts[1]);
                int distance = Integer.parseInt(parts[2]);

                String startCity = idToCity.get(startCityId);
                String endCity = idToCity.get(endCityId);

                // Add routes for both directions
                citiesMap.get(startCity).add(new Route(endCity, distance));
                citiesMap.get(endCity).add(new Route(startCity, distance));
            }

            System.out.println("Cities in map: " + citiesMap.keySet());
            System.out.println("Map loaded successfully.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Return true if the city exists in the loaded map
    public boolean hasCity(String city) {
        if (city == null) return false;
        return citiesMap.containsKey(city.toLowerCase());
    }

    // Return a set of available city names
    public Set<String> getCities() {
        return new TreeSet<>(citiesMap.keySet());
    }

    // Method to find the shortest path between two cities using Dijkstra's Algorithm
    public LinkedList<String> findShortestPath(String start, String end) {
        // Check if the cities exist in the map
        if (!citiesMap.containsKey(start) || !citiesMap.containsKey(end)) {
            System.out.println("One or both cities are missing from the map.");
            return new LinkedList<>();  // Return empty path if cities are missing
        }

        // Initialize priority queue, distance map, and previous city map
        PriorityQueue<CityNode> pq = new PriorityQueue<>(Comparator.comparingInt(c -> c.distance));
        Map<String, Integer> cityDistances = new HashMap<>();
        Map<String, String> previousCity = new HashMap<>();
        LinkedList<String> path = new LinkedList<>();

        // Set initial distances to infinity for all cities
        for (String city : citiesMap.keySet()) {
            cityDistances.put(city, Integer.MAX_VALUE);
        }
        cityDistances.put(start, 0);
        pq.add(new CityNode(start, 0));

        // Dijkstra's Algorithm
        while (!pq.isEmpty()) {
            CityNode currentNode = pq.poll();
            String currentCity = currentNode.city;

            // If we reach the destination city, reconstruct the path
            if (currentCity.equals(end)) {
                String city = end;
                while (city != null) {
                    path.addFirst(city);
                    city = previousCity.get(city);
                }
                return path;
            }

            // Explore neighbors of the current city
            List<Route> neighbors = citiesMap.get(currentCity);
            if (neighbors == null) {
                continue;  // Skip cities with no neighbors
            }

            for (Route route : neighbors) {
                String neighborCity = route.city;
                int newDist = currentNode.distance + route.distance;

                // If a shorter path to the neighbor is found, update the distance and previous city
                if (newDist < cityDistances.get(neighborCity)) {
                    cityDistances.put(neighborCity, newDist);
                    previousCity.put(neighborCity, currentCity);
                    pq.add(new CityNode(neighborCity, newDist));
                }
            }
        }

        return path;  // If no path is found, return an empty path
    }

    // Helper class to represent a city with its distance from the start city
    private static class CityNode {
        String city;
        int distance;

        CityNode(String city, int distance) {
            this.city = city;
            this.distance = distance;
        }
    }

    // Helper class to represent a route between two cities
    private static class Route {
        String city;
        int distance;

        Route(String city, int distance) {
            this.city = city;
            this.distance = distance;
        }
    }
}
