/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.data

import android.content.Context
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.withContext
import com.kuvandikov.base.util.CoroutineDispatchers
import com.kuvandikov.domain.models.Optional
import com.kuvandikov.domain.models.some

open class LocalFilesRepo @Inject constructor(private val context: Context, private val dispatchers: CoroutineDispatchers) {

    var suffix: String = ""

    private fun getFile(name: String) = File(context.filesDir, "$name.$suffix")

    suspend fun read(name: String): Optional<String> = withContext(dispatchers.io) {
        val file = getFile(name)
        val data = when (file.exists()) {
            true -> {
                val data = file.bufferedReader(Charsets.UTF_8).readText()
                when (data.isBlank()) {
                    true -> Optional.None
                    else -> {
                        some(data)
                    }
                }
            }
            else -> Optional.None
        }
        data
    }

    suspend fun save(name: String, data: String) = withContext(dispatchers.io) {
        with(getFile(name)) {
            delete()
            bufferedWriter().use {
                it.write(data)
            }
        }
    }
}
