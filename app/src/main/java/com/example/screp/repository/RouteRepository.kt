package com.example.screp.repository

import androidx.lifecycle.LiveData
import com.example.screp.dao.RouteDao
import com.example.screp.data.Route
import com.google.android.gms.maps.model.LatLng

class RouteRepository(private val routeDao: RouteDao) {
    fun getRoute(): LiveData<List<Route>> =
        routeDao.getRoutes()

    fun getRouteLatAndLong(routeId: Int): LiveData<List<LatLng>> =
        routeDao.getRouteLatAndLong(routeId)

    suspend fun insertRoute(route: Route) = routeDao.insert(route)
}