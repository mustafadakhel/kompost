package com.mustafadakhel.kompost.lifecycle.activity

import androidx.activity.ComponentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.mustafadakhel.kompost.core.DefaultProducer
import com.mustafadakhel.kompost.core.ProduceKey
import com.mustafadakhel.kompost.core.Producer
import com.mustafadakhel.kompost.core.kompostLogger
import com.mustafadakhel.kompost.core.producerOrNull
import kotlin.reflect.KClass

/**
 * An extension property for [ComponentActivity] to get the [ProduceKey] for the [ActivityScopedFarm].
 * The [ProduceKey] is created using the class of the [ComponentActivity] and the farm ID as the tag.
 * This key is used to identify the [ActivityScopedFarm] associated with the [ComponentActivity].
 */
internal val ComponentActivity.activityScopedFarmProduceKey: ProduceKey
    get() = ProduceKey(this::class, tag = this::class.farmId)

/**
 * An extension property for [KClass] of [ComponentActivity] to get the farm ID for the [ActivityScopedFarm].
 * The farm ID is a unique identifier for the [ActivityScopedFarm] associated with the [ComponentActivity] class.
 * It is created by concatenating the name of the [ActivityScopedFarm] and the canonical name of the activity class.
 */
internal val KClass<out ComponentActivity>.farmId: String
    get() = "$ActivityScopedFarmName.${this.java.canonicalName}"

/**
 * A constant that holds the name of the ActivityScopedFarm. This name is used as part of the unique identifier for each ActivityScopedFarm instance.
 */
private const val ActivityScopedFarmName = "ActivityScopedFarm"

/**
 * A class that represents an [ActivityScopedFarm] which is a type of [Producer].
 * It is constructed with a [KClass] of [ComponentActivity] and a [RootActivitiesFarm].
 * The [ActivityScopedFarm] is identified by the farmId of the activity class and is a child of the [RootActivitiesFarm].
 *
 * This farm manages dependencies that are scoped to a specific activity class.
 * All instances of the same activity class share the same [ActivityScopedFarm].
 *
 * @param activityClass The [KClass] of the [ComponentActivity] associated with this [ActivityScopedFarm].
 * @param activitiesFarm The [RootActivitiesFarm] that is the parent of this [ActivityScopedFarm].
 * @constructor Creates a new instance of [ActivityScopedFarm].
 */
public class ActivityScopedFarm internal constructor(
    activityClass: KClass<out ComponentActivity>,
    activitiesFarm: RootActivitiesFarm
) : Producer by DefaultProducer(id = activityClass.farmId, parent = activitiesFarm)

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
 * An extension function for [ComponentActivity] that either retrieves an existing [ActivityScopedFarm] or creates a new one.
 * The function first tries to retrieve an existing [ActivityScopedFarm] using the [activityScopedFarmOrNull] function.
 * If an existing [ActivityScopedFarm] is not found, a new one is created using the [createActivityScopedFarm] function.
 * The [productionScope] parameter is a lambda with [ActivityScopedFarm] as its receiver that is used to configure the [ActivityScopedFarm].
 * The [productionScope] is only used when a new [ActivityScopedFarm] is created.
 * The [activitiesFarm] parameter is an instance of [RootActivitiesFarm] which is used to retrieve or create the [ActivityScopedFarm].
 *
 * @param activitiesFarm An instance of [RootActivitiesFarm] which is used to retrieve or create the [ActivityScopedFarm]. Default value is the root activities farm.
 * @param productionScope A lambda with [ActivityScopedFarm] as its receiver that is used to configure the [ActivityScopedFarm]. Default value is an empty lambda.
 * @return The existing or newly created [ActivityScopedFarm].
 */
internal fun ComponentActivity.getOrCreateActivityScopedFarm(
    activitiesFarm: RootActivitiesFarm = rootActivitiesFarm(),
    productionScope: ActivityScopedFarm.() -> Unit = {}
): ActivityScopedFarm {
    return activityScopedFarmOrNull(activitiesFarm) ?: createActivityScopedFarm(
        activitiesFarm,
        productionScope
    )
}

/**
 * An exception that is thrown when an attempt is made to create an [ActivityScopedFarm] that already exists.
 */
public class ActivityScopedFarmAlreadyExistsException :
    IllegalStateException("Activity farm already exists")

/**
 * An extension function for the [ComponentActivity] class that creates a new [ActivityScopedFarm].
 * The function first checks if an [ActivityScopedFarm] already exists for the [ComponentActivity] using the [activityScopedFarmOrNull] function.
 * If an [ActivityScopedFarm] already exists, the function throws an [ActivityScopedFarmAlreadyExistsException].
 * If an [ActivityScopedFarm] does not exist, a new one is created using the [ActivityScopedFarm] constructor.
 * The new [ActivityScopedFarm] is then configured using the [productionScope] parameter, which is a lambda with [ActivityScopedFarm] as its receiver.
 * After the [ActivityScopedFarm] is created and configured, it is added to the [RootActivitiesFarm] associated with the [ComponentActivity].
 *
 * @param activitiesFarm An instance of [RootActivitiesFarm] which is used to retrieve or create the [ActivityScopedFarm]. Default value is the root activities farm.
 * @param productionScope A lambda with [ActivityScopedFarm] as its receiver that is used to configure the [ActivityScopedFarm].
 * @return The newly created [ActivityScopedFarm].
 * @throws ActivityScopedFarmAlreadyExistsException If an [ActivityScopedFarm] already exists for the [ComponentActivity].
 */
internal fun ComponentActivity.createActivityScopedFarm(
    activitiesFarm: RootActivitiesFarm = rootActivitiesFarm(),
    productionScope: ActivityScopedFarm.() -> Unit = {}
): ActivityScopedFarm {
    if (activityScopedFarmOrNull(activitiesFarm) != null)
        throw ActivityScopedFarmAlreadyExistsException()
    kompostLogger.log("Creating activity farm for $this")
    return ActivityScopedFarm(this::class, activitiesFarm)
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
