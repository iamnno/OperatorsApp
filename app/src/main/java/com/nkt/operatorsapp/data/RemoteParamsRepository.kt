package com.nkt.operatorsapp.data

import com.google.firebase.firestore.FirebaseFirestore
import com.nkt.operatorsapp.data.repositories.ParamsRepository
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class RemoteParamsRepository @Inject constructor(
    private val db: FirebaseFirestore
) : ParamsRepository {

    override suspend fun getAll(): Map<String, String> =
        suspendCancellableCoroutine { continuation ->
            val params = mutableMapOf<String, String>()

            db.collection("params")
                .limit(1)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot.documents) {
                        val documentId = document.id

                        db.collection("params").document(documentId)
                            .get()
                            .addOnSuccessListener {
                                params[PARAM_1] = it?.data?.get(PARAM_1).toString()
                                params[PARAM_2] = it?.data?.get(PARAM_2).toString()
                                params[PARAM_3] = it?.data?.get(PARAM_3).toString()

                                continuation.resume(params)
                            }

                        break
                    }
                }.addOnFailureListener { e ->
                    continuation.resumeWithException(e)
                }
        }

    override suspend fun updateAll(params: Map<String, String>): Boolean =
        suspendCancellableCoroutine { continuation ->
            db.collection("params")
                .limit(1)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot.documents) {
                        val documentId = document.id

                        val updatedData = mapOf(
                            PARAM_1 to params[PARAM_1],
                            PARAM_2 to params[PARAM_2],
                            PARAM_3 to params[PARAM_3],
                        )

                        db.collection("params").document(documentId).update(updatedData)
                            .addOnSuccessListener {
                                continuation.resume(true)
                            }
                            .addOnFailureListener { e ->
                                continuation.resume(false)
                            }

                        break
                    }
                }
                .addOnFailureListener {
                    continuation.resume(false)
                }
        }

    companion object {
        const val PARAM_1 = "param1"
        const val PARAM_2 = "param2"
        const val PARAM_3 = "param3"
    }
}