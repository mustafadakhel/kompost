package com.dakhel.kompost.lifecycle.activity

import androidx.activity.ComponentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.dakhel.kompost.Farm
import com.dakhel.kompost.ProduceKey
import com.dakhel.kompost.Producer
import com.dakhel.kompost.farmOrNull

private const val ActivityScopedFarmName = "ActivityScopedFarm"

internal val ComponentActivity.activityScopedFarmProduceKey: ProduceKey
    get() = ProduceKey(this::class, tag = farmId)

internal val ComponentActivity.farmId: String
    get() = "${ActivityScopedFarmName}.${this.hashCode()}"

class ActivityScopedFarm internal constructor(
    activity: ComponentActivity,
    activitiesFarm: ApplicationRootActivitiesFarm
) : Producer by Farm(id = activity.farmId, parent = activitiesFarm)

internal fun ComponentActivity.activityScopedFarmOrNull(
    activitiesFarm: ApplicationRootActivitiesFarm = rootActivitiesFarm()
): ActivityScopedFarm? = farmOrNull(activitiesFarm, activityScopedFarmProduceKey)

fun ComponentActivity.getOrCreateActivityScopedFarm(
    activitiesFarm: ApplicationRootActivitiesFarm = rootActivitiesFarm(),
    productionScope: ActivityScopedFarm.() -> Unit = {}
): ActivityScopedFarm {
    return farmOrNull(activitiesFarm, activityScopedFarmProduceKey)
        ?: createActivityScopedFarm(activitiesFarm, productionScope)
}

class ActivityScopedFarmAlreadyExistsException :
    IllegalStateException("Activity farm already exists")

internal fun ComponentActivity.createActivityScopedFarm(
    activitiesFarm: ApplicationRootActivitiesFarm = rootActivitiesFarm(),
    productionScope: ActivityScopedFarm.() -> Unit = {}
): ActivityScopedFarm {
    if (activityScopedFarmOrNull() != null)
        throw ActivityScopedFarmAlreadyExistsException()
    return ActivityScopedFarm(this, activitiesFarm)
        .apply(productionScope)
        .also {
            activitiesFarm.produceActivityScopedFarm(
                activity = this,
                farm = it
            )
        }
}

private fun ApplicationRootActivitiesFarm.produceActivityScopedFarm(
    activity: ComponentActivity,
    farm: ActivityScopedFarm
) {
    val key = activity.activityScopedFarmProduceKey
    activity.lifecycle.addObserver(object : DefaultLifecycleObserver {
        override fun onDestroy(owner: LifecycleOwner) {
            farm.destroyAllCrops()
            destroy(key)
            super.onDestroy(owner)
        }
    })
    produce(key) { farm }
}

inline fun <reified T> ComponentActivity.lazyActivitySupply(
    tag: String? = null
): Lazy<T> = lazy { activitySupply(tag) }

inline fun <reified T> ComponentActivity.activitySupply(
    tag: String? = null,
    activitiesFarm: ApplicationRootActivitiesFarm = rootActivitiesFarm()
): T {
    return getOrCreateActivityScopedFarm(activitiesFarm)
        .supply(ProduceKey(T::class, tag = tag))
}
