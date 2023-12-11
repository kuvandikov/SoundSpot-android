/*
 * Copyright (C) 2021, Alashov Berkeli
 * All rights reserved.
 */
package com.kuvandikov.domain.models

sealed class InvokeStatus
object InvokeStarted : InvokeStatus()
object InvokeSuccess : InvokeStatus()
data class InvokeError(val throwable: Throwable) : InvokeStatus()
