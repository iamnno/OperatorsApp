package com.nkt.operatorsapp.di

import com.nkt.operatorsapp.data.NetworkAuthRepository
import com.nkt.operatorsapp.data.RemoteParamsRepository
import com.nkt.operatorsapp.data.repositories.QuestionnaireRepository
import com.nkt.operatorsapp.data.RemoteQuestionnaireRepository
import com.nkt.operatorsapp.data.RemoteUsersRepository
import com.nkt.operatorsapp.data.repositories.AuthRepository
import com.nkt.operatorsapp.data.repositories.ParamsRepository
import com.nkt.operatorsapp.data.repositories.UsersRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindsQuestionnaireRepository(
        questionnaireRepository: RemoteQuestionnaireRepository
    ): QuestionnaireRepository

    @Binds
    @Singleton
    abstract fun bindsUsersRepository(
        usersRepository: RemoteUsersRepository
    ): UsersRepository

    @Binds
    @Singleton
    abstract fun bindsParamsRepository(
        paramsRepository: RemoteParamsRepository
    ): ParamsRepository

    @Binds
    @Singleton
    abstract fun bindsAuthRepository(
        authRepository: NetworkAuthRepository
    ): AuthRepository
}