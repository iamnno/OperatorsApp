package com.nkt.operatorsapp.data.repositories

import com.nkt.operatorsapp.data.User

interface AuthRepository {

    fun isUserSignedIn(): Boolean

    suspend fun getUserProfile(): User

    suspend fun signIn(username: String, password: String): User

    suspend fun signOut()
}