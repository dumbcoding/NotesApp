package com.example.notesapp.feature_note.domain.usecase

import com.example.notesapp.feature_note.domain.model.Note
import com.example.notesapp.feature_note.domain.repository.NoteRepository
import com.example.notesapp.feature_note.domain.util.NoteOrder
import com.example.notesapp.feature_note.domain.util.OrderType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetNotes(
    private val repository: NoteRepository
) {
    operator fun invoke(
        noteOrder: NoteOrder = NoteOrder.Date(OrderType.Descending)
    ): Flow<List<Note>>{
        return repository.getNotes().map {
            notes -> when(noteOrder.orderType){
                is OrderType.Ascending -> {
                    when(noteOrder){
                        is NoteOrder.Title -> notes.sortedWith(compareBy({it.pinned},{it.title.lowercase()}))
                        is NoteOrder.Date -> notes.sortedWith(compareBy({it.pinned},{it.timestamp}))
                    }
                }
                is OrderType.Descending -> {
                    when(noteOrder) {
                        is NoteOrder.Title -> notes.sortedWith(compareByDescending<Note> {
                            it.pinned
                        }.thenByDescending {
                            it.title.lowercase()
                        })
                        is NoteOrder.Date -> notes.sortedWith(compareByDescending<Note> {
                            it.pinned
                        }.thenByDescending {
                            it.timestamp
                        })
                    }
                }
            }
        }
    }
}