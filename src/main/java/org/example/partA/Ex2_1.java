package org.example.partA;


import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class Ex2_1 {

    /**
     * create n files with random amount of lines in each file
     * @param n number of files to create
     * @param seed to pass to the Random class when initializes a new Random class instance
     * @param bound the argument to pass to random.nextInt()
     * @return returns a list with the names of the files created
     */
    public static String[] createTextFiles(int n, int seed, int bound) {
        String[] filenames = new String[n];
        String name;
        for (int i = 0; i < n; i++) {
            name = "file_" + (i + 1) + ".txt";
            try (FileWriter myWriter = new FileWriter(name, true)) {
                filenames[i] = name;
                BufferedWriter bf = new BufferedWriter(myWriter);
                Random rand = new Random(seed);
                int j = rand.nextInt(bound);
                for (int k = 0; k < j; k++) {
                    myWriter.write("Hello world!\n");
                }
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        }
        return filenames;
    }

    /**
     * a function that reads the amount of lines in each file and sum it.
     * @param fileNames a list containing the names of the files created
     * @return the number of lines across all files
     */
    public static int getNumOfLines(String[] fileNames) {
        int lines = 0;
        for (String filename : fileNames) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(filename));
                while (reader.readLine() != null) lines++;
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }

        }
        return lines;
    }

    /**
     * a function that reads the amount of lines in each file and sum it but this time we are using threads to spread out
     * the load of the work
     * @param fileNames a list containing the names of the files created
     * @return the number of lines across all files
     */
    public static int getNumOfLinesThreads(String[] fileNames) {
        List<FileReaderThread> threads = new ArrayList<>();
        for (String name : fileNames) {
            FileReaderThread readerThread = new FileReaderThread(name);
            readerThread.start();
            threads.add(readerThread);
        }
        for (FileReaderThread thread : threads) {
            if (thread.isAlive()) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    System.out.println("An error occurred.");
                    e.printStackTrace();
                }
            }
        }
        return FileReaderThread.sum.get();
    }

    /**
     * a function that reads the amount of lines in each file and sum it but this time we are using threadPool to spread out
     * the load of the work
     * @param fileNames a list containing the names of the files created
     * @return the number of lines across all files
     */
    public static int getNumOfLinesThreadPool(String[] fileNames) {
        int sum = 0;
        try (ExecutorService threadPool = Executors.newFixedThreadPool(fileNames.length)) {
            List<Future<Integer>> result = new ArrayList<>();
            for (String name : fileNames) {
                result.add(threadPool.submit(new FileReaderTask(name)));
            }
            for (Future<Integer> future : result) {
                sum += future.get();
            }
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return sum;
    }

    /**
     * helper method to delete the files created from the system for cleanup
     * @param fileNames a list containing the names of the files created
     */
    public static void deleteFiles(String[] fileNames) {
        for (String name : fileNames) {
            File myObj = new File(name);
            myObj.delete();
        }
    }

    public static void main(String[] args) {
        //create the files and print out the time it took for each of the methods to read the lines
        String[] filenames = createTextFiles(1000, 5, 99999);
        Instant before = Instant.now();
        int lines = getNumOfLines(filenames);
        Instant after = Instant.now();
        long delta = Duration.between(before, after).toMillis();
        System.out.println("time without threads is: " + delta + " milliseconds and read " + lines + " lines");

        before = Instant.now();
        lines = getNumOfLinesThreads(filenames);
        after = Instant.now();
        delta = Duration.between(before, after).toMillis();
        System.out.println("time with threads is: " + delta + " milliseconds and read " + lines + " lines");

        before = Instant.now();
        lines = getNumOfLinesThreadPool(filenames);
        after = Instant.now();
        delta = Duration.between(before, after).toMillis();
        System.out.println("time with threadPool is: " + delta + " milliseconds and read " + lines + " lines");
        deleteFiles(filenames);
    }
}
