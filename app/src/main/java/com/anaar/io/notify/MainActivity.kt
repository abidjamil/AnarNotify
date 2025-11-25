package com.anaar.io.notify

import android.app.Activity
import android.app.role.RoleManager
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.anaar.io.notify.ui.theme.AnaarNotifyTheme
import com.anaar.io.notify.ui.theme.PinkPrimary
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

    private fun bindMyService() {
        val mCallServiceIntent = Intent("android.telecom.CallScreeningService")
        mCallServiceIntent.setPackage(applicationContext.packageName)
        val mServiceConnection: ServiceConnection = object : ServiceConnection {
            override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
                // iBinder is an instance of CallScreeningService.CallScreenBinder
                // CallScreenBinder is an inner class present inside CallScreenService
            }

            override fun onServiceDisconnected(componentName: ComponentName) {}
            override fun onBindingDied(name: ComponentName) {}
        }
        bindService(mCallServiceIntent, mServiceConnection, BIND_AUTO_CREATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val context = this
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = getSystemService(ROLE_SERVICE) as RoleManager
            val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING)
            val startForRequestRoleResult = registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { result: androidx.activity.result.ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    //  you will get result here in result.data
                    bindMyService()
                }
            }
            startForRequestRoleResult.launch(intent)
        }



        setContent {
            AnaarNotifyTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    UserIdInputScreen(
                        modifier = Modifier.padding(innerPadding),
                        onSaveClick = { userId ->
                            Toast.makeText(context, "saved", Toast.LENGTH_SHORT).show()
                            CoroutineScope(Dispatchers.IO).launch {
                                context.saveUserId(userId)
                            }
                        },
                        onClearClick = {
                            Toast.makeText(context, "deleted", Toast.LENGTH_SHORT).show()
                            CoroutineScope(Dispatchers.IO).launch {
                                context.clearUserId()
                            }
                        })
                }
            }
        }
    }
}

@Composable
fun UserIdInputScreen(
    modifier: Modifier? = null,
    onSaveClick: (String) -> Unit,
    onClearClick: (String) -> Unit
) {
    val context = LocalContext.current

    var userId by remember { mutableStateOf("") }
    val savedUserIdFlow = remember { context.getUserIdFlow() }
    val savedUserId by savedUserIdFlow.collectAsState(initial = "")

    // update TextField with saved value on launch
    LaunchedEffect(savedUserId) {
        if (savedUserId.isNotEmpty()) {
            userId = savedUserId
        }
    }

    Column(
        modifier= Modifier.padding(top = 50.dp),
        horizontalAlignment = Alignment.CenterHorizontally, // centers horizontally
        verticalArrangement = Arrangement.Center // centers vertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.icon),
            contentDescription = "My Image",
            modifier = Modifier.size(100.dp).fillMaxWidth()
                .padding(20.dp, 20.dp), // size of the image
            contentScale = ContentScale.Crop // how the image scales inside the box
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(16.dp)
        ) {



            // Input + Save button
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp, 20.dp)
            ) {
                OutlinedTextField(
                    value = userId,
                    onValueChange = { newValue ->
                            // Allow only digits and limit to 10 characters
                            if (newValue.length <= 10 && newValue.all { it.isDigit() }) {
                                userId = newValue
                            }
                    },
                    label = { Text("Enter your ID") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (userId.isEmpty()) {
                            Toast.makeText(context, "Please input id", Toast.LENGTH_SHORT).show()
                        } else {
                            onSaveClick(userId)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PinkPrimary, // background color
                        contentColor = Color.White           // text/icon color
                    )
                ) {
                    Text("Save")
                }
            }

            // Bottom continue button (100dp from bottom)
            Button(
                onClick = {
                    if (userId.isEmpty()) {
                        Toast.makeText(context, "Please input id", Toast.LENGTH_SHORT).show()
                    } else {
                        userId=""
                        onClearClick(userId)
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 100.dp)
                    .fillMaxWidth(0.8f)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PinkPrimary, // background color
                    contentColor = Color.White)
            ) {
                Text("Delete")
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(text = "Stored User ID: $savedUserId")

        }
    }

}

@Preview
@Composable
fun previewScreen() {
    UserIdInputScreen(
        onSaveClick = {

        },
        onClearClick = {

        })
}