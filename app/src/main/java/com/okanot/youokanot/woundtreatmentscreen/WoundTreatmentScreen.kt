package com.okanot.youokanot.woundtreatmentscreen

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.okanot.youokanot.MainActivity
import com.okanot.youokanot.R

data class Treatment(
    val typeOfInjury: String,
    val suggestedFirstAid: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WoundTreatmentScreen(treatment: Treatment, woundImage: Bitmap?, navController: NavController) {
    val steps = treatment.suggestedFirstAid.split("\n")
    val checkedStates = remember { mutableStateListOf(*Array(steps.size) { false }) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Wound Treatment Details") },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        Box(Modifier.padding(8.dp)) {
            Image(painter = painterResource(R.drawable.background), contentDescription = "", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            Card {

            }
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Treatment Overview",
                    fontSize = 20.sp,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Type of Injury: ${treatment.typeOfInjury}",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.bodyLarge
                )

                woundImage?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "Image of the wound",
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .width(224.dp)
                            .height(224.dp),
                        contentScale = ContentScale.Fit
                    )
                } ?: Text(
                    text = "No image available",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "Suggested Treatment",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.bodyLarge
                )
                ElevatedCard {
                    Column(modifier = Modifier.padding(8.dp)) {
                        steps.forEachIndexed { index, step ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(checked = checkedStates[index], onCheckedChange = {checkedStates[index] = !checkedStates[index] })
                                Text(
                                    text = step,
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Start,
                                    style = MaterialTheme.typography.bodyLarge
                                )

                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }

                }




                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        navController.navigate(MainActivity.Nav.CAMERA_SCREEN.name)

                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Acknowledge Treatment")
                }
            }
        }

    }
}
