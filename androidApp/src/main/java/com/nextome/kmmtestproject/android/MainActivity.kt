package com.nextome.kmmtestproject.android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    val viewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel.initKmmBeacons(application)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.startScan()

                viewModel.observeResults()?.collectLatest {
                    Log.e("KmmBeacons", "Found ${it.size} beacons:")

                    it.forEach {
                        Log.e("KmmBeacons", "${it.uuid}, ${it.major}, ${it.minor}")
                    }
                }
            }
        }
    }

    override fun onPause() {
        viewModel.stopScan()
        super.onPause()
    }
}