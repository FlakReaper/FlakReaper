package io.github.flakreaper.analyzer.stackmatcher.analyzer

import java.io.File

interface ReportGenerator {
    fun generate(file: File, onlyConstructor: Boolean, minimumDurationInMs: Int)
}
