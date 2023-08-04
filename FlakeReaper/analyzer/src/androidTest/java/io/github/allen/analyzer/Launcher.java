package io.github.allen.analyzer;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;
import java.util.Stack;

import io.github.allen.analyzer.stackmatcher.TraceMatcher;
import io.github.allen.analyzer.stackmatcher.analyzer.ParseWorker;
import io.github.allen.analyzer.stackmatcher.common.FileSystem;
import io.github.allen.analyzer.stackmatcher.core.AnalyzerResult;
import io.github.allen.analyzer.stackmatcher.core.ProfileData;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class Launcher {

    @Test
    public void matchStack() {
        File runTimeTraceFile = new File(Environment.getExternalStorageDirectory(), "espresso_success_2228.trace");
        File exceptionStackFile = new File(Environment.getExternalStorageDirectory(), "exceptionstack.txt");

        ParseWorker parseWorker = new ParseWorker(runTimeTraceFile, new FileSystem());
        AnalyzerResult result = parseWorker.generateWorkerResult().traceContainer.getResult();
        List<Stack<String>> exceptionStackList = parseWorker.parseExceptionStack(exceptionStackFile);
        List<ProfileData> mainProfileDatas = result.getData().get(result.getMainThreadId());
//        List<ProfileData> testProfileDatas = result.getData().get(28317);
        ProfileData matchedNode = new TraceMatcher().match(mainProfileDatas, exceptionStackList.get(1));
        Log.i("matchStack", "startTime: " + matchedNode.getGlobalStartTimeInMillisecond());
        Log.i("matchStack", "endTime: " + matchedNode.getGlobalEndTimeInMillisecond());
        //        mainProfileDatas.forEach(p -> {
//
//        });
    }

    @Test
    public void generateSidsByLog() {
        String path = Environment.getExternalStorageDirectory() + "/103178305860172.txt";
        LogAnalyzer analyzer = new LogAnalyzer(path);
        // 解析日志生成LogMessage集合
        List<LogMessage> logMessages = analyzer.parseLog();
        // 根据LogMessage集合生成EventLifecycle集合
        List<EventLifecycle> eventLifecycles = analyzer.buildEventLifecycle(logMessages);
        // 进行EventLifecycle之间的冲突判定，返回冲突的set相关的eventLifecycle的sid
        List<String> sids = analyzer.judgeCollision(eventLifecycles);
        // 输出sid
        analyzer.output(sids, path);
    }
}