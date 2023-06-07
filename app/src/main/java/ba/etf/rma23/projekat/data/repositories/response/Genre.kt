package ba.etf.rma23.projekat.data.repositories.response

import com.google.gson.annotations.SerializedName

data class Genre(@SerializedName("id") val id: Int,
                 @SerializedName("name") val name:String)
