package io.github.flakreaper.analyzer.stackmatcher.common

import io.github.flakreaper.analyzer.stackmatcher.core.AnalyzerResult
import io.github.flakreaper.analyzer.stackmatcher.analyzer.TraceAnalyzer
import io.github.flakreaper.analyzer.stackmatcher.analyzer.converter.DeObfuscatorConverter
import proguard.retrace.DeObfuscator
import java.io.File
//import javax.swing.filechooser.FileNameExtensionFilter


data class TraceContainer(val traceFile: File, val result: AnalyzerResult)

class FileSystem(
//    private val log: AppLogger
) {
//    val fileFilters = mutableListOf<FileNameExtensionFilter>().apply {
//        add(FileNameExtensionFilter("Android Studio trace files", "trace"))
//        add(FileNameExtensionFilter("compressed AS trace files", "zip"))
//        add(FileNameExtensionFilter("AS trace with bookmarks", "twb"))
//    }
//    val mappingFilters = mutableListOf<FileNameExtensionFilter>().apply {
//        add(FileNameExtensionFilter("text mapping file", "txt"))
//    }

    private val tempDirectory = System.getProperty("java.io.tmpdir")
    private val analyzer = TraceAnalyzer()

    private var mappingFile: File? = null
    private var cachedDeobfuscator: DeObfuscator? = null

    fun openMappingFile(file: File) {
        mappingFile = file
    }

    /**
     * Should run in worker thread.
     */
    fun readFile(file: File): TraceContainer {
        if (mappingFile != null && cachedDeobfuscator == null) {
            val deObfuscator = DeObfuscator(mappingFile)
            analyzer.nameConverter = DeObfuscatorConverter(deObfuscator)
            cachedDeobfuscator = deObfuscator
        }

        if (file.extension == "trace") {
            return readTraceFile(file)
        }
        throw IllegalStateException("Wrong file format")
    }

    private fun readTraceFile(traceFile: File): TraceContainer {
        val analyzerResult = analyzer.analyze(traceFile)

//        val bookmarks = Bookmarks(traceFile.name, settings, log)
        return TraceContainer(traceFile, analyzerResult)
    }


}
