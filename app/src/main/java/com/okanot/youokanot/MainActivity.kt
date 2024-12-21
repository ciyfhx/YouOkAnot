package com.okanot.youokanot

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.okanot.youokanot.model.WoundClassifier
import com.okanot.youokanot.ui.theme.YouOkAnotTheme
import com.okanot.youokanot.utils.CameraButton
import com.okanot.youokanot.viewmodels.ImageViewModel
import com.okanot.youokanot.woundtreatmentscreen.Treatment
import com.okanot.youokanot.woundtreatmentscreen.WoundTreatmentScreen

class MainActivity : ComponentActivity() {

    enum class Nav {
        CAMERA_SCREEN,
        TREATMENT_SCREEN
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            YouOkAnotTheme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavHost(navController = navController, innerPadding = innerPadding)
                }
            }
        }
    }
}

@Composable
fun AppNavHost(navController: NavHostController, innerPadding: PaddingValues) {
    val viewModel: ImageViewModel = viewModel()
    NavHost(navController = navController, startDestination = MainActivity.Nav.CAMERA_SCREEN.name) {
        composable(MainActivity.Nav.CAMERA_SCREEN.name) {
            Greeting(name = "Android", modifier = Modifier.padding(innerPadding))
            CameraButton { bitmap ->
                if (bitmap != null) {
                    viewModel.updateImage(bitmap)
                    val classifier = WoundClassifier(navController.context)
                    Toast.makeText(navController.context, "Predicted: ${classifier.classify(bitmap)}", Toast.LENGTH_LONG).show()
//                    navController.navigate(MainActivity.Nav.TREATMENT_SCREEN.name)
                } else {
                    Toast.makeText(navController.context, "No image captured", Toast.LENGTH_LONG).show()
                }
            }
        }
        composable(MainActivity.Nav.TREATMENT_SCREEN.name) {
            WoundTreatmentScreen(
                treatment = Treatment("Cut", "Clean the wound and apply a bandage"),
                woundImage = viewModel.image.value
            )
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
    YouOkAnotTheme {
        Greeting("Android")
    }
}
