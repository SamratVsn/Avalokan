package com.example.avalokan.ui.common

//How long the UI waits for the first repository emission before it stops the
//skeleton and shows an empty/error state.
//
//Repositories emit the Room cache first, so a populated cache resolves the
//skeleton almost immediately. This timeout only covers the cold, empty,
//offline case where no emission may ever arrive.

internal const val CONTENT_LOADING_TIMEOUT_MS = 2_000L
