package ba.etf.rma23.projekat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ba.etf.rma23.projekat.data.repositories.GameReview

class UserImpressionListAdapter () :RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    private var userImpressions: MutableList<UserImpression> = mutableListOf()
    constructor(gameReviews: List<GameReview>) : this() {
        mapGameReviewsInUserImpressions(gameReviews)
    }

    private fun mapGameReviewsInUserImpressions(gameReviews: List<GameReview>){
        gameReviews.forEach loop@{
            if(it.review ==null && it.rating== null)
                return@loop
            if(it.review !=null && it.rating!= null) {
                userImpressions.add(UserRating(it.username!!, it.timestamp!!.toLong(), it.rating!!.toDouble()))
                userImpressions.add(UserReview(it.username!!,it.timestamp!!.toLong(),it.review!!))
            }
            else if(it.review!=null)
                userImpressions.add(UserReview(it.username!!,it.timestamp!!.toLong(),it.review!!))
            else if(it.rating!=null)
                userImpressions.add(UserRating(it.username!!, it.timestamp!!.toLong(), it.rating!!.toDouble()))
        }
    }
    inner class UserRatingViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val username: TextView = itemView.findViewById(R.id.username_textview)
        val rating: RatingBar = itemView.findViewById(R.id.rating_bar)
    }
    inner class UserReviewViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val username: TextView = itemView.findViewById(R.id.username_textview)
        val review: TextView = itemView.findViewById(R.id.review_textview)
    }

    override fun getItemViewType(position: Int): Int {
        when(userImpressions[position]){
            is UserRating -> return 1
            is UserReview -> return 2
            else -> return 3
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when(viewType){
            1 -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_rating,parent,false)
                return UserRatingViewHolder(view)
            }
            2 -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_review,parent,false)
                return UserReviewViewHolder(view)
            }
            else ->{
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_review,parent,false)
                return UserReviewViewHolder(view)
            }
        }
    }

    override fun getItemCount(): Int {
        return userImpressions.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(userImpressions[position]){
            is UserRating -> {
                (holder as UserRatingViewHolder).username.text = (userImpressions[position] as UserRating).username
                (holder as UserRatingViewHolder).rating.rating = (userImpressions[position] as UserRating).rating.toFloat()
            }
            is UserReview -> {
                (holder as UserReviewViewHolder).username.text = (userImpressions[position] as UserReview).username
                (holder as UserReviewViewHolder).review.text = (userImpressions[position] as UserReview).review
            }
        }
    }

    private fun updateImpressions(gameReviews: List<GameReview>){
        userImpressions = mutableListOf()
        mapGameReviewsInUserImpressions(gameReviews)
        notifyDataSetChanged()
    }
}