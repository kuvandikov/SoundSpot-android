/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.ui.utils

import android.content.res.Resources
import com.kuvandikov.base.util.extensions.interpunctize
import com.kuvandikov.base.util.localizeDuration
import com.kuvandikov.soundspot.domain.entities.Audios
import com.kuvandikov.soundspot.ui.media.R
import com.kuvandikov.i18n.TextCreator

data class AudiosCountDuration(val count: Int, val duration: Long = Long.MAX_VALUE) {
    companion object {
        fun from(audios: Audios) = AudiosCountDuration(audios.size, audios.sumOf { it.durationMillis() })
    }
}

object AudiosCountDurationTextCreator : TextCreator<AudiosCountDuration> {

    override fun AudiosCountDuration.localize(resources: Resources): String {
        val count = resources.getQuantityString(R.plurals.songs_count, count, count)
        val duration = resources.localizeDuration(duration, true)
        return listOf(count, duration).interpunctize()
    }
}
