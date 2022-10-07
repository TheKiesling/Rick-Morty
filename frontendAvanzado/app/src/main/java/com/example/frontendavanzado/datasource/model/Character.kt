package com.example.frontendavanzado.datasource.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Character(
    @PrimaryKey val id: Int,
    val image: String,
    val name: String,
    val gender: String,
    val origin: String,
    val species: String,
    val status: String,
    val episodesAppearance: Int
)