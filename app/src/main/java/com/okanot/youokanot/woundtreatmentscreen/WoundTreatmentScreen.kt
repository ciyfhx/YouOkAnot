package com.okanot.youokanot.woundtreatmentscreen

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Treatment(
    val typeOfInjury: String,
    val suggestedFirstAid: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WoundTreatmentScreen(treatment: Treatment, woundImage: Bitmap?) {
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

            woundImage?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Image of the wound",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
            } ?: Text(
                text = "No image available",
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "Type of Injury: ${treatment.typeOfInjury}",
                fontSize = 16.sp,
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                text = "Suggested First Aid: ${treatment.suggestedFirstAid}",
                fontSize = 16.sp,
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { /* Implement any follow-up action here */ },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Acknowledge Treatment")
            }
        }
    }
}
