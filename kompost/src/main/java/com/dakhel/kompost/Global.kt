package com.dakhel.kompost

import org.jetbrains.annotations.TestOnly

/**
 * Sets the global farm to a mock farm for testing purposes.
 *
 * This function is annotated with [@TestOnly], which means it should only be used in test code.
 * It replaces the current global farm with a mock farm provided as a parameter.
 * This is useful for testing code that interacts with the global farm, as it allows the test to control the behavior of the global farm.
 *
 * @param mockFarm The mock farm to set as the global farm.
 */
@TestOnly
fun mockGlobalFarm(mockFarm: GlobalFarm) {
    globalFarm = mockFarm
}

/**
 * Resets the global farm for testing purposes.
 *
 * This function is annotated with [@TestOnly], which means it should only be used in test code.
 * It destroys all crops in the current global farm and sets the global farm to null.
 * This is useful for testing code that interacts with the global farm, as it allows the test to reset the state of the global farm between tests.
 */
@TestOnly
fun resetGlobalFarm() {
    globalFarm?.destroyAllCrops()
    globalFarm = null
}

/**
 * Represents a global farm as a producer.
 *
 * This interface extends the [Producer] interface, which means it inherits all the methods of a [Producer].
 * Any class implementing this interface will have to provide implementations for the [Producer] methods.
 */
interface GlobalFarm : Producer

/**
 * A class that represents the default implementation of the [GlobalFarm] interface.
 *
 * This class is a private class that implements the [GlobalFarm] interface and delegates all [Producer] methods to an instance of [Farm].
 * The [Farm] instance is initialized with a [GlobalFarmId] and [null] as parameters.
 * The [GlobalFarmId] is a unique identifier for the global farm.
 * The [null] parent parameter indicates that this global farm does not have a parent [Producer] from which to retrieve produce if it is not available in the farm.
 *
 * This class is used internally within the [kompost] package and is not intended to be used directly.
 * Instead, use the [globalFarm] function to get the instance of the [GlobalFarm].
 */
private class DefaultGlobalFarm : GlobalFarm, Producer by Farm(id = GlobalFarmId, parent = null)

/**
 * The [globalFarm] variable holds the instance of the global farm.
 * It is marked as [Volatile] to ensure that changes to it are immediately visible to all threads.
 */
@Volatile
private var globalFarm: GlobalFarm? = null

/**
 * The [globalFarmLock] is a constant that is used as a lock object for synchronizing access to the [globalFarm] variable.
 */
private const val globalFarmLock = "globalFarmLock"

/**
 * Returns the instance of the global farm.
 *
 * This function is marked as [Synchronized] to ensure that only one thread can execute it at a time.
 * If the [globalFarm] variable is not [null], it returns the existing instance.
 * Otherwise, it creates a new instance of [DefaultGlobalFarm], assigns it to the [globalFarm] variable, and returns the new instance.
 * The creation of the new instance is also synchronized on the [globalFarmLock] object to ensure that only one thread can create a new instance at a time.
 *
 * @return The instance of the global farm.
 */
@Synchronized
fun globalFarm(): GlobalFarm {
    val globalFarm1 = globalFarm
    if (globalFarm1 !== null)
        return globalFarm1

    return synchronized(globalFarmLock) {
        val globalFarm2 = globalFarm
        if (globalFarm2 !== null) {
            globalFarm2
        } else {
            val newGlobalFarm = DefaultGlobalFarm()
            globalFarm = newGlobalFarm
            newGlobalFarm
        }
    }
}

/**
 * The [GlobalFarmId] is a constant that holds the unique identifier for the global farm.
 */
private const val GlobalFarmId = "globalFarm"
