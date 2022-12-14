package com.example.screp.helpers

import android.util.Log
import androidx.compose.ui.text.buildAnnotatedString
import java.text.SimpleDateFormat
import java.util.*

class CalendarUtil {
    private val calendar = Calendar.getInstance()

    //get the current time and store as Long
    fun getCurrentTime(): Long {
        return calendar.time.time
    }

    // get today's date as string
    fun getTodayDate(): String {
        val currentDate = getCurrentTime()
        return SimpleDateFormat("yyyy-MM-dd").format(currentDate)
    }


    // get calculated date by time delta:
    fun getCalculatedDate(dateFormat: String = "yyyy-MM-dd", days: Int): String? {
        val cal = Calendar.getInstance()
        val format = SimpleDateFormat(dateFormat)
        cal.add(Calendar.DAY_OF_YEAR, days)
        return format.format(cal.timeInMillis)
    }


    // Format time value as Long to formatted date and time string
    fun convertLongToTime(format: String = "yyyy.MM.dd HH:mm", time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat(format)
        return format.format(date)
    }

    // Format time for record card
    fun formatTimeForRecordCard(startTime: Long, endTime: Long): String{
        var dateString = "${convertLongToTime(time=startTime)} - ${convertLongToTime(time=endTime)}"
        val startTimeDateString = convertLongToTime("yyyy.MM.dd", startTime)
        val endTimeDateString = convertLongToTime("yyyy.MM.dd", endTime)
        if (startTimeDateString == endTimeDateString){
            dateString = buildAnnotatedString {
                append(startTimeDateString)
                append("\n")
                append(convertLongToTime("HH:mm", startTime))
                append(" - ")
                append(convertLongToTime("HH:mm", endTime))
            }.toString()
        }
        return dateString
    }


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

    fun getCurrentHour(): Int {
        return calendar.get(Calendar.HOUR_OF_DAY)
    }

    fun getCurrentMinute(): Int {
        return calendar.get(Calendar.MINUTE)
    }

    //receive time in string, ex "5:00", "18:05"
    //return a Long Date Time
    fun getDateTime(time: String): Long {
        val timeArr = time.split(":")
        calendar.set(Calendar.HOUR_OF_DAY, timeArr[0].toInt());
        calendar.set(Calendar.MINUTE, timeArr[1].toInt());

        return calendar.time.time;
    }
}