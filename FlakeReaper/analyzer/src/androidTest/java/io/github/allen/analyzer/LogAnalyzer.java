package io.github.allen.analyzer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LogAnalyzer {

    private String logPath;

    public LogAnalyzer(String logPath) {
        this.logPath = logPath;
    }

    /**
     * 解析日志生成LogMessage集合
     *
     * @return
     */
    public List<LogMessage> parseLog() {

        List<LogMessage> logMessages = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(logPath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    String threadName = parts[0].trim();
                    long timestamp = Long.parseLong(parts[1].trim());
                    String method = parts[2].trim();
                    int param = Integer.parseInt(parts[3].trim());
                    String extraParam = parts[4].trim();

                    LogMessage logMessage = new LogMessage(threadName, timestamp, method, param, extraParam);
                    logMessages.add(logMessage);
                } else {
//                    System.out.println("Invalid entry format: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.out.println("Error parsing number: " + e.getMessage());
        }

        return logMessages;
    }

    /**
     * 根据LogMessage集合生成EventLifecycle集合
     * 主要就是进行EventLifecycle类中属性配对，关键配对属性：sid、各属性中的param(msgHash)，threadName
     *
     * @param logMessages
     * @return
     */
    public List<EventLifecycle> buildEventLifecycle(List<LogMessage> logMessages) {
        // 先按时间排序
        logMessages = logMessages.stream()
                .sorted(Comparator.comparing(LogMessage::getTimestamp))
                .collect(Collectors.toList());

        // msgHash - LogMsg，可利用msgHash快速定位enqueueMsgLogMsg
        Map<Integer, LogMessage> enqueueMsgLogMap = logMessages.stream()
                .filter(logMessage -> AnalyzerUtils.isNumeric(logMessage.getExtraParam())) // 数字就是enqueueMsg
                .collect(Collectors.toMap(LogMessage::getParam, Function.identity()));

        // 筛选出enqueueMessage的log，初始化EventLifecycles
        List<EventLifecycle> eventLifecycles = logMessages.stream()
                .filter(logMessage -> AnalyzerUtils.isNumeric(logMessage.getExtraParam()))
                .map(EventLifecycle::new)
                .collect(Collectors.toList());

        // sid - EventLifecycle，可利用sid快速定位EventLifecycle
        Map<Long, EventLifecycle> eventLifecycleMap = new HashMap<>();
        eventLifecycles.forEach(e -> {
            EventLifecycle eventLifecycle = eventLifecycleMap.get(e.getSid());
            if (Objects.isNull(eventLifecycle)) {
                eventLifecycleMap.put(e.getSid(), e);
            }
        });

        // msgHash - EventLifecycle，可利用msgHash快速定位EventLifecycle
        Map<Integer, EventLifecycle> dispatchMsgBeforeMap = new HashMap<>();


        for (LogMessage logMessage : logMessages) {
            switch (logMessage.getExtraParam()) {

                case "dispatchMessage beforeHookedMethod":
                    // enqueueMessageQueue出队，也就是enqueueMessage与dispatch before匹配
                    // 根据msgHash获取到对应的enqueueMessage，再用enqueueMessage的sid获取到ventLifecycle
                    LogMessage enqueueMessage = enqueueMsgLogMap.get(logMessage.getParam());
                    if (Objects.isNull(enqueueMessage)) break;

                    EventLifecycle originalEventLifecycle = eventLifecycleMap.get(Long.parseLong(enqueueMessage.getExtraParam()));
                    originalEventLifecycle.setBeforeDispatchMessage(logMessage);
                    // 存储msgHash和eventLifecycle，方便后续dispatchMessage after获取
                    dispatchMsgBeforeMap.put(logMessage.getParam(), originalEventLifecycle);
                    break;
                case "dispatchMessage afterHookedMethod": // 6. in
                    // dispatchMessageStack出栈，也就是dispatchMessage after与也就是dispatchMessage before匹配
                    originalEventLifecycle = dispatchMsgBeforeMap.get(logMessage.getParam());
                    if (Objects.isNull(originalEventLifecycle)) break;
                    originalEventLifecycle.setAfterDispatchMessage(logMessage);
                    break;
            }
        }
        // 过滤无用数据
        eventLifecycles = eventLifecycles.stream()
                .filter(e -> Objects.nonNull(e.getBeforeDispatchMessage()))
                .filter(e -> Objects.nonNull(e.getAfterDispatchMessage()))
                .collect(Collectors.toList());

        for (LogMessage logMessage : logMessages) {
            switch (logMessage.getExtraParam()) {
                case "get":
                    // timestamp在dispatch before和after之间，且thread_name等于eventLifecycle的dispatch 线程名
                    // 就将这个get加入到eventLifecycle中
                    for (EventLifecycle eventLifecycle : eventLifecycles) {
                        if (logMessage.getTimestamp() >= eventLifecycle.getBeforeDispatchMessage().getTimestamp()
                                && logMessage.getTimestamp() <= eventLifecycle.getAfterDispatchMessage().getTimestamp()
                                && logMessage.getThreadName().equals(eventLifecycle.getBeforeDispatchMessage().getThreadName())) {
                            eventLifecycle.addGetUiOperationList(logMessage);
                            break;
                        }
                    }
                    break;

                case "set":
                    // 同上
                    for (EventLifecycle eventLifecycle : eventLifecycles) {
                        if (logMessage.getTimestamp() >= eventLifecycle.getBeforeDispatchMessage().getTimestamp()
                                && logMessage.getTimestamp() <= eventLifecycle.getAfterDispatchMessage().getTimestamp()
                                && logMessage.getThreadName().equals(eventLifecycle.getBeforeDispatchMessage().getThreadName())) {
                            eventLifecycle.addSetUiOperationList(logMessage);
                            break;
                        }
                    }
                    break;
            }
        }
        return eventLifecycles;
    }

    /**
     * 进行EventLifecycle之间的冲突判定，返回冲突的set相关的eventLifecycle的sid
     *
     * @return
     */
    public List<String> judgeCollision(List<EventLifecycle> eventLifecycles) {
        List<EventLifecycle> collisionEventLifecycles = new ArrayList<>();
        for (int i = 0; i < eventLifecycles.size() - 1; i++) {
            EventLifecycle frontEventLifecycle = eventLifecycles.get(i);
            for (int j = i + 1; j < eventLifecycles.size(); j++) {
                EventLifecycle rearEventLifecycle = eventLifecycles.get(j);
                // 判断front和rear的get/set是否存在冲突，setUiMsgs为冲突的set方法
                List<LogMessage> setUiMsgs =
                        judgeCollision(frontEventLifecycle, rearEventLifecycle);
                if (setUiMsgs.size() != 0) {
                    // 存在冲突
                    collisionEventLifecycles.add(frontEventLifecycle);
                }
                setUiMsgs =
                        judgeCollision(rearEventLifecycle, frontEventLifecycle);
                if (setUiMsgs.size() != 0) {
                    // 存在冲突
                    collisionEventLifecycles.add(rearEventLifecycle);
                }
            }
        }

        // 同一个线程的enqueueMsgs不会发生冲突
        return collisionEventLifecycles.stream()
                .map(EventLifecycle::getSid)
                .map(String::valueOf)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 根据get、set msg List，判断冲突，冲突则将set msg放入响应集合中
     * 判断冲突：相同mId
     *
     * @return
     */
    private List<LogMessage> judgeCollision(EventLifecycle eventSet, EventLifecycle eventGet) {
        List<LogMessage> setUiOperationList = eventSet.getSetUiOperationList();
        List<LogMessage> getUiOperationList = eventGet.getGetUiOperationList();
        List<LogMessage> collisionList = new ArrayList<>();
        setUiOperationList.forEach(setUiOperation -> {
            getUiOperationList.forEach(getUiOperation -> {
                if (setUiOperation.getParam() != -1
//                        && setUiOperation.getParam() != 0 // 不考虑thisObj为null
                        && setUiOperation.getParam() == getUiOperation.getParam()) {
                    collisionList.add(setUiOperation);
//                    System.out.println(
//                            "collision happen，eventSet sid：" + eventSet.getSid() + "\n"
//                            + "eventGet sid:" + eventGet.getSid() + "\n"
//                            + "eventSet method msg:" + setUiOperation + "\n"
//                            + "eventGet method msg:" + getUiOperation + "\n" + "------"
//                    );
                }
            });
        });
        return collisionList;
    }


    public void output(List<String> sids, String logPath) {
        try {
            File logFile = new File(logPath + "-static_id_list.txt");
            BufferedWriter bw;
            bw = new BufferedWriter(new FileWriter(logFile));
            sids.forEach(sid -> {
                try {
                    bw.write(sid);
                    bw.newLine(); // 这将添加一个新行
                } catch (IOException e) {
                }
            });
            bw.close();
        } catch (IOException e) {
        }
    }

}
