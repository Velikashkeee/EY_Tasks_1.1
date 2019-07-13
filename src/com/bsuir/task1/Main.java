package com.bsuir.task1;

import java.io.IOException;
import java.util.Scanner;

public class Main {

    final static int FILES_COUNT = 5;
    final static String FILES_PATH =System.getProperty("user.home")+"\\IdeaProjects\\EY_tasks\\src\\com\\bsuir\\task1\\files\\";
    final static String COMBINED_FILE_NAME = "combinedFile";

    final static String DELETE_SEQENCE = "fsb";

    public static void main(String[] args) {
        FileGenerator.createTextFiles(FILES_PATH, FILES_COUNT);
        Scanner input = new Scanner(System.in);
        System.out.println(" Enter \"1\" to combineFiles \n Enter \"2\" to import files to DB \n Enter \"3\" to get sum and median \n Enter \"0\" to exit");
        try {
            while (input.nextInt() != 0) {
                switch(input.nextInt()){
                    case 1:
                        FileEtc.combineFiles(FILES_PATH, FILES_COUNT, COMBINED_FILE_NAME, DELETE_SEQENCE);
                        System.out.println(FileEtc.getDelCount() + " \"" + DELETE_SEQENCE + "\" strings have been found and deleted in " + FILES_COUNT + " files");
                        break;

                    case 2:
                        Database.importFiles(FILES_PATH, FILES_COUNT);
                        break;

                    case 3:
                        System.out.println("Number sum: "+Database.receiveNumberSum());
                        System.out.println("Median: "+ Database.receiveMedian());
                        break;

                    default:
                        System.out.println("Enter \"1\" to combineFiles \n Enter \"2\" to import files to DB \n Enter \"3\" to get sum and median \n Enter \"0\" to exit");
                        break;

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
