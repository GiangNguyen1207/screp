package com.example.screp.repository

import androidx.lifecycle.LiveData
import com.example.screp.dao.PhotoDao
import com.example.screp.data.Photo

class PhotoRepository(private val photoDao: PhotoDao) {
    fun getPhotos(): LiveData<List<Photo>> =
        photoDao.getPhotos()

    fun getPhotoByName(photoName: String): LiveData<Photo> =
        photoDao.getPhotoByName(photoName)

    fun getPhotoByCity(cityName: String): LiveData<List<Photo>> =
        photoDao.getPhotoByCity(cityName)

    suspend fun insertPhoto(photo: Photo) = photoDao.insert(photo)
}