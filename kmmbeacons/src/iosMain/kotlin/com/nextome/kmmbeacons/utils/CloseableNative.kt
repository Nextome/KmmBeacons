package com.nextome.kbeaconscanner.utils

public actual interface Closeable {
    public actual fun close()
}

@PublishedApi
internal actual fun Throwable.addSuppressedInternal(other: Throwable) {
}