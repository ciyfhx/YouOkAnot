package com.okanot.youokanot

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.okanot.youokanot.firstaidscreen.InjuryListScreen
import com.okanot.youokanot.model.Injury
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
        NEARBY_CLINIC_SCREEN,
        FIRST_AID_SCREEN
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
                        ElevatedCard(modifier = Modifier.size(width=180.dp, height=180.dp).padding(8.dp), onClick = {
                            navController.navigate(MainActivity.Nav.FIRST_AID_SCREEN.name)
                        }) {
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
                    ClinicLocation("Northeast Medical Group (Simei Clinic)", 1.343228291528875, 103.95370116266504),
                    ClinicLocation("DA Clinic @Simei", 1.3436633386700283, 103.95366625394678),
                    ClinicLocation("Parkway Shenton Medical Clinic, ESR BizPark @Changi", 1.3361932036514028, 103.96381295011560),
                    ClinicLocation("Raffles Medical Changi City Point", 1.3353513511638537, 103.96202671705439),
                    ClinicLocation("FAITH Clinic (Simei)", 1.3468094397402544, 103.956743020226),
                    ClinicLocation("EH Medical Family Clinic (Bedok South)", 1.3179233130313497, 103.94760180341642),
                    ClinicLocation("True Medical Clinic Bedok South", 1.3188182751024995, 103.94561415813378),
                    ClinicLocation("Greenhealth Family Clinic", 1.3454945931603437, 103.94618166643288),
                    ClinicLocation("Tampines Clinic And Surgery Pte Ltd", 1.3465687673456572, 103.94501280418588),
                    ClinicLocation("Emmanuel Medical Clinic", 1.349171646580365, 103.95003749526711),
                    ClinicLocation("University Health Service", 1.3454225826545088, 103.68262147921027),
                    ClinicLocation("Central 24-Hr Clinic (Jurong West / Pioneer North) - CHAS | GP Clinic | 24 小时 诊所", 1.3416487465221192, 103.6910636508905),
                    ClinicLocation("West Point Clinic", 1.3475925859527749, 103.69590207849194),
                    ClinicLocation("Hisemainn Medical Clinic (Jurong West)", 1.3545429388069719, 103.70581552283615),
                    ClinicLocation("Healthway Medical (Jurong West Central)", 1.341178645502053, 103.70748263174303),
                    ClinicLocation("OneCare Clinic Jurong West", 1.3498103402107966, 103.71963838576133),
                    ClinicLocation("Minmed Clinic (Jurong East)", 1.3337336984737473, 103.74393362334031),
                    ClinicLocation("Redwood Clinic and Surgery Pte Ltd", 1.289737902315691, 103.81685086638535),
                    ClinicLocation("ACMS Medical Clinic", 1.3016087017471452, 103.83778851453391),
                    ClinicLocation("Parkway Shenton Medical Clinic, Woodlands MRT", 1.437503846950999, 103.78749957085402),
                    ClinicLocation("EH Medical Family Clinic (Woodlands)", 1.4466709463981282, 103.80656416856903),
                    ClinicLocation("Central 24-Hr Clinic (Woodlands / Marsiling) - CHAS | GP Clinic | 24 小时 诊所", 1.4311505928740107, 103.77406193775788),
                    ClinicLocation("DA Clinic @ Anchorvale", 1.39379866749494, 103.88975826389444),
                    ClinicLocation("Pinnacle Family Clinic - Compassvale", 1.398096437619152, 103.89857989394652),
                    ClinicLocation("Keystone Clinic & Surgery (Serangoon)", 1.354004610834468, 103.86849984503293),
                    ClinicLocation("Union Medical Clinic & Surgery", 1.3695267256944266, 103.87212813265774),
                    ClinicLocation("EC Family Clinic", 1.3209890895742913, 103.90475710287595),
                    ClinicLocation("NovaHealth TCM Clinic @Eunos (Family Clinic)", 1.3169895371002276, 103.90256196801207),
                    ClinicLocation("Clover Medical Clinic Pte Ltd", 1.3973910794752333, 103.74646772087151),
                    ClinicLocation("Greenlife Clinic & Surgery Pte Ltd", 1.398157982075967, 103.74698711816465),
                    ClinicLocation("Healthway Medical (Yishun Ave 11)", 1.4254128421379697, 103.84955601944476)

                )
            )
        }
        composable(MainActivity.Nav.FIRST_AID_SCREEN.name) {
            InjuryListScreen(
                navController = navController,
                injuries = listOf(
                    Injury("Abrasion"),
                    Injury("Bruises"),
                    Injury("Burns"),
                    Injury("Laceration"),
                    Injury("Normal")
                ),
                onInjurySelected = { injury ->
                    // Handle injury selection
                    val treatment = when (injury.name) {
                        "Abrasion" -> Treatment("Abrasion", "Wash the wound with water and soap" +
                                "\nRemove any visible debris" +
                                "\nDry the wound with a clean cloth" +
                                "\n(If have) Apply antibacterial ointment/cream to the wound")
                        "Bruises" -> Treatment("Bruises", "Wrap the ice around with a clean cloth and apply it on the bruise for 1 - 2 days" +
                                "\nIf ice is not available, leave it untouched" +
                                "\nAfter 2 days, start applying warm compress")
                        "Burns" -> Treatment("Burns", "Remove any clothing or jewellery near the burnt area" +
                                "\nRun the burnt area in running area" +
                                "\nIf running water is not available, wrap ice around with a clean cloth and apply it on the burnt area" +
                                "\nDO NOT apply ice, toothpaste, oil, butter or any other remedies" +
                                "\nDO NOT burst the blister")
                        "Laceration" -> Treatment("Laceration", "Wash your hands\n" +
                                "Find a clean cloth and apply pressure on the wound until the bleeding stops\n" +
                                "Rinse the wound with clean water to rid it of any debris\n" +
                                "Apply bandage on the wound. If no bandage is available, find a clean cloth and wrap it around the wound\n" +
                                "If there is still bleeding, immediately seek a doctor")
                        "Normal" -> Treatment("Normal", "You are fine!")
                        else -> null
                    }
                    if (treatment != null) {
                        viewModel.setTreatment(treatment)
                        navController.navigate(MainActivity.Nav.TREATMENT_SCREEN.name)
                    } else {
                        Log.e("AppNavHost", "Invalid injury selected: ${injury.name}")
                    }
                }
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
