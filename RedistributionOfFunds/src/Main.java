import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        String inputFile  = "input.txt";
        String outputFile = "output.txt";

        int threshold;
        Map<String, Integer> accounts = new TreeMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            String firstLine = br.readLine();
            if (firstLine == null) {
                System.err.println("Lack of data in file");
                return;
            }
            threshold = parseThreshold(firstLine);

            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(":", 2);
                accounts.put(parts[0].trim(), Integer.parseInt(parts[1].trim()));
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error during reading file: " + e.getMessage());
            return;
        }

        List<Transfer> transfers = Redistributor.redistributeMoney(accounts, threshold);

        writeTransfers(outputFile, transfers);
    }

    private static int parseThreshold(String line) {
        return Integer.parseInt(line.split(":", 2)[1].trim());
    }

    private static void writeTransfers(String file, List<Transfer> transfers) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (Transfer t : transfers) {
                bw.write(t.donorId + ":" + t.amount + " -> " + t.recipientId);
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error during writing to file: " + e.getMessage());
        }
    }
}