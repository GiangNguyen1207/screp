package com.example.screp.helpers

import java.util.*

class CalendarUtil {
    private val calendar = Calendar.getInstance()

    //get the beginning of a given date
    fun getCurrentDateStart(givenDate: String? = null): Long {
        if (givenDate != null) {
            //Example Date: "2022-09-27"
            //Month index starts with 0. To get correct month, index needs to minus 1
            val dateList = givenDate.split("-")
            calendar.set(Calendar.DAY_OF_MONTH, dateList[2].toInt());
            calendar.set(Calendar.MONTH, dateList[1].toInt() - 1);
            calendar.set(Calendar.YEAR, dateList[0].toInt());
        }
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.time.time
    }

    //get the end of a given date
    fun getCurrentDateEnd(givenDate: String? = null): Long {
        if (givenDate != null) {
            //Example Date: "2022-09-27"
            //Month index starts with 0. To get correct month, index needs to minus 1
            val dateList = givenDate.split("-")
            calendar.set(Calendar.DAY_OF_MONTH, dateList[2].toInt());
            calendar.set(Calendar.MONTH, dateList[1].toInt() - 1);
            calendar.set(Calendar.YEAR, dateList[0].toInt());
        }
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        return calendar.time.time
    }

    //get next day in calendar. Used to forecast weather of 24 hours in advance.
    fun getNextDate(): Long {
        calendar.add(Calendar.DATE, 1)
        return calendar.time.time
    }

    //get time in String. For example: 7.30, 10, 18.30, 21
    fun getTime(givenDateTime: Long, hasMinute: Boolean = true): String {
        val date = Date(givenDateTime)
        calendar.time = date
        return if (hasMinute)
            "${calendar.get(Calendar.HOUR_OF_DAY)}.${calendar.get(Calendar.MINUTE)}"
        else "${calendar.get(Calendar.HOUR_OF_DAY)}"
    }
}