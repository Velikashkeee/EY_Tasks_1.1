package com.bsuir.task2;

import org.apache.poi.hssf.extractor.ExcelExtractor;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


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

    public static void ExceltoSQL(String path){
        //isClassRow shows whether excel row contains class name or not
        boolean isClassRow = false;



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

                        //Add excelFile with initialisation info(bankName, balance sheet, ..etc) to DB

                        try(Statement statement = connection.createStatement()){
                            statement.executeUpdate("INSERT INTO ey_task2.excel_files (file_name, bank_name, balance_sheet, period) \n" + " VALUES (\"" + file.getName() + "\", \"" + bank + "\", \"" + balance + "\", \"" + period + "\");");
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
