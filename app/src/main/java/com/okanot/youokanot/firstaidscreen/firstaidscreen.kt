package com.okanot.youokanot.firstaidscreen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.okanot.youokanot.MainActivity
import com.okanot.youokanot.R
import com.okanot.youokanot.model.Injury

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InjuryListScreen(navController: NavHostController, injuries: List<Injury>, onInjurySelected: (Injury) -> Unit) {
    Scaffold(
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Image(painter = painterResource(R.drawable.background), contentDescription = "", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)

            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.Start
            ) {
                injuries.forEach { injury ->
                    Button(
                        onClick = {
                            Log.d("InjuryListScreen", "Selected injury: ${injury.name}")
                            onInjurySelected(injury)
                            navController.navigate(MainActivity.Nav.TREATMENT_SCREEN.name)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(injury.name)
                    }
                }
            }
        }
    }
}