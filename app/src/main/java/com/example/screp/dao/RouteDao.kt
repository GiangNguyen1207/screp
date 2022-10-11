package com.example.screp.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.screp.data.Route
import com.google.android.gms.maps.model.LatLng

@Dao
interface RouteDao {

    @Query("SELECT * FROM route")
    fun getRoutes(): LiveData<List<Route>>

    @Query("SELECT latitude, longitude FROM route WHERE route.routeId = :routeId ")
    fun getRouteLatAndLong(routeId: Int): LiveData<List<LatLng>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(route: Route): Long
}