package com.example.locaationapp

import android.content.Context
                import android.os.Bundle
                import android.Manifest
                import android.app.Activity
                import android.widget.Toast
                import androidx.activity.ComponentActivity
                import androidx.activity.compose.rememberLauncherForActivityResult
                import androidx.activity.compose.setContent
                import androidx.activity.result.contract.ActivityResultContracts
                import androidx.compose.foundation.background
                import androidx.compose.foundation.layout.Arrangement
                import androidx.compose.foundation.layout.Box
                import androidx.compose.foundation.layout.Column
                import androidx.compose.foundation.layout.fillMaxSize
                import androidx.compose.material.icons.Icons
                import androidx.compose.material.icons.filled.LocationOn
                import androidx.compose.material3.Button
                import androidx.compose.material3.ButtonDefaults
                import androidx.compose.material3.FloatingActionButton
                import androidx.compose.material3.Icon
                import androidx.compose.material3.MaterialTheme
                import androidx.compose.material3.Surface
                import androidx.compose.material3.Text
                import androidx.compose.runtime.Composable
                import androidx.compose.ui.Alignment
                import androidx.compose.ui.Modifier
                import androidx.compose.ui.graphics.Brush
                import androidx.compose.ui.graphics.Color
                import androidx.compose.ui.platform.LocalContext
                import androidx.lifecycle.viewmodel.compose.viewModel
                import androidx.core.app.ActivityCompat
                import com.example.locaationapp.ui.theme.LOCAATIONAPPTheme

        class MainActivity : ComponentActivity() {
            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                setContent {
                    val viewModel: LocationViewModel = viewModel()
                    LOCAATIONAPPTheme {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            MyApp(viewModel)
                        }
                    }
                }
            }
        }

        @Composable
        fun MyApp(viewModel: LocationViewModel) {
            val context = LocalContext.current
            val locationUtils = LocationUtils(context)
            GradientBackground {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    LocationDisplay(locationUtils, viewModel, context)
                    FloatingActionButton(
                        onClick = { /* Get location logic */ },
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = "Get Location")
                    }
                }
            }
        }

        @Composable
        fun GradientBackground(content: @Composable () -> Unit) {
            val gradientBrush =Brush.verticalGradient(
                colors = listOf(Color(0xFF00BCD4), Color(0xFF009688))
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(gradientBrush)
            ) {
                content()
            }
        }

@Composable
fun LocationDisplay(
    locationUtils: LocationUtils,
    viewModel: LocationViewModel,
    context: Context
) {
    val location = viewModel.location.value
    val address = location?.let {
        locationUtils.reverseGeocodeLocation(location)
    }

    val activityContext = context as? Activity ?: return

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true &&
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
                locationUtils.requestLocationUpdates(viewModel)
            } else {
                val rationalRequired = ActivityCompat.shouldShowRequestPermissionRationale(
                    activityContext,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                    activityContext,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )

                if (rationalRequired) {
                    Toast.makeText(activityContext, "Location Permission Is Required for this feature to work", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(activityContext, "Location Permission Is Required, please enable it!", Toast.LENGTH_LONG).show()
                }
            }
        }
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (location != null) {
            Text("Address: ${location.latitude} ${location.longitude}\n$address")
        } else {
            Text("Location Not Available")
        }

        Button(
            onClick = {
                if (locationUtils.hasLocationPermission(activityContext)) {
                    locationUtils.requestLocationUpdates(viewModel)
                } else {
                    requestPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6200EE)
            )
        ) {
            Text(text = "Get Location")
        }
    }
}
