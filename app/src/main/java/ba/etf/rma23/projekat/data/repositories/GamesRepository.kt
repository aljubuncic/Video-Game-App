package ba.etf.rma23.projekat.data.repositories

import ba.etf.rma23.projekat.Game
import ba.etf.rma23.projekat.data.repositories.response.ESRBRating
import ba.etf.rma23.projekat.data.repositories.response.GameResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class GamesRepository {
    companion object {
        private var age : Int? = null
        private var gameList : List<Game>? = null
        private var PEGI : MutableMap<Int,Int> = HashMap<Int,Int>()
        private var ESRB : MutableMap<Int,ESRBRating> = HashMap<Int,ESRBRating>()

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

        fun getAge():Int?{
            return age
        }

        fun setAge(age:Int?):Unit{
            this.age = age
        }

        private fun convertTimestampToDate(timestamp: Long): String {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = Date(timestamp * 1000)
            return dateFormat.format(date)
        }

        private fun buildRequestBody(id: Int): RequestBody {
            val text = "fields id,name,platforms.id,platforms.name,first_release_date,rating,"  +
                        "age_ratings.category,age_ratings.rating,cover.url,genres.id,genres.name," +
                        "involved_companies.company.name,involved_companies.developer,involved_companies.publisher,summary; where id = $id;"
            return text.toRequestBody()
        }
        suspend fun getGameById(id: Int): Game {
            return withContext(Dispatchers.IO) {
                var response = IGDBApiConfig.retrofit.getGameById(buildRequestBody(id))
                if(response == null)
                    throw Exception("Game not found")
                var gameResponse = response.body()!!.first()
                gameResponse = modifyGameResponse(gameResponse)
                return@withContext Game(gameResponse)
            }
        }
        private fun modifyGameResponse(it: GameResponse):GameResponse{
            if(it.ageRatings==null)
                it.esrbRating = ""
            else {
                it.ageRatings.forEach { ageRating ->
                    if (ageRating.category == 1) {
                        it.esrbRating = ESRB[ageRating.rating]!!.name
                    }
                    if(ageRating.category == 2){
                        it.pegiRating = PEGI[ageRating.rating]!!
                    }
                }
                if(it.esrbRating == null)
                    it.esrbRating = ""
            }
            if(it.title == null)
                it.title = ""
            if(it.rating == null)
                it.rating = 0.0
            if(it.description == null)
                it.description = ""
            if(it.releaseDate == null)
                it.releaseDate = ""
            else
                it.releaseDate = convertTimestampToDate(it.releaseDate.toLong())
            if(it.platforms==null)
                it.platform = ""
            else
                it.platform = it.platforms.first().name
            if(it.genres==null)
                it.genre = ""
            else
                it.genre = it.genres.first().name
            if(it.cover==null)
                it.coverImage = "";
            else
                it.coverImage = it.cover.url
            if(it.involvedCompanies==null || it.involvedCompanies.isEmpty()) {
                it.developer = ""
                it.publisher = ""
            }
            else {
                it.involvedCompanies.forEach{involvedCompany ->
                    if(involvedCompany.isDeveloper)
                        it.developer = involvedCompany.company.name
                    if(involvedCompany.isPublisher)
                        it.publisher = involvedCompany.company.name
                }
            }
            if(it.developer == null)
                it.developer = ""
            if (it.publisher == null)
                it.publisher = ""
            return it
        }
        /*suspend fun getPlatformNameById(id: Int): String {
            return withContext(Dispatchers.IO) {
                var response = IGDBApiConfig.retrofit.getPlatformsById(buildRequestBody(id))
                val responseBody = response.body()
                return@withContext responseBody!!.first().name
            }
        }
        suspend fun getGenreNameById(id: Int): String {
            return withContext(Dispatchers.IO) {
                var response = IGDBApiConfig.retrofit.getGenresById(buildRequestBody(id))
                val responseBody = response.body()
                return@withContext responseBody!!.first().name
            }
        }
        suspend fun getCoverUrlById(id:Int): String {
            return withContext(Dispatchers.IO) {
                var response = IGDBApiConfig.retrofit.getCoverById(buildRequestBody(id))
                val responseBody = response.body()
                if(responseBody!!.isEmpty())
                    return@withContext ""
                return@withContext responseBody!!.first().url
            }
        }
        suspend fun getEsrbRatingById(id : Int): Int? {
            return withContext(Dispatchers.IO) {
                var response = IGDBApiConfig.retrofit.getAgeRatingById(buildRequestBody(id))
                val responseBody = response.body()
                val ageRating = responseBody!!.first()
                if(ageRating.category!=1)
                    return@withContext null
                return@withContext ageRating.rating
            }
        }
        suspend fun getCompanyNameById(company : Int): String {
            return withContext(Dispatchers.IO) {
                var response = IGDBApiConfig.retrofit.getCompanyById(buildRequestBody(company))
                val responseBody = response.body()
                return@withContext responseBody!!.first().name
            }
        }
        suspend fun getPublisherName(involvedCompanies :List<Int>): String {
            return withContext(Dispatchers.IO) {
                involvedCompanies.forEach {
                    var response = IGDBApiConfig.retrofit.getInvolvedCompanyById(buildRequestBody(it))
                    val responseBody = response.body()
                    if (responseBody!!.isEmpty())
                        return@withContext ""
                    if(responseBody!!.first().isPublisher)
                        return@withContext getCompanyNameById(responseBody!!.first().companyId)
                }
                return@withContext ""
            }
        }
        suspend fun getDeveloperName(involvedCompanies :List<Int>): String {
            return withContext(Dispatchers.IO) {
                involvedCompanies.forEach {
                    var response = IGDBApiConfig.retrofit.getInvolvedCompanyById(buildRequestBody(it))
                    val responseBody = response.body()
                    if (responseBody!!.isEmpty())
                        return@withContext ""
                    if(responseBody!!.first().isDeveloper)
                        return@withContext getCompanyNameById(responseBody!!.first().companyId)
                }
                return@withContext ""
            }
        }*/

        suspend fun getGamesByName(name:String):List<Game>{
            return withContext(Dispatchers.IO) {
                val fields = "id,name,platforms.id,platforms.name,first_release_date,rating," +
                        "age_ratings.category,age_ratings.rating,cover.url,genres.id,genres.name," +
                        "involved_companies.company.name,involved_companies.developer,involved_companies.publisher,summary"
                val response = IGDBApiConfig.retrofit.getGamesByName(fields,name,10)
                val responseBody = response.body()
                val games = mutableListOf<Game>()
                if(responseBody == null)
                    return@withContext games
                responseBody.forEach {
                    modifyGameResponse(it)
                    games.add(Game(it))
                }
                gameList = games
                return@withContext games
            }
        }

        suspend fun getGamesSafe(name:String):List<Game>{
            return withContext(Dispatchers.IO){
                if(age == null)
                    return@withContext listOf()
                val fields = "id,name,platforms.id,platforms.name,first_release_date,rating," +
                        "age_ratings.category,age_ratings.rating,cover.url,genres.id,genres.name," +
                        "involved_companies.company.name,involved_companies.developer,involved_companies.publisher,summary"
                val response = IGDBApiConfig.retrofit.getGamesByName(fields,name,10)
                val responseBody = response.body()
                val games = mutableListOf<Game>()
                if(responseBody == null)
                    return@withContext games
                responseBody.forEach loop@{
                    if(it.ageRatings==null)
                        it.esrbRating = ""
                    else {
                        it.ageRatings.forEach { ageRating ->
                            if(ageRating.category == 2){
                                if(age!!<PEGI[ageRating.rating]!!)
                                    return@loop
                                it.pegiRating = PEGI[ageRating.rating]!!
                            }
                            else if (ageRating.category == 1) {
                                if(age!!<ESRB[ageRating.rating]!!.age)
                                    return@loop
                                it.esrbRating = ESRB[ageRating.rating]!!.name
                            }
                        }
                        if(it.esrbRating == null)
                            it.esrbRating = ""
                    }
                    if(it.title == null)
                        it.title = ""
                    if(it.rating == null)
                        it.rating = 0.0
                    if(it.description == null)
                        it.description = ""
                    if(it.releaseDate == null)
                        it.releaseDate = ""
                    else
                        it.releaseDate = convertTimestampToDate(it.releaseDate.toLong())
                    if(it.platforms==null)
                        it.platform = ""
                    else
                        it.platform = it.platforms.first().name
                    if(it.genres==null)
                        it.genre = ""
                    else
                        it.genre = it.genres.first().name
                    if(it.cover==null)
                        it.coverImage = "";
                    else
                        it.coverImage = it.cover.url
                    if(it.involvedCompanies==null || it.involvedCompanies.isEmpty()) {
                        it.developer = ""
                        it.publisher = ""
                    }
                    else {
                        it.involvedCompanies.forEach{involvedCompany ->
                            if(involvedCompany.isDeveloper)
                                it.developer = involvedCompany.company.name
                            if(involvedCompany.isPublisher)
                                it.publisher = involvedCompany.company.name
                        }
                    }
                    if(it.developer == null)
                        it.developer = ""
                    if (it.publisher == null)
                        it.publisher = ""
                    games.add(Game(it))
                }
                gameList = games
                return@withContext games

            }
        }
        suspend fun sortGames():List<Game> {
            if(gameList==null)
                throw Exception("There has not been any searched games recently")
            val savedGames = AccountGamesRepository.getSavedGames()
            gameList = gameList!!.sortedWith <Game> (object : Comparator <Game> {
                override fun compare(game1: Game, game2: Game): Int {
                    if(game1 in savedGames){
                        if(game2 !in savedGames)
                            return -1
                        else{
                            if(game1.title>game2.title)
                                return 1
                            else if (game1.title == game2.title)
                                return 0
                            else
                                return -1
                        }
                    }
                    else if(game2 in savedGames)
                        return 1
                    else{
                        if(game1.title>game2.title)
                            return 1
                        else if (game1.title == game2.title)
                            return 0
                        else
                            return -1
                    }
                }
            })
            return gameList!!
        }
    }
}