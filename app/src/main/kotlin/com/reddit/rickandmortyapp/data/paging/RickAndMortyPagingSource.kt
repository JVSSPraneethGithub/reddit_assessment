package com.reddit.rickandmortyapp.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.reddit.rickandmortyapp.data.api.RickAndMortyApi
import com.reddit.rickandmortyapp.domain.entities.RickAndMortyCharacterEntity

class RickAndMortyPagingSource(
    private val rickAndMortyApi: RickAndMortyApi
): PagingSource<Int, RickAndMortyCharacterEntity>() {

    override fun getRefreshKey(state: PagingState<Int, RickAndMortyCharacterEntity>): Int? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.nextKey
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RickAndMortyCharacterEntity> {
        return try {
            val page = params.key ?: 1
            val response = rickAndMortyApi.getCharacters(page)
            if ( response.results.isNotEmpty()) {
                LoadResult.Page(
                    data = response.results.map {
                        RickAndMortyCharacterEntity(
                            id = it.id,
                            name = it.name,
                            image = it.image
                        )
                    },
                    prevKey = if (page == 1) null else (page - 1),
                    nextKey = if ( page == response.info.pages) null else (page + 1))
            } else LoadResult.Error(throw Exception("No Response"))
        } catch (
            e: Exception
        ) {
            LoadResult.Error(e)
        }
    }
}