package com.dakhel.kompost.application

import android.app.Application
import android.content.Context
import com.dakhel.kompost.Farm
import com.dakhel.kompost.GlobalFarm
import com.dakhel.kompost.ProduceKey
import com.dakhel.kompost.Producer
import com.dakhel.kompost.farmOrNull
import com.dakhel.kompost.globalFarm
import com.dakhel.kompost.produce
import com.dakhel.kompost.supply

private const val ApplicationFarmName = "ApplicationFarm"

internal val Application.applicationFarmProduceKey: ProduceKey
    get() = ProduceKey(this::class, tag = applicationFarmId)

private val Application.applicationFarmId: String
    get() = "${ApplicationFarmName}.${this.hashCode()}"

class ApplicationFarm internal constructor(
    application: Application,
    parent: GlobalFarm
) : Producer by Farm(application.applicationFarmId, parent)

fun Application.applicationFarmOrNull(): ApplicationFarm? =
    farmOrNull(globalFarm(), applicationFarmProduceKey)

fun Application.applicationFarm(): ApplicationFarm {
    return applicationFarmOrNull() ?: error("Application farm not created")
}

class ApplicationFarmAlreadyExistsException :
    IllegalStateException("Application farm already exists")

fun Application.createApplicationFarm(
    productionScope: ApplicationFarm.() -> Unit = {}
): ApplicationFarm {
    val globalFarm = globalFarm()

    if (applicationFarmOrNull() != null) {
        throw ApplicationFarmAlreadyExistsException()
    }
    return ApplicationFarm(this, globalFarm)
        .apply {
            produceApplicationContext(applicationContext)
            productionScope()
        }
        .also {
            val key = applicationFarmProduceKey
            globalFarm.produce(key) { it }
        }
}

private const val ApplicationContextTag = "Kompost:ApplicationContextTag"

fun ApplicationFarm.produceApplicationContext(
    applicationContext: Context
) = produce(ApplicationContextTag) {
    applicationContext
}

fun Producer.supplyApplicationContext(): Context = supply(ApplicationContextTag)

inline fun <reified T> Application.applicationSupply(
    tag: String? = null
): T = applicationFarmOrNull()?.supply(tag) ?: error("Application farm not created")

inline fun <reified T> Application.lazyApplicationSupply(
    tag: String? = null
): Lazy<T> = lazy { applicationSupply(tag) }
