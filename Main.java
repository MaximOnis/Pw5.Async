import java.util.concurrent.*;

public class Main {
    private static String proccessData(String source) {
        try {
            Thread.sleep((long) (Math.random() * 2000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "Data from " + source;
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        long globalStartTime = System.currentTimeMillis();

        ExecutorService executor = Executors.newFixedThreadPool(3);

        CompletableFuture<String> source1 = CompletableFuture.supplyAsync(() -> proccessData("First Source"), executor);
        CompletableFuture<String> source2 = CompletableFuture.supplyAsync(() -> proccessData("Second Source"), executor);
        CompletableFuture<String> source3 = CompletableFuture.supplyAsync(() -> proccessData("Third Source"), executor);

        CompletableFuture<Void> proc = CompletableFuture.allOf(source1, source2, source3);
        proc.thenRun(() -> {
            String result1 = source1.join();
            String result2 = source2.join();
            String result3 = source3.join();
            System.out.println("All data procesed:");
            System.out.println(result1);
            System.out.println(result2);
            System.out.println(result3);
        }).join();
        long globalEndTime = System.currentTimeMillis();
        System.out.println("Work Time: " + (globalEndTime- globalStartTime) + " ms");
        executor.shutdown();
    }
}