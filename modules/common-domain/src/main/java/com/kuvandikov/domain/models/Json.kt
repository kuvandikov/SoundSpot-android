/*
 * Copyright (C) 2021, Anvarbek Kuvandikov
 * All rights reserved.
 */
package com.kuvandikov.domain.models

import kotlinx.serialization.json.Json

val DEFAULT_JSON_FORMAT = Json {
    ignoreUnknownKeys = true
    coerceInputValues = true
}

val JSON = DEFAULT_JSON_FORMAT
