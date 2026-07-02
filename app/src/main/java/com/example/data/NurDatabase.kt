package com.example.data

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "nur_app_state")
data class NurAppState(
  @PrimaryKey val id: Int = 1,
  val isDarkMode: Boolean = false,
  val userName: String = "Sarah Jenkins",
  val userXp: Int = 12450,
  val userLevel: Int = 12,
  val streakDays: Int = 12,
  val profileStreakDays: Int = 14,
  val fajrCompleted: Boolean = true,
  val dhuhrCompleted: Boolean = false,
  val asrCompleted: Boolean = false,
  val maghribCompleted: Boolean = false,
  val ishaCompleted: Boolean = false,
  val waterIntakeMl: Float = 1200f,
  val quranPages: Int = 5,
  val studyHours: Float = 2.5f,
  val techProgress: Int = 60,
  val workoutActiveMin: Int = 42,
  val workoutCalories: Int = 320,
  val workoutHeartRate: Int = 145,
  val workoutDurationSelected: Int = 30, // 20, 30, 40, 60
  val workoutActivitySelected: String = "Running", // Gym, Running, Yoga, Cycling
  val currentMood: String = "Feeling Excellent", // Excellent, Good, Calm, Normal
  val dietCalorieGoal: Int = 2000,
  val dietCaloriesConsumed: Int = 1250,
  val dietCarbsGoalG: Int = 250,
  val dietCarbsConsumedG: Int = 145,
  val dietProteinGoalG: Int = 130,
  val dietProteinConsumedG: Int = 85,
  val dietFatGoalG: Int = 65,
  val dietFatConsumedG: Int = 38,
  val userWeightKg: Float = 72.0f,
  val weatherTempC: Float = 35.0f,
  val fitnessGoal: String = "Maintain Weight"
)

@Entity(tableName = "nur_activity_log")
data class NurActivityLog(
  @PrimaryKey(autoGenerate = true) val id: Int = 0,
  val title: String,
  val subtitle: String,
  val xpReward: Int,
  val type: String, // PRAYER, WORKOUT, STUDY, QURAN, MOOD
  val timestamp: Long = System.currentTimeMillis()
)

@Dao
interface NurDao {
  @Query("SELECT * FROM nur_app_state WHERE id = 1 LIMIT 1")
  fun getAppStateFlow(): Flow<NurAppState?>

  @Query("SELECT * FROM nur_app_state WHERE id = 1 LIMIT 1")
  suspend fun getAppState(): NurAppState?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAppState(state: NurAppState)

  @Update
  suspend fun updateAppState(state: NurAppState)

  @Query("SELECT * FROM nur_activity_log ORDER BY timestamp DESC LIMIT 20")
  fun getActivityLogsFlow(): Flow<List<NurActivityLog>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertActivityLog(log: NurActivityLog)

  @Query("DELETE FROM nur_activity_log")
  suspend fun clearActivityLogs()
}

@Database(entities = [NurAppState::class, NurActivityLog::class], version = 3, exportSchema = false)
abstract class NurDatabase : RoomDatabase() {
  abstract fun nurDao(): NurDao
}
