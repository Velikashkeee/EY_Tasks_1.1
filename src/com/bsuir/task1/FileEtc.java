package com.bsuir.task1;

import java.io.*;

public class FileEtc {

    //variable is showing how much rows with given delete sequence of chars have been found

    private static int delCount = 0;

    public static void combineFiles(String filesPath,int filesCount, String fileName, String delStr) throws IOException {
        //creating file that consists of other files in /com/bsuir/files
        File combinedFile = new File(filesPath+fileName+".txt");
        FileWriter writer = new FileWriter(combinedFile,false);
        String line;
        for (int i = 0; i < filesCount; i++) {
            File file = new File (filesPath+"file"+i+".txt");

            //deleting strings that contain delete symbol sequence
            deleteStringFromFile(file,delStr);

            FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);


            while((line = bufferedReader.readLine()) != null){
                    writer.write(line + System.lineSeparator());
            }
        }
        writer.close();
    }

    private static void deleteStringFromFile(File file, String deleteString) throws FileNotFoundException {
        String string = null;
        StringBuilder stringBuilder = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))){

            while ((string = reader.readLine()) != null){
                if (!string.contains(deleteString)){
                    stringBuilder.append(string).append("\n");
                }
                else{
                    delCount++;
                }
            }

            string = stringBuilder.toString();

        } catch (IOException e){
            e.printStackTrace();
        }

        char[] buffer = new char[string.length()];
        string.getChars(0,string.length(), buffer, 0);

        try (FileWriter writer = new FileWriter(file,false)){
            writer.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static int getDelCount() {
        return delCount;
    }
}
