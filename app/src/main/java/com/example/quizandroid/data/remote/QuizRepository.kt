package com.example.quizandroid.data.remote

import com.example.quizandroid.data.model.Question
import com.example.quizandroid.data.model.Quiz
import com.example.quizandroid.ui.QuestionFormState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.Source
import kotlinx.coroutines.tasks.await

data class QuizAttempt(
    val quizId: String = "",
    val score: Int = 0,
    val completedAt: Long = System.currentTimeMillis()
)

class QuizRepository {
    private val db = FirebaseFirestore.getInstance()

    init {
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
        db.firestoreSettings = settings
    }

    suspend fun publishQuiz(
        quizIdToUpdate: String? = null,
        title: String,
        authorId: String,
        authorName: String,
        questionsStates: List<QuestionFormState>,
        status: String = "ativo"
    ): Boolean {
        return try {
            val quizRef =
                if (quizIdToUpdate != null) db.collection("quizzes").document(quizIdToUpdate)
                else db.collection("quizzes").document()

            val quizId = quizRef.id

            val quizData = hashMapOf(
                "id" to quizId,
                "title" to title,
                "authorId" to authorId,
                "authorName" to authorName,
                "questionCount" to questionsStates.size,
                "isActive" to (status == "ativo"),
                "status" to status
            )

            if (quizIdToUpdate == null) quizData["createdAt"] = System.currentTimeMillis()

            quizRef.set(quizData, SetOptions.merge())

            if (quizIdToUpdate != null) {
                val oldQuestions = try {
                    db.collection("questions").whereEqualTo("quizId", quizId).get().await()
                } catch (e: Exception) {
                    db.collection("questions").whereEqualTo("quizId", quizId).get(Source.CACHE)
                        .await()
                }
                for (doc in oldQuestions.documents) {
                    doc.reference.delete()
                }
            }

            val questionsCollection = db.collection("questions")

            questionsStates.forEachIndexed { index, state ->
                val questionRef = questionsCollection.document()
                val question = Question(
                    id = questionRef.id,
                    quizId = quizId,
                    text = state.questionText,
                    options = state.options,
                    correctAnswerIndex = state.correctAnswerIndex,
                    orderIndex = index
                )
                questionRef.set(question)
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            true
        }
    }

    suspend fun getActiveQuizzes(): List<Quiz> {
        return try {
            val snapshot = db.collection("quizzes").get().await()
            val allQuizzes = snapshot.toObjects(Quiz::class.java)
            allQuizzes.filter { quiz -> quiz.status == "ativo" || (quiz.status.isNullOrEmpty() && quiz.isActive) }
        } catch (e: Exception) {
            try {
                val cachedSnapshot = db.collection("quizzes").get(Source.CACHE).await()
                val allQuizzes = cachedSnapshot.toObjects(Quiz::class.java)
                allQuizzes.filter { quiz -> quiz.status == "ativo" || (quiz.status.isNullOrEmpty() && quiz.isActive) }
            } catch (e2: Exception) {
                emptyList()
            }
        }
    }

    suspend fun getMyDrafts(userId: String): List<Quiz> {
        return try {
            val snapshot = db.collection("quizzes").get().await()
            val allQuizzes = snapshot.toObjects(Quiz::class.java)
            allQuizzes.filter { quiz -> quiz.status == "rascunho" && quiz.authorId == userId }
        } catch (e: Exception) {
            try {
                val cachedSnapshot = db.collection("quizzes").get(Source.CACHE).await()
                val allQuizzes = cachedSnapshot.toObjects(Quiz::class.java)
                allQuizzes.filter { quiz -> quiz.status == "rascunho" && quiz.authorId == userId }
            } catch (e2: Exception) {
                emptyList()
            }
        }
    }

    suspend fun releaseQuiz(quizId: String): Boolean {
        return try {
            db.collection("quizzes").document(quizId).update("status", "ativo", "isActive", true)
            true
        } catch (e: Exception) {
            true
        }
    }

    suspend fun getQuestionsByQuizId(quizId: String): List<Question> {
        return try {
            val snapshot = db.collection("questions").whereEqualTo("quizId", quizId).get().await()
            val questions = snapshot.toObjects(Question::class.java)
            questions.sortedBy { it.orderIndex }
        } catch (e: Exception) {
            try {
                val cachedSnapshot =
                    db.collection("questions").whereEqualTo("quizId", quizId).get(Source.CACHE)
                        .await()
                val questions = cachedSnapshot.toObjects(Question::class.java)
                questions.sortedBy { it.orderIndex }
            } catch (e2: Exception) {
                emptyList()
            }
        }
    }

    suspend fun saveQuizAttempt(uid: String, quizId: String, score: Int): Boolean {
        return try {
            val attempt = QuizAttempt(quizId, score)
            db.collection("users").document(uid).collection("completed_quizzes").document(quizId)
                .set(attempt)
            true
        } catch (e: Exception) {
            true
        }
    }

    suspend fun getUserCompletedQuizzes(uid: String): List<QuizAttempt> {
        return try {
            val snapshot =
                db.collection("users").document(uid).collection("completed_quizzes").get().await()
            snapshot.toObjects(QuizAttempt::class.java)
        } catch (e: Exception) {
            try {
                val cachedSnapshot =
                    db.collection("users").document(uid).collection("completed_quizzes")
                        .get(Source.CACHE).await()
                cachedSnapshot.toObjects(QuizAttempt::class.java)
            } catch (e2: Exception) {
                emptyList()
            }
        }
    }

    suspend fun deleteQuiz(quizId: String): Boolean {
        return try {
            db.collection("quizzes").document(quizId).delete()
            val questions = try {
                db.collection("questions").whereEqualTo("quizId", quizId).get().await()
            } catch (e: Exception) {
                db.collection("questions").whereEqualTo("quizId", quizId).get(Source.CACHE).await()
            }
            for (doc in questions.documents) {
                doc.reference.delete()
            }
            true
        } catch (e: Exception) {
            true
        }
    }
}