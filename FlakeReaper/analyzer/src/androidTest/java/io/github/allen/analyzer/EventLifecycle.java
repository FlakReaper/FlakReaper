package io.github.allen.analyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EventLifecycle {

    private long sid;

    private LogMessage beforeEnqueueMessage;

    private LogMessage afterEnqueueMessage;

    private LogMessage beforeDispatchMessage;

    private LogMessage afterDispatchMessage;

    private List<LogMessage> getUiOperationList = new ArrayList<>();

    private List<LogMessage> setUiOperationList = new ArrayList<>();

    public EventLifecycle() {
    }

    public EventLifecycle(LogMessage beforeEnqueueMessage) {
        this.beforeEnqueueMessage = beforeEnqueueMessage;
        this.sid = Long.parseLong(beforeEnqueueMessage.getExtraParam());
    }

    public EventLifecycle(LogMessage beforeEnqueueMessage, LogMessage afterEnqueueMessage, LogMessage beforeDispatchMessage, LogMessage afterDispatchMessage, List<LogMessage> getUiOperationList, List<LogMessage> setUiOperationList, int sid, String threadName) {
        this.beforeEnqueueMessage = beforeEnqueueMessage;
        this.afterEnqueueMessage = afterEnqueueMessage;
        this.beforeDispatchMessage = beforeDispatchMessage;
        this.afterDispatchMessage = afterDispatchMessage;
        this.getUiOperationList = getUiOperationList;
        this.setUiOperationList = setUiOperationList;
        this.sid = sid;
    }

    public LogMessage getBeforeEnqueueMessage() {
        return beforeEnqueueMessage;
    }

    public void setBeforeEnqueueMessage(LogMessage beforeEnqueueMessage) {
        this.beforeEnqueueMessage = beforeEnqueueMessage;
    }

    public LogMessage getAfterEnqueueMessage() {
        return afterEnqueueMessage;
    }

    public void setAfterEnqueueMessage(LogMessage afterEnqueueMessage) {
        this.afterEnqueueMessage = afterEnqueueMessage;
        this.sid = Long.parseLong(afterEnqueueMessage.getExtraParam());
    }

    public LogMessage getBeforeDispatchMessage() {
        return beforeDispatchMessage;
    }

    public void setBeforeDispatchMessage(LogMessage beforeDispatchMessage) {
        this.beforeDispatchMessage = beforeDispatchMessage;
    }

    public LogMessage getAfterDispatchMessage() {
        return afterDispatchMessage;
    }

    public void setAfterDispatchMessage(LogMessage afterDispatchMessage) {
        this.afterDispatchMessage = afterDispatchMessage;
    }

    public List<LogMessage> getGetUiOperationList() {
        return getUiOperationList;
    }

    public void addGetUiOperationList(LogMessage logMessage) {
        this.getUiOperationList.add(logMessage);
    }

    public List<LogMessage> getSetUiOperationList() {
        return setUiOperationList;
    }

    public void addSetUiOperationList(LogMessage logMessage) {
        this.setUiOperationList.add(logMessage);
    }

    public void setGetUiOperationList(List<LogMessage> getUiOperationList) {
        this.getUiOperationList = getUiOperationList;
    }

    public void setSetUiOperationList(List<LogMessage> setUiOperationList) {
        this.setUiOperationList = setUiOperationList;
    }

    public long getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventLifecycle that = (EventLifecycle) o;
        return sid == that.sid && Objects.equals(beforeEnqueueMessage, that.beforeEnqueueMessage) && afterEnqueueMessage.equals(that.afterEnqueueMessage) && beforeDispatchMessage.equals(that.beforeDispatchMessage) && afterDispatchMessage.equals(that.afterDispatchMessage) && getUiOperationList.equals(that.getUiOperationList) && setUiOperationList.equals(that.setUiOperationList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sid, beforeEnqueueMessage, afterEnqueueMessage, beforeDispatchMessage, afterDispatchMessage, getUiOperationList, setUiOperationList);
    }

    @Override
    public String toString() {
        return "EventLifecycle{" +
                "sid=" + sid +
                ", beforeEnqueueMessage=" + beforeEnqueueMessage +
                ", afterEnqueueMessage=" + afterEnqueueMessage +
                ", beforeDispatchMessage=" + beforeDispatchMessage +
                ", afterDispatchMessage=" + afterDispatchMessage +
                ", getUiOperationList=" + getUiOperationList +
                ", setUiOperationList=" + setUiOperationList +
                '}';
    }

}
