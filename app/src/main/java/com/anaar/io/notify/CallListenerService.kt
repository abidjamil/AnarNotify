package com.anaar.io.notify

import android.os.Build
import android.os.Handler
import android.os.Looper
import android.telecom.Call
import android.telecom.CallScreeningService
import android.util.Log
import android.widget.Toast
import androidx.datastore.preferences.core.stringPreferencesKey
import com.anaar.io.notify.api.RetrofitClient
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

data class CallRequest(
    val user_id: Int,
    val phone: String
)

data class User(
    val email: String,
    val password: String
)
class MyCallScreeningService : CallScreeningService() {

    override fun onScreenCall(callDetails: Call.Details) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (callDetails.getCallDirection() == Call.Details.DIRECTION_INCOMING) {
                val response = CallResponse.Builder()
                response.setDisallowCall(false)
                response.setRejectCall(false)
                response.setSilenceCall(false)
                response.setSkipCallLog(false)
                response.setSkipNotification(false)
                CoroutineScope(Dispatchers.IO).launch {
                    callDetails.getHandle()?.let {
                        sendCallToApi(callDetails.getHandle().toString())
                    }
                }
                respondToCall(callDetails, response.build())

            }
        }
    }

    private suspend fun sendCallToApi(phone: String) {
        val userId = applicationContext.userIdDataStore.data
            .map { prefs -> prefs[stringPreferencesKey("user_id")]?.toIntOrNull() ?: 0 }
            .first()

        if(phone.removeRange(0,3).isEmpty().not()) {

            val request = CallRequest(user_id = userId, phone = phone.removeRange(0, 3))

            try {
                val response = RetrofitClient.apiService.sendCall(request)
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(
                        applicationContext,
                        "Number posted successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                Log.d("notify app", "sendCallToApi: " + Gson().toJson(response.isSuccessful))
            } catch (e: Exception) {
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT).show()
                }
                Log.d("notify app", "sendCallToApi: failure" + Gson().toJson(e))

            }
        }
    }
}

