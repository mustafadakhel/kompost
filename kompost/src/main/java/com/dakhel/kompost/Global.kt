package com.dakhel.kompost

import org.jetbrains.annotations.TestOnly

@TestOnly
fun mockGlobalFarm(mockFarm: GlobalFarm) {
    globalFarm = mockFarm
}

@TestOnly
fun resetGlobalFarm() {
    globalFarm?.destroyAllCrops()
    globalFarm = null
}

class GlobalFarm internal constructor() : Producer by Farm(GlobalFarmId, null)

@Volatile
private var globalFarm: GlobalFarm? = null
private const val globalFarmLock = "globalFarmLock"

@Synchronized
internal fun globalFarm(): GlobalFarm {
    val globalFarm1 = globalFarm
    if (globalFarm1 !== null)
        return globalFarm1

    return synchronized(globalFarmLock) {
        val globalFarm2 = globalFarm
        if (globalFarm2 !== null) {
            globalFarm2
        } else {
            val newGlobalFarm = GlobalFarm()
            globalFarm = newGlobalFarm
            newGlobalFarm
        }
    }
}

private const val GlobalFarmId = "globalFarm"
