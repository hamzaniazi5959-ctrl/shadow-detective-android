package com.example.data.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Dao
interface DetectiveDao {
    @Query("SELECT * FROM player_profile WHERE id = 1")
    fun observeProfile(): Flow<PlayerProfileEntity?>

    @Query("SELECT * FROM player_profile WHERE id = 1")
    suspend fun getProfileOnce(): PlayerProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProfile(profile: PlayerProfileEntity)

    @Query("SELECT * FROM evidence_items ORDER BY taskId ASC, timestamp DESC")
    fun observeAllEvidence(): Flow<List<EvidenceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvidence(evidence: EvidenceEntity)

    @Query("SELECT * FROM jail_inmates ORDER BY taskId ASC")
    fun observeJailInmates(): Flow<List<JailInmateEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJailInmate(inmate: JailInmateEntity)

    @Query("SELECT * FROM ai_media_gallery ORDER BY timestamp DESC")
    fun observeAiMedia(): Flow<List<AiMediaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAiMedia(media: AiMediaEntity)

    @Query("DELETE FROM evidence_items")
    suspend fun clearAllEvidence()

    @Query("DELETE FROM jail_inmates")
    suspend fun clearAllInmates()
}

@Database(
    entities = [
        PlayerProfileEntity::class,
        EvidenceEntity::class,
        JailInmateEntity::class,
        AiMediaEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class DetectiveDatabase : RoomDatabase() {
    abstract fun detectiveDao(): DetectiveDao

    companion object {
        @Volatile
        private var INSTANCE: DetectiveDatabase? = null

        fun getInstance(context: Context): DetectiveDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DetectiveDatabase::class.java,
                    "shadow_detective.db"
                ).fallbackToDestructiveMigration(true).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class DetectiveRepository(private val dao: DetectiveDao) {
    val profileFlow: Flow<PlayerProfileEntity?> = dao.observeProfile()
    val evidenceFlow: Flow<List<EvidenceEntity>> = dao.observeAllEvidence()
    val inmatesFlow: Flow<List<JailInmateEntity>> = dao.observeJailInmates()
    val aiMediaFlow: Flow<List<AiMediaEntity>> = dao.observeAiMedia()

    suspend fun ensureInitialized(): PlayerProfileEntity {
        val existing = dao.getProfileOnce()
        if (existing != null) return existing
        val initial = PlayerProfileEntity()
        dao.saveProfile(initial)
        return initial
    }

    suspend fun updateProfile(transform: (PlayerProfileEntity) -> PlayerProfileEntity) {
        val current = ensureInitialized()
        val updated = transform(current)
        dao.saveProfile(updated)
    }

    suspend fun addEvidence(evidence: EvidenceEntity) {
        dao.insertEvidence(evidence)
    }

    suspend fun addJailInmate(inmate: JailInmateEntity) {
        dao.insertJailInmate(inmate)
    }

    suspend fun addAiMedia(media: AiMediaEntity) {
        dao.insertAiMedia(media)
    }

    suspend fun resetNewGame() {
        dao.clearAllEvidence()
        dao.clearAllInmates()
        dao.saveProfile(PlayerProfileEntity())
    }
}
