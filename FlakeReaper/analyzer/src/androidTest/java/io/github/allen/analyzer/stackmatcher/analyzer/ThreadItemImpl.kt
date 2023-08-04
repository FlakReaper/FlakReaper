package io.github.allen.analyzer.stackmatcher.analyzer

import io.github.allen.analyzer.stackmatcher.core.ThreadItem

class ThreadItemImpl(
    override val name: String,
    override val threadId: Int
): ThreadItem {
    override fun toString() = name
}
