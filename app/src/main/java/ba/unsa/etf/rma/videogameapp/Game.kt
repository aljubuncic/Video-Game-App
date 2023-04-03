package ba.unsa.etf.rma.videogameapp

data class Game(val title: String,
                val platform: String,
                val releaseDate: String,
                val rating: Double,
                val coverImage: String,
                val esrbRating: String,
                val developer: String,
                val publisher: String,
                val genre: String,
                val description: String,
                val userImpressions: List<UserImpression>)
