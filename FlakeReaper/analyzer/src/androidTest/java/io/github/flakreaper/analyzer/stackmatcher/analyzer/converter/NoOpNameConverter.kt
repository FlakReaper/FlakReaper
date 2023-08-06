package io.github.flakreaper.analyzer.stackmatcher.analyzer.converter

object NoOpNameConverter : NameConverter {
    override fun convertClassName(sourceClassName: String) = sourceClassName

    override fun convertMethodName(className: String, sourceMethodName: String, type: String?) = sourceMethodName
}