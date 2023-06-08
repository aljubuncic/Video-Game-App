package ba.etf.rma23.projekat.data.repositories

import ba.etf.rma23.projekat.Game
import ba.etf.rma23.projekat.data.repositories.response.SwaggerGameResponse
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface AccountApi {
    @POST("account/{aid}/game")
    suspend fun saveGame(@Path("aid") acHash:String,@Body requestBody: RequestBody):Response<JSONObject>
    @GET("account/{aid}/games")
    suspend fun getSavedGames(@Path("aid") acHash: String): Response<List<SwaggerGameResponse>>
    @DELETE("account/{aid}/game/{gid}/")
    suspend fun removeGame(@Path("aid") acHash: String,@Path("gid") igdb_id:Int):Response<JSONObject>
}