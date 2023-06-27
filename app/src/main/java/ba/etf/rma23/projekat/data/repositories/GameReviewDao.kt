package ba.etf.rma23.projekat.data.repositories

import androidx.room.*

@Dao
interface GameReviewDao {
    @Query("SELECT * FROM gamereview WHERE online = false")
    suspend fun getOfflineReviews():List<GameReview>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(gameReview: GameReview)
    @Update
    suspend fun update(gameReview: GameReview)
}