package com.mustafadakhel.kompost.lifecycle.fragment

import androidx.fragment.app.Fragment
import com.mustafadakhel.kompost.core.DefaultProducer
import com.mustafadakhel.kompost.core.ProduceKey
import com.mustafadakhel.kompost.core.Producer
import com.mustafadakhel.kompost.core.kompostLogger
import com.mustafadakhel.kompost.core.producerOrNull
import com.mustafadakhel.kompost.lifecycle.activity.RootActivitiesFarm
import com.mustafadakhel.kompost.lifecycle.activity.rootActivitiesFarm

/**
 * An extension property for [RootActivitiesFarm] that generates a [ProduceKey] for the root fragments farm.
 * The [ProduceKey] is generated using the class of the [ApplicationRootFragmentsFarm] and a tag which is the ID of the root fragments farm.
 *
 * @return The [ProduceKey] for the root fragments farm.
 */
internal val RootActivitiesFarm.rootFragmentsFarmProduceKey: ProduceKey
    get() = ProduceKey(
        ApplicationRootFragmentsFarm::class,
        tag = "$id$ActivityRootFragmentsFarmName"
    )

/**
 * A constant that holds the name of the FragmentsFarm.
 * This name is used as part of the unique identifier when generating a [ProduceKey] for the ApplicationRootFragmentsFarm.
 */
private const val ActivityRootFragmentsFarmName = "FragmentsFarm"

/**
 * A class that represents the root fragments farm in the com.mustafadakhel.kompost.android.com.mustafadakhel.kompost.android.application.
 * It is a producer that delegates its production responsibilities to a [DefaultProducer].
 * The [DefaultProducer] is created with an ID and the [RootActivitiesFarm] as its parent.
 *
 * @param id The ID of the root fragments farm.
 * @param applicationFarm The [RootActivitiesFarm] associated with this [ApplicationRootFragmentsFarm].
 */
public class ApplicationRootFragmentsFarm internal constructor(
    id: String,
    applicationFarm: RootActivitiesFarm
) : Producer by DefaultProducer(id = id, parent = applicationFarm)

/**
 * An extension function for [RootActivitiesFarm] that retrieves the existing root fragments farm.
 * The function uses the [rootFragmentsFarmProduceKey] extension property to retrieve the root fragments farm.
 *
 * @return The existing root fragments farm, or null if it does not exist.
 */
public fun RootActivitiesFarm.rootFragmentsFarmOrNull(): ApplicationRootFragmentsFarm? =
    producerOrNull(this, rootFragmentsFarmProduceKey)

/**
 * An internal function for the [RootActivitiesFarm] class that gets or creates a [ApplicationRootFragmentsFarm].
 *
 * The function first tries to get the existing [ApplicationRootFragmentsFarm] using the [rootFragmentsFarmOrNull] function.
 * If the [ApplicationRootFragmentsFarm] does not exist, it creates a new one using the [createRootFragmentsFarm] function.
 * The [productionScope] parameter is a lambda with [ApplicationRootFragmentsFarm] as its receiver that is used to configure the new [ApplicationRootFragmentsFarm] if it is created. Default is an empty lambda.
 *
 * @param productionScope A lambda with [ApplicationRootFragmentsFarm] as its receiver that is used to configure the new [ApplicationRootFragmentsFarm] if it is created. Default is an empty lambda.
 * @return The existing or newly created [ApplicationRootFragmentsFarm].
 */
internal fun RootActivitiesFarm.getOrCreateFragmentsFarm(
    productionScope: ApplicationRootFragmentsFarm.() -> Unit = {}
): ApplicationRootFragmentsFarm {
    return rootFragmentsFarmOrNull() ?: createRootFragmentsFarm(productionScope)
}

/**
 * An extension function for [Fragment] that retrieves the existing root fragments farm.
 * The function first retrieves the [RootActivitiesFarm] associated with the activity of the [Fragment].
 * Then, it uses the [rootFragmentsFarmOrNull] extension function to retrieve the root fragments farm.
 * If the root fragments farm does not exist, the function throws an error.
 *
 * @return The existing root fragments farm.
 * @throws IllegalStateException if the root fragments farm does not exist.
 */
public fun Fragment.rootFragmentsFarm(): ApplicationRootFragmentsFarm {
    val activity = activity ?: error("Fragment is not attached to activity")
    return activity.rootActivitiesFarm()
        .rootFragmentsFarmOrNull()
        ?: error("Root fragments farm not created")
}

/**
 * An exception that is thrown when an attempt is made to create a [RootActivitiesFarm] that already exists.
 */
public class RootFragmentsFarmAlreadyExistsException :
    IllegalStateException("Root fragments farm already exists")

/**
 * An extension function for [RootActivitiesFarm] that creates a new root fragments farm.
 * The function first checks if a root fragments farm already exists using the [rootFragmentsFarmOrNull] function.
 * If a root fragments farm already exists, the function throws an IllegalArgumentException.
 * If a root fragments farm does not exist, the function creates a new one using the [ApplicationRootFragmentsFarm] constructor.
 * The function takes a [productionScope] as a parameter, which is a lambda that defines the production scope of the new root fragments farm.
 * The [productionScope] is applied to the new root fragments farm.
 * After creating the new root fragments farm, the function produces it using the [Producer.produce] function and the [rootFragmentsFarmProduceKey] extension property.
 *
 * @param productionScope The lambda that defines the production scope of the new root fragments farm.
 * @return The newly created root fragments farm.
 * @throws IllegalArgumentException if a root fragments farm already exists.
 */
public fun RootActivitiesFarm.createRootFragmentsFarm(
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