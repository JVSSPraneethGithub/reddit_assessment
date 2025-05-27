package com.reddit.rickandmortyapp.display

import androidx.annotation.VisibleForTesting
import androidx.annotation.VisibleForTesting.Companion.PRIVATE
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.reddit.rickandmortyapp.display.UiState.Completed.Success
import com.reddit.rickandmortyapp.domain.entities.RickAndMortyCharacterEntity
import com.reddit.rickandmortyapp.domain.usecase.RedditAssessmentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ExperimentalForInheritanceCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface UiState {
    data object Loading : UiState
    sealed interface Completed : UiState {
        @OptIn(ExperimentalForInheritanceCoroutinesApi::class)
        data class Success(val paging: Flow<PagingData<RickAndMortyCharacterEntity>>) :
            Completed, Flow<PagingData<RickAndMortyCharacterEntity>> by paging
        data class Error(val message: String) : Completed
    }
}

@HiltViewModel
class RedditAssessmentViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val useCase: RedditAssessmentUseCase
): ViewModel() {

    val KEY_PREFETCH_DISTANCE = 5

    @VisibleForTesting(otherwise = PRIVATE)
    val KEY_PAGING_DATA = "PAGING_DATA"
    @VisibleForTesting(otherwise = PRIVATE)
    val KEY_ENTITY_LIST = "ENTITY_LIST"

    init {
        pullToRefresh()
    }

    val snackbarState = SnackbarHostState()

    @OptIn(ExperimentalCoroutinesApi::class)
    val pagingData : StateFlow<UiState> =
        savedStateHandle.getStateFlow(
            KEY_PAGING_DATA, false
        ).flatMapLatest { status ->
            flowOf(
                Success(useCase()) as UiState.Completed
            ).also {
                savedStateHandle[KEY_PAGING_DATA] = true
            }
        }.catch { e ->
            emit(UiState.Completed.Error(e.message ?: "Unknown error"))
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            UiState.Loading
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val displayList : StateFlow<SnapshotStateList<RickAndMortyCharacterEntity>> =
        savedStateHandle.getStateFlow(
            KEY_ENTITY_LIST, emptyList<RickAndMortyCharacterEntity>()
        ).flatMapLatest { entities ->
            flowOf(entities.toMutableStateList())
        }.catch { e ->
            emit(emptyList<RickAndMortyCharacterEntity>().toMutableStateList())
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            SnapshotStateList()
        )

    fun cacheDisplayList(
        input: List<RickAndMortyCharacterEntity>
    ) {
        if ( input.isNotEmpty() ) {
            val displayList = savedStateHandle.get<List<RickAndMortyCharacterEntity>>(KEY_ENTITY_LIST)
                ?.toMutableList() ?: mutableListOf()
            input.forEach { entity ->
                if ( !displayList.any { it.id == entity.id } ) {
                    displayList.add(entity)
                }
            }
            displayList.sortedBy { it.id }
            savedStateHandle[KEY_ENTITY_LIST] = displayList
        }
    }

    fun pullToRefresh() {
        savedStateHandle[KEY_PAGING_DATA] = false
        savedStateHandle[KEY_ENTITY_LIST] = emptyList<RickAndMortyCharacterEntity>()
    }

    fun showSnackBar(
        message: String,
        duration: SnackbarDuration
    ) {
        viewModelScope.launch {
            snackbarState.showSnackbar(
                message = message,
                duration = duration
            )
        }
    }
}