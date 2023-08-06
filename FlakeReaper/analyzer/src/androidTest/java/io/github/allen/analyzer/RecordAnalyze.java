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
        String totalPath = "/yyy/xxx/";
        calculateAllAverageResult(totalPath);
    }

    private static void calculateAllAverageResult(String directoryPath) {
        File directory = new File(directoryPath);

        if (directory.exists() && directory.isDirectory()) {
            File[] subDirectories = directory.listFiles(File::isDirectory);
            if (subDirectories != null) {
                for (File subDirectory : subDirectories) {
                    calculateAppAverageResult(subDirectory.getAbsolutePath());
                }
            }
        } else {
            System.out.println(directoryPath + "dir not exit or invalid");
        }
    }


    private static void calculateAppAverageResult(String directoryPath) {
        File directory = new File(directoryPath);

        if (directory.exists() && directory.isDirectory()) {
            File[] subDirectories = directory.listFiles(File::isDirectory);
            if (subDirectories != null) {
                for (File subDirectory : subDirectories) {
                    calculateTestAverageResult(subDirectory.getAbsolutePath());
                }
            }
        } else {
            System.out.println(directoryPath + "dir not exit or invalid");
        }
    }

    private static void calculateTestAverageResult(String directoryPath) {
        File directory = new File(directoryPath);

        if (directory.exists() && directory.isDirectory()) {
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
            System.err.println(directoryPath + "dir not exit or invalid");
        }
    }

    private static Result generate(String path) {
        LogAnalyzer analyzer = new LogAnalyzer(path);
        List<LogMessage> logMessages = analyzer.parseLog();
        List<EventLifecycle> eventLifecycles = analyzer.buildEventLifecycle(logMessages);
        List<String> sids = analyzer.judgeCollision(eventLifecycles);
        analyzer.output(sids, path);
        return new Result(eventLifecycles.size(), sids.size());
    }


    private static String calculateAverageTime(String filePath) {
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

        double average = 0;
        if (!timeValues.isEmpty()) {
            double sum = 0;
            for (double value : timeValues) {
                sum += value;
            }
            average = sum / timeValues.size();
        } else {
//            System.out.println("error");
        }

        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return decimalFormat.format(average);
    }

}