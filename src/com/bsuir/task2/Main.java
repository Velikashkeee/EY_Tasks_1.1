package com.bsuir.task2;


import java.util.ArrayList;
import java.util.List;

public class Main {

    private static final String EXCEL_FILE = "C:\\Users\\Артём\\IdeaProjects\\EY_tasks_1\\src\\com\\bsuir\\task2\\files\\Excel.xls";

    public static void main(String[] args){
        List<String> filesList = new ArrayList<>();
        Database.excelToSQL(EXCEL_FILE);
        Database.printExcel();
        filesList = Database.getFilesList();
        Database.printFilesList(filesList);
    }
}
