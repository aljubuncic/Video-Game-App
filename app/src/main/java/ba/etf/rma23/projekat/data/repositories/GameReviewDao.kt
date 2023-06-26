package ba.etf.rma23.projekat.data.repositories

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface GameReviewDao {
    @Query("SELECT * FROM gamereview WHERE online = false")
    suspend fun getOfflineReviews():List<GameReview>
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(gameReview: GameReview)
}