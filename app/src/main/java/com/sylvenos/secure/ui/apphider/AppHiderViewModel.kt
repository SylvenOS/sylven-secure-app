package com.sylvenos.secure.ui.apphider

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sylvenos.secure.domain.model.AppInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the App Hider screen.
 *
 * Manages the list of installed apps and their hidden/visible state.
 * TODO: inject [GetInstalledAppsUseCase] and [AppHiderRepository] via Hilt.
 */
class AppHiderViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AppHiderUiState())
    val uiState: StateFlow<AppHiderUiState> = _uiState.asStateFlow()

    fun loadApps() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            // TODO: call GetInstalledAppsUseCase
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    fun toggleHide(packageName: String) {
        // TODO: call AppHiderRepository.setHidden(packageName, !isCurrentlyHidden)
    }
}

data class AppHiderUiState(
    val apps      : List<AppInfo> = emptyList(),
    val isLoading : Boolean       = false,
    val error     : String?       = null
)
