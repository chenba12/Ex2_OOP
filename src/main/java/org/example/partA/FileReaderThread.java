package org.example.partA;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class FileReaderThread extends Thread {

    //data members
    private final String filename;
    public static AtomicInteger sum=new AtomicInteger(0);
    //constructor
    public FileReaderThread(String filename) {
        this.filename = filename;
    }

    /**
     * read the amount lines of a given file and store the result sum (an Atomic Integer that works with threads)
     */
    @Override
    public void run() {
        super.run();
        int lines = 0;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            while (reader.readLine() != null) lines++;
            sum.addAndGet(lines);
            reader.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }


    /**
     * @return string representation of the class
     */
    @Override
    public String toString() {
        return "FileReaderThread{" +
                "filename='" + filename + '\'' +
                '}';
    }
}
