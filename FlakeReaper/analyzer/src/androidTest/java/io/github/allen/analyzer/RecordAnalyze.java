package io.github.allen.analyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RecordAnalyze {
    public static void main(String[] args) throws Exception{
        String totalPath = "/Users/wangyongjiang/Desktop/addition/";
        calculateAllAverageResult(totalPath);
    }

    /**
     * 全统计
     */
    private static void calculateAllAverageResult(String directoryPath) {
//        String directoryPath = "/Users/wangyongjiang/Desktop/runTest/";
        File directory = new File(directoryPath);

        if (directory.exists() && directory.isDirectory()) {
            File[] subDirectories = directory.listFiles(File::isDirectory);
            if (subDirectories != null) {
                for (File subDirectory : subDirectories) {
                    calculateAppAverageResult(subDirectory.getAbsolutePath());
                }
            }
        } else {
            System.out.println(directoryPath + "目录不存在或不是一个有效的目录。");
        }
    }

    /**
     * APP级统计
     */
    private static void calculateAppAverageResult(String directoryPath) {
//        String directoryPath = "/Users/wangyongjiang/Desktop/runTest/espresso/";
        File directory = new File(directoryPath);

        if (directory.exists() && directory.isDirectory()) {
            File[] subDirectories = directory.listFiles(File::isDirectory);
            if (subDirectories != null) {
                for (File subDirectory : subDirectories) {
                    calculateTestAverageResult(subDirectory.getAbsolutePath());
                }
            }
        } else {
            System.out.println(directoryPath + "目录不存在或不是一个有效的目录。");
        }
    }

    /**
     * Test级统计
     * @param directoryPath
     */
    private static void calculateTestAverageResult(String directoryPath) {
//        String directoryPath = "/Users/wangyongjiang/Desktop/runTest/espresso/espresso.changeTheme"; // 指定目录路径
        File directory = new File(directoryPath);

        if (directory.exists() && directory.isDirectory()) {
            // 获取所有日志文件.txt
            File[] files = directory.listFiles((dir, name) -> Pattern.matches("\\d+\\.txt", name));
            if (files != null && files.length != 0) {
                Result averageResult = new Result(0, 0, "0");
                for (File file : files) {
                    Result result = generate(file.getAbsolutePath());
                    averageResult.setEventNum(averageResult.getEventNum() + result.getEventNum());
                    averageResult.setEventRaceNum(averageResult.getEventRaceNum() + result.getEventRaceNum());
                }
                int num = files.length;
                averageResult.setEventNum(averageResult.getEventNum() / num);
                averageResult.setEventRaceNum(averageResult.getEventRaceNum() / num);

                files = directory.listFiles((dir, name) -> Pattern.matches("output.log", name));
                if (files != null && files.length != 0) {
                    String averageTime = calculateAverageTime(files[0].getAbsolutePath());
                    averageResult.setTime(averageTime);
                }
                averageResult.print(directoryPath);
                AnalyzerUtils.output(averageResult, directoryPath);
            }
        } else {
            System.err.println(directoryPath + "目录不存在或不是一个有效的目录。");
        }
    }

    private static Result generate(String path) {
        LogAnalyzer analyzer = new LogAnalyzer(path);
        // 解析日志生成LogMessage集合
        List<LogMessage> logMessages = analyzer.parseLog();
        // 根据LogMessage集合生成EventLifecycle集合
        List<EventLifecycle> eventLifecycles = analyzer.buildEventLifecycle(logMessages);
        // 进行EventLifecycle之间的冲突判定，返回冲突的set相关的eventLifecycle的sid
        List<String> sids = analyzer.judgeCollision(eventLifecycles);
        // 输出sid
        analyzer.output(sids, path);
        return new Result(eventLifecycles.size(), sids.size());
    }

    /**
     * 根据output.log计算平均时间
     * @param filePath
     * @return
     */
    private static String calculateAverageTime(String filePath) {
//        String filePath = "/path/to/text.txt"; // 文本文件路径
        List<Double> timeValues = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            Pattern pattern = Pattern.compile("Time: (\\d+\\.\\d+)");
            while ((line = br.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    double time = Double.parseDouble(matcher.group(1));
                    timeValues.add(time);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 计算平均值
        double average = 0;
        if (!timeValues.isEmpty()) {
            double sum = 0;
            for (double value : timeValues) {
                sum += value;
            }
            average = sum / timeValues.size();
//            System.out.println("平均值：" + average);
        } else {
//            System.out.println("没有找到符合条件的时间值。");
        }

        // 保留两位小数
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return decimalFormat.format(average);
    }

}