package com.example.screp.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RouteNumber(private val context: Context) {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("RouteNumber")
        val ROUTE_NUMBER = stringPreferencesKey("route_number")
    }

    // to get the route number
    val getRouteNumber: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[ROUTE_NUMBER] ?: "0"
        }

    // to save the route number
    suspend fun saveRouteNumber(number: String) {
        context.dataStore.edit { preferences ->
            preferences[ROUTE_NUMBER] = number
        }
    }
}