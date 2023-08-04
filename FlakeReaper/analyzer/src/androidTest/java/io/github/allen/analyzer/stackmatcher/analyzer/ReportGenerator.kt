package io.github.allen.analyzer.stackmatcher.analyzer

import java.io.File

interface ReportGenerator {
    fun generate(file: File, onlyConstructor: Boolean, minimumDurationInMs: Int)
}
