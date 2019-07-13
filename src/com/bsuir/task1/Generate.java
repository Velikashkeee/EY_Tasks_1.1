package com.bsuir.task1;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Generate {

    private final static String LATIN_DICT = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final static String RUS_DICT = "абвгдеёжзийклмнопрстуфхцчшщъыьэюяАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ";
    private final static long FIVE_YEARS = 157680000000L;
    private final static double BOTTOM_BORDER = 1;
    private final static double TOP_BORDER = 20;
    private final static String SEPARATOR = "||";


    public static String GenerateAll(){
        int evenNumber = rnd(1,100000000);
        while(evenNumber % 2 != 0)
            evenNumber = rnd(1,100000000);
        return generateDate(FIVE_YEARS) + SEPARATOR +
                        generateRandomCharacters(10,LATIN_DICT) + SEPARATOR +
                        generateRandomCharacters(10, RUS_DICT) + SEPARATOR +
                        evenNumber + SEPARATOR +
                        generateDouble(BOTTOM_BORDER,TOP_BORDER - 1) + SEPARATOR;  //TOP_BORDER-1 to get random number in [1..20) range

    }

    //get random long number

    private static long rnd(long min, long max)
    {
        max -= min;
        return (long)(Math.random() * ++max) + min;
    }

    //get random int number

    private static int rnd(int min, int max)
    {
        max -= min;
        return (int)(Math.random() * ++max) + min;
    }

    //get random formatted double number

    private static String generateDouble(double min, double max)
    {
        max -= min;

        double result = (Math.random() * ++max) + min;

        return new DecimalFormat("#00.00000000").format(result);
    }

    //generating random date in range [current time-year, current time]

    private static String generateDate(long year){
        long beginPoint = System.currentTimeMillis() - year;
        long endPoint = System.currentTimeMillis();
        long randomMillis = rnd(beginPoint,endPoint);
        Calendar randomDate = new GregorianCalendar();
        randomDate.setTimeInMillis(randomMillis);
        int tempMonth = randomDate.get(Calendar.MONTH)+1;
        int tempDay = randomDate.get(Calendar.DAY_OF_MONTH);

        //additional zeroes to make date "03.05.2019" instead of "3.5.2019"

        String addZeroMonth = "";
        String addZeroDay = "";
        if (tempMonth-10<0)
            addZeroMonth = "0";
        if (tempDay-10<0)
            addZeroDay = "0";
        return addZeroDay + randomDate.get(Calendar.DAY_OF_MONTH) +
                "." + addZeroMonth + tempMonth +
                "." + randomDate.get(Calendar.YEAR);
    }

    //generating random latin and russian characters

    private static String generateRandomCharacters(int amount, String dict){
        char randomChar;

        String randomString = "";
        for (int i = 0; i < amount; i++) {
            int randomTemp = rnd(0,dict.length()-1);
            randomChar = dict.charAt(randomTemp);
            randomString+=randomChar;
        }

        return randomString;
    }

}
