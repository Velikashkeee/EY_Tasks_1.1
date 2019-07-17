package com.bsuir.task1;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileGenerator {

    //number of lines in each file

    public final static int LINES_COUNT = 200;

    //creating 'count' files in given path

    public static void createTextFiles(String path,int count){
        for (int i = 0; i < count; i++) {
            File file = new File(path+"file"+i+".txt");
            try {
                boolean created = file.createNewFile();
                FileWriter writer = new FileWriter(file,false);
                for (int j = 0; j < LINES_COUNT; j++) {
                    writer.write(Generate.GenerateAll() + System.lineSeparator());
                }
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
