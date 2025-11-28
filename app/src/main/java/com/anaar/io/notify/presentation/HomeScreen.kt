package com.anaar.io.notify.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.anaar.io.notify.R
import com.anaar.io.notify.clearUserId
import com.anaar.io.notify.getUserIdFlow
import com.anaar.io.notify.ui.theme.PinkPrimary
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onLogoutClick: (String) -> Unit
) {
    val context = LocalContext.current

    var userId by remember { mutableStateOf("") }
    val savedUserIdFlow = remember { context.getUserIdFlow() }
    val savedUserId by savedUserIdFlow.collectAsState(initial = "")

    // ✅ Compose-friendly permission launcher
    val permissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissions.entries.forEach { (permissionName, isGranted) ->
            if (isGranted) {
                Log.d("Permissions", "$permissionName granted")
            } else {
                Log.d("Permissions", "$permissionName denied")
            }
        }
    }

    // ✅ Build the list of permissions we actually need to request
    val requiredPermissions = remember {
        buildList {
            // Only on Android 13+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(Manifest.permission.POST_NOTIFICATIONS)
            }

            add(Manifest.permission.READ_PHONE_STATE)
            add(Manifest.permission.CALL_PHONE)
            add(Manifest.permission.READ_CALL_LOG)
            add(Manifest.permission.WRITE_CALL_LOG)
            add(Manifest.permission.MANAGE_OWN_CALLS)
            add(Manifest.permission.READ_CONTACTS)

            // NOTE: INTERNET & FOREGROUND_SERVICE permissions are
            // normal / not runtime, so no need to request them here.
        }
    }

    // ✅ Request permissions once when screen appears
    LaunchedEffect(Unit) {
        val toRequest = requiredPermissions.filter { permission ->
            ContextCompat.checkSelfPermission(context, permission) !=
                    PackageManager.PERMISSION_GRANTED
        }

        if (toRequest.isNotEmpty()) {
            permissionsLauncher.launch(toRequest.toTypedArray())
        } else {
            Log.d("Permissions", "All permissions already granted")
        }
    }

    // Update TextField with saved value on launch
    LaunchedEffect(savedUserId) {
        if (savedUserId.isNotEmpty()) {
            userId = savedUserId
        }
    }

    Column(
        modifier = modifier.padding(top = 50.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Image(
            painter = painterResource(id = R.drawable.icon),
            contentDescription = "My Image",
            modifier = Modifier
                .size(100.dp)
                .fillMaxWidth()
                .padding(20.dp, 20.dp), // size of the image
            contentScale = ContentScale.Crop // how the image scales inside the box
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(16.dp)
        ) {
            Text(
                text = "Receiving Calls ...",
                modifier = Modifier
                    .fillMaxHeight()
                    .align(Alignment.Center)
            )

            Button(
                onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        context.clearUserId()
                    }
                    onLogoutClick(userId)
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 100.dp)
                    .fillMaxWidth(0.8f)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PinkPrimary,
                    contentColor = Color.White
                )
            ) {
                Text("Logout")
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {
    HomeScreen(
        onLogoutClick = { }
    )
}
