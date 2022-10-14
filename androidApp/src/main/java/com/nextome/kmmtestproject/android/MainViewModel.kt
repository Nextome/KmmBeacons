package com.nextome.kmmtestproject.android

import android.app.Application
import androidx.lifecycle.ViewModel
import com.nextome.kmmbeacons.KmmBeacons

class MainViewModel: ViewModel() {

    var kmmBeacons: KmmBeacons? = null

    fun initKmmBeacons(context: Application){
        KmmBeacons.init(context)
        kmmBeacons = KmmBeacons()
    }

    fun startScan() = kmmBeacons?.startScan()
    fun stopScan() = kmmBeacons?.stopScan()
    fun observeResults() = kmmBeacons?.observeResults()
}