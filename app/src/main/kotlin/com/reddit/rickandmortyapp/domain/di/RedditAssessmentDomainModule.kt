package com.reddit.rickandmortyapp.domain.di

import com.reddit.rickandmortyapp.domain.repositories.RedditAssessmentRepository
import com.reddit.rickandmortyapp.domain.usecase.RedditAssessmentUseCase
import com.reddit.rickandmortyapp.domain.usecase.RickAndMortyUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class RedditAssessmentDomainModule {

    @Provides
    fun providesRickAndMortyUseCase(
        repository: RedditAssessmentRepository
    ) : RedditAssessmentUseCase = RickAndMortyUseCase(repository)
}