package com.reddit.rickandmortyapp.domain.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RickAndMortyCharacterEntity(
    val id: Int,
    val name: String,
    val image: String
) : Parcelable