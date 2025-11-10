package com.example.userdisplay.data

data class UserResponse(
    val results: List<UserData>
)

data class UserData(
    val name: UserName,
    val email: String,
    val picture: UserPic
)

data class UserName(
    val first: String,
    val last: String
)

data class UserPic(
    val large: String
)