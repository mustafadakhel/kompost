package com.mustafadakhel.kompost.lifecycle.viewModel

import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.mustafadakhel.kompost.core.CannotCastHarvestedSeedException
import com.mustafadakhel.kompost.core.DefaultProducer
import com.mustafadakhel.kompost.core.DuplicateProduceException
import com.mustafadakhel.kompost.core.NoSuchSeedException
import com.mustafadakhel.kompost.core.ProduceKey
import com.mustafadakhel.kompost.core.Producer
import com.mustafadakhel.kompost.core.kompostLogger
import com.mustafadakhel.kompost.core.producerOrNull
import com.mustafadakhel.kompost.lifecycle.KompostLifecycleDsl
import com.mustafadakhel.kompost.lifecycle.activity.RootActivitiesFarm
import com.mustafadakhel.kompost.lifecycle.activity.rootActivitiesFarm
import kotlin.reflect.KClass

private val KClass<out ViewModel>.viewModelProduceKey: ProduceKey
    get() = ProduceKey(this)

private val RootActivitiesFarm.viewModelsFarmProduceKey: ProduceKey
    get() = ProduceKey(kClass = this::class, tag = viewModelsFarmId)

private val RootActivitiesFarm.viewModelsFarmId: String
    get() = "$id$ViewModelsFarmName"

private const val ViewModelsFarmName = "ViewModelsFarm"

/**
 * The [ViewModelsFarm] class is responsible for managing the lifecycle of ViewModel dependencies in the com.mustafadakhel.kompost.android.com.mustafadakhel.kompost.android.application.
 * It is a producer of ViewModels and uses the [DefaultProducer] class to manage the production of ViewModels.
 * The [ViewModelsFarm] class is created with an id and an instance of [RootActivitiesFarm].
 *
 * @param id The unique identifier for this [ViewModelsFarm].
 * @param rootActivitiesFarm The [RootActivitiesFarm] that this [ViewModelsFarm] belongs to.
 * @constructor Creates a new instance of [ViewModelsFarm].
 */
@KompostLifecycleDsl
public class ViewModelsFarm internal constructor(
    id: String,
    rootActivitiesFarm: RootActivitiesFarm,
) : Producer by DefaultProducer(id = id, parent = rootActivitiesFarm) {

    internal val factory by lazy { viewModelFactory() }

    private val viewModelWithSavedStateSeedBeds =
        java.util.concurrent.ConcurrentHashMap<ProduceKey, ViewModelWithSavedStateSeedBed<*>>()

    private fun viewModelFactory(): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val key = modelClass.kotlin.viewModelProduceKey
                kompostLogger.log("Creating ViewModel: $key")
                if (viewModelWithSavedStateSeedBeds.containsKey(key)) {
                    kompostLogger.log("Creating ViewModel with SavedState: $key")
                    return supplyViewModelWithSavedState(key, extras)
                }
                kompostLogger.log("Creating ViewModel: $key")
                return supply(key)
            }
        }
    }

    /**
     * Inline function to produce a ViewModel with a SavedStateHandle.
     * This function takes a lambda function as a parameter, which is used to create the ViewModel.
     * The lambda function should take a SavedStateHandle as a parameter and return an instance of the ViewModel.
     * The function generates a ProduceKey for the ViewModel class and calls the produceViewModelWithSavedState function with the key and the lambda function.
     *
     * @param produce A lambda function that takes a SavedStateHandle as a parameter and returns an instance of the ViewModel.
     */
    @KompostLifecycleDsl
    public inline fun <reified VM : ViewModel> produceViewModelWithSavedState(
        noinline produce: (savedStateHandle: SavedStateHandle) -> VM
    ) {
        val key = ProduceKey(kClass = VM::class, tag = null)
        produceViewModelWithSavedState(key, produce)
    }

    /**
     * Function to produce a ViewModel with a SavedStateHandle.
     * This function takes a ProduceKey and a lambda function as parameters.
     * The lambda function should take a SavedStateHandle as a parameter and return an instance of the ViewModel.
     * The function creates a ViewModelWithSavedStateSeedBed with the lambda function and stores it in the viewModelWithSavedStateSeedBeds map with the ProduceKey as the key.
     * If a ViewModelWithSavedStateSeedBed already exists for the given ProduceKey, a DuplicateProduceException is thrown.
     *
     * @param key The ProduceKey for the ViewModel.
     * @param produce A lambda function that takes a SavedStateHandle as a parameter and returns an instance of the ViewModel.
     */
    @KompostLifecycleDsl
    public fun <VM : ViewModel> produceViewModelWithSavedState(
        key: ProduceKey,
        produce: (savedStateHandle: SavedStateHandle) -> VM
    ) {
        val seedBed = ViewModelWithSavedStateSeedBed { savedStateHandle ->
            produce(savedStateHandle)
        }
        if (viewModelWithSavedStateSeedBeds.containsKey(key)) {
            kompostLogger.log("ViewModel with SavedState already exists: $key")
            throw DuplicateProduceException(key)
        }
        kompostLogger.log("Producing ViewModel with SavedState: $key")
        viewModelWithSavedStateSeedBeds[key] = seedBed
    }

    @Suppress("UNCHECKED_CAST")
    private fun <VM : ViewModel> supplyViewModelWithSavedState(
        key: ProduceKey,
        extras: CreationExtras,
    ): VM {
        val bed = viewModelWithSavedStateSeedBeds[key]
            ?: throw NoSuchSeedException(key)
        kompostLogger.log("Harvesting ViewModel with SavedState: $key")
        val harvestedCrop = bed.harvest(extras)
        kompostLogger.log("Harvested ViewModel with SavedState: $key")
        return harvestedCrop as? VM
            ?: throw CannotCastHarvestedSeedException(key, harvestedCrop)
    }

    private class ViewModelWithSavedStateSeedBed<VM : ViewModel>(
        seed: (savedStateHandle: SavedStateHandle) -> VM
    ) {
        private val crop: ViewModelWithSavedStateCrop<VM> by lazy { ViewModelWithSavedStateCrop(seed) }

        fun harvest(extras: CreationExtras) = crop.harvest(extras)

        class ViewModelWithSavedStateCrop<VM : ViewModel>(
            private val seed: (savedStateHandle: SavedStateHandle) -> VM
        ) {
            fun harvest(extras: CreationExtras) = seed.invoke(extras.createSavedStateHandle())
        }
    }
}

/**
 * An extension function to get the ViewModelsFarm from an [RootActivitiesFarm].
 * This function retrieves the ViewModelsFarm from the [RootActivitiesFarm] using the viewModelsFarmProduceKey.
 * If a ViewModelsFarm does not exist for the given ProduceKey, null is returned.
 *
 * @return The ViewModelsFarm if it exists, null otherwise.
 */
public fun RootActivitiesFarm.viewModelsFarmOrNull(): ViewModelsFarm? =
    producerOrNull(this, viewModelsFarmProduceKey)

/**
 * An extension function to get the ViewModelsFarm from a Fragment.
 * This function retrieves the ViewModelsFarm from the rootActivitiesFarm of the Fragment's activity using the viewModelsFarmOrNull function.
 * If a ViewModelsFarm does not exist, an error is thrown.
 *
 * @return The ViewModelsFarm if it exists.
 * @throws RuntimeException if the ViewModelsFarm does not exist.
 */
public fun Fragment.viewModelsFarm(): ViewModelsFarm {
    return requireActivity().rootActivitiesFarm().viewModelsFarmOrNull()
        ?: error("ViewModels farm not created")
}

/**
 * An extension function to get the ViewModelsFarm from a ComponentActivity.
 * This function retrieves the ViewModelsFarm from the rootActivitiesFarm of the ComponentActivity using the viewModelsFarmOrNull function.
 * If a ViewModelsFarm does not exist, an error is thrown.
 *
 * @return The ViewModelsFarm if it exists.
 * @throws RuntimeException if the ViewModelsFarm does not exist.
 */
public fun ComponentActivity.viewModelsFarm(): ViewModelsFarm {
    return rootActivitiesFarm().viewModelsFarmOrNull()
        ?: error("ViewModels farm not created")
}

/**
 * An extension function to create a ViewModelsFarm in an [RootActivitiesFarm].
 * This function checks if a ViewModelsFarm already exists using the viewModelsFarmOrNull function.
 * If a ViewModelsFarm already exists, an IllegalArgumentException is thrown.
 * If a ViewModelsFarm does not exist, a new ViewModelsFarm is created and added to the [RootActivitiesFarm].
 * The function takes a lambda function as a parameter, which is used to set up the ViewModelsFarm.
 *
 * @param productionScope A lambda function that sets up the ViewModelsFarm.
 * @return The created ViewModelsFarm.
 * @throws IllegalArgumentException if a ViewModelsFarm already exists.
 */
