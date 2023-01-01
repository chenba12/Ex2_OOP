package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.Callable;

public class FileReaderTask implements Callable<Integer> {
    private String name;
    public FileReaderTask(String name){
        this.name=name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "FileReaderTask{" +
                "name='" + name + '\'' +
                '}';
    }

    @Override
    public Integer call() throws Exception {
        int lines = 0;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(name));
            while (reader.readLine() != null) lines++;
            reader.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return lines;
    }
}
