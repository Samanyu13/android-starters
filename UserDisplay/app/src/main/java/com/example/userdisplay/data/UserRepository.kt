package com.example.userdisplay.data

import com.example.userdisplay.network.RetrofitClient
import com.example.userdisplay.network.UserService

class UserRepository(private val service: UserService = RetrofitClient.service) {
    suspend fun fetchUser(): Result<UserData> {
        return try {
            val response = service.getRandomUser()
            val user = response.results.firstOrNull()

            user?.let { Result.success(it) }
                ?: Result.failure(Exception("API returned no results!"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}