@KompostLifecycleDsl
public fun RootActivitiesFarm.createViewModelsFarm(
    productionScope: ViewModelsFarm.() -> Unit
): ViewModelsFarm {
    require(viewModelsFarmOrNull() == null) {
        "ViewModels farm already exists"
    }
    kompostLogger.log("Creating ViewModelsFarm")
    return ViewModelsFarm(ViewModelsFarmName, this)
        .apply(productionScope)
        .also {
            val key = viewModelsFarmProduceKey
            produce(key) { it }
        }
}

/**
 * Function to supply a ViewModel.
 * This function takes a KClass of the ViewModel, a ViewModelStore, and CreationExtras as parameters.
 * The function creates a ViewModelProvider with the ViewModelStore, the factory, and the CreationExtras.
 * The function then retrieves the ViewModel from the ViewModelProvider using the java class of the ViewModel KClass.
 *
 * @param vmClass The KClass of the ViewModel.
 * @param viewModelStore The ViewModelStore used to store the ViewModel.
 * @param extras The CreationExtras used to create the ViewModel.
 * @return The created ViewModel.
 */
public fun <VM : ViewModel> ViewModelsFarm.supplyViewModel(
    vmClass: KClass<VM>,
    viewModelStore: ViewModelStore,
    extras: CreationExtras,
): VM {
    val provider = ViewModelProvider(viewModelStore, factory, extras)
    return provider[vmClass.java]
}

/**
 * An extension function to get a lazy ViewModel from a ComponentActivity.
 * This function takes two lambda functions as parameters, which are used to get the ViewModelStore and the CreationExtras.
 * The function returns a lazy delegate that retrieves the ViewModel from the ComponentActivity using the viewModel function.
 *
 * @param viewModelStore A lambda function that returns the ViewModelStore used to store the ViewModel.
 * @param extras A lambda function that returns the CreationExtras used to create the ViewModel.
 * @return A lazy delegate for the ViewModel.
 */
public inline fun <reified VM : ViewModel> ComponentActivity.lazyViewModel(
    crossinline viewModelStore: () -> ViewModelStore = { this.viewModelStore },
    crossinline extras: () -> CreationExtras = { this.defaultViewModelCreationExtras },
): Lazy<VM> = lazy {
    viewModel<VM>(viewModelStore(), extras())
}

/**
 * An extension function to get a ViewModel from a ComponentActivity.
 * This function takes a ViewModelStore and CreationExtras as parameters.
 * The function retrieves the ViewModel from the ViewModelsFarm using the supplyViewModel function.
 *
 * @param viewModelStore The ViewModelStore used to store the ViewModel.
 * @param extras The CreationExtras used to create the ViewModel.
 * @return The ViewModel.
 */
public inline fun <reified VM : ViewModel> ComponentActivity.viewModel(
    viewModelStore: ViewModelStore = this.viewModelStore,
    extras: CreationExtras = this.defaultViewModelCreationExtras,
): VM = viewModelsFarm().supplyViewModel(
    vmClass = VM::class,
    viewModelStore = viewModelStore,
    extras = extras,
)

/**
 * An extension function to get a lazy ViewModel from a Fragment.
 * This function takes two lambda functions as parameters, which are used to get the ViewModelStore and the CreationExtras.
 * The function returns a lazy delegate that retrieves the ViewModel from the Fragment using the viewModel function.
 *
 * @param viewModelStore A lambda function that returns the ViewModelStore used to store the ViewModel.
 * @param extras A lambda function that returns the CreationExtras used to create the ViewModel.
 * @return A lazy delegate for the ViewModel.
 */
public inline fun <reified VM : ViewModel> Fragment.lazyViewModel(
    crossinline viewModelStore: () -> ViewModelStore = { this.viewModelStore },
    crossinline extras: () -> CreationExtras = { this.defaultViewModelCreationExtras },
): Lazy<VM> = lazy {
    viewModel<VM>(viewModelStore(), extras())
}

/**
 * An extension function to get a ViewModel from a Fragment.
 * This function takes a ViewModelStore and CreationExtras as parameters.
 * The function retrieves the ViewModel from the ViewModelsFarm using the supplyViewModel function.
 *
 * @param viewModelStore The ViewModelStore used to store the ViewModel.
 * @param extras The CreationExtras used to create the ViewModel.
 * @return The ViewModel.
 */
public inline fun <reified VM : ViewModel> Fragment.viewModel(
    viewModelStore: ViewModelStore = this.viewModelStore,
    extras: CreationExtras = this.defaultViewModelCreationExtras,
): VM = viewModelsFarm().supplyViewModel(
    vmClass = VM::class,
    viewModelStore = viewModelStore,
    extras = extras,
)
