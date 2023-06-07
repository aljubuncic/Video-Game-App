package ba.etf.rma23.projekat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class VideoGameListAdapter(
    private var games: List<Game>,
    private val onItemClicked: (game: Game) -> Unit
) : RecyclerView.Adapter<VideoGameListAdapter.VideoGameViewHolder>() {
    inner class VideoGameViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val gameTitle: TextView = itemView.findViewById(R.id.item_title_textview)
        val gameReleaseDate: TextView = itemView.findViewById(R.id.game_release_date_textview)
        val gameRating: TextView = itemView.findViewById(R.id.game_rating_textview)
        val gamePlatform: TextView = itemView.findViewById(R.id.game_platform_textview)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoGameViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_game,parent,false)
        return VideoGameViewHolder(view)
    }

    override fun getItemCount(): Int {
        return games.size
    }

    override fun onBindViewHolder(holder: VideoGameViewHolder, position: Int) {
        holder.gameTitle.text=games[position].title
        holder.gameReleaseDate.text=games[position].releaseDate
        holder.gameRating.text= games[position].rating.toString()
        holder.gamePlatform.text=games[position].platform
        holder.itemView.setOnClickListener{
            onItemClicked(games[position])
        }
    }
    fun updateGames(games: List<Game>){
        this.games=games
        notifyDataSetChanged()
    }
}