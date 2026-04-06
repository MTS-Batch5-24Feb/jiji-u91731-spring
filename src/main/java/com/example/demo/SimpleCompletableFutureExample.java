package com.example.demo.training;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


public class SimpleCompletableFutureExample {

    public static void main(String[] args) throws Exception {

        // -----------------------------
        // 1. thenCompose with exception
        // -----------------------------
        CompletableFuture<String> composeResult =
                getNumber()
                        .thenCompose(num -> multiply(num))
                        .exceptionally(ex -> {
                            System.out.println("Compose Error: " + ex.getMessage());
                            return "Recovered from compose error";
                        });

        System.out.println("thenCompose result: " + composeResult.get());


        // -----------------------------
        // 2. thenCombine with exception
        // -----------------------------
        CompletableFuture<Integer> f1 = CompletableFuture.supplyAsync(() -> {
            System.out.println("Task 1 running...");
            return 10;
        });

        CompletableFuture<Integer> f2 = CompletableFuture.supplyAsync(() -> {
            System.out.println("Task 2 running...");
            throw new RuntimeException("Error in Task 2");
        });

        CompletableFuture<Integer> combineResult =
                f1.thenCombine(
                        f2.exceptionally(ex -> {
                            System.out.println("Combine Error: " + ex.getMessage());
                            return 0; // fallback value
                        }),
                        (a, b) -> a + b
                );

        System.out.println("thenCombine result: " + combineResult.get());
    }


    // --- helper methods ---

    static CompletableFuture<Integer> getNumber() {
        return CompletableFuture.supplyAsync(() -> {
            System.out.println("Getting number...");
            throw new RuntimeException("Failed to get number");
            // return 5;
        });
    }

    static CompletableFuture<String> multiply(int num) {
        return CompletableFuture.supplyAsync(() -> {
            return "Result: " + (num * 2);
        });
    }
}