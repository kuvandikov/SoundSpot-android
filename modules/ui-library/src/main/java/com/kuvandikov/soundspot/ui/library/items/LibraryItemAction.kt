/*
 * Copyright (C) 2023, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.soundspot.ui.library.items

import com.kuvandikov.soundspot.domain.entities.LibraryItem
import com.kuvandikov.soundspot.ui.library.R

internal typealias LibraryItemActionHandler = (LibraryItemAction) -> Unit

internal sealed class LibraryItemAction(open val item: LibraryItem) {
    data class Edit(override val item: LibraryItem) : LibraryItemAction(item)
    data class Delete(override val item: LibraryItem) : LibraryItemAction(item)
    data class Download(override val item: LibraryItem) : LibraryItemAction(item)

    companion object {
        fun from(actionLabelRes: Int, item: LibraryItem) = when (actionLabelRes) {
            R.string.library_item_menu_edit -> Edit(item)
            R.string.library_item_menu_delete -> Delete(item)
            R.string.library_item_menu_download -> Download(item)
            else -> error("Unknown action: $actionLabelRes")
        }
    }
}
