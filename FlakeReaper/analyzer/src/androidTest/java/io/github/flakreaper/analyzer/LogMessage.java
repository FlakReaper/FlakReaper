package io.github.flakreaper.analyzer;

public class LogMessage {
    private String threadName;

    private long timestamp;

    private String method;

    private int param;


    private String extraParam;

    public LogMessage(String threadName, long timestamp, String method, int param, String extraParam) {
        this.threadName = threadName;
        this.timestamp = timestamp;
        this.method = method;
        this.param = param;
        this.extraParam = extraParam;
    }

    public LogMessage() {
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public int getParam() {
        return param;
    }

    public void setParam(int param) {
        this.param = param;
    }

    public String getExtraParam() {
        return extraParam;
    }

    public void setExtraParam(String extraParam) {
        this.extraParam = extraParam;
    }

    @Override
    public String toString() {
        return "LogMessage{" +
                "threadName='" + threadName + '\'' +
                ", timestamp=" + timestamp +
                ", method='" + method + '\'' +
                ", param=" + param +
                ", extraParam='" + extraParam + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LogMessage that = (LogMessage) o;

        if (timestamp != that.timestamp) return false;
        if (param != that.param) return false;
        if (!threadName.equals(that.threadName)) return false;
        if (!method.equals(that.method)) return false;
        return extraParam.equals(that.extraParam);
    }

    @Override
    public int hashCode() {
        int result = threadName.hashCode();
        result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
        result = 31 * result + method.hashCode();
        result = 31 * result + param;
        result = 31 * result + extraParam.hashCode();
        return result;
    }
}
