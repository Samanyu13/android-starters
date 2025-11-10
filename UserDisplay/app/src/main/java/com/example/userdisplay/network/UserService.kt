package com.example.userdisplay.network

import com.example.userdisplay.data.UserResponse
import retrofit2.http.GET

interface UserService {
    @GET("api/")
    suspend fun getRandomUser(): UserResponse
}