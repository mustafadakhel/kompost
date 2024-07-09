package com.mustafadakhel.kompost.lifecycle.activity

import androidx.activity.ComponentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.mustafadakhel.kompost.core.DefaultProducer
import com.mustafadakhel.kompost.core.ProduceKey
import com.mustafadakhel.kompost.core.Producer
import com.mustafadakhel.kompost.core.kompostLogger
import com.mustafadakhel.kompost.core.producerOrNull

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
    get() = "$ActivityScopedFarmName.${this.hashCode()}"

/**
 * A constant that holds the name of the ActivityScopedFarm. This name is used as part of the unique identifier for each ActivityScopedFarm instance.
 */
private const val ActivityScopedFarmName = "ActivityScopedFarm"

/**
 * The [ActivityScopedFarm] class is responsible for managing the lifecycle of activity-scoped dependencies in the com.mustafadakhel.kompost.android.com.mustafadakhel.kompost.android.application.
 * It is a producer of activity-scoped dependencies and uses the [DefaultProducer] class to manage the production of these dependencies.
 * The [ActivityScopedFarm] class is created with an activity and an instance of [RootActivitiesFarm].
 *
 * @param activity The [ComponentActivity] that this [ActivityScopedFarm] is associated with.
 * @param activitiesFarm The [RootActivitiesFarm] that this [ActivityScopedFarm] belongs to.
 * @constructor Creates a new instance of [ActivityScopedFarm].
 */
public class ActivityScopedFarm internal constructor(
    activity: ComponentActivity,
    activitiesFarm: RootActivitiesFarm
) : Producer by DefaultProducer(id = activity.farmId, parent = activitiesFarm)

/**
 * An extension function for [ComponentActivity] that retrieves the existing [ActivityScopedFarm].
 * The function uses the [activityScopedFarmProduceKey] extension property to retrieve the [ActivityScopedFarm].
 * If an [ActivityScopedFarm] does not exist, the function returns null.
 *
 * @param activitiesFarm The [RootActivitiesFarm] associated with the [ComponentActivity]. Default value is the root activities farm.
 * @return The existing [ActivityScopedFarm], or null if it does not exist.
 */
public fun ComponentActivity.activityScopedFarmOrNull(
    activitiesFarm: RootActivitiesFarm = rootActivitiesFarm()
): ActivityScopedFarm? = producerOrNull(activitiesFarm, activityScopedFarmProduceKey)

/**
 * An extension function to get or create an [ActivityScopedFarm] for a [ComponentActivity].
 * This function retrieves the [ActivityScopedFarm] from the [RootActivitiesFarm] using the [activityScopedFarmProduceKey].
 * If an [ActivityScopedFarm] does not exist, a new one is created and added to the [RootActivitiesFarm].
 * The function takes a lambda function as a parameter, which is used to set up the [ActivityScopedFarm].
 *
 * @param activitiesFarm The [RootActivitiesFarm] to retrieve or add the [ActivityScopedFarm] to.
 * @param productionScope A lambda function that sets up the [ActivityScopedFarm].
 * @return The retrieved or created [ActivityScopedFarm].
 */
internal fun ComponentActivity.getOrCreateActivityScopedFarm(
    activitiesFarm: RootActivitiesFarm = rootActivitiesFarm(),
    productionScope: ActivityScopedFarm.() -> Unit = {}
): ActivityScopedFarm {
    return producerOrNull(activitiesFarm, activityScopedFarmProduceKey)
        ?: createActivityScopedFarm(activitiesFarm, productionScope)
}

/**
 * An exception that is thrown when an attempt is made to create an [ActivityScopedFarm] that already exists.
 */
public class ActivityScopedFarmAlreadyExistsException :
    IllegalStateException("Activity farm already exists")

/**
 * An extension function to create an [ActivityScopedFarm] for a [ComponentActivity].
 * This function checks if an [ActivityScopedFarm] already exists using the [activityScopedFarmOrNull] function.
 * If an [ActivityScopedFarm] already exists, an [ActivityScopedFarmAlreadyExistsException] is thrown.
 * If an [ActivityScopedFarm] does not exist, a new one is created and added to the [RootActivitiesFarm].
 * The function takes a lambda function as a parameter, which is used to set up the [ActivityScopedFarm].
 *
 * @param activitiesFarm The [RootActivitiesFarm] to add the [ActivityScopedFarm] to.
 * @param productionScope A lambda function that sets up the [ActivityScopedFarm].
 * @return The created [ActivityScopedFarm].
 * @throws ActivityScopedFarmAlreadyExistsException if an [ActivityScopedFarm] already exists.
 */
internal fun ComponentActivity.createActivityScopedFarm(
    activitiesFarm: RootActivitiesFarm = rootActivitiesFarm(),
    productionScope: ActivityScopedFarm.() -> Unit = {}
): ActivityScopedFarm {
    if (activityScopedFarmOrNull(activitiesFarm) != null)
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
 * A function that is used to produce an [ActivityScopedFarm] for a given [ComponentActivity] within the [RootActivitiesFarm].
 * It first creates a [ProduceKey] for the [ActivityScopedFarm] using the [activityScopedFarmProduceKey] extension property.
 * Then, it adds a [DefaultLifecycleObserver] to the lifecycle of the [ComponentActivity] to handle the destruction of the [ActivityScopedFarm] when the [ComponentActivity] is destroyed.
 * Finally, it produces the [ActivityScopedFarm] in the [RootActivitiesFarm] using the created [ProduceKey].
 *
 * @param activity The [ComponentActivity] for which the [ActivityScopedFarm] is to be produced.
 * @param farm The [ActivityScopedFarm] to be produced.
 */
private fun RootActivitiesFarm.produceActivityScopedFarm(
    activity: ComponentActivity,
    farm: ActivityScopedFarm
) {
    val key = activity.activityScopedFarmProduceKey
    kompostLogger.log("Producing activity farm for $activity with key $key")
    activity.lifecycle.addObserver(object : DefaultLifecycleObserver {
        override fun onDestroy(owner: LifecycleOwner) {
            kompostLogger.log("Destroying activity farm for $activity with key $key")
            farm.destroyAllCrops()
            destroy(key)
            activity.lifecycle.removeObserver(this)
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
public inline fun <reified T> ComponentActivity.lazyActivitySupply(
    tag: String? = null
): Lazy<T> = lazy { activitySupply(tag) }

/**
 * An extension function for [ComponentActivity] to supply an activity-scoped dependency.
 * This function retrieves the dependency from the [ActivityScopedFarm] associated with the [ComponentActivity].
 * The [ActivityScopedFarm] is retrieved or created using the [getOrCreateActivityScopedFarm] function.
 * The function takes an optional tag as a parameter, which is used as part of the [ProduceKey] to identify the dependency.
 * The function also takes an [RootActivitiesFarm] as a parameter, which is used to retrieve or create the [ActivityScopedFarm].
 *
 * @param tag The tag to identify the dependency. Default value is null.
 * @param activitiesFarm The [RootActivitiesFarm] to retrieve or add the [ActivityScopedFarm] to. Default value is the root activities farm.
 * @return The retrieved dependency of type [T].
 */
public inline fun <reified T> ComponentActivity.activitySupply(
    tag: String? = null,
    activitiesFarm: RootActivitiesFarm = rootActivitiesFarm()
): T {
    return (activityScopedFarmOrNull(activitiesFarm) ?: activitiesFarm)
        .supply(ProduceKey(T::class, tag = tag))
}
