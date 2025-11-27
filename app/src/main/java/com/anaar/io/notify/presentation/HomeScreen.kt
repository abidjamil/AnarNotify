package com.anaar.io.notify.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anaar.io.notify.clearUserId
import com.anaar.io.notify.getUserIdFlow
import com.anaar.io.notify.ui.theme.PinkPrimary
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    modifier: Modifier? = null,
    onLogoutClick: (String) -> Unit
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
        modifier = Modifier.padding(top = 50.dp),
        horizontalAlignment = Alignment.CenterHorizontally, // centers horizontally
        verticalArrangement = Arrangement.Center // centers vertically
    ) {

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

            // Bottom continue button (100dp from bottom)
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
                    containerColor = PinkPrimary, // background color
                    contentColor = Color.White
                )
            ) {
                Text("Logout")
            }

            Spacer(modifier = Modifier.height(20.dp))


        }
    }

}

@Preview
@Composable
fun previewScreen() {
    HomeScreen(
        onLogoutClick = {

        })
}