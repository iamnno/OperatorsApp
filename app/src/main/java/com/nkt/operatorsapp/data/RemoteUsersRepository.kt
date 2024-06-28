package com.nkt.operatorsapp.data

import com.google.firebase.firestore.FirebaseFirestore
import com.nkt.operatorsapp.data.repositories.UsersRepository
import kotlinx.coroutines.suspendCancellableCoroutine
import org.mindrot.jbcrypt.BCrypt
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class RemoteUsersRepository @Inject constructor(
    private val db: FirebaseFirestore
) : UsersRepository {
    override suspend fun getAll(): List<User> = suspendCancellableCoroutine { continuation ->
        val users = mutableListOf<User>()

        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val username = document.data["username"].toString()
                    val type = document.data["type"].toString()
                    val hash = document.data["hash"].toString()

                    users.add(
                        User(
                            type = getType(type),
                            username = username,
                            hash = hash
                        )
                    )
                }
                continuation.resume(users)
            }
            .addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
    }

    override suspend fun create(username: String, type: UserType, hash: String): Boolean =
        suspendCancellableCoroutine { continuation ->
            val user = User(type = type, username = username, hash = hash)

            db.collection("users")
                .add(user)
                .addOnSuccessListener {
                    continuation.resume(true)
                }.addOnFailureListener { e ->
                    continuation.resume(false)
                }
        }

    override suspend fun delete(user: User): Boolean = suspendCancellableCoroutine { continuation ->
        db.collection("users")
            .whereEqualTo("username", user.username)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    val documentId = document.id

                    db.collection("users")
                        .document(documentId)
                        .delete()
                        .addOnSuccessListener {
                            continuation.resume(true)
                        }
                        .addOnFailureListener {
                            continuation.resume(false)
                        }
                }
            }
            .addOnFailureListener {
                continuation.resume(false)
            }
    }

    companion object {
        fun getType(type: String): UserType {
            return when (type) {
                "OPERATOR_1" -> UserType.OPERATOR_1
                "OPERATOR_2" -> UserType.OPERATOR_2
                else -> {
                    UserType.ADMINISTRATOR
                }
            }
        }
    }


}