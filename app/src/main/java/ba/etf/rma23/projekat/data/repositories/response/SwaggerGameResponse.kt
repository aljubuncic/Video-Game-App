package ba.etf.rma23.projekat.data.repositories.response

import com.google.gson.annotations.SerializedName

data class SwaggerGameResponse(@SerializedName("igdb_id")val igdb_id: Int,
                @SerializedName("name")val name: String)