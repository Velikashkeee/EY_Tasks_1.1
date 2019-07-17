package com.bsuir.task2;

import com.mysql.cj.xdevapi.SqlDataResult;
import org.apache.poi.hssf.extractor.ExcelExtractor;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class Database {
    //Databases URL, login and password for connection
    private static final String URL =  "jdbc:mysql://localhost:3306/ey_task2"+
            "?verifyServerCertificate=false"+
            "&useSSL=false"+
            "&requireSSL=false"+
            "&useLegacyDatetimeCode=false"+
            "&amp"+
            "&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    //hardcoded indexes of initialisation rows: bank_name, balance_sheet and period

    private static final int LAST_INITIALISATION_ROW = 7;
    private static final int BANK_ROW_INDEX = 0;
    private static final int BALANCE_ROW_INDEX = 1;
    private static final int PERIOD_ROW_INDEX = 2;

    //variables for importing rows from excel file to DB

    private static double bankAccountId;
    private static String bankAccId;
    private static String className;
    private static double openingBalanceActive;
    private static double openingBalancePassive;
    private static double circulationCredit;
    private static double circulationDebit;
    private static double closingBalanceActive;
    private static double closingBalancePassive;


    public static void ShowExcelFiles(String path){
        try (InputStream inp = new FileInputStream(path)) {
            HSSFWorkbook wb = new HSSFWorkbook(new POIFSFileSystem(inp));
            ExcelExtractor extractor = new ExcelExtractor(wb);
            extractor.setFormulasNotResults(false);
            extractor.setIncludeSheetNames(false);
            extractor.getText();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void printFilesList(List<String> filesList){
        for (String file:filesList) {
            System.out.println(file);
        }
    }

    public static List<String> getFilesList(){
        List<String> resultList = new ArrayList<>();
        try(Connection connection = DriverManager.getConnection(URL,USER,PASSWORD)){
            try (Statement statement = connection.createStatement()){
                ResultSet resultSet = statement.executeQuery("SELECT file_name FROM excel_files");
                while (resultSet.next()){
                    resultList.add(resultSet.getString("file_name"));
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return resultList;
    }

    public static void printExcel(){
        // Stores all excel files stored in DB
        ResultSet fileSet;
        // Stores all classes of excel file
        ResultSet classSet;
        // Stores data of excel files
        ResultSet dataSet;
        try(Connection connection = DriverManager.getConnection(URL,USER,PASSWORD)){
            try(Statement fileStatement = connection.createStatement()){
                StringBuffer table = new StringBuffer();

                fileSet = fileStatement.executeQuery("SELECT * FROM excel_files");
                while (fileSet.next()){
                    table.append(fileSet.getString("file_name")+"\n");
                    table.append(fileSet.getString("bank_name") + "\n\t\t");
                    table.append(fileSet.getString("balance_sheet")+"\n\t\t\t");
                    table.append(fileSet.getString("period")+"\n");
                    table.append(fileSet.getString("table_header"));
                    try(Statement classStatement = connection.createStatement()){
                        classSet = classStatement.executeQuery("SELECT class_name FROM classes WHERE file_name =\""+ fileSet.getString("file_name")+"\"");
                        while (classSet.next()){
                            table.append(classSet.getString("class_name") + "\n");
                            try(Statement dataStatement = connection.createStatement()){
                                dataSet = dataStatement.executeQuery("SELECT * FROM classes_data WHERE class_name =\""+classSet.getString("class_name")+"\" AND file_name =\"" + fileSet.getString("file_name")+"\"");
                                while (dataSet.next()){
                                    table.append(dataSet.getString("bank_account_id") + "\t");
                                    table.append(dataSet.getDouble("open_balance_active") + "\t");
                                    table.append(dataSet.getDouble("open_balance_passive") + "\t");
                                    table.append(dataSet.getDouble("circulate_debit") + "\t");
                                    table.append(dataSet.getDouble("circulate_credit")+ "\t");
                                    table.append(dataSet.getDouble("close_balance_active") + "\t");
                                    table.append(dataSet.getDouble("close_balance_passive") + "\n");
                                }
                            }
                        }
                    }
                    table.append("\n\n\n");
                }
                System.out.println(table);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static void excelToSQL(String path){
        //isClassRow shows whether excel row contains class name or not
        boolean isClassRow = false;

        String tableHeader = null;
        String bank = null;
        String balance = null;
        String period = null;

        // trying to open Excel file and connect to DB

        try (InputStream inp = new FileInputStream(path)) {
            HSSFWorkbook wb = new HSSFWorkbook(new POIFSFileSystem(inp));
            File file = new File (path);
            wb.setMissingCellPolicy(Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            try(Connection connection =DriverManager.getConnection(URL,USER,PASSWORD)) {
                for(int i = 0; i < wb.getNumberOfSheets(); ++i){
                    HSSFSheet sheet = wb.getSheetAt(i);
                    if (sheet!=null){

                        // getting bank name

                        HSSFRow rowBank = sheet.getRow(BANK_ROW_INDEX);
                        if (rowBank!=null){
                            HSSFCell cell = rowBank.getCell(0);
                            if (cell != null){
                                bank = cell.getRichStringCellValue().toString();
                            }
                        }

                        //getting bank balance sheet

                        HSSFRow rowBalance = sheet.getRow(BALANCE_ROW_INDEX);
                        if (rowBalance!=null){
                            HSSFCell cell = rowBalance.getCell(0);
                            if (cell != null){
                                balance = cell.getRichStringCellValue().toString();
                            }
                        }

                        //getting balance sheet period

                        HSSFRow rowPeriod = sheet.getRow(PERIOD_ROW_INDEX);
                        if (rowPeriod!=null){
                            HSSFCell cell = rowPeriod.getCell(0);
                            if (cell != null){
                                period = cell.getRichStringCellValue().toString();
                            }
                        }

                        //getting table header
                        tableHeader = "";
                        int firstRow = 6;
                        int lastRow = 7;

                        for (int j = firstRow; j <= lastRow; j++) {
                            HSSFRow row = sheet.getRow(j);
                            if (row != null){
                                int firstCell = row.getFirstCellNum();
                                int lastCell = row.getLastCellNum();
                                for (int k = firstCell; k < lastCell; ++k) {
                                    HSSFCell cell = row.getCell(k);
                                    if (cell != null){
                                        tableHeader += cell.getRichStringCellValue() + "\t\t";
                                    }
                                }
                            }
                            tableHeader += "\n\t";
                        }

                        //Add excelFile with initialisation info(bankName, balance sheet, ..etc) to DB

                        try(Statement statement = connection.createStatement()){
                            statement.executeUpdate("INSERT INTO ey_task2.excel_files (file_name, bank_name, balance_sheet, period, table_header) \n" + " VALUES (\"" + file.getName() + "\", \"" + bank + "\", \"" + balance + "\", \"" + period + "\", \"" + tableHeader + "\");");
                        } catch (SQLException e){
                            e.printStackTrace();
                        }


                    }
                }
                for (int i = 0; i < wb.getNumberOfSheets(); ++i) {
                    HSSFSheet sheet = wb.getSheetAt(i);
                    if (sheet != null) {
                        int firstRow = LAST_INITIALISATION_ROW + 1;
                        int lastRow = sheet.getLastRowNum();
                        for (int j = firstRow; j <= lastRow; ++j) {
                            // inserting a new row of data to db if it doesn't contain class name
                            if ((j > firstRow) && !isClassRow){
                                try(Statement statement = connection.createStatement()){
                                    statement.executeUpdate("INSERT INTO ey_task2.classes_data (bank_account_id, open_balance_active, open_balance_passive, circulate_debit, circulate_credit, close_balance_active, close_balance_passive, class_name, file_name) \n" + " VALUES (\"" + bankAccId + "\", " + openingBalanceActive + ", " + openingBalancePassive + ", " + circulationDebit + ", " + circulationCredit + ", " + closingBalanceActive + ", " + closingBalancePassive + ", \"" + className  + "\", \"" + file.getName() +"\");");
                                } catch (SQLException e){
                                    e.printStackTrace();
                                }
                            //inserting new row with class name to db
                            } else if(j > firstRow){
                                try(Statement statement = connection.createStatement()){
                                    statement.executeUpdate("INSERT INTO ey_task2.classes (class_name, file_name) \n" + " VALUES (\"" + className + "\", \"" + file.getName() +"\");");
                                } catch (SQLException e){
                                    e.printStackTrace();
                                }
                            }
                            isClassRow = false;
                            HSSFRow row = sheet.getRow(j);
                            if (row != null) {
                                int firstCell = row.getFirstCellNum();
                                int lastCell = row.getLastCellNum();

                                for (int k = firstCell; k < lastCell; ++k) {
                                    HSSFCell cell = row.getCell(k);
                                    if (cell != null) {
                                        switch (cell.getCellType()) {
                                            case STRING:
                                                bankAccId = cell.getRichStringCellValue().getString();
                                                if ((bankAccId.contains("КЛАСС")) && (!bankAccId.contains("ПО КЛАССУ"))){
                                                    className = bankAccId;
                                                    isClassRow = true;
                                                }
                                                break;
                                            case NUMERIC:
                                                switch (k){
                                                        case 0:
                                                            bankAccountId = cell.getNumericCellValue();
                                                            bankAccId = ""+bankAccountId;
                                                        case 1:
                                                            openingBalanceActive = cell.getNumericCellValue();
                                                            break;
                                                        case 2:
                                                            openingBalancePassive = cell.getNumericCellValue();
                                                            break;
                                                        case 3:
                                                            circulationDebit = cell.getNumericCellValue();
                                                            break;
                                                        case 4:
                                                            circulationCredit = cell.getNumericCellValue();
                                                            break;
                                                        case 5:
                                                            closingBalanceActive = cell.getNumericCellValue();
                                                            break;
                                                        case 6:
                                                            closingBalancePassive = cell.getNumericCellValue();
                                                            break;
                                                    }
                                                    break;
                                            default:
                                                throw new RuntimeException("Unexpected cell type (" + cell.getCellType() + ")");
                                        }

                                    }
                                }
                            }
                        }

                    }
                }
            } catch (SQLException e){
                e.printStackTrace();
            }
            wb.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

}
