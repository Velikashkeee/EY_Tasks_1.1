package com.bsuir.task2;



public class Main {

    private static final String EXCEL_FILE = "C:\\Users\\Артём\\IdeaProjects\\EY_tasks\\src\\com\\bsuir\\task2\\Excel.xls";

    public static void main(String[] args){
        Database.ExceltoSQL(EXCEL_FILE);
        Database.ShowExcelFiles(EXCEL_FILE);
    }
}
