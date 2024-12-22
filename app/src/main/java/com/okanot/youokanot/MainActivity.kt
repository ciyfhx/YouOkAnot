package com.okanot.youokanot

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.okanot.youokanot.model.WoundCategory
import com.okanot.youokanot.model.WoundClassifier
import com.okanot.youokanot.nearbyclinicscreen.ClinicLocation
import com.okanot.youokanot.nearbyclinicscreen.NearbyClinicsScreen
import com.okanot.youokanot.ui.theme.YouOkAnotTheme
import com.okanot.youokanot.utils.CameraButton
import com.okanot.youokanot.viewmodels.ImageViewModel
import com.okanot.youokanot.woundtreatmentscreen.Treatment
import com.okanot.youokanot.woundtreatmentscreen.WoundTreatmentScreen

class MainActivity : ComponentActivity() {

    enum class Nav {
        CAMERA_SCREEN,
        TREATMENT_SCREEN,
        NEARBY_CLINIC_SCREEN
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            YouOkAnotTheme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.fillMaxSize()){
                        Image(painter = painterResource(R.drawable.background), contentDescription = "", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)

                        AppNavHost(navController = navController, innerPadding = innerPadding)

                    }
                }
            }
        }
    }
}

@Composable
fun AppNavHost(navController: NavHostController, innerPadding: PaddingValues) {
    val viewModel: ImageViewModel = viewModel()
    // Camera permission request launcher
    var hasPermission by remember { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
    }
        // Request camera permission
        // Use a side-effect to launch the permission request once
        LaunchedEffect(Unit) {
            if (!hasPermission) {
                permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }
    NavHost(navController = navController, startDestination = MainActivity.Nav.CAMERA_SCREEN.name) {
        composable(MainActivity.Nav.CAMERA_SCREEN.name) {

            Column (
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(Modifier.fillMaxWidth()) {
                    Text("You ok anot?", fontSize = 36.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        ElevatedCard(modifier = Modifier.size(width=180.dp, height=180.dp).padding(8.dp)) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                Image(painter = painterResource(R.drawable.bandaid), contentDescription = "", modifier = Modifier.fillMaxSize().padding(24.dp), contentScale = ContentScale.Crop)

                                Column(verticalArrangement = Arrangement.Bottom, modifier = Modifier.fillMaxHeight()) {
                                    Box(modifier = Modifier.fillMaxWidth().background(color = Color(1f,1f,1f,0.5f))) {
                                        Text("First Aid", fontSize = 18.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                                    }

                                }

                            }
                        }

                        ElevatedCard (modifier = Modifier.size(width=180.dp, height=180.dp).padding(8.dp), onClick = {
                            navController.navigate(MainActivity.Nav.NEARBY_CLINIC_SCREEN.name)
                        }) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                Image(painter = painterResource(R.drawable.clinic_8638587), contentDescription = "", modifier = Modifier.fillMaxSize().padding(24.dp), contentScale = ContentScale.Crop)

                                Column(verticalArrangement = Arrangement.Bottom, modifier = Modifier.fillMaxHeight()) {
                                    Box(modifier = Modifier.fillMaxWidth().background(color = Color(1f,1f,1f,0.5f))) {

                                        Text(
                                            "Clinic",
                                            fontSize = 18.sp,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }

                                }
                            }

                        }
                    }

                }


                CameraButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                        .padding(16.dp) // Optional padding around the button
                ) {
                        bitmap ->
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
            }
        }
        composable(MainActivity.Nav.TREATMENT_SCREEN.name) {
            WoundTreatmentScreen(
                treatment = viewModel.treatment.value!! ,
                woundImage = viewModel.image.value,
                navController = navController
            )
        }
        composable(MainActivity.Nav.NEARBY_CLINIC_SCREEN.name) {
            NearbyClinicsScreen(
                clinicLocations = mutableListOf(
                    ClinicLocation("1", 1.3521, 103.8198),

                )
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
