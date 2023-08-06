package io.github.flakreaper.analyzer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class AnalyzerUtils {

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    public static void output(Result result, String path) {
        try {
            File logFile = new File(path + "/event_data.txt");
            BufferedWriter bw;
            bw = new BufferedWriter(new FileWriter(logFile));
            try {
                bw.write("event race pair数量（平均)：" + result.getEventRaceNum());
                bw.newLine(); // 这将添加一个新行
                bw.write("总event数量（平均)：" + result.getEventNum());
                bw.newLine();
                bw.write("平均时间：" + result.getTime());
                bw.newLine();
            } catch (IOException e) {
            }
            bw.close();
        } catch (IOException e) {
        }
    }

}
