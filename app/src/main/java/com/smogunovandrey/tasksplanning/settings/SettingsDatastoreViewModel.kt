package com.smogunovandrey.tasksplanning.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SettingsDatastoreViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>
    ): ViewModel() {

    val radiusFlow = dataStore.data.map {
        it[KEY_GEOFENCE_RADIUS_DATA_STORE] ?: DEFAULT_GEOFENCE_RADIUS
    }

    suspend fun setRadius(newRadius: Float){

        dataStore.edit {
            it[KEY_GEOFENCE_RADIUS_DATA_STORE] = newRadius
        }
    }

        companion object{
            private const val KEY_GEOFENCE_RADIUS = "key goofence radius"
            private const val DEFAULT_GEOFENCE_RADIUS = 20.0f
            val KEY_GEOFENCE_RADIUS_DATA_STORE = floatPreferencesKey(KEY_GEOFENCE_RADIUS)
        }
}