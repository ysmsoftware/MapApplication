package com.application.mapapplication

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserManager(content: Context) {

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("map_user_pref")
        val USER_ID_KEY = intPreferencesKey("USER_ID")
        val USER_LOGIN_KEY = intPreferencesKey("USER_LOGIN")
    }
    private val mDataStore: DataStore<Preferences> = content.dataStore

    suspend fun storeUser(login:Int, id: Int) {
        mDataStore.edit { preferences ->
            preferences[USER_ID_KEY] = id
            preferences[USER_LOGIN_KEY] = login
        }
    }
    val userIdFlow: Flow<Int> = mDataStore.data.map {
        it[USER_ID_KEY] ?: 0
    }
    val userLoginFlow: Flow<Int> = mDataStore.data.map {
        it[USER_LOGIN_KEY] ?: 0
    }

}