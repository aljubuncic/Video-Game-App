package ba.etf.rma23.projekat.data.repositories.response

import com.google.gson.annotations.SerializedName

data class GameResponse(@SerializedName("id") val id: Int,
                        @SerializedName("name") var title: String,
                        @SerializedName("platforms") var platforms: List<Platform>,
                        var platform: String,
                        @SerializedName("first_release_date") var releaseDate: String,
                        @SerializedName("rating") var rating: Double,
                        @SerializedName("cover") var cover: Cover,
                        var coverImage: String,
                        @SerializedName("age_ratings") var ageRatings: List<AgeRating>,
                        var esrbRating: String,
                        @SerializedName("involved_companies") var involvedCompanies : List<InvolvedCompany>,
                        var developer: String,
                        var publisher: String,
                        @SerializedName("genres") var genres: List<Genre>,
                        var genre : String,
                        @SerializedName("summary") var description: String,
                        var pegiRating: Int = -1)
