package com.example.android.trackmysleepquality.sleeptracker

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.convertDurationToFormatted
import com.example.android.trackmysleepquality.convertNumericQualityToString
import com.example.android.trackmysleepquality.database.SleepNight

@BindingAdapter("sleepLengthFormatted")
fun TextView.setTextViewSleepLength(item: SleepNight?) {
    item?.let {
        this.text = convertDurationToFormatted(it.startTimeMilli, it.endTimeMilli, context.resources)
    }
}

@BindingAdapter("sleepQualityString")
fun TextView.setTextViewSleepQuality(item: SleepNight?) {
    item?.let {
        this.text = convertNumericQualityToString(it.sleepQuality, context.resources)
    }
}

@BindingAdapter("sleepQualityImage")
fun ImageView.setImageViewSleepQuality(item: SleepNight?) {
    item?.let {
        this.setImageResource(when (it.sleepQuality) {
            0 -> R.drawable.ic_sleep_0
            1 -> R.drawable.ic_sleep_1
            2 -> R.drawable.ic_sleep_2
            3 -> R.drawable.ic_sleep_3
            4 -> R.drawable.ic_sleep_4
            5 -> R.drawable.ic_sleep_5
            else -> R.drawable.ic_sleep_active
        })
    }
}