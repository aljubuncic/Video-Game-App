package ba.etf.rma23.projekat.data.repositories.response

import com.google.gson.annotations.SerializedName

data class InvolvedCompany(@SerializedName("company") val company: Company,
                           @SerializedName("developer") val isDeveloper: Boolean,
                           @SerializedName("publisher") val isPublisher: Boolean)
