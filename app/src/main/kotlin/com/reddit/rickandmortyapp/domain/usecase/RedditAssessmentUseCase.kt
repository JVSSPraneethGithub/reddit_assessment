package com.reddit.rickandmortyapp.domain.usecase

import androidx.paging.PagingData
import com.reddit.rickandmortyapp.domain.entities.RickAndMortyCharacterEntity
import com.reddit.rickandmortyapp.domain.repositories.RedditAssessmentRepository
import kotlinx.coroutines.flow.Flow

interface RedditAssessmentUseCase {
    operator fun invoke() : Flow<PagingData<RickAndMortyCharacterEntity>>
}

class RickAndMortyUseCase(
    private val repository: RedditAssessmentRepository
) : RedditAssessmentUseCase {
    override operator fun invoke() = repository.getRickAndMortyCharacters()
}