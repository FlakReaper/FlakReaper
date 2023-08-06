package io.github.flakreaper.analyzer.stackmatcher.analyzer

import io.github.flakreaper.analyzer.stackmatcher.core.ThreadItem

class ThreadItemImpl(
    override val name: String,
    override val threadId: Int
): ThreadItem {
    override fun toString() = name
}
