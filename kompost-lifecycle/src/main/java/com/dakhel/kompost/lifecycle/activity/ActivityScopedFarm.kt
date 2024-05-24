package com.dakhel.kompost.lifecycle.activity

import androidx.activity.ComponentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.dakhel.kompost.Farm
import com.dakhel.kompost.ProduceKey
import com.dakhel.kompost.Producer
import com.dakhel.kompost.application.ApplicationFarm
import com.dakhel.kompost.farmOrNull
/**
 * An extension property for [ComponentActivity] to get the [ProduceKey] for the [ActivityScopedFarm].
 * The [ProduceKey] is created using the class of the [ComponentActivity] and the farm ID as the tag.
 * This key is used to identify the [ActivityScopedFarm] associated with the [ComponentActivity].
 */
internal val ComponentActivity.activityScopedFarmProduceKey: ProduceKey
    get() = ProduceKey(this::class, tag = farmId)

/**
 * An extension property for [ComponentActivity] to get the farm ID for the [ActivityScopedFarm] associated with the [ComponentActivity].
 * The farm ID is a unique identifier for the [ActivityScopedFarm] associated with the [ComponentActivity].
 * It is created by concatenating the name of the [ActivityScopedFarm] and the hash code of the [ComponentActivity].
 */
internal val ComponentActivity.farmId: String
    get() = "${ActivityScopedFarmName}.${this.hashCode()}"

/**
 * A constant that holds the name of the ActivityScopedFarm. This name is used as part of the unique identifier for each ActivityScopedFarm instance.
 */
private const val ActivityScopedFarmName = "ActivityScopedFarm"

/**
 * The [ActivityScopedFarm] class is responsible for managing the lifecycle of activity-scoped dependencies in the application.
 * It is a producer of activity-scoped dependencies and uses the [Farm] class to manage the production of these dependencies.
 * The [ActivityScopedFarm] class is created with an activity and an instance of [ApplicationRootActivitiesFarm].
 *
 * @param activity The [ComponentActivity] that this [ActivityScopedFarm] is associated with.
 * @param activitiesFarm The [ApplicationRootActivitiesFarm] that this [ActivityScopedFarm] belongs to.
 * @constructor Creates a new instance of [ActivityScopedFarm].
 */
class ActivityScopedFarm internal constructor(
    activity: ComponentActivity,
    activitiesFarm: ApplicationRootActivitiesFarm
) : Producer by Farm(id = activity.farmId, parent = activitiesFarm)

/**
 * An extension function for [ComponentActivity] that retrieves the existing [ActivityScopedFarm].
 * The function uses the [activityScopedFarmProduceKey] extension property to retrieve the [ActivityScopedFarm].
 * If an [ActivityScopedFarm] does not exist, the function returns null.
 *
 * @param activitiesFarm The [ApplicationRootActivitiesFarm] associated with the [ComponentActivity]. Default value is the root activities farm.
 * @return The existing [ActivityScopedFarm], or null if it does not exist.
 */
internal fun ComponentActivity.activityScopedFarmOrNull(
    activitiesFarm: ApplicationRootActivitiesFarm = rootActivitiesFarm()
): ActivityScopedFarm? = farmOrNull(activitiesFarm, activityScopedFarmProduceKey)

/**
 * An extension function to get or create an [ActivityScopedFarm] for a [ComponentActivity].
 * This function retrieves the [ActivityScopedFarm] from the [ApplicationRootActivitiesFarm] using the [activityScopedFarmProduceKey].
 * If an [ActivityScopedFarm] does not exist, a new one is created and added to the [ApplicationRootActivitiesFarm].
 * The function takes a lambda function as a parameter, which is used to set up the [ActivityScopedFarm].
 *
 * @param activitiesFarm The [ApplicationRootActivitiesFarm] to retrieve or add the [ActivityScopedFarm] to.
 * @param productionScope A lambda function that sets up the [ActivityScopedFarm].
 * @return The retrieved or created [ActivityScopedFarm].
 */
