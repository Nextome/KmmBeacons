package com.nextome.kbeaconscanner

import platform.CoreLocation.CLProximity

fun CLProximity.asKScanProximity(): KScanProximity{
   return when(this){
        CLProximity.CLProximityFar -> KScanProximity.FAR
        CLProximity.CLProximityImmediate -> KScanProximity.IMMEDIATE
        CLProximity.CLProximityNear -> KScanProximity.NEAR
        CLProximity.CLProximityUnknown -> KScanProximity.UNKNOWN
        else -> KScanProximity.UNKNOWN
    }
}