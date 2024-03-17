package com.renandeldotti.notes.utils

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.renandeldotti.notes.utils.SharedPrefsMgt.appSettingsDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.IOException

/*
Prefer to use DataStore instead of SharedPreferences
 */

/*
TODO: Change this to a class and use Dagger to make it a singleton
https://www.youtube.com/watch?v=Q5OKmS0unAI 06:51
 */
object SharedPrefsMgt {

    private val Context.appSettingsDataStore:
            DataStore<Preferences> by preferencesDataStore(name = "app_settings")

    //private val spmScope = CoroutineScope(Dispatchers.IO)
    private val pOnboardKey = booleanPreferencesKey("spm_user-passed-onboard")

    private const val TAG = "SharedPrefsMgt"

    suspend fun hasUserPassedOnboard(context: Context): Flow<Boolean?> =
        getFromDataStore(context.appSettingsDataStore, pOnboardKey)

    suspend fun setUserPassedOnboard(context: Context) {
        hasUserPassedOnboard(context).collect {
            if (it == true) {
                Log.d(TAG, "SUPO: value already set!")
            } else {
                Log.d(TAG, "SUPO: saving value")
                saveInDataStore(context.appSettingsDataStore, pOnboardKey, true)
            }
        }
    }

    private suspend fun <T> getFromDataStore(dataStore: DataStore<Preferences>, key: Preferences.Key<T>): Flow<T?> {
        return dataStore.data
            .catch {
                if (it is IOException) {
                    Log.e(TAG, "HUPO: error reading value!")
                    emit(emptyPreferences())
                } else {
                    throw it
                }
            }.map {
                it[key]
            }
    }

    private suspend fun <T> saveInDataStore(dataStore: DataStore<Preferences>, key: Preferences.Key<T>, value: T) {
        dataStore.edit {
            it[key] = value
        }
    }
}

