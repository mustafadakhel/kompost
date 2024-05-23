package com.dakhel.kompost.lifecycle.fragment

import androidx.fragment.app.Fragment
import com.dakhel.kompost.Farm
import com.dakhel.kompost.ProduceKey
import com.dakhel.kompost.Producer
import com.dakhel.kompost.lifecycle.activity.ApplicationRootActivitiesFarm
import com.dakhel.kompost.lifecycle.activity.rootActivitiesFarm

private const val ActivityRootFragmentsFarmName = "FragmentsFarm"

private val ApplicationRootActivitiesFarm.rootFragmentsFarmProduceKey: ProduceKey
    get() = ProduceKey(
        ApplicationRootFragmentsFarm::class,
        tag = "$id${ActivityRootFragmentsFarmName}"
    )

class ApplicationRootFragmentsFarm internal constructor(
    id: String,
    applicationFarm: ApplicationRootActivitiesFarm
) : Producer by Farm(id = id, parent = applicationFarm)

fun ApplicationRootActivitiesFarm.rootFragmentsFarmOrNull(): ApplicationRootFragmentsFarm? {
    val key = rootFragmentsFarmProduceKey

    return if (contains(key)) {
        supply(key)
    } else null
}

fun Fragment.rootFragmentsFarm(): ApplicationRootFragmentsFarm {
    val activity = activity ?: error("Fragment is not attached to activity")
    return activity.rootActivitiesFarm()
        .rootFragmentsFarmOrNull()
        ?: error("Root fragments farm not created")
}

fun ApplicationRootActivitiesFarm.createRootFragmentsFarm(
    productionScope: ApplicationRootFragmentsFarm.() -> Unit
): ApplicationRootFragmentsFarm {
    require(rootFragmentsFarmOrNull() == null) {
        "Fragments farm already exists"
    }
    return ApplicationRootFragmentsFarm(ActivityRootFragmentsFarmName, this)
        .apply(productionScope)
        .also {
            val key = rootFragmentsFarmProduceKey
            produce(key) { it }
        }
}
