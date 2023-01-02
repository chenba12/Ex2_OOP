package org.example;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class Ex2_1 {

    public static String[] createTextFiles(int n, int seed, int bound){
       String[] filenames=new String[n];
       String name="";
        for (int i=0;i<n;i++){
            try {
                name="file_"+(i+1)+".txt";
                FileWriter myWriter = new FileWriter(name,true);
                filenames[i]=name;
                Random rand = new Random(seed);
                int j=rand.nextInt(bound);
                for (int k=0;k<j;k++){
                    myWriter.write("Hello world!\n");
                }
                myWriter.close();
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        }
        return filenames;
    }

    public static int getNumOfLines(String[] fileNames){
        int lines=0;
        for (String filename: fileNames){
            try {
                BufferedReader reader = new BufferedReader(new FileReader(filename));
                while (reader.readLine() != null) lines++;
            }
            catch (IOException e){
                System.out.println("An error occurred.");
                e.printStackTrace();
            }

        }
        return lines;
    }

    public static int getNumOfLinesThreads(String[] fileNames) {
        int sum=0;
        for (String name:fileNames){
            try {
                FileReaderThread readerThread = new FileReaderThread(name);
                readerThread.start();
                readerThread.join();
            } catch (InterruptedException e){
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        }
        List<Integer> sumOfLines=FileReaderThread.getSumOfLines();
        for (int num:sumOfLines){
            sum+=num;
        }
        return sum;
    }

    public static int getNumOfLinesThreadPool(String[] fileNames){
        int sum=0;
        try {
            ExecutorService threadPool = Executors.newFixedThreadPool(fileNames.length);
            Future<Integer>[] result = new Future[fileNames.length];
            int i = 0;
            for (String name : fileNames) {
                result[i] = threadPool.submit(new FileReaderTask(name));
                i++;
            }
            for (Future<Integer> future : result) {
                sum+=future.get();
            }
            threadPool.shutdown();
        } catch (InterruptedException | ExecutionException e){
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        return sum;
    }

    public static void main(String[] args) {
        String[] filenames=createTextFiles(1000,5,99999);
        Instant before = Instant.now();
        int lines=getNumOfLines(filenames);
        Instant after = Instant.now();
        long delta = Duration.between(before, after).toMillis(); // .toWhatsoever()
        System.out.println("time without threads is: "+delta+" milliseconds and read "+lines+" lines");

        before = Instant.now();
        lines=getNumOfLinesThreads(filenames);
        after = Instant.now();
        delta = Duration.between(before, after).toMillis(); // .toWhatsoever()
        System.out.println("time with threads is: "+delta+" milliseconds and read "+lines+" lines");

        before = Instant.now();
        lines=getNumOfLinesThreadPool(filenames);
        after = Instant.now();
        delta = Duration.between(before, after).toMillis(); // .toWhatsoever()
        System.out.println("time with threadPool is: "+delta+" milliseconds and read "+lines+" lines");
    }
}
