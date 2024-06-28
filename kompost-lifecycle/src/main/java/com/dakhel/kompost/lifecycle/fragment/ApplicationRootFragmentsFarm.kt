package com.dakhel.kompost.lifecycle.fragment

import androidx.fragment.app.Fragment
import com.dakhel.kompost.DefaultProducer
import com.dakhel.kompost.ProduceKey
import com.dakhel.kompost.Producer
import com.dakhel.kompost.application.kompostLogger
import com.dakhel.kompost.producerOrNull
import com.dakhel.kompost.lifecycle.activity.ApplicationRootActivitiesFarm
import com.dakhel.kompost.lifecycle.activity.rootActivitiesFarm

/**
 * An extension property for [ApplicationRootActivitiesFarm] that generates a [ProduceKey] for the root fragments farm.
 * The [ProduceKey] is generated using the class of the [ApplicationRootFragmentsFarm] and a tag which is the ID of the root fragments farm.
 *
 * @return The [ProduceKey] for the root fragments farm.
 */
val ApplicationRootActivitiesFarm.rootFragmentsFarmProduceKey: ProduceKey
    get() = ProduceKey(
        ApplicationRootFragmentsFarm::class,
        tag = "$id${ActivityRootFragmentsFarmName}"
    )

/**
 * A constant that holds the name of the FragmentsFarm.
 * This name is used as part of the unique identifier when generating a [ProduceKey] for the ApplicationRootFragmentsFarm.
 */
private const val ActivityRootFragmentsFarmName = "FragmentsFarm"

/**
 * A class that represents the root fragments farm in the application.
 * It is a producer that delegates its production responsibilities to a [DefaultProducer].
 * The [DefaultProducer] is created with an ID and the [ApplicationRootActivitiesFarm] as its parent.
 *
 * @param id The ID of the root fragments farm.
 * @param applicationFarm The [ApplicationRootActivitiesFarm] associated with this [ApplicationRootFragmentsFarm].
 */
class ApplicationRootFragmentsFarm internal constructor(
    id: String,
    applicationFarm: ApplicationRootActivitiesFarm
) : Producer by DefaultProducer(id = id, parent = applicationFarm)

/**
 * An extension function for [ApplicationRootActivitiesFarm] that retrieves the existing root fragments farm.
 * The function uses the [rootFragmentsFarmProduceKey] extension property to retrieve the root fragments farm.
 *
 * @return The existing root fragments farm, or null if it does not exist.
 */
fun ApplicationRootActivitiesFarm.rootFragmentsFarmOrNull(): ApplicationRootFragmentsFarm? =
    producerOrNull(this, rootFragmentsFarmProduceKey)

/**
 * An internal function for the [ApplicationRootActivitiesFarm] class that gets or creates a [ApplicationRootFragmentsFarm].
 *
 * The function first tries to get the existing [ApplicationRootFragmentsFarm] using the [rootFragmentsFarmOrNull] function.
 * If the [ApplicationRootFragmentsFarm] does not exist, it creates a new one using the [createRootFragmentsFarm] function.
 * The [productionScope] parameter is a lambda with [ApplicationRootFragmentsFarm] as its receiver that is used to configure the new [ApplicationRootFragmentsFarm] if it is created. Default is an empty lambda.
 *
 * @param productionScope A lambda with [ApplicationRootFragmentsFarm] as its receiver that is used to configure the new [ApplicationRootFragmentsFarm] if it is created. Default is an empty lambda.
 * @return The existing or newly created [ApplicationRootFragmentsFarm].
 */
internal fun ApplicationRootActivitiesFarm.getOrCreateFragmentsFarm(
    productionScope: ApplicationRootFragmentsFarm.() -> Unit = {}
): ApplicationRootFragmentsFarm {
    return rootFragmentsFarmOrNull() ?: createRootFragmentsFarm(productionScope)
}

/**
 * An extension function for [Fragment] that retrieves the existing root fragments farm.
 * The function first retrieves the [ApplicationRootActivitiesFarm] associated with the activity of the [Fragment].
 * Then, it uses the [rootFragmentsFarmOrNull] extension function to retrieve the root fragments farm.
 * If the root fragments farm does not exist, the function throws an error.
 *
 * @return The existing root fragments farm.
 * @throws IllegalStateException if the root fragments farm does not exist.
 */
fun Fragment.rootFragmentsFarm(): ApplicationRootFragmentsFarm {
    val activity = activity ?: error("Fragment is not attached to activity")
    return activity.rootActivitiesFarm()
        .rootFragmentsFarmOrNull()
        ?: error("Root fragments farm not created")
}

/**
 * An exception that is thrown when an attempt is made to create a [ApplicationRootActivitiesFarm] that already exists.
 */
class RootFragmentsFarmAlreadyExistsException :
    IllegalStateException("Root fragments farm already exists")

/**
 * An extension function for [ApplicationRootActivitiesFarm] that creates a new root fragments farm.
 * The function first checks if a root fragments farm already exists using the [rootFragmentsFarmOrNull] function.
 * If a root fragments farm already exists, the function throws an IllegalArgumentException.
 * If a root fragments farm does not exist, the function creates a new one using the [ApplicationRootFragmentsFarm] constructor.
 * The function takes a [productionScope] as a parameter, which is a lambda that defines the production scope of the new root fragments farm.
 * The [productionScope] is applied to the new root fragments farm.
 * After creating the new root fragments farm, the function produces it using the [produce] function and the [rootFragmentsFarmProduceKey] extension property.
 *
 * @param productionScope The lambda that defines the production scope of the new root fragments farm.
 * @return The newly created root fragments farm.
 * @throws IllegalArgumentException if a root fragments farm already exists.
 */
fun ApplicationRootActivitiesFarm.createRootFragmentsFarm(
    productionScope: ApplicationRootFragmentsFarm.() -> Unit = {}
): ApplicationRootFragmentsFarm {
    if (rootFragmentsFarmOrNull() != null)
        throw RootFragmentsFarmAlreadyExistsException()
    kompostLogger.log("Creating root fragments farm")
    return ApplicationRootFragmentsFarm(ActivityRootFragmentsFarmName, this)
        .apply(productionScope)
        .also {
            val key = rootFragmentsFarmProduceKey
            produce(key) { it }
        }
}
