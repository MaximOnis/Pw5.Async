import java.util.*;
import java.util.concurrent.*;

public class Ex2 {
    public static Map<String, Object> getroute(String transport){

        double time = Math.random() * 10 + 5;
        time = Math.round(time * 10) / 10.0;

        double price = Math.random() * 500 + 100;
        price = Math.round(price * 100) / 100.0;

        Map <String, Object> res = new HashMap<>();
        res.put("transport", transport);
        res.put("time", time);
        res.put("price", price);

        return res;
    }

    private static Map<String, Object> findBestRoute(List<Map<String, Object>> routes) {
        Map<String, Object> bestRoute = null;
        double bestRatio = Double.MAX_VALUE;

        for (Map<String, Object> route : routes) {
            double price = (double) route.get("price");
            double time = (double) route.get("time");
            double ratio = price / time;

            if (ratio < bestRatio) {
                bestRatio = ratio;
                bestRoute = route;
            }
        }

        if (bestRoute == null) {
            throw new RuntimeException("No routes available");
        }

        return bestRoute;
    }

    private static void print_stats(Map<String, Object> detail){
        System.out.println(detail.toString());
    }


    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CompletableFuture<Map<String, Object>> plane = CompletableFuture.supplyAsync(() -> getroute("plane"));
        CompletableFuture<Map<String, Object>> bus = CompletableFuture.supplyAsync(() -> getroute("bus"));
        CompletableFuture<Map<String, Object>> train = CompletableFuture.supplyAsync(() -> getroute("train"));

        CompletableFuture<Void> planestats = plane.thenCompose(details ->
                CompletableFuture.runAsync(() -> print_stats(details)));
        CompletableFuture<Void> busstats = bus.thenCompose(details ->
                CompletableFuture.runAsync(() -> print_stats(details)));
        CompletableFuture<Void> trainstats = train.thenCompose(details ->
                CompletableFuture.runAsync(() -> print_stats(details)));

        CompletableFuture<Void> best = CompletableFuture.allOf(plane, bus, train);
        best.thenRun(() -> {
            try {
                Map<String, Object> planemap = plane.get();
                Map<String, Object> busmap = bus.get();
                Map<String, Object> trainmap = train.get();

                planestats.get();
                busstats.get();
                trainstats.get();

                Map<String, Object> bestOption = findBestRoute(Arrays.asList(trainmap, busmap, planemap));
                System.out.println("\nOptimal Route: " + bestOption);
            } catch (InterruptedException | ExecutionException e) {
                System.out.println("ss");
                e.printStackTrace();
            }
        });

        best.join();
    }
}
