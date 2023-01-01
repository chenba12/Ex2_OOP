package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileReaderThread extends Thread {
    private String filename;
    private static List<Integer> sumOfLines=new ArrayList<>();
    public FileReaderThread(String filename) {
        this.filename = filename;
    }

    @Override
    public void run() {
        super.run();
        int lines = 0;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            while (reader.readLine() != null) lines++;
            sumOfLines.add(lines);
            reader.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public static List<Integer> getSumOfLines() {
        return sumOfLines;
    }

    @Override
    public String toString() {
        return "FileReaderThread{" +
                "filename='" + filename + '\'' +
                '}';
    }
}
