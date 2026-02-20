package com.example.quizandroid.data.remote

import com.example.quizandroid.data.model.Question
import com.example.quizandroid.data.model.Quiz
import com.example.quizandroid.ui.QuestionFormState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

data class QuizAttempt(
    val quizId: String = "",
    val score: Int = 0,
    val completedAt: Long = System.currentTimeMillis()
)

class QuizRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun publishQuiz(
        quizIdToUpdate: String? = null,
        title: String,
        authorId: String,
        authorName: String,
        questionsStates: List<QuestionFormState>,
        status: String = "ativo"
    ): Boolean {
        return try {
            val quizRef = if (quizIdToUpdate != null) {
                db.collection("quizzes").document(quizIdToUpdate)
            } else {
                db.collection("quizzes").document()
            }
            val quizId = quizRef.id

            val quiz = Quiz(
                id = quizId,
                title = title,
                authorId = authorId,
                authorName = authorName,
                questionCount = questionsStates.size,
                isActive = (status == "ativo"),
                status = status
            )

            val quizData = hashMapOf(
                "id" to quiz.id,
                "title" to quiz.title,
                "authorId" to quiz.authorId,
                "authorName" to quiz.authorName,
                "questionCount" to quiz.questionCount,
                "isActive" to quiz.isActive,
                "status" to status
            )

            if (quizIdToUpdate == null) {
                quizData["createdAt"] = System.currentTimeMillis()
            }

            quizRef.set(quizData, SetOptions.merge()).await()

            if (quizIdToUpdate != null) {
                val oldQuestions = db.collection("questions").whereEqualTo("quizId", quizId).get().await()
                for (doc in oldQuestions.documents) {
                    doc.reference.delete().await()
                }
            }

            val questionsCollection = db.collection("questions")

            // --- CORREÇÃO AQUI: forEachIndexed pega a pergunta e a posição dela (index) ---
            questionsStates.forEachIndexed { index, state ->
                val questionRef = questionsCollection.document()
                val question = Question(
                    id = questionRef.id,
                    quizId = quizId,
                    text = state.questionText,
                    options = state.options,
                    correctAnswerIndex = state.correctAnswerIndex,
                    orderIndex = index // <-- Salva a ordem exata em que foi digitada
                )
                questionRef.set(question).await()
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getActiveQuizzes(): List<Quiz> {
        return try {
            val snapshot = db.collection("quizzes").get().await()
            val allQuizzes = snapshot.toObjects(Quiz::class.java)
            allQuizzes.filter { quiz -> quiz.status == "ativo" || (quiz.status != "rascunho" && quiz.isActive) }
        } catch (e: Exception) { emptyList() }
    }

    suspend fun getMyDrafts(userId: String): List<Quiz> {
        return try {
            val snapshot = db.collection("quizzes")
                .whereEqualTo("authorId", userId)
                .whereEqualTo("status", "rascunho")
                .get().await()
            snapshot.toObjects(Quiz::class.java)
        } catch (e: Exception) { emptyList() }
    }

    suspend fun releaseQuiz(quizId: String): Boolean {
        return try {
            db.collection("quizzes").document(quizId).update("status", "ativo", "isActive", true).await()
            true
        } catch (e: Exception) { false }
    }

    // --- CORREÇÃO AQUI: Organiza as perguntas pela posição salva antes de devolver para a tela ---
    suspend fun getQuestionsByQuizId(quizId: String): List<Question> {
        return try {
            val snapshot = db.collection("questions").whereEqualTo("quizId", quizId).get().await()
            val questions = snapshot.toObjects(Question::class.java)
            // Faz a ordenação localmente para não dar erro de índice no Firebase
            questions.sortedBy { it.orderIndex }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun saveQuizAttempt(uid: String, quizId: String, score: Int): Boolean {
        return try {
            val attempt = QuizAttempt(quizId, score)
            db.collection("users").document(uid).collection("completed_quizzes").document(quizId).set(attempt).await()
            true
        } catch (e: Exception) { false }
    }

    suspend fun getUserCompletedQuizzes(uid: String): List<QuizAttempt> {
        return try {
            val snapshot = db.collection("users").document(uid).collection("completed_quizzes").get().await()
            snapshot.toObjects(QuizAttempt::class.java)
        } catch (e: Exception) { emptyList() }
    }
}