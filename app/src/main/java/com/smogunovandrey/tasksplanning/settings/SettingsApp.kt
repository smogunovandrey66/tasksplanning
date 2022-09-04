package com.smogunovandrey.tasksplanning.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton



@InstallIn(SingletonComponent::class)
@Module
object SettingsModule {
    private val Context.dataStore by preferencesDataStore("PREF_NAME")

    @Singleton
    @Provides
    fun provideDataStore(@ApplicationContext context: Context) = context.dataStore
}

private const val PREF_NAME = "settings"