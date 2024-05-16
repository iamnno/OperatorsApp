package com.nkt.operatorsapp.data.repositories

import com.nkt.operatorsapp.data.User

interface AuthRepository {

    suspend fun isUserSignedIn(): User?

    suspend fun signIn(username: String, password: String): String?

    suspend fun signOut()
}