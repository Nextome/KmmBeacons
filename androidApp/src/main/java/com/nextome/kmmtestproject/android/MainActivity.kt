package com.nextome.kmmtestproject.android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.nextome.kmmbeacons.KmmBeacons
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val kmmBeacons = KmmBeacons(application)

        GlobalScope.launch {
            kmmBeacons.observeResults().collect{
                Log.e("test", "Found ${it.size} beacons")

                it.forEach {
                    Log.e("test", "Found ${it.uuid}, ${it.major}, ${it.minor}")
                }
            }
        }

    }
}