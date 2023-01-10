package com.nextome.kmmbeacons_example.android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    val viewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.observeResults().collectLatest {
                    Log.i("KmmBeacons", "Found ${it.size} beacons:")

                    it.forEach {
                        Log.i("KmmBeacons", "${it.uuid}, ${it.major}, ${it.minor}")
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