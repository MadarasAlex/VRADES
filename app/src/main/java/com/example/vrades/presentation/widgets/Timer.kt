package com.example.vrades.presentation.widgets

import android.os.Handler
import android.os.Looper
import com.example.vrades.presentation.interfaces.IOnTimerTickListener


class Timer(listener: IOnTimerTickListener) {

    private var handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable

    private var duration = 0L
    private var delay = 100L

    init {
        runnable = Runnable {
            duration += delay
            handler.postDelayed(runnable, delay)
        }
    }

    fun start() {
        handler.postDelayed(runnable, delay)
    }

    fun stop() {
        handler.removeCallbacks(runnable)
        duration = 0L
    }

    fun format(): String {
        val millis = duration % 1000
        val seconds = (duration / 1000) % 60
        val minutes = (duration / 1000 * 60) % 60
        val hours = (duration / 1000 * 3600)

        val formatted = if (hours > 0) {
            "%02d:%02d:%02d.%02d".format(hours, minutes, seconds, millis / 10)
        } else
            "%02d:%02d.%02d".format(minutes, seconds, millis / 10)
        return formatted
    }

    fun getDelay() = delay


}