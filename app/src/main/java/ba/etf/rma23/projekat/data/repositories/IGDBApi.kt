package ba.etf.rma23.projekat.data.repositories

import ba.etf.rma23.projekat.*
import ba.etf.rma23.projekat.data.repositories.response.*
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface IGDBApi {
    @Headers("Client-ID: " + BuildConfig.IGDB_API_CLIENT_ID,"Authorization: " + BuildConfig.IGDB_API_AUTHORIZATION)
    @POST("games")
    suspend fun getGamesByName(@Query("fields") fields:String, @Query("search") name:String, @Query("limit") limit:Int):Response<List<GameResponse>>
    @Headers("Client-ID: " + BuildConfig.IGDB_API_CLIENT_ID,"Authorization: " + BuildConfig.IGDB_API_AUTHORIZATION)
    @POST("games")
    suspend fun getGamesSafe(@Body requestBody: RequestBody):Response<List<GameResponse>>






    @Headers("Client-ID: f8lhsi8lnzno5s5qsukldo03lwxmcf","Authorization: Bearer 4chmtjeuxetesv3ab0acsu6o9dxbnb")
    @POST("platforms")
    suspend fun getPlatformsById(@Body requestBody: RequestBody):Response<List<Platform>>

    @Headers("Client-ID: f8lhsi8lnzno5s5qsukldo03lwxmcf","Authorization: Bearer 4chmtjeuxetesv3ab0acsu6o9dxbnb")
    @POST("genres")
    suspend fun getGenresById(@Body requestBody: RequestBody):Response<List<Genre>>

    @Headers("Client-ID: f8lhsi8lnzno5s5qsukldo03lwxmcf","Authorization: Bearer 4chmtjeuxetesv3ab0acsu6o9dxbnb")
    @POST("covers")
    suspend fun getCoverById(@Body requestBody: RequestBody):Response<List<Cover>>

    @Headers("Client-ID: f8lhsi8lnzno5s5qsukldo03lwxmcf","Authorization: Bearer 4chmtjeuxetesv3ab0acsu6o9dxbnb")
    @POST("age_ratings")
    suspend fun getAgeRatingById(@Body requestBody: RequestBody):Response<List<AgeRating>>

    @Headers("Client-ID: f8lhsi8lnzno5s5qsukldo03lwxmcf","Authorization: Bearer 12ftnuz6wcunxksxgf6mikq2jousqz")
    @POST("games")
    suspend fun getGameById(@Body requestBody: RequestBody):Response<List<GameResponse>>

    @Headers("Client-ID: f8lhsi8lnzno5s5qsukldo03lwxmcf","Authorization: Bearer 4chmtjeuxetesv3ab0acsu6o9dxbnb")
    @POST("involved_companies")
    suspend fun getInvolvedCompanyById(@Body requestBody: RequestBody):Response<List<InvolvedCompany>>

    @Headers("Client-ID: f8lhsi8lnzno5s5qsukldo03lwxmcf","Authorization: Bearer 4chmtjeuxetesv3ab0acsu6o9dxbnb")
    @POST("companies")
    suspend fun getCompanyById(@Body requestBody: RequestBody):Response<List<Company>>
}