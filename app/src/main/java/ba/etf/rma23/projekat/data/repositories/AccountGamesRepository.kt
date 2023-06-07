package ba.etf.rma23.projekat.data.repositories

import ba.etf.rma23.projekat.Game
import ba.etf.rma23.projekat.data.repositories.response.ESRBRating
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.converter.gson.GsonConverterFactory

class AccountGamesRepository {
    companion object{
        var accountHash: String? = null
        var age: Int? = null
        var favoriteGames: MutableList<Game> = mutableListOf()
        private var PEGI : MutableMap<Int,Int> = HashMap<Int,Int>()
        private var ESRB : MutableMap<Int, ESRBRating> = HashMap<Int, ESRBRating>()

        init {
            PEGI[1] = 3
            PEGI[2] = 7
            PEGI[3] = 12
            PEGI[4] = 16
            PEGI[5] = 18
            ESRB[6] = ESRBRating("RP",-1)
            ESRB[7] = ESRBRating("EC",3)
            ESRB[8] = ESRBRating("E",-1)
            ESRB[9] = ESRBRating("E10",10)
            ESRB[10] = ESRBRating("T",13)
            ESRB[11] = ESRBRating("M",17)
            ESRB[12] = ESRBRating("AO",18)
        }


        fun setHash(acHash:String):Boolean{
            val assigned = accountHash!=null
            accountHash = acHash
            return assigned
        }
        fun getHash():String{
            if(accountHash == null)
                throw java.lang.Exception("Account hash is null")
            return accountHash!!
        }
        suspend fun saveGame(game: Game):Game{
            return withContext(Dispatchers.IO){
                if(accountHash == null)
                    throw java.lang.Exception("Hash is null")
                val requestBodyString = "{\n" +
                        "  \"game\": {\n" +
                        "    \"igdb_id\": "+ game.id + ",\n" +
                        "    \"name\": \"" + game.title + "\"\n" +
                        "  }\n" +
                        "}"
                val requestBody = requestBodyString.toRequestBody()
                val response = AccountApiConfig.retrofit.saveGame(accountHash!!,requestBody)
                if(response.isSuccessful) {
                    favoriteGames.add(game)
                    return@withContext game
                }
                else {
                    println(response.message())
                    throw Exception(response.message())}
            }
        }
        suspend fun getSavedGames():List<Game>{
            return withContext(Dispatchers.IO){
                if(accountHash == null)
                    throw java.lang.Exception("Hash is null")
                val games : MutableList<Game> = mutableListOf()
                val response = AccountApiConfig.retrofit.getSavedGames(accountHash!!)
                if(response.body()==null)
                    return@withContext listOf()
                response.body()!!.forEach {
                    val game = GamesRepository.getGameById(it.igdb_id)
                    games.add(game)
                }
                return@withContext games
            }
        }
        suspend fun removeGame(id:Int):Boolean{
            return withContext(Dispatchers.IO){
                if(accountHash == null)
                    throw java.lang.Exception("Hash is null")
                val response = AccountApiConfig.retrofit.removeGame(accountHash!!,id)
                return@withContext response.isSuccessful
            }
        }
        suspend fun removeNonSafe():Boolean{
            return withContext(Dispatchers.IO){
                val games = getSavedGames()
                /*games.forEach {
                    if(it.pegiRating == -1)
                        if(age < ESRB[])
                }*/
                return@withContext true
            }
        }
        suspend fun getGamesContainingString(query:String):List<Game>{
            return withContext(Dispatchers.IO){
                var games = getSavedGames()
                return@withContext games.filter {
                    it.title.contains(query)
                }
            }
        }
        fun setAge(age:Int):Boolean{
            if(age<3 || age>100)
                return false
            this.age = age
            return true
        }
    }
}