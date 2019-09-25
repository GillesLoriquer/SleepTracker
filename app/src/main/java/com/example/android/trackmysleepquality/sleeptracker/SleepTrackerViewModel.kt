/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.trackmysleepquality.sleeptracker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import kotlinx.coroutines.*

/**
 * ViewModel for SleepTrackerFragment.
 */
// AndroidViewModel is the same as ViewModel, but it takes the application context as a parameter
// and makes it available as a property
class SleepTrackerViewModel(
        val database: SleepDatabaseDao,
        application: Application) : AndroidViewModel(application) {

    private var viewModelJob = Job()

    private var uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _allNights = database.getAllNights()
    val allNights: LiveData<List<SleepNight>>
            get() = _allNights

    private var tonight = MutableLiveData<SleepNight?>()

    private val _navigateToSleepQuality = MutableLiveData<SleepNight>()
    val navigateToSleepQuality: LiveData<SleepNight>
        get() = _navigateToSleepQuality

    val startButtonVisible: LiveData<Boolean> = Transformations.map(tonight) {
        it == null
    }

    val stopButtonVisible: LiveData<Boolean> = Transformations.map(tonight) {
        it != null
    }

    val clearButtonVisible: LiveData<Boolean> = Transformations.map(_allNights) {
        it?.isNotEmpty()
    }

    private var _showSnackbarEvent = MutableLiveData<Boolean>()
    val showSnackBarEvent: LiveData<Boolean>
        get() = _showSnackbarEvent

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    init {
        initializeTonight()
    }

    // Coroutine launched in the UI Scope
    private fun initializeTonight() {
        uiScope.launch {
            tonight.value = getTonightFromDatabase()
        }
    }

    // Coroutine lauched in Dispatchers.IO context
    private suspend fun getTonightFromDatabase(): SleepNight? {
        return withContext(Dispatchers.IO) {
            var night = database.getTonight()

            if (night?.endTimeMilli != night?.startTimeMilli) {
                night = null
            }
            night
        }
    }

    // Coroutine launched in the UI Scope
    fun onStartTracking() {
        uiScope.launch {
            val newNight = SleepNight()
            insert(newNight)
            tonight.value = getTonightFromDatabase()
        }
    }

    // Coroutine lauched in Dispatchers.IO context
    private suspend fun insert(night: SleepNight) {
        return withContext(Dispatchers.IO) {
            database.insert(night)
        }
    }

    // Coroutine launched in the UI Scope
    fun onStopTracking() {
        uiScope.launch {
            val oldNight = tonight.value ?: return@launch
            oldNight.endTimeMilli = System.currentTimeMillis()
            update(oldNight)
            _navigateToSleepQuality.value = oldNight
        }
    }

    // Coroutine lauched in Dispatchers.IO context
    private suspend fun update(night: SleepNight) {
        return withContext(Dispatchers.IO) {
            database.update(night)
        }
    }

    // Coroutine launched in the UI Scope
    fun onClear() {
        uiScope.launch {
            clear()
            tonight.value = null
            _showSnackbarEvent.value = true
        }
    }

    // Coroutine lauched in Dispatchers.IO context
    suspend fun clear() {
        withContext(Dispatchers.IO) {
            database.clear()
        }
    }

    fun doneNavigating() {
        _navigateToSleepQuality.value = null
    }

    fun doneShowingSnackbar() {
        _showSnackbarEvent.value = false
    }
}

