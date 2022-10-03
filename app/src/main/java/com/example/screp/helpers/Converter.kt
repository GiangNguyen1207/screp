package com.example.screp.helpers

import kotlin.math.ceil

class Converter {

    //convert Double to Integer temperature
    fun roundingTemperature(temp: Double): Int {
        return ceil(temp).toInt()
    }
}