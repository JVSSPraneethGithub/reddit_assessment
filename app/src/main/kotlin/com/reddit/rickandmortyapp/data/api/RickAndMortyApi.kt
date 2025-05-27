package com.reddit.rickandmortyapp.data.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Query

interface RickAndMortyApi {

    @GET("api/character/?")
    suspend fun getCharacters(@Query("page") page: Int): RickAndMortyResponse
}

@Serializable
data class RickAndMortyResponse (
    @SerialName("info"    ) val info    : Info,
    @SerialName("results" ) val results : ArrayList<RickAndMortyCharacter> = arrayListOf()
)

@Serializable
data class Info (
    @SerialName("count" ) val count : Int,
    @SerialName("pages" ) val pages : Int,
    @SerialName("next"  ) val next  : String?,
    @SerialName("prev"  ) val prev  : String?
)

@Serializable
data class RickAndMortyCharacter (
    @SerialName("id"       ) val id       : Int,
    @SerialName("name"     ) val name     : String,
    @SerialName("image"    ) val image    : String
)
