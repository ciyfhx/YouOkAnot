package com.okanot.youokanot

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.okanot.youokanot.model.WoundCategory
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
            Column {
                Greeting(name = "You OK Anot", modifier = Modifier.padding(innerPadding))
                CameraButton { bitmap ->
                    if (bitmap != null) {
                        viewModel.updateImage(bitmap)
                        val classifier = WoundClassifier(navController.context)
                        val predicted = classifier.classify(bitmap)
                        val treatment = when(predicted) {
                            WoundCategory.Abrasion -> {
                                Treatment("Abrasion", "Wash the wound with water and soap" +
                                        "\nRemove any visible debris" +
                                        "\nDry the wound with a clean cloth" +
                                        "\n(If have) Apply antibacterial ointment/cream to the wound")
                            }

                            WoundCategory.Bruises -> {
                                Treatment("Bruises", "Wrap the ice around with a clean cloth and apply it on the bruise for 1 - 2 days" +
                                        "\nIf ice is not available, leave it untouched" +
                                        "\nAfter 2 days, start applying warm compress")
                            }

                            WoundCategory.Burns -> {
                                Treatment("Burns", "Remove any clothing or jewellery near the burnt area" +
                                        "\nRun the burnt area in running area" +
                                        "\nIf running water is not available, wrap ice around with a clean cloth and apply it on the burnt area" +
                                        "\nDO NOT apply ice, toothpaste, oil, butter or any other remedies" +
                                        "\nDO NOT burst the blister")
                            }
                            WoundCategory.Laceration -> {
                                Treatment("Laceration", "Wash your hands\n" +
                                        "Find a clean cloth and apply pressure on the wound until the bleeding stops\n" +
                                        "Rinse the wound with clean water to rid it of any debris\n" +
                                        "Apply bandage on the wound. If no bandage is available, find a clean cloth and wrap it around the wound\n" +
                                        "If there is still bleeding, immediately seek a doctor")
                            }
                            WoundCategory.Normal -> {
                                Treatment("Normal", "You are fine!")
                            }
                        }
                        viewModel.setTreatment(treatment)
                        Toast.makeText(navController.context, "Predicted: $predicted", Toast.LENGTH_LONG).show()
                        navController.navigate(MainActivity.Nav.TREATMENT_SCREEN.name)
                    } else {
                        Toast.makeText(navController.context, "No image captured", Toast.LENGTH_LONG).show()
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { /* Implement any follow-up action here */ },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("First Aid")
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Clinic Button
                Button(
                    onClick = { /* Implement any follow-up action here */ },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Clinic")
                }
            }
        }
        composable(MainActivity.Nav.TREATMENT_SCREEN.name) {
            WoundTreatmentScreen(
                treatment = viewModel.treatment.value!! ,
                woundImage = viewModel.image.value,
                navController = navController
            )
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Welcome to $name!",
        modifier = modifier
    )


}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    YouOkAnotTheme {
        val navController = rememberNavController()
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            AppNavHost(navController = navController, innerPadding = innerPadding)
        }
    }
}
