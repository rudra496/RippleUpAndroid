package com.yft.rippleup.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yft.rippleup.data.repo.ActivityItem
import com.yft.rippleup.data.repo.StatsRepository
import com.yft.rippleup.data.repo.UserSnapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Single app-wide ViewModel. Holds the repository and exposes reactive state for
 * the dashboard and any screen that needs to log an action.
 */
class StatsViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = StatsRepository(app.applicationContext)

    val snapshot: StateFlow<UserSnapshot?> = repo.snapshot.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), null,
    )

    val recentActivity: StateFlow<List<ActivityItem>> = repo.recentActivity.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList(),
    )

    /** Fires +1 each time an action is logged, so the UI can trigger confetti. */
    val pulseTick = MutableStateFlow(0)

    init { viewModelScope.launch { repo.ensureSeeded() } }

    fun logAction(
        title: String, points: Int, co2Kg: Double, colorTag: String, iconTag: String,
    ) {
        viewModelScope.launch {
            repo.logAction(title, points, co2Kg, colorTag, iconTag)
            pulseTick.value += 1
        }
    }

    fun completeOnboarding(name: String) {
        viewModelScope.launch { repo.completeOnboarding(name) }
    }
}
