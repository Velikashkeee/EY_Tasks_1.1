package com.bsuir.task1;


import java.io.*;
import java.sql.*;


public class Database {
    //Databases URL, login and password for connection
    private static final String URL =  "jdbc:mysql://localhost:3306/ey_task1_textfiles"+
                                        "?verifyServerCertificate=false"+
                                        "&useSSL=false"+
                                        "&allowPublicKeyRetrieval=true"+
                                        "&requireSSL=false"+
                                        "&useLegacyDatetimeCode=false"+
                                        "&amp"+
                                        "&serverTimezone=UTC";

    private static final String USER = "root";
    private static final String PASSWORD = "root";

    //static class variables for connection to db

    private static Connection connection;
    private static Statement statement1;
    private static Statement statement2;
    private static ResultSet resultSet;
    private static ResultSet resultSetForCount;

    //global variables for import data to the database

    private static String date = null;
    private static String eng = null;
    private static String rus = null;
    private static int number = 0;
    private static double realNumber = 0;

    //variables for check current amount of imported and remaining lines

    private static int current = 0;
    private static int remain = 0;

    public static void importFiles(String filesPath, int filesCount) throws IOException {

        current = 0;
        remain = filesCount* FileGenerator.LINES_COUNT - FileEtc.getDelCount();

        String line = null;
        String[] parsedData;

        //Get connection with the Database

        try {
            connection = DriverManager.getConnection(URL,USER,PASSWORD);
            for (int i = 0; i < filesCount; i++) {
                File file = new File (filesPath+"file"+i+".txt");

                FileInputStream fileInputStream = new FileInputStream(file);
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                // importing files to DB

                try {
                    statement1 = connection.createStatement();
                    statement1.executeUpdate("INSERT INTO ey_task1_textfiles.files (name) \n" + " VALUES (\"" + file.getName() + "\");");

                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    try{statement1.close();} catch (SQLException e){}
                }

                while((line = bufferedReader.readLine()) != null){

                    // Parsing date

                    parsedData = line.split("\\|\\|");

                    date = parsedData[0];
                    eng = parsedData[1];
                    rus = parsedData[2];
                    number = Integer.parseInt(parsedData[3]);
                    realNumber =  Double.parseDouble(parsedData[4].replace(",","."));

                    //importing rows to DB

                    try {
                        statement2 = connection.createStatement();
                        statement2.executeUpdate("INSERT INTO ey_task1_textfiles.files_content (file_name, date, eng, rus, int_number, real_number) \n" + " VALUES (\"" + file.getName() + "\", \""+date+"\", \"" + eng + "\", \"" + rus + "\", " + number +", " + realNumber + ");");
                        current++;
                        remain--;
                        System.out.println("Imported lines: " + current + "  Lines left: "+ remain);
                    } catch (SQLException e){
                        e.printStackTrace();
                    }
                    finally {
                        try{statement2.close();} catch (SQLException e){}
                    }

                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try{connection.close();} catch (SQLException e){}
        }

    }

    //get sum of all integer numbers in DB

    public static long receiveNumberSum() {
        long result = 0;
        try {
            connection = DriverManager.getConnection(URL,USER,PASSWORD);
            statement1 = connection.createStatement();
            resultSet=statement1.executeQuery("SELECT int_number FROM  files_content");
            while (resultSet.next()){
                result+=resultSet.getInt("int_number");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally{
            try {connection.close();} catch (SQLException e){}
            try {statement1.close();} catch (SQLException e){}
            try {resultSet.close();} catch (SQLException e){}
        }

        return result;

    }

    //get median of all double numbers in DB

    public static double receiveMedian() {
        double result = 0;
        int rowCount = 0;
        try {
            connection = DriverManager.getConnection(URL,USER,PASSWORD);
            statement1 = connection.createStatement();
            statement2 = connection.createStatement();
            resultSet=statement1.executeQuery("SELECT real_number FROM  files_content ORDER BY real_number");
            resultSetForCount = statement2.executeQuery("SELECT COUNT(real_number) FROM files_content");

            while (resultSetForCount.next()){
                rowCount = resultSetForCount.getInt(1);
            }

            if ((rowCount != 0) && (rowCount % 2 != 0)){
                while (resultSet.next()) {
                    if(resultSet.absolute(rowCount / 2))
                        result = resultSet.getDouble("real_number");
                }
            } else if ((rowCount != 0) && (rowCount % 2 == 0)){
                 if (resultSet.next()) {
                    if (resultSet.absolute(rowCount / 2)) {
                        result += resultSet.getDouble("real_number");
                    }
                    if (resultSet.absolute(rowCount / 2 + 1))
                        result += resultSet.getDouble("real_number");
                }
                result = result/2;
            }
            else{
                result = 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        //closing connection

        finally{
            try {connection.close();} catch (SQLException e){}
            try {statement1.close();} catch (SQLException e){}
            try {resultSet.close();} catch (SQLException e){}
            try {statement2.close();} catch (SQLException e){}
            try {resultSetForCount.close();} catch (SQLException e){}
        }

        return result;

    }
}
