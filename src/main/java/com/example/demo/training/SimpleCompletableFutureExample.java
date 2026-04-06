package com.example.demo.training;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class CompletableDemo {

    public static void main(String[] args) throws Exception {

        System.out.println("=== 1. thenCompose + exception ===");
        composeWithException();

        System.out.println("\n=== 2. thenCombine + exception ===");
        combineWithException();

        System.out.println("\n=== 3. thenCompose + exceptionallyCompose ===");
        composeWithAsyncRecovery();
    }

    // -------------------------------------------------------------
    // 1. thenCompose + exception handling
    // -------------------------------------------------------------
    static void composeWithException() throws Exception {

        CompletableFuture<String> future =
                getUserId()                       // returns userId
                        .thenCompose(id -> getUserData(id)) // chaining second async call
                        .exceptionally(ex -> {
                            System.out.println("compose exception caught: " + ex.getMessage());
                            return "Recovered User Data";
                        });

        System.out.println("Result: " + future.get());
    }

    static CompletableFuture<String> getUserId() {
        return CompletableFuture.supplyAsync(() -> {
            System.out.println("Fetching user id...");
            throw new RuntimeException("User ID service down");
        });
    }

    static CompletableFuture<String> getUserData(String userId) {
        return CompletableFuture.supplyAsync(() -> {
            return "UserData for " + userId;
        });
    }

    // -------------------------------------------------------------
    // 2. thenCombine + exception handling
    // -------------------------------------------------------------
    static void combineWithException() throws Exception {

        CompletableFuture<Integer> future1 =
                CompletableFuture.supplyAsync(() -> {
                    System.out.println("Running API 1...");
                    throw new RuntimeException("API 1 failed");
                });

        CompletableFuture<Integer> future2 =
                CompletableFuture.supplyAsync(() -> {
                    System.out.println("Running API 2...");
                    return 20;
                });

        CompletableFuture<Integer> combined =
                future1
                        .exceptionally(ex -> {
                            System.out.println("future1 failed: " + ex.getMessage());
                            return 0;  // fallback for failed future1
                        })
                        .thenCombine(future2, (a, b) -> a + b);

        System.out.println("Combined result: " + combined.get());
    }

    // -------------------------------------------------------------
    // 3. thenCompose + exceptionallyCompose (async recovery)
    // -------------------------------------------------------------
    static void composeWithAsyncRecovery() throws Exception {

        CompletableFuture<String> future =
                CompletableFuture.supplyAsync(() -> {
                            System.out.println("Primary service...");
                            throw new RuntimeException("Primary failed");
                        })
                        .exceptionallyCompose(ex -> {
                            System.out.println("Recovering asynchronously: " + ex.getMessage());
                            return backupService();
                        });

        System.out.println("Async recovery result: " + future.get());
    }

    static CompletableFuture<String> backupService() {
        return CompletableFuture.supplyAsync(() -> "Recovered using backup service");
    }
}