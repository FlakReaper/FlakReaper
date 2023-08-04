package io.github.allen.analyzer.stackmatcher.analyzer

import io.github.allen.analyzer.stackmatcher.core.ThreadTimeBounds

data class ThreadTimeBoundsImpl(
    override var minTime: Double = Double.MAX_VALUE,
    override var maxTime: Double = 0.0
) : ThreadTimeBounds
