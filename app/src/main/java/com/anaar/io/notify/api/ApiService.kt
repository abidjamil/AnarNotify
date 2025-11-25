package com.anaar.io.notify.api

import com.anaar.io.notify.CallRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("api/autofill-phone")
    suspend fun sendCall(
        @Body request: CallRequest
    ): Response<Unit>
}
