package ba.etf.rma23.projekat.data.repositories

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Response

class GameReviewsRepository {
    companion object{
        suspend fun getOfflineReviews(context: Context):List<GameReview>{
            return withContext(Dispatchers.IO) {
                val db = AppDatabase.getInstance(context)
                return@withContext db.gameReviewDao().getOfflineReviews()
            }
        }
        suspend fun sendOfflineReviews(context: Context):Int{
            return withContext(Dispatchers.IO){
                val gameReviews = getOfflineReviews(context)
                var counter = 0
                for(gameReview in gameReviews)
                    if(sendReview(context,gameReview))
                        counter++

                return@withContext counter
            }
        }
        suspend fun sendReview(context: Context,gameReview: GameReview):Boolean{
            return withContext(Dispatchers.IO){
                var response: Response<JSONObject>? =null
                try {
                    val game = GamesRepository.getGameById(gameReview.igdb_id)
                    if (game !in AccountGamesRepository.getSavedGames())
                        AccountGamesRepository.saveGame(game)
                    val requestBody =
                        "{ \"review\": \"${gameReview.review}\", \"rating\": ${gameReview.rating} }"
                    response = AccountApiConfig.retrofit.sendReview(
                        AccountGamesRepository.getHash(),
                        gameReview.igdb_id,
                        requestBody.toRequestBody("application/json".toMediaTypeOrNull())
                    )
                    if (response.isSuccessful) {
                        gameReview.online = true
                        val db = AppDatabase.getInstance(context)
                        db.gameReviewDao().update(gameReview)
                    }
                    else {
                        gameReview.online = false
                        val db = AppDatabase.getInstance(context)
                        db.gameReviewDao().insert(gameReview)
                    }
                }
                catch (_:Exception){
                    gameReview.online = false
                    val db = AppDatabase.getInstance(context)
                    db.gameReviewDao().insert(gameReview)
                    return@withContext false
                }
                return@withContext response.isSuccessful
            }
        }
        suspend fun getReviewsForGame(igdb_id:Int):List<GameReview>{
            return withContext(Dispatchers.IO){
                val response = AccountApiConfig.retrofit.getReviewsForGame(igdb_id)
                val gameReviews = response.body()
                if(gameReviews == null)
                    return@withContext listOf()
                gameReviews.forEach {
                    it.igdb_id = igdb_id
                    it.online = true
                }
                return@withContext gameReviews
            }
        }
    }
}