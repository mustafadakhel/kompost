package com.dakhel.kompost.lifecycle.fragment

import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.dakhel.kompost.Farm
import com.dakhel.kompost.ProduceKey
import com.dakhel.kompost.Producer

private const val FragmentScopedFarmName = "FragmentScopedFarm"

private val Fragment.fragmentScopedFarmProduceKey: ProduceKey
    get() = ProduceKey(this::class, tag = farmId)

internal val Fragment.farmId: String
    get() = "$FragmentScopedFarmName.${this.hashCode()}"

class FragmentScopedFarm internal constructor(
    fragment: Fragment,
    applicationRootFragmentsFarm: ApplicationRootFragmentsFarm
) : Producer by Farm(id = fragment.farmId, parent = applicationRootFragmentsFarm)

fun Fragment.getOrCreateFragmentScopedFarm(
    productionScope: FragmentScopedFarm.() -> Unit = {}
): FragmentScopedFarm {
    return fragmentScopedFarmOrNull() ?: createFragmentScopedFarm(productionScope)
}

internal fun Fragment.fragmentScopedFarmOrNull(): FragmentScopedFarm? {
    val key = fragmentScopedFarmProduceKey

    val fragmentsFarm = rootFragmentsFarm()

    return if (fragmentsFarm.contains(key)) {
        fragmentsFarm.supply(key)
    } else null
}

class FragmentScopedFarmAlreadyExistsException :
    IllegalStateException("Fragment farm already exists")

fun Fragment.createFragmentScopedFarm(
    productionScope: FragmentScopedFarm.() -> Unit
): FragmentScopedFarm {
    val fragmentsFarm = rootFragmentsFarm()
    if (fragmentScopedFarmOrNull() != null)
        throw FragmentScopedFarmAlreadyExistsException()
    return FragmentScopedFarm(this, fragmentsFarm)
        .apply(productionScope)
        .also {
            fragmentsFarm.produceFragmentFarm(
                fragment = this,
                farm = it
            )
        }
}

private fun ApplicationRootFragmentsFarm.produceFragmentFarm(
    fragment: Fragment,
    farm: FragmentScopedFarm
) {
    val key = fragment.fragmentScopedFarmProduceKey
    fragment.lifecycle.addObserver(object : DefaultLifecycleObserver {
        override fun onDestroy(owner: LifecycleOwner) {
            farm.destroyAllCrops()
            destroy(key)
            super.onDestroy(owner)
        }
    })
    produce(key) { farm }
}

inline fun <reified T> Fragment.lazyFragmentSupply(
    tag: String? = null
): Lazy<T> = lazy { fragmentSupply(tag) }

inline fun <reified T> Fragment.fragmentSupply(
    tag: String? = null
): T {
    return getOrCreateFragmentScopedFarm()
        .supply(ProduceKey(T::class, tag = tag))
}
