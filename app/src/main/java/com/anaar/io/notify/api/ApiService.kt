package com.anaar.io.notify.api

import com.anaar.io.notify.CallRequest
import com.anaar.io.notify.User
import com.anaar.io.notify.model.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("api/autofill-phone")
    suspend fun sendCall(
        @Body request: CallRequest
    ): Response<Unit>

    @POST("api/notify_login")
    suspend fun login(
        @Body request: User
    ): Response<LoginResponse>
}
