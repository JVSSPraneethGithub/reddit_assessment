package com.reddit.rickandmortyapp.data.di

import com.reddit.rickandmortyapp.data.api.RickAndMortyApi
import com.reddit.rickandmortyapp.data.repositories.RickAndMortyRepository
import com.reddit.rickandmortyapp.domain.repositories.RedditAssessmentRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class RickAndMortyDataModule {

    @Provides
    fun providesRickAndMortyRepository(
        api: RickAndMortyApi
    ) : RedditAssessmentRepository = RickAndMortyRepository(api)
}