package com.reddit.rickandmortyapp.domain.repositories

import androidx.paging.PagingData
import com.reddit.rickandmortyapp.domain.entities.RickAndMortyCharacterEntity
import kotlinx.coroutines.flow.Flow

interface RedditAssessmentRepository {
    fun getRickAndMortyCharacters() : Flow<PagingData<RickAndMortyCharacterEntity>>
}