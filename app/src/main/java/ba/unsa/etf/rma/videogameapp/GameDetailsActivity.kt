package ba.unsa.etf.rma.videogameapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class GameDetailsActivity : AppCompatActivity(){
    private lateinit var homeButton: Button
    private lateinit var detailsButton: Button
    private lateinit var game: Game
    private lateinit var title: TextView
    private lateinit var coverImage: ImageView
    private lateinit var platform: TextView
    private lateinit var releaseDate: TextView
    private lateinit var esrbRating: TextView
    private lateinit var developer: TextView
    private lateinit var publisher: TextView
    private lateinit var genre: TextView
    private lateinit var description: TextView
    private lateinit var userImpressions: RecyclerView
    private lateinit var userImpressionListAdapter: UserImpressionListAdapter

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_details_activity)

        homeButton = findViewById(R.id.home_button)
        detailsButton = findViewById(R.id.details_button)
        detailsButton = findViewById(R.id.details_button)
        title = findViewById(R.id.game_title_textview)
        coverImage = findViewById(R.id.cover_imageview)
        platform = findViewById(R.id.platform_textview)
        releaseDate = findViewById(R.id.release_date_textview)
        esrbRating = findViewById(R.id.esrb_rating_textview)
        developer = findViewById(R.id.developer_textview)
        publisher = findViewById(R.id.publisher_textview)
        genre = findViewById(R.id.genre_textview)
        description = findViewById(R.id.description_textview)
        userImpressions = findViewById(R.id.impression_list)

        val extras = intent.extras
        if(extras!=null){
            game = GameData.getDetails(extras.getString("game_title",""))!!
            populateViewElements()
        }
        else
            finish()

        userImpressions.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        userImpressionListAdapter = UserImpressionListAdapter(game.userImpressions.sortedByDescending { userImpression -> userImpression.timestamp })
        userImpressions.adapter = userImpressionListAdapter

        detailsButton.isEnabled = false
        homeButton.setOnClickListener{
            openHomeWindow()
        }
    }
    private fun populateViewElements(){
        title.text = game.title

        val context = coverImage.context
        var id = context.resources.getIdentifier(game.coverImage,"drawable",context.packageName)
        if(id==0)
            id = context.resources.getIdentifier("joystick_logo","drawable",context.packageName)
        coverImage.setImageResource(id)

        platform.text = game.platform
        releaseDate.text = game.releaseDate
        esrbRating.text = game.esrbRating
        developer.text = game.developer
        publisher.text = game.publisher
        genre.text = game.genre
        description.text = game.description
    }

    override fun onBackPressed() {
        openHomeWindow()
    }

    private fun openHomeWindow() {
        val intent = Intent(this, HomeActivity::class.java).apply {
            putExtra("last_opened_game", game.title)
        }
        startActivity(intent)
    }
}