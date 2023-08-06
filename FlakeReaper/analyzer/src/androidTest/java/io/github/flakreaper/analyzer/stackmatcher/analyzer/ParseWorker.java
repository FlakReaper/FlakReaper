package io.github.flakreaper.analyzer.stackmatcher.analyzer;

import android.util.Log;

import io.github.flakreaper.analyzer.stackmatcher.common.FileSystem;
import io.github.flakreaper.analyzer.stackmatcher.common.TraceContainer;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ParseWorker {
    private final File traceFile;
    private final FileSystem fileSystem;

    public ParseWorker(File traceFile, FileSystem fileSystem) {
        this.traceFile = traceFile;
        this.fileSystem = fileSystem;
    }

    public WorkerResult generateWorkerResult() {
        try {
            return new WorkerResult(fileSystem.readFile(traceFile));
        } catch (Throwable t) {
            Log.e("matchStack", t.toString());
//            t.printStackTrace();
            throw t;
//            return new WorkerResult(t);
        }
    }

    /**
     * analy ExceptionStackFile
     * @param exceptionStackFile
     * @return
     */
    public List<Stack<String>> parseExceptionStack(File exceptionStackFile) {

        List<Stack<String>> stackList = new ArrayList<>();
        stackList.add(new Stack<>());

        try (BufferedReader reader = new BufferedReader(new FileReader(exceptionStackFile))) {
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.trim().startsWith("Caused by")) {
                    Stack<String> stack = new Stack<>();
                    stackList.add(stack);
                }
                if (line.trim().startsWith("at")) {
                    String modifiedLine = line.substring(4).replaceAll("\\([^\\(\\)]*\\)", "");
                    stackList.get(stackList.size() - 1).push(modifiedLine);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < stackList.size(); i++) {
            Stack<String> stack = new Stack<>();
            Stack<String> originalStack = stackList.get(i);
            while (!originalStack.isEmpty()) {
                stack.push(originalStack.pop());
            }
            stackList.set(i, stack);
        }
        return stackList;
    }

    public static class WorkerResult {
        @Nullable
        public final TraceContainer traceContainer;
        @Nullable
        final Throwable throwable;

        public WorkerResult(Throwable throwable) {
            this.traceContainer = null;
            this.throwable = throwable;
        }

        public WorkerResult(TraceContainer traceContainer) {
            this.traceContainer = traceContainer;
            this.throwable = null;
        }
    }
}
