package com.example.screp.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.screp.data.Photo

@Dao
interface PhotoDao {

    @Query("SELECT * FROM photo")
    fun getPhotos(): LiveData<List<Photo>>

    @Query("SELECT * FROM photo WHERE photo.photoName = :photoName ")
    fun getPhotoByName(photoName: String): LiveData<Photo>

    @Query("SELECT * FROM photo WHERE photo.cityName = :cityName ")
    fun getPhotoByCity(cityName: String): LiveData<List<Photo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(photo: Photo): Long

    @Delete
    suspend fun delete(photo: Photo)
}