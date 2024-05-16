package com.nkt.operatorsapp.data

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.firestore.FirebaseFirestore
import com.nkt.operatorsapp.data.RemoteUsersRepository.Companion.getType
import com.nkt.operatorsapp.data.repositories.AuthRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import org.mindrot.jbcrypt.BCrypt
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class AuthRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val db: FirebaseFirestore
) : AuthRepository {
    override suspend fun isUserSignedIn(): User? = suspendCancellableCoroutine { continuation ->
        val userId = get()
        if (userId == null || userId == "") {
            continuation.resume(null)
        } else {
            db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener {
                    val username = it?.data?.get("username").toString()
                    val userType = it?.data?.get("type").toString()
                    val hash = it?.data?.get("hash").toString()

                    val user = User(getType(userType), username, hash)
                    continuation.resume(user)
                }
                .addOnFailureListener {
                    continuation.resume(null)
                }
        }
    }

    private fun get(): String? {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE)

        return sharedPreferences.getString(USER_SHARED_KEY, null)
    }

    private fun save(userId: String) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString(USER_SHARED_KEY, userId)
        editor.apply()
    }

    override suspend fun signIn(username: String, password: String): String? =
        suspendCancellableCoroutine { continuation ->
            db.collection("users")
                .whereEqualTo("username", username)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot.documents) {
                        val documentId = document.id

                        db.collection("users")
                            .document(documentId)
                            .get()
                            .addOnSuccessListener {
                                if (BCrypt.checkpw(password, it?.data?.get("hash")?.toString())) {
                                    continuation.resume(documentId)
                                    save(documentId)
                                } else {
                                    continuation.resume(null)
                                }
                            }
                            .addOnFailureListener {
                                continuation.resume(null)
                            }
                    }
                }
                .addOnFailureListener {
                    continuation.resume(null)
                }
        }

    override suspend fun signOut() {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString(USER_SHARED_KEY, "")
        editor.apply()
    }

    companion object {
        private const val PREFS_FILE_NAME = "MyPreferences"
        private const val USER_SHARED_KEY = "USER_SHARED_KEY"
    }
}