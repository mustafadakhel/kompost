package com.mustafadakhel.kompost.android.application

import android.app.Application
import android.content.Context
import com.mustafadakhel.kompost.core.DefaultProducer
import com.mustafadakhel.kompost.core.GlobalFarm
import com.mustafadakhel.kompost.core.KompostLogger
import com.mustafadakhel.kompost.core.ProduceKey
import com.mustafadakhel.kompost.core.Producer
import com.mustafadakhel.kompost.core.globalFarm
import com.mustafadakhel.kompost.core.kompostLogger
import com.mustafadakhel.kompost.core.produce
import com.mustafadakhel.kompost.core.producerOrNull
import com.mustafadakhel.kompost.core.supply

/**
 * An extension property for the [Application] class.
 * It generates a [ProduceKey] for the [ApplicationFarm] using the class of the application and [applicationFarmId].
 *
 * @receiver The [Application] instance for which the [ProduceKey] is generated.
 * @return The [ProduceKey] for the [ApplicationFarm].
 */
internal val Application.applicationFarmProduceKey: ProduceKey
    get() = ProduceKey(this::class, tag = applicationFarmId)

/**
 * An extension property for the [Application] class.
 * It generates a unique identifier for the [ApplicationFarm] using the name of the [ApplicationFarm] class and the hash code of the application.
 *
 * @receiver The [Application] instance for which the unique identifier is generated.
 * @return The unique identifier for the [ApplicationFarm].
 */
private val Application.applicationFarmId: String
    get() = "$ApplicationFarmName.${this.hashCode()}"

/**
 * A private constant that holds the name of the [ApplicationFarm] class.
 * This name is used as a tag when generating a unique identifier for the [ApplicationFarm].
 */
private const val ApplicationFarmName = "ApplicationFarm"

/**
 * [ApplicationFarm] is a class that represents a farm specific to an application.
 * It is constructed internally with an [Application] instance and a [GlobalFarm] instance as parents.
 * It delegates its [Producer] responsibilities to a [DefaultProducer] instance created with the application's unique farm ID and the parent [GlobalFarm].
 *
 * @param application The [Application] instance for which this [ApplicationFarm] is created.
 * @param parent The [GlobalFarm] instance that acts as the parent of this [ApplicationFarm].
 * @constructor Creates an [ApplicationFarm] instance with the provided [Application] and [GlobalFarm].
 */
public class ApplicationFarm internal constructor(
    application: Application,
    parent: GlobalFarm
) : Producer by DefaultProducer(id = application.applicationFarmId, parent = parent)

/**
 * An extension function for the [Application] class.
 * It tries to get the [ApplicationFarm] for the application from the [GlobalFarm].
 * If the [ApplicationFarm] does not exist, it returns null.
 * The [globalFarm] parameter is an instance of [GlobalFarm] which is used to retrieve the [ApplicationFarm]. Default value is the global farm.
 *
 * @param globalFarm An instance of [GlobalFarm] which is used to retrieve the [ApplicationFarm]. Default value is the global farm.
 * @return The [ApplicationFarm] for the application, or null if it does not exist.
 */
public fun Application.applicationFarmOrNull(
    globalFarm: GlobalFarm = globalFarm()
): ApplicationFarm? = producerOrNull(globalFarm, applicationFarmProduceKey)

/**
 * An extension function for the [Application] class that retrieves the [ApplicationFarm] for the application.
 * The function first tries to retrieve the [ApplicationFarm] using the [applicationFarmOrNull] function.
 * If the [ApplicationFarm] does not exist, the function throws an [IllegalStateException].
 * The [globalFarm] parameter is an instance of [GlobalFarm] which is used to retrieve the [ApplicationFarm]. Default value is the global farm.
 *
 * @param globalFarm An instance of [GlobalFarm] which is used to retrieve the [ApplicationFarm]. Default value is the global farm.
 * @return The [ApplicationFarm] for the application.
 * @throws IllegalStateException If the [ApplicationFarm] does not exist.
 */
public fun Application.applicationFarm(
    globalFarm: GlobalFarm = globalFarm()
): ApplicationFarm {
    return applicationFarmOrNull(globalFarm) ?: error("Application farm not created")
}

/**
 * An exception that is thrown when an attempt is made to create an [ApplicationFarm] that already exists.
 */
public class ApplicationFarmAlreadyExistsException :
    IllegalStateException("Application farm already exists")

