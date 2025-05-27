package com.reddit.rickandmortyapp.data.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.reddit.rickandmortyapp.data.api.RickAndMortyApi
import com.reddit.rickandmortyapp.data.paging.RickAndMortyPagingSource
import com.reddit.rickandmortyapp.domain.repositories.RedditAssessmentRepository

class RickAndMortyRepository(
    private val api: RickAndMortyApi
) : RedditAssessmentRepository {

    override fun getRickAndMortyCharacters() = Pager(
        config = PagingConfig(
            pageSize = 20
        ),
        pagingSourceFactory = { RickAndMortyPagingSource(api) }
    ).flow
}