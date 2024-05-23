package com.dakhel.kompost.sample.presentation.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dakhel.kompost.lifecycle.viewModel.lazyViewModel
import com.dakhel.kompost.sample.presentation.theme.theme.KompostTheme
import com.dakhel.kompost.sample.presentation.viewModel.MainViewModel

class ComposeActivity : ComponentActivity() {
    private val viewModel by lazyViewModel<MainViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.log()
        setContent {
            KompostTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Button(onClick = {
                        Intent(this, MainActivity::class.java).also {
                            it.putExtra("dummy key", "dummy value")
                            startActivity(it)
                        }
                    }) {
                        Text("Go to MainActivity")
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    KompostTheme {
        Greeting("Android")
    }
}
