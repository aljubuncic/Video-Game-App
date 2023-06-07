package ba.etf.rma23.projekat.data.repositories

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AccountApiConfig {
    val retrofit : AccountApi = Retrofit.Builder()
        .baseUrl("https://rma23ws.onrender.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(AccountApi::class.java)
}