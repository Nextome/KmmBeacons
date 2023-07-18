package com.nextome.kmmbeacons_example.android

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.nextome.kmmbeacons.KmmBeacons

class MainViewModel(ctx: Application): AndroidViewModel(ctx) {

    var kmmBeacons: KmmBeacons = KmmBeacons()

    init {
        startScan()
    }

    fun startScan() = kmmBeacons.startScan()
    fun stopScan() = kmmBeacons.stopScan()
    fun observeResults() = kmmBeacons.observeResults()
    fun setRssiThreshold(value: Int) = kmmBeacons.setRssiThreshold(value)

    fun observeNonBeacons() = kmmBeacons.observeNonBeacons()

    override fun onCleared() {
        super.onCleared()
        stopScan()
    }
}