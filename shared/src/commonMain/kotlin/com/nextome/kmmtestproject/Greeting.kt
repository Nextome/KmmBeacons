package com.nextome.kmmtestproject

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class Greeting {
    fun greeting(): String {
        return "Hello, ${Platform().platform}!"
    }
}