/**
 * An extension function for the [Application] class that creates an [ApplicationFarm].
 * The function first checks if an [ApplicationFarm] already exists for the application using the [applicationFarmOrNull] function.
 * If an [ApplicationFarm] already exists, the function throws an [ApplicationFarmAlreadyExistsException].
 * If an [ApplicationFarm] does not exist, a new one is created using the [ApplicationFarm] constructor.
 * The new [ApplicationFarm] is then configured using the [productionScope] parameter, which is a lambda with [ApplicationFarm] as its receiver.
 * After the [ApplicationFarm] is created and configured, it is added to the [GlobalFarm] associated with the application.
 *
 * @param globalFarm An instance of [GlobalFarm] which is used to retrieve or create the [ApplicationFarm]. Default value is the global farm.
 * @param productionScope A lambda with [ApplicationFarm] as its receiver that is used to configure the [ApplicationFarm]. Default value is an empty lambda.
 * @return The newly created [ApplicationFarm].
 * @throws ApplicationFarmAlreadyExistsException If an [ApplicationFarm] already exists for the application.
 */
@KompostDsl
public fun Application.createApplicationFarm(
    globalFarm: GlobalFarm = globalFarm(),
    loggingEnabled: Boolean = false,
    productionScope: ApplicationFarm.() -> Unit = {}
): ApplicationFarm {

    KompostLogger.toggleLogging(loggingEnabled)

    if (applicationFarmOrNull(globalFarm) != null) {
        throw ApplicationFarmAlreadyExistsException()
    }
    return ApplicationFarm(this, globalFarm)
        .apply {
            // Produces the application context in the [ApplicationFarm] for convenience.
            produceApplicationContext(applicationContext)
            kompostLogger.log("Creating application farm for ${this@createApplicationFarm}")
            productionScope()
        }
        .also {
            val key = applicationFarmProduceKey
            globalFarm.produce(key) { it }
            kompostLogger.log("Application farm created for ${this@createApplicationFarm}")
        }
}

/**
 * A private constant that holds the tag for the application context in 'Kompost'.
 * This tag is used when producing and supplying the application context in the [ApplicationFarm].
 */
private const val ApplicationContextTag = "Kompost:ApplicationContextTag"

/**
 * A private extension function for the [ApplicationFarm] class.
 * It produces the application context in the [ApplicationFarm].
 *
 * The function uses the [ApplicationContextTag] as the key to produce the application context.
 * The produced value is the provided [applicationContext].
 *
 * @param applicationContext The [Context] instance that represents the application context.
 */
private fun ApplicationFarm.produceApplicationContext(
    applicationContext: Context
) = produce(ApplicationContextTag) {
    applicationContext
}

/**
 * An extension function for the [Producer] interface.
 * It supplies the application context that has been produced with the creation of the [ApplicationFarm]
 *
 * The function uses the [ApplicationContextTag] as the key to supply the application context.
 *
 * @return The [Context] instance that represents the application context.
 */
public fun Producer.supplyApplicationContext(): Context =
    supply(ApplicationContextTag)

/**
 * An inline function for the [Application] class.
 * It supplies a value from the [ApplicationFarm] for the application.
 *
 * The function first tries to get the [ApplicationFarm] for the application.
 * If the [ApplicationFarm] does not exist, it throws an error.
 * Otherwise, it supplies the value from the [ApplicationFarm] using the provided [tag].
 *
 * @param tag The tag used to supply the value from the [ApplicationFarm]. Default is null.
 * @param globalFarm An instance of [GlobalFarm] which is used to retrieve the [ApplicationFarm]. Default value is the global farm.
 * @return The supplied value.
 * @throws IllegalStateException If the [ApplicationFarm] does not exist.
 */
public inline fun <reified T> Application.applicationSupply(
    tag: String? = null,
    globalFarm: GlobalFarm = globalFarm()
): T = applicationFarmOrNull(globalFarm)?.supply(tag) ?: error("Application farm not created")

/**
 * An inline function for the [Application] class.
 * It lazily supplies a value from the [ApplicationFarm] for the application.
 *
 * The function uses the [applicationSupply] function to supply the value.
 * The supplied value is cached, so it is only supplied once.
 *
 * @param tag The tag used to supply the value from the [ApplicationFarm]. Default is null.
 * @param globalFarm An instance of [GlobalFarm] which is used to retrieve the [ApplicationFarm]. Default value is the global farm.
 * @return A [Lazy] instance that represents the lazily supplied value.
 */
public inline fun <reified T> Application.lazyApplicationSupply(
    tag: String? = null,
    globalFarm: GlobalFarm = globalFarm()
): Lazy<T> = lazy { applicationSupply(tag, globalFarm) }
