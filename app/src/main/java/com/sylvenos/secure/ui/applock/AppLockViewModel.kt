package com.sylvenos.secure.ui.applock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sylvenos.secure.domain.model.AppInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the App Lock screen.
 *
 * Manages the list of installed apps and their locked/unlocked state.
 * TODO: inject [GetInstalledAppsUseCase] and [ToggleAppLockUseCase] via Hilt.
 */
class AppLockViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AppLockUiState())
    val uiState: StateFlow<AppLockUiState> = _uiState.asStateFlow()

    fun loadApps() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            // TODO: call GetInstalledAppsUseCase
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    fun toggleLock(packageName: String) {
        // TODO: call ToggleAppLockUseCase
    }
}

data class AppLockUiState(
    val apps      : List<AppInfo> = emptyList(),
    val isLoading : Boolean       = false,
    val error     : String?       = null
)
