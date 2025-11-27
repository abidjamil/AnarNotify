package com.anaar.io.notify.api

import com.anaar.io.notify.CallRequest
import com.anaar.io.notify.model.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {
    @POST("api/autofill-phone")
    suspend fun sendCall(
        @Body request: CallRequest
    ): Response<Unit>

    @FormUrlEncoded
    @POST("api/notify_login")
    suspend fun login(
        @FieldMap fields: Map<String, String>
    ): Response<LoginResponse>
}
