package com.mustafadakhel.kompost.sample.presentation.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.os.bundleOf
import com.mustafadakhel.kompost.android.lifecycle.viewModel.lazyViewModel
import com.mustafadakhel.kompost.sample.presentation.theme.theme.KompostTheme
import com.mustafadakhel.kompost.sample.presentation.viewModel.MainViewModel
import kotlin.reflect.KClass

class ComposeActivity : ComponentActivity() {
    private val viewModel by lazyViewModel<MainViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.log()
        setContent {
            KompostTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box {
                        Button(
                            modifier = Modifier.align(Alignment.Center),
                            onClick = {
                                MainActivity::class.start(
                                    this@ComposeActivity,
                                    bundleOf("dummy key" to "dummy value")
                                )
                            }
                        ) {
                            Text("Go to MainActivity")
                        }
                    }
                }
            }
        }
    }
}

fun KClass<out ComponentActivity>.start(
    activity: Context,
    extras: Bundle
) {
    Intent(
        activity,
        java
    ).also {
        it.putExtras(extras)
        activity.startActivity(it)
    }
}
