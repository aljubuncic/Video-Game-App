package ba.etf.rma23.projekat.data.repositories.response

import com.google.gson.annotations.SerializedName

data class Cover(@SerializedName("id") val id: Int,
                 @SerializedName("url") val url: String)
