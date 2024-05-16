package com.nkt.operatorsapp.data.repositories

interface ParamsRepository {

    suspend fun getAll(): Map<String, String>

    suspend fun updateAll(params: Map<String, String>): Boolean
}