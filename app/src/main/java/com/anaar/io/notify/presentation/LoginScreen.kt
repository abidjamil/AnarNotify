package com.anaar.io.notify.presentation

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anaar.io.notify.R
import com.anaar.io.notify.api.RetrofitClient
import com.anaar.io.notify.saveUserId
import com.anaar.io.notify.ui.theme.PinkPrimary
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun LoginScreen(onLoginClick: (String) -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
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


        // Email Field
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password Field
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Login Button
        Button(
            onClick = { login(context, email, password, onLoginClick) },
            colors = ButtonDefaults.buttonColors(
                containerColor = PinkPrimary,
                contentColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Login")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Forgot Password
        Text(
            text = "Forgot password?",
            color = PinkPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Divider + Create account
        Text(
            text = "Don’t have an account? Create one",
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

fun login(context: Context, email: String?, password: String?, onLoginClick: (String) -> Unit) {
    if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
        Toast.makeText(context, "Enter correct credentials", Toast.LENGTH_SHORT).show()
        return
    }
    CoroutineScope(Dispatchers.IO).launch {
        val request= HashMap<String, String>()
        request.put("email",email)
        request.put("password",password)
        try {
            val response = RetrofitClient.apiService.login(request)
            Log.d("notify app", "sendCallToApi: " + response)
            if (response.isSuccessful && !response.body()?.error!!) {
                val userId = response.body()?.user_id ?: ""
                context.saveUserId(userId.toString())
                withContext(Dispatchers.Main) {
                    onLoginClick.invoke(userId.toString())
                }
            }
            else{
                Toast.makeText(context, response.body()?.error_msg?:"Wrong credentials", Toast.LENGTH_SHORT).show()
            }
           Log.d("notify app", "sendCallToApi: " + Gson().toJson(response.body()))
        } catch (e: Exception) {
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
            }
            Log.d("notify app", "sendCallToApi: failure" + Gson().toJson(e))

        }
    }

}

@Preview
@Composable
fun loginScreenPreview() {
    LoginScreen(onLoginClick = {})
}
