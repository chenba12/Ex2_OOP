package org.example.partA;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.Callable;

public class FileReaderTask implements Callable<Integer> {


    //data members
    private final String name;

    //constructor
    public FileReaderTask(String name){
        this.name=name;
    }

    public String getName() {
        return name;
    }

    /**
     * read the amount lines of a given file
     * @return the amount of lines read can be used with a Future object to get the result
     * @throws Exception
     */
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

    /**
     * @return string representation of the class
     */
    @Override
    public String toString() {
        return "FileReaderTask{" +
                "name='" + name + '\'' +
                '}';
    }
}
