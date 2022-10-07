package com.example.frontendavanzado.datasource.localSource

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.frontendavanzado.datasource.model.Character

@Database(entities = [Character::class], version = 1)
abstract class Database: RoomDatabase() {
    abstract fun characterDao(): CharacterDao
}