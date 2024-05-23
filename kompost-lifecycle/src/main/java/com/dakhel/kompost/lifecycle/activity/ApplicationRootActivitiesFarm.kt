package com.dakhel.kompost.lifecycle.activity

import androidx.activity.ComponentActivity
import com.dakhel.kompost.Farm
import com.dakhel.kompost.ProduceKey
import com.dakhel.kompost.Producer
import com.dakhel.kompost.application.ApplicationFarm
import com.dakhel.kompost.application.applicationFarm
import com.dakhel.kompost.farmOrNull

private const val ActivitiesFarmName = "ActivitiesFarm"

internal val ApplicationFarm.rootActivitiesFarmProduceKey: ProduceKey
    get() = ProduceKey(this::class, tag = rootActivitiesFarmId)

private val ApplicationFarm.rootActivitiesFarmId: String
    get() = "${ActivitiesFarmName}.${this.hashCode()}"

class ApplicationRootActivitiesFarm internal constructor(
    applicationFarm: ApplicationFarm
) : Producer by Farm(id = applicationFarm.rootActivitiesFarmId, parent = applicationFarm)

internal fun ApplicationFarm.getOrCreateActivitiesFarm(
    productionScope: ApplicationRootActivitiesFarm.() -> Unit = {}
): ApplicationRootActivitiesFarm {
    return rootActivitiesFarmOrNull() ?: createRootActivitiesFarm(productionScope)
}

internal fun ApplicationFarm.rootActivitiesFarmOrNull(): ApplicationRootActivitiesFarm? =
    farmOrNull(this, rootActivitiesFarmProduceKey)

fun ComponentActivity.rootActivitiesFarm(): ApplicationRootActivitiesFarm {
    return application.applicationFarm().rootActivitiesFarmOrNull()
        ?: error("Activities farm not created")
}

class ActivitiesFarmAlreadyExistsException : IllegalStateException("Activities farm already exists")

fun ApplicationFarm.createRootActivitiesFarm(
    productionScope: ApplicationRootActivitiesFarm.() -> Unit = {}
): ApplicationRootActivitiesFarm {
    if (rootActivitiesFarmOrNull() != null)
        throw ActivitiesFarmAlreadyExistsException()
    return ApplicationRootActivitiesFarm(this)
        .apply(productionScope)
        .also {
            val key = rootActivitiesFarmProduceKey
            produce(key) { it }
        }
}
