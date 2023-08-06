package io.github.flakreaper.analyzer.stackmatcher.core

import java.io.File

interface TraceReader {
    fun readTraceFile(traceFile: File): AnalyzerResult
}
