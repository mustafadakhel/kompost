package com.dakhel.kompost.lifecycle.viewModel

import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.dakhel.kompost.CannotCastHarvestedSeedException
import com.dakhel.kompost.DuplicateProduceException
import com.dakhel.kompost.Farm
import com.dakhel.kompost.NoSuchSeedException
import com.dakhel.kompost.ProduceKey
import com.dakhel.kompost.Producer
import com.dakhel.kompost.farmOrNull
import com.dakhel.kompost.lifecycle.activity.ApplicationRootActivitiesFarm
import com.dakhel.kompost.lifecycle.activity.rootActivitiesFarm
import kotlin.reflect.KClass

/**
 * Extension property for KClass<out ViewModel> to get a ProduceKey.
 * This property generates a ProduceKey for the ViewModel KClass.
 *
 * @return The generated ProduceKey.
 */
private val KClass<out ViewModel>.viewModelProduceKey: ProduceKey
    get() {
        return ProduceKey(this)
    }

// Constant for the name of the ViewModelsFarm.
private const val ViewModelsFarmName = "ViewModelsFarm"

/**
 * Extension property for ApplicationRootActivitiesFarm to get a ProduceKey.
 * This property generates a ProduceKey for the ApplicationRootActivitiesFarm with a tag that includes the id and the ViewModelsFarmName.
 *
 * @return The generated ProduceKey.
 */
private val ApplicationRootActivitiesFarm.viewModelsFarmProduceKey: ProduceKey
    get() = ProduceKey(kClass = this::class, tag = "$id${ViewModelsFarmName}")

/**
 * The `ViewModelsFarm` class is responsible for managing the lifecycle of ViewModel dependencies in the application.
 * It is a producer of ViewModels and uses the `Farm` class to manage the production of ViewModels.
 * The `ViewModelsFarm` class is created with an id and an instance of `ApplicationRootActivitiesFarm`.
 *
 * @param id The unique identifier for this `ViewModelsFarm`.
 * @param applicationFarm The `ApplicationRootActivitiesFarm` that this `ViewModelsFarm` belongs to.
 * @constructor Creates a new instance of `ViewModelsFarm`.
 */
class ViewModelsFarm internal constructor(
    id: String,
    applicationFarm: ApplicationRootActivitiesFarm,
) : Producer by Farm(id = id, parent = applicationFarm) {

    /**
     * Lazy property for the ViewModelProvider.Factory used to create ViewModels.
     */
    internal val factory by lazy { viewModelFactory() }

    /**
     * A mutable map that holds instances of ViewModelWithSavedStateSeedBed.
     * The key is a String, and the value is an instance of ViewModelWithSavedStateSeedBed.
     * This map is used to store and retrieve ViewModelWithSavedStateSeedBed instances for different ViewModels.
     */
    private val viewModelWithSavedStateSeedBeds =
        mutableMapOf<String, ViewModelWithSavedStateSeedBed<*>>()

    /**
     * Creates a ViewModelProvider.Factory for creating ViewModels.
     */
    private fun viewModelFactory(): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val producer = this@ViewModelsFarm
                val key = modelClass.kotlin.viewModelProduceKey
                if (viewModelWithSavedStateSeedBeds.containsKey(key.value))
                    return producer.supplyViewModelWithSavedState(key, extras)
                return producer.supply(key)
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
    inline fun <reified VM : ViewModel> produceViewModelWithSavedState(
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
    fun <VM : ViewModel> produceViewModelWithSavedState(
        key: ProduceKey,
        produce: (savedStateHandle: SavedStateHandle) -> VM
    ) {
        val seedBed = ViewModelWithSavedStateSeedBed { savedStateHandle ->
            produce(savedStateHandle)
        }
        if (viewModelWithSavedStateSeedBeds.containsKey(key.value))
            throw DuplicateProduceException(key)
        viewModelWithSavedStateSeedBeds[key.value] = seedBed
    }

    /**
     * Function to supply a ViewModel with a SavedStateHandle.
     * This function takes a ProduceKey and CreationExtras as parameters.
     * The function retrieves the ViewModelWithSavedStateSeedBed from the viewModelWithSavedStateSeedBeds map using the ProduceKey.
     * If a ViewModelWithSavedStateSeedBed does not exist for the given ProduceKey, a NoSuchSeedException is thrown.
     * The function then calls the harvest function on the ViewModelWithSavedStateSeedBed with the CreationExtras to create the ViewModel.
     * If the created ViewModel cannot be cast to the expected type, a CannotCastHarvestedSeedException is thrown.
     *
     * @param key The ProduceKey for the ViewModel.
     * @param extras The CreationExtras used to create the ViewModel.
     * @return The created ViewModel.
     */
    private fun <VM : ViewModel> supplyViewModelWithSavedState(
        key: ProduceKey,
        extras: CreationExtras,
    ): VM {
        val bed = viewModelWithSavedStateSeedBeds[key.value]
            ?: throw NoSuchSeedException(key)
        val harvestedCrop = bed.harvest(extras)
        return harvestedCrop as? VM
            ?: throw CannotCastHarvestedSeedException(key, harvestedCrop)
    }

    /**
     * Class for creating a ViewModel with a SavedStateHandle.
     * This class takes a lambda function as a parameter in its constructor.
     * The lambda function should take a SavedStateHandle as a parameter and return an instance of the ViewModel.
     * The class has a lazy property for a ViewModelWithSavedStateCrop, which is created using the lambda function.
     * The class also has a harvest function that calls the harvest function on the ViewModelWithSavedStateCrop with the CreationExtras to create the ViewModel.
     *
     * @param seed A lambda function that takes a SavedStateHandle as a parameter and returns an instance of the ViewModel.
     */
    class ViewModelWithSavedStateSeedBed<VM : ViewModel>(
        seed: (savedStateHandle: SavedStateHandle) -> VM
    ) {
        private val crop: ViewModelWithSavedStateCrop<VM> by lazy { ViewModelWithSavedStateCrop(seed) }

        fun harvest(extras: CreationExtras) = crop.harvest(extras)

        /**
         * Class for creating a ViewModel with a SavedStateHandle.
         * This class takes a lambda function as a parameter in its constructor.
         * The lambda function should take a SavedStateHandle as a parameter and return an instance of the ViewModel.
         * The class has a harvest function that calls the lambda function with the SavedStateHandle created from the CreationExtras to create the ViewModel.
         *
         * @param seed A lambda function that takes a SavedStateHandle as a parameter and returns an instance of the ViewModel.
         */
        class ViewModelWithSavedStateCrop<VM : ViewModel>(
            private val seed: (savedStateHandle: SavedStateHandle) -> VM
        ) {
            fun harvest(extras: CreationExtras) = seed.invoke(extras.createSavedStateHandle())
        }
    }
}

/**
 * Extension function to get the ViewModelsFarm from an ApplicationRootActivitiesFarm.
 * This function retrieves the ViewModelsFarm from the ApplicationRootActivitiesFarm using the viewModelsFarmProduceKey.
 * If a ViewModelsFarm does not exist for the given ProduceKey, null is returned.
 *
 * @return The ViewModelsFarm if it exists, null otherwise.
 */
fun ApplicationRootActivitiesFarm.viewModelsFarmOrNull(): ViewModelsFarm? =
    farmOrNull(this, viewModelsFarmProduceKey)

/**
 * Extension function to get the ViewModelsFarm from a Fragment.
 * This function retrieves the ViewModelsFarm from the rootActivitiesFarm of the Fragment's activity using the viewModelsFarmOrNull function.
 * If a ViewModelsFarm does not exist, an error is thrown.
 *
 * @return The ViewModelsFarm if it exists.
 * @throws RuntimeException if the ViewModelsFarm does not exist.
 */
fun Fragment.viewModelsFarm(): ViewModelsFarm {
    return requireActivity().rootActivitiesFarm().viewModelsFarmOrNull()
        ?: error("ViewModels farm not created")
}

/**
 * Extension function to get the ViewModelsFarm from a ComponentActivity.
 * This function retrieves the ViewModelsFarm from the rootActivitiesFarm of the ComponentActivity using the viewModelsFarmOrNull function.
 * If a ViewModelsFarm does not exist, an error is thrown.
 *
 * @return The ViewModelsFarm if it exists.
 * @throws RuntimeException if the ViewModelsFarm does not exist.
 */
fun ComponentActivity.viewModelsFarm(): ViewModelsFarm {
    return rootActivitiesFarm().viewModelsFarmOrNull()
        ?: error("ViewModels farm not created")
}

/**
 * Extension function to create a ViewModelsFarm in an ApplicationRootActivitiesFarm.
 * This function checks if a ViewModelsFarm already exists using the viewModelsFarmOrNull function.
 * If a ViewModelsFarm already exists, an IllegalArgumentException is thrown.
 * If a ViewModelsFarm does not exist, a new ViewModelsFarm is created and added to the ApplicationRootActivitiesFarm.
 * The function takes a lambda function as a parameter, which is used to set up the ViewModelsFarm.
 *
 * @param productionScope A lambda function that sets up the ViewModelsFarm.
 * @return The created ViewModelsFarm.
 * @throws IllegalArgumentException if a ViewModelsFarm already exists.
 */
fun ApplicationRootActivitiesFarm.createViewModelsFarm(
    productionScope: ViewModelsFarm.() -> Unit
): ViewModelsFarm {
    require(viewModelsFarmOrNull() == null) {
        "ViewModels farm already exists"
    }
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
fun <VM : ViewModel> ViewModelsFarm.supplyViewModel(
    vmClass: KClass<VM>,
    viewModelStore: ViewModelStore,
    extras: CreationExtras,
): VM {
    val provider = ViewModelProvider(viewModelStore, factory, extras)
    return provider[vmClass.java]
}

/**
 * Extension function to get a lazy ViewModel from a ComponentActivity.
 * This function takes two lambda functions as parameters, which are used to get the ViewModelStore and the CreationExtras.
 * The function returns a lazy delegate that retrieves the ViewModel from the ComponentActivity using the viewModel function.
 *
 * @param viewModelStore A lambda function that returns the ViewModelStore used to store the ViewModel.
 * @param extras A lambda function that returns the CreationExtras used to create the ViewModel.
 * @return A lazy delegate for the ViewModel.
 */
inline fun <reified VM : ViewModel> ComponentActivity.lazyViewModel(
    crossinline viewModelStore: () -> ViewModelStore = { this.viewModelStore },
    crossinline extras: () -> CreationExtras = { this.defaultViewModelCreationExtras },
) = lazy {
    viewModel<VM>(viewModelStore(), extras())
}

/**
 * Extension function to get a ViewModel from a ComponentActivity.
 * This function takes a ViewModelStore and CreationExtras as parameters.
 * The function retrieves the ViewModel from the ViewModelsFarm using the supplyViewModel function.
 *
 * @param viewModelStore The ViewModelStore used to store the ViewModel.
 * @param extras The CreationExtras used to create the ViewModel.
 * @return The ViewModel.
 */
inline fun <reified VM : ViewModel> ComponentActivity.viewModel(
    viewModelStore: ViewModelStore = this.viewModelStore,
    extras: CreationExtras = this.defaultViewModelCreationExtras,
): VM = viewModelsFarm().supplyViewModel(
    vmClass = VM::class,
    viewModelStore = viewModelStore,
    extras = extras,
)

/**
 * Extension function to get a lazy ViewModel from a Fragment.
 * This function takes two lambda functions as parameters, which are used to get the ViewModelStore and the CreationExtras.
 * The function returns a lazy delegate that retrieves the ViewModel from the Fragment using the viewModel function.
 *
 * @param viewModelStore A lambda function that returns the ViewModelStore used to store the ViewModel.
 * @param extras A lambda function that returns the CreationExtras used to create the ViewModel.
 * @return A lazy delegate for the ViewModel.
 */
inline fun <reified VM : ViewModel> Fragment.lazyViewModel(
    crossinline viewModelStore: () -> ViewModelStore = { this.viewModelStore },
    crossinline extras: () -> CreationExtras = { this.defaultViewModelCreationExtras },
) = lazy {
    viewModel<VM>(viewModelStore(), extras())
}

/**
 * Extension function to get a ViewModel from a Fragment.
 * This function takes a ViewModelStore and CreationExtras as parameters.
 * The function retrieves the ViewModel from the ViewModelsFarm using the supplyViewModel function.
 *
 * @param viewModelStore The ViewModelStore used to store the ViewModel.
 * @param extras The CreationExtras used to create the ViewModel.
 * @return The ViewModel.
 */
inline fun <reified VM : ViewModel> Fragment.viewModel(
    viewModelStore: ViewModelStore = this.viewModelStore,
    extras: CreationExtras = this.defaultViewModelCreationExtras,
): VM = viewModelsFarm().supplyViewModel(
    vmClass = VM::class,
    viewModelStore = viewModelStore,
    extras = extras,
)
