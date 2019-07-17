package com.bsuir.task2;



public class Main {

    private static final String EXCEL_FILE = "C:\\Users\\Артём\\IdeaProjects\\EY_tasks_1\\src\\com\\bsuir\\task2\\files\\Excel.xls";

    public static void main(String[] args){
        Database.excelToSQL(EXCEL_FILE);
        Database.printExcel();
    }
}
