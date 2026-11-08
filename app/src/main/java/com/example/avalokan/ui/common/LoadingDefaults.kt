package com.example.avalokan.ui.common

// How long a content flow may stay non-empty-less before its ContentState
// commits to Empty (an emission arrived but carried nothing) or Error (no
// emission arrived at all).
//
// Repositories emit the Room cache first, so a populated cache resolves the
// skeleton almost immediately. This timeout only covers the cold, empty,
// offline case where the network may never answer.

internal const val CONTENT_LOADING_TIMEOUT_MS = 2_000L
