package io.github.allen.analyzer;

public class Result {

    private double eventNum;

    private double eventRaceNum;

    private String time;

    public double getEventNum() {
        return eventNum;
    }

    public double getEventRaceNum() {
        return eventRaceNum;
    }

    public void setEventNum(double eventNum) {
        this.eventNum = eventNum;
    }

    public void setEventRaceNum(double eventRaceNum) {
        this.eventRaceNum = eventRaceNum;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public Result(double eventNum, double eventRaceNum, String time) {
        this.eventNum = eventNum;
        this.eventRaceNum = eventRaceNum;
        this.time = time;
    }

    public Result(double eventNum, double eventRaceNum) {
        this.eventNum = eventNum;
        this.eventRaceNum = eventRaceNum;
    }

    public Result() {
    }

    public void print(String path) {

    }
}
