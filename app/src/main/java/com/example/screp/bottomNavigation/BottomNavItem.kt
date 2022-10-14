package com.example.screp.bottomNavigation

import com.example.screp.R

sealed class BottomNavItem(var title:String, var icon:Int, var screen_route:String){

    object Record : BottomNavItem("Record", R.drawable.ic_record,"record")
    object Graph: BottomNavItem("Graph",R.drawable.ic_graph,"graph")
    object Weather: BottomNavItem("Weather",R.drawable.ic_weather,"weather")
    object Photos: BottomNavItem("Photos",R.drawable.ic_photos,"photos")
    object PhotoDetail : BottomNavItem("Photos",R.drawable.ic_photos,"photoDetail")
    object Settings : BottomNavItem("Settings",R.drawable.ic_setting,"settings")
    object SettingsEdit : BottomNavItem("Settings",R.drawable.ic_setting,"edit")
}