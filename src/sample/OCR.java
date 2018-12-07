package sample;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class OCR {

    private static String binaryPath;

    public static void getBinaryPath() {
        File file = new File("tesseractbin.txt");
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String st;
            while ((st = br.readLine()) != null) {
                if (!st.trim().equals("")) {
                    binaryPath = st;
                    return;
                }
            }
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static String getTotalReceiptPrice(String imagePath) {
        if (binaryPath == null) {
            getBinaryPath();
        }

        String outputFile = "./txt/latestImage";
        String total = "";

        try {
            Process tess = Runtime.getRuntime().exec(new String[] { binaryPath, imagePath, outputFile });
            while (tess.isAlive()) {
                continue;
            }

            BufferedReader br = new BufferedReader(new FileReader(outputFile + ".txt"));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("Total")) {
                    total = line.toLowerCase().replace("total","").replace("$","").trim();
                }
            }
        } catch (IOException e) {
            //e.printStackTrace();
        }
        return total;
    }

}
