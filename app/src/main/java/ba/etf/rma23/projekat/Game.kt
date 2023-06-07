package ba.etf.rma23.projekat

import ba.etf.rma23.projekat.data.repositories.response.GameResponse
import com.google.gson.annotations.SerializedName

data class Game(val id: Int,
                val title: String,
                val platform: String,
                val releaseDate: String,
                val rating: Double,
                val coverImage: String,
                val esrbRating: String,
                val developer: String,
                val publisher: String,
                val genre: String,
                val description: String,
                val userImpressions: List<UserImpression>,
                var pegiRating: Int = -1
     )
{
    public constructor(gameResponse: GameResponse) :
            this(gameResponse.id,gameResponse.title,gameResponse.platform,gameResponse.releaseDate,gameResponse.rating,gameResponse.coverImage!!,
                gameResponse.esrbRating,gameResponse.developer,
                gameResponse.publisher,gameResponse.genre,gameResponse.description,
                listOf(),gameResponse.pegiRating
            )
}
