package com.nkt.operatorsapp.data.repositories

import com.nkt.operatorsapp.data.User
import com.nkt.operatorsapp.data.UserType

interface UsersRepository {

    suspend fun getAll(): List<User>

    suspend fun create(username: String, type: UserType, hash: String): Boolean

    suspend fun delete(user: User): Boolean
}