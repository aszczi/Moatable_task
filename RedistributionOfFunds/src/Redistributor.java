import java.util.*;

public class Redistributor {

    public static List<Transfer> redistributeMoney(Map<String, Integer> accounts, int threshold) {
        if (!valid(accounts, threshold)) {
            return Collections.emptyList();
        }

        List<Transfer> transfers = new ArrayList<>();
        PriorityQueue<Map.Entry<String, Integer>> donors = new PriorityQueue<>(
                Comparator.comparingInt(e -> -(e.getValue() - threshold))
        );
        PriorityQueue<Map.Entry<String, Integer>> recipients = new PriorityQueue<>(
                Comparator.comparingInt(e -> -(threshold - e.getValue()))
        );

        for (var entry : accounts.entrySet()) {
            int bal = entry.getValue();
            if (bal > threshold) {
                donors.offer(new AbstractMap.SimpleEntry<>(entry.getKey(), bal));
            } else if (bal < threshold) {
                recipients.offer(new AbstractMap.SimpleEntry<>(entry.getKey(), bal));
            }
        }

        while (!donors.isEmpty() && !recipients.isEmpty()) {
            var donorEntry = donors.poll();
            var recEntry   = recipients.poll();

            int surplus    = donorEntry.getValue() - threshold;
            int deficiency = threshold - recEntry.getValue();
            int amt        = Math.min(surplus, deficiency);

            if (amt <= 0) continue;

            transfers.add(new Transfer(donorEntry.getKey(), recEntry.getKey(), amt));

            int newDonBal = donorEntry.getValue() - amt;
            int newRecBal = recEntry.getValue() + amt;
            accounts.put(donorEntry.getKey(), newDonBal);
            accounts.put(recEntry.getKey(),    newRecBal);

            if (newDonBal > threshold)
                donors.offer(new AbstractMap.SimpleEntry<>(donorEntry.getKey(), newDonBal));
            if (newRecBal < threshold)
                recipients.offer(new AbstractMap.SimpleEntry<>(recEntry.getKey(), newRecBal));
        }

        return transfers;
    }

    private static boolean valid(Map<String, Integer> accounts, int threshold) {
        if (threshold < 0) {
            System.err.println("Threshold cant be negative");
            return false;
        }
        boolean ok = accounts.values().stream().allMatch(v -> v >= 0);
        if (!ok) {
            System.err.println("Account values ≥ 0");
        }
        return ok;
    }
}