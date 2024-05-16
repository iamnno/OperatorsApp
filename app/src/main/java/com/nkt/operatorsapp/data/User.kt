package com.nkt.operatorsapp.data

data class User(
    val type: UserType,
    val username: String,
    val hash: String
)