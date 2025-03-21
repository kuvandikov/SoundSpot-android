/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

const val DATMUSIC_FIRST_PAGE_INDEX = 0

@Parcelize
data class CaptchaSolution(
    val captchaId: Long,
    val captchaIndex: Int,
    val captchaKey: String,
) : Parcelable {
    companion object {
        fun CaptchaSolution.toQueryMap() = mapOf(
            "captcha_id" to captchaId,
            "captcha_index" to captchaIndex,
            "captcha_key" to captchaKey
        )
    }
}
