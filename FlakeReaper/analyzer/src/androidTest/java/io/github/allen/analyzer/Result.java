package io.github.allen.analyzer;

public class Result {

    /**
     * EventLifecycle数量
     */
    private double eventNum;

    /**
     * static_id数量
     */
    private double eventRaceNum;

    /**
     * Test耗时
     */
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
        System.out.println(path + ":");
        System.out.println("event race pair数量（平均):" + this.eventRaceNum);
        System.out.println("总event数量(平均):" + this.eventNum);
        System.out.println("平均时间:" + this.time);
        System.out.println("--------");
    }
}
