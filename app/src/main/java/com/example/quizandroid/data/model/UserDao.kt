package com.example.quizandroid.data.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM user_stats WHERE uid = :userId")
    fun getUserById(userId: String): Flow<UserEntity?>

    @Query("UPDATE user_stats SET totalScore = totalScore + :points, quizzesDone = quizzesDone + 1 WHERE uid = :userId")
    suspend fun updateScore(userId: String, points: Int)

    @Query("UPDATE user_stats SET avatar = :newAvatar WHERE uid = :userId")
    suspend fun updateAvatar(userId: String, newAvatar: String)
}