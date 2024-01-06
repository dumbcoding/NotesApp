package com.example.notesapp.feature_note.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Note(
    val title: String,
    val content: String,
    val timestamp: Long,
    var pinned: Boolean,
    @PrimaryKey val id: Int? = null
)
class InvalidNoteException(message: String): Exception(message)
