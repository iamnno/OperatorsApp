package com.nkt.operatorsapp.data.repositories

interface QuestionnaireRepository {

    suspend fun getAll(): List<String>

    suspend fun save(keyWord: String): Boolean

    suspend fun deleteById(id: String): Boolean

    suspend fun delete(query: String): Boolean
}