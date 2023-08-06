package io.github.flakreaper.analyzer;

import android.os.Environment;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.List;
import java.util.Stack;

import io.github.flakreaper.analyzer.stackmatcher.TraceMatcher;
import io.github.flakreaper.analyzer.stackmatcher.analyzer.ParseWorker;
import io.github.flakreaper.analyzer.stackmatcher.common.FileSystem;
import io.github.flakreaper.analyzer.stackmatcher.core.AnalyzerResult;
import io.github.flakreaper.analyzer.stackmatcher.core.ProfileData;

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
        List<LogMessage> logMessages = analyzer.parseLog();
        List<EventLifecycle> eventLifecycles = analyzer.buildEventLifecycle(logMessages);
        List<String> sids = analyzer.judgeCollision(eventLifecycles);
        analyzer.output(sids, path);
    }
}