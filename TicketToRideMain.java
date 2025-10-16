/* Date: 12/11/2024
   @author Trent Davis
   Description: This program reads in from the text files so we can find the shortest path from city to city
*/
import java.util.*;

public class TicketToRideMain {

    public static void main(String[] args) {
        // Try a set of known map files when searching for city pairs
        String[] candidateMaps = {"usMap.txt", "wiMap.txt"};

        Scanner scanner = new Scanner(System.in);
        try {
            // Ask the user to input the start and end cities
            System.out.print("Enter the start city: ");
            String startCity = scanner.nextLine().trim().toLowerCase();  // Convert input to lowercase

            System.out.print("Enter the end city: ");
            String endCity = scanner.nextLine().trim().toLowerCase();    // Convert input to lowercase

            TicketToRideBoard board = null;
            String usedMap = null;

            // Try each candidate map until we find one that contains both cities
            for (String mapFileCandidate : candidateMaps) {
                try {
                    TicketToRideBoard candidate = new TicketToRideBoard(mapFileCandidate);
                    // Use the board only if it contains both cities
                    if (candidate.hasCity(startCity) && candidate.hasCity(endCity)) {
                        board = candidate;
                        usedMap = mapFileCandidate;
                        break;
                    }
                } catch (Exception e) {
                    // Ignore and try next candidate
                }
            }

            if (board == null) {
                System.out.println("One or both cities are missing from the available maps.");
                System.out.println("Tried maps: " + Arrays.toString(candidateMaps));
                System.out.println("Available cities in each map:");
                for (String mapFileCandidate : candidateMaps) {
                    try {
                        TicketToRideBoard candidate = new TicketToRideBoard(mapFileCandidate);
                        System.out.println(mapFileCandidate + ": " + candidate.getCities());
                    } catch (Exception e) {
                        System.out.println(mapFileCandidate + ": (could not load)");
                    }
                }
                return;
            }

            // Find the shortest path between the cities
            LinkedList<String> shortestPath = board.findShortestPath(startCity, endCity);

            // Display the results
            if (shortestPath.isEmpty()) {
                System.out.println("No path found between " + startCity + " and " + endCity);
            } else {
                System.out.println("(Map: " + usedMap + ") The shortest path from " + startCity + " to " + endCity + " is:");
                for (String city : shortestPath) {
                    System.out.print(city + " ");
                }
                System.out.println();
            }

        } catch (Exception e) {
            System.err.println("An error occurred while processing the map file or finding the shortest path.");
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}
