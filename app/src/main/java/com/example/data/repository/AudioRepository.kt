package com.example.data.repository

import com.example.data.dao.AudioTrackDao
import com.example.data.model.AudioTrackEntity
import kotlinx.coroutines.flow.Flow

class AudioRepository(private val dao: AudioTrackDao) {

    val allTracks: Flow<List<AudioTrackEntity>> = dao.getAllTracks()
    val savedTracks: Flow<List<AudioTrackEntity>> = dao.getSavedTracks()
    val processedTracks: Flow<List<AudioTrackEntity>> = dao.getProcessedTracks()
    val recentTracks: Flow<List<AudioTrackEntity>> = dao.getRecentTracks()

    suspend fun getTrackById(id: Long): AudioTrackEntity? = dao.getTrackById(id)

    suspend fun insertTrack(track: AudioTrackEntity): Long = dao.insertTrack(track)

    suspend fun updateTrack(track: AudioTrackEntity) = dao.updateTrack(track)

    suspend fun updateSavedStatus(id: Long, isSaved: Boolean) = dao.updateSavedStatus(id, isSaved)

    suspend fun renameTrack(id: Long, newTitle: String) = dao.renameTrack(id, newTitle)

    suspend fun deleteTrack(track: AudioTrackEntity) = dao.deleteTrack(track)

    suspend fun deleteTrackById(id: Long) = dao.deleteTrackById(id)

    suspend fun getCount(): Int = dao.getCount()
}
