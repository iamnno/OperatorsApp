package com.nkt.operatorsapp.data

import com.google.firebase.firestore.FirebaseFirestore
import com.nkt.operatorsapp.data.repositories.QuestionnaireRepository
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class RemoteQuestionnaireRepository @Inject constructor(
    private val db: FirebaseFirestore
) : QuestionnaireRepository {

    override suspend fun getAll(): List<String> = suspendCancellableCoroutine { continuation ->
        val queries = mutableListOf<String>()
        db.collection("queries")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    queries.add(document.data["text"].toString())
                }

                continuation.resume(queries)
            }
            .addOnFailureListener {
                continuation.resumeWithException(it)
            }
    }

    override suspend fun save(keyWord: String): Boolean =
        suspendCancellableCoroutine { continuation ->
            db.collection("queries")
                .add(mapOf("text" to keyWord))
                .addOnSuccessListener {
                    continuation.resume(true)
                }
                .addOnFailureListener { e ->
                    continuation.resume(false)
                }
        }

    override suspend fun deleteById(id: String): Boolean =
        suspendCancellableCoroutine { continuation ->
            db.collection("queries")
                .document(id)
                .delete()
                .addOnFailureListener {
                    continuation.resume(true)
                }
                .addOnFailureListener {
                    continuation.resume(false)
                }
        }

    override suspend fun delete(query: String): Boolean =
        suspendCancellableCoroutine { continuation ->
            db.collection("queries")
                .whereEqualTo("text", query)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot.documents) {
                        val documentId = document.id

                        db.collection("queries")
                            .document(documentId)
                            .delete()
                            .addOnFailureListener {
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
}