package ba.unsa.etf.rma.videogameapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomeActivity : AppCompatActivity() {
    private lateinit var logoImage: ImageView
    private lateinit var homeButton: Button
    private lateinit var detailsButton: Button
    private lateinit var searchQuery: TextView
    private lateinit var searchButton: Button
    private lateinit var gameList: RecyclerView
    private lateinit var videoGameListAdapter: VideoGameListAdapter
    private var videoGameList = GameData.getAll()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        logoImage = findViewById(R.id.logo_image)
        homeButton = findViewById(R.id.home_button)
        detailsButton = findViewById(R.id.details_button)
        searchQuery = findViewById(R.id.search_query_edittext)
        searchButton = findViewById(R.id.search_button)
        gameList = findViewById(R.id.game_list)
        gameList.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        videoGameListAdapter = VideoGameListAdapter(videoGameList,{game -> showGameDetails(game)})
        gameList.adapter = videoGameListAdapter

        homeButton.isEnabled = false
        logoImage.setImageResource(R.drawable.joystick_logo)

        val extras = intent.extras
        if(extras == null)
            detailsButton.isEnabled = false
        else {
            detailsButton.setOnClickListener{
                val game = GameData.getDetails(extras.getString("last_opened_game",""))
                if (game != null) {
                    showGameDetails(game)
                }
            }
        }
    }
    private fun showGameDetails(game: Game){
        val intent = Intent(this,GameDetailsActivity::class.java).apply{
            putExtra("game_title",game.title)
        }
        startActivity(intent)
    }
}