package ba.etf.rma23.projekat.data.repositories

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class GameReview (
    @PrimaryKey(autoGenerate = true)
    @SerializedName("rating") @ColumnInfo(name = "rating") var rating: Int?,
    @SerializedName("review") @ColumnInfo(name = "review") var review: String?,
    @ColumnInfo("igdb_id") var igdb_id: Int,
    @ColumnInfo(name = "online") var online: Boolean,
    @SerializedName("student") var username: String?,
    @SerializedName("timestamp") var timestamp: String?)
{
    constructor(rating: String, review: String, igdb_id: Int, online: Boolean) : this(null,null,igdb_id,online,null,null) {
        if(rating.isNotBlank() && rating.toInt()>=1 && rating.toInt()<=5)
            this.rating = rating.toInt()
        if(review.isNotBlank())
            this.review = review
    }
}