fun ComponentActivity.getOrCreateActivityScopedFarm(
    activitiesFarm: ApplicationRootActivitiesFarm = rootActivitiesFarm(),
    productionScope: ActivityScopedFarm.() -> Unit = {}
): ActivityScopedFarm {
    return farmOrNull(activitiesFarm, activityScopedFarmProduceKey)
        ?: createActivityScopedFarm(activitiesFarm, productionScope)
}

/**
 * An exception that is thrown when an attempt is made to create an [ActivityScopedFarm] that already exists.
 */
class ActivityScopedFarmAlreadyExistsException :
    IllegalStateException("Activity farm already exists")

/**
 * An extension function to create an [ActivityScopedFarm] for a [ComponentActivity].
 * This function checks if an [ActivityScopedFarm] already exists using the [activityScopedFarmOrNull] function.
 * If an [ActivityScopedFarm] already exists, an [ActivityScopedFarmAlreadyExistsException] is thrown.
 * If an [ActivityScopedFarm] does not exist, a new one is created and added to the [ApplicationRootActivitiesFarm].
 * The function takes a lambda function as a parameter, which is used to set up the [ActivityScopedFarm].
 *
 * @param activitiesFarm The [ApplicationRootActivitiesFarm] to add the [ActivityScopedFarm] to.
 * @param productionScope A lambda function that sets up the [ActivityScopedFarm].
 * @return The created [ActivityScopedFarm].
 * @throws ActivityScopedFarmAlreadyExistsException if an [ActivityScopedFarm] already exists.
 */
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

/**
 * A function that is used to produce an [ActivityScopedFarm] for a given [ComponentActivity] within the [ApplicationRootActivitiesFarm].
 * It first creates a [ProduceKey] for the [ActivityScopedFarm] using the [activityScopedFarmProduceKey] extension property.
 * Then, it adds a [DefaultLifecycleObserver] to the lifecycle of the [ComponentActivity] to handle the destruction of the [ActivityScopedFarm] when the [ComponentActivity] is destroyed.
 * Finally, it produces the [ActivityScopedFarm] in the [ApplicationRootActivitiesFarm] using the created [ProduceKey].
 *
 * @param activity The [ComponentActivity] for which the [ActivityScopedFarm] is to be produced.
 * @param farm The [ActivityScopedFarm] to be produced.
 */
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

/**
 * An extension function for [ComponentActivity] to lazily supply an activity-scoped dependency.
 * This function uses the [lazy] function to lazily initialize the dependency.
 * The dependency is retrieved using the [activitySupply] function.
 * The function takes an optional tag as a parameter, which is used as part of the [ProduceKey] to identify the dependency.
 *
 * @param tag The tag to identify the dependency. Default value is null.
 * @return A [Lazy] instance that lazily initializes the dependency.
 */
inline fun <reified T> ComponentActivity.lazyActivitySupply(
    tag: String? = null
): Lazy<T> = lazy { activitySupply(tag) }

/**
 * An extension function for [ComponentActivity] to supply an activity-scoped dependency.
 * This function retrieves the dependency from the [ActivityScopedFarm] associated with the [ComponentActivity].
 * The [ActivityScopedFarm] is retrieved or created using the [getOrCreateActivityScopedFarm] function.
 * The function takes an optional tag as a parameter, which is used as part of the [ProduceKey] to identify the dependency.
 * The function also takes an [ApplicationRootActivitiesFarm] as a parameter, which is used to retrieve or create the [ActivityScopedFarm].
 *
 * @param tag The tag to identify the dependency. Default value is null.
 * @param activitiesFarm The [ApplicationRootActivitiesFarm] to retrieve or add the [ActivityScopedFarm] to. Default value is the root activities farm.
 * @return The retrieved dependency of type [T].
 */
inline fun <reified T> ComponentActivity.activitySupply(
    tag: String? = null,
    activitiesFarm: ApplicationRootActivitiesFarm = rootActivitiesFarm()
): T {
    return getOrCreateActivityScopedFarm(activitiesFarm)
        .supply(ProduceKey(T::class, tag = tag))
}
