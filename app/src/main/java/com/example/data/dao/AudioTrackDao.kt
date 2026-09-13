package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AudioTrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AudioTrackDao {

    @Query("SELECT * FROM audio_tracks ORDER BY createdAt DESC")
    fun getAllTracks(): Flow<List<AudioTrackEntity>>

    @Query("SELECT * FROM audio_tracks WHERE isSaved = 1 ORDER BY createdAt DESC")
    fun getSavedTracks(): Flow<List<AudioTrackEntity>>

    @Query("SELECT * FROM audio_tracks WHERE processedFilePath IS NOT NULL ORDER BY createdAt DESC")
    fun getProcessedTracks(): Flow<List<AudioTrackEntity>>

    @Query("SELECT * FROM audio_tracks ORDER BY createdAt DESC LIMIT 10")
    fun getRecentTracks(): Flow<List<AudioTrackEntity>>

    @Query("SELECT * FROM audio_tracks WHERE id = :id LIMIT 1")
    suspend fun getTrackById(id: Long): AudioTrackEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: AudioTrackEntity): Long

    @Update
    suspend fun updateTrack(track: AudioTrackEntity)

    @Query("UPDATE audio_tracks SET isSaved = :isSaved WHERE id = :id")
    suspend fun updateSavedStatus(id: Long, isSaved: Boolean)

    @Query("UPDATE audio_tracks SET title = :newTitle WHERE id = :id")
    suspend fun renameTrack(id: Long, newTitle: String)

    @Delete
    suspend fun deleteTrack(track: AudioTrackEntity)

    @Query("DELETE FROM audio_tracks WHERE id = :id")
    suspend fun deleteTrackById(id: Long)

    @Query("SELECT COUNT(*) FROM audio_tracks")
    suspend fun getCount(): Int
}
