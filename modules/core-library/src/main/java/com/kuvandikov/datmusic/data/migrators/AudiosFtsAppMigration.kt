/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.datmusic.data.migrators

import javax.inject.Inject
import com.kuvandikov.base.migrator.AppMigration
import com.kuvandikov.datmusic.data.interactors.backup.CreateDatmusicBackup

class AudiosFtsAppMigration @Inject constructor(
    private val datmusicBackup: CreateDatmusicBackup
) : AppMigration(id = "audios_fts_initial_index_fix") {

    override suspend fun apply() {
        // creating backup will create Downloads playlist & clear unused entities
        // triggering Audios FTS table to repopulate
        datmusicBackup.execute(Unit)
    }
}
