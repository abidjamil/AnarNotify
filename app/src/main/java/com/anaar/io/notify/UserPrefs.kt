package com.anaar.io.notify

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

val Context.userIdDataStore by preferencesDataStore(name = "user_prefs")

val USER_ID_KEY = stringPreferencesKey("user_id")

suspend fun Context.saveUserId(userId: String) {
    userIdDataStore.edit { prefs ->
        prefs[USER_ID_KEY] = userId
    }
}

suspend fun Context.clearUserId() {
    userIdDataStore.edit { prefs ->
        prefs.remove(USER_ID_KEY)
    }
}

fun Context.getUserIdFlow() =
    userIdDataStore.data.map { prefs -> prefs[USER_ID_KEY] ?: "" }
