/**
 * Copyright 2026 Salesforce, Inc.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials provided
 * with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to
 * endorse or promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.salesforce.marketingcloud.learningapp

import androidx.lifecycle.MutableLiveData

/**
 * Shared in-memory state that bridges [BaseLearningApplication]'s IAM event listener
 * with the [screens.Home] fragment UI.
 */
object IamState {
    /** When true, the next automatic IAM display is suppressed (one-shot). */
    @Volatile var suppressMessages: Boolean = false

    /**
     * The ID of the most recently suppressed message.
     * Set by [BaseLearningApplication] when [suppressMessages] caused a message to be blocked.
     * Null until a suppression has occurred.
     */
    @Volatile var suppressedMessageId: String? = null

    /**
     * Human-readable description of the last IAM lifecycle event (shown or closed).
     * Null until the first event fires.
     */
    @Volatile var lastEvent: String? = null

    /**
     * Emits the suppressed message ID immediately when [suppressMessages] blocks a message.
     * [BaseLearningApplication] posts to this via [MutableLiveData.postValue]
     * [screens.Home] observes it with viewLifecycleOwner so updates are lifecycle-aware.
     */
    val suppressedIdLiveData = MutableLiveData<String?>()
}
