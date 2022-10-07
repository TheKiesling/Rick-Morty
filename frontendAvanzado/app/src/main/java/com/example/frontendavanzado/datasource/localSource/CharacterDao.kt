package com.example.frontendavanzado.datasource.localSource

import androidx.room.*
import com.example.frontendavanzado.datasource.model.Character

@Dao
interface CharacterDao {
    @Query("SELECT * FROM character")
    suspend fun getCharacters(): List<Character>

    @Query("DELETE FROM character")
    suspend fun deleteAll(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(character: Character)

    @Query("SELECT * FROM character WHERE id = :id")
    suspend fun getCharacterById(id: Int): Character

    @Update
    suspend fun update(character: Character)

    @Delete
    suspend fun delete(character: Character): Int
}