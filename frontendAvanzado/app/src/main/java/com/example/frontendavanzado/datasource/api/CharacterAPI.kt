package com.example.frontendavanzado.datasource.api

import com.example.frontendavanzado.datasource.model.AllAssetsResponse
import com.example.frontendavanzado.datasource.model.Result
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface CharacterAPI {

    @GET("/api/character")
    fun getCharacters() : Call<AllAssetsResponse>

    @GET("/api/character/{id}")
    fun getCharacter(
        @Path("id") id: Int
    ) : Call<Result>

}