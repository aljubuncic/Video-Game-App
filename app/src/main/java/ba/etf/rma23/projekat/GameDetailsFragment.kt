package ba.etf.rma23.projekat

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ba.etf.rma23.projekat.data.repositories.AccountGamesRepository
import ba.etf.rma23.projekat.data.repositories.GameReview
import ba.etf.rma23.projekat.data.repositories.GameReviewsRepository
import ba.etf.rma23.projekat.data.repositories.GamesRepository
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.*

class GameDetailsFragment : Fragment(){

    private var bottomNavigationView: BottomNavigationView? = null
    private var homeMenuItem: MenuItem? = null
    private var gameDetailsMenuItem: MenuItem? = null
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
    private lateinit var favoriteButton : ImageButton
    private lateinit var removeButton : ImageButton
    private lateinit var reviewInput : EditText
    private lateinit var ratingInput: EditText
    private lateinit var addReviewButton: ImageButton
    private lateinit var userImpressions: RecyclerView
    private lateinit var gameReviews: List<GameReview>
    private lateinit var userImpressionListAdapter: UserImpressionListAdapter

    @SuppressLint("RestrictedApi", "MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.game_details_fragment,container,false)

        if(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            bottomNavigationView = requireActivity().findViewById(R.id.bottom_nav)
            homeMenuItem = bottomNavigationView?.menu?.get(0)
            gameDetailsMenuItem = bottomNavigationView?.menu?.get(1)
        }

        title = view.findViewById(R.id.item_title_textview)
        coverImage = view.findViewById(R.id.cover_imageview)
        platform = view.findViewById(R.id.platform_textview)
        releaseDate = view.findViewById(R.id.release_date_textview)
        esrbRating = view.findViewById(R.id.esrb_rating_textview)
        developer = view.findViewById(R.id.developer_textview)
        publisher = view.findViewById(R.id.publisher_textview)
        genre = view.findViewById(R.id.genre_textview)
        description = view.findViewById(R.id.description_textview)
        userImpressions = view.findViewById(R.id.impression_list)
        favoriteButton = view.findViewById(R.id.favorite_button)
        removeButton = view.findViewById(R.id.remove_button)
        reviewInput = view.findViewById(R.id.review_input)
        ratingInput = view.findViewById(R.id.rating_input)
        addReviewButton = view.findViewById(R.id.add_review_button)


        try {
            val extras = requireArguments()
            getGameById(extras.getInt("game_id"))
            populateViewElements()
        }
        catch (e: IllegalStateException) {
            requireActivity().finish()
        }

        favoriteButton.setOnClickListener{
            addToFavorites()
        }
        removeButton.setOnClickListener{
            removeFromFavorites()
        }

        addReviewButton.setOnClickListener {
            addReview()
        }

        getGameReviews()

        userImpressions.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
        userImpressionListAdapter = UserImpressionListAdapter(gameReviews)
        userImpressions.adapter = userImpressionListAdapter

        if(homeMenuItem!=null)
            homeMenuItem!!.isEnabled=true
        if (gameDetailsMenuItem != null) {
            gameDetailsMenuItem!!.isChecked = true
            gameDetailsMenuItem!!.isEnabled=false
        }

        bottomNavigationView?.setOnItemSelectedListener {
            when(it.itemId){
                R.id.homeItem -> {
                        openHomeWindow()
                    true
                }
                R.id.gameDetailsItem -> {
                    true
                }
                else -> false
            }
        }
        return view
    }

    private fun populateViewElements(){
        title.text = game.title

        //val context = coverImage.context
        /*var id = context.resources.getIdentifier(game.coverImage,"drawable",context.packageName)
        if(id==0)
            id = context.resources.getIdentifier("joystick_logo","drawable",context.packageName)
        */
        //coverImage.setImageResource(id)
        val context = coverImage.context
        Glide.with(context)
            .load("https:"+game.coverImage)
            .placeholder(R.drawable.joystick_logo)
            .into(coverImage)

        platform.text = game.platform
        releaseDate.text = game.releaseDate
        esrbRating.text = game.esrbRating
        developer.text = game.developer
        publisher.text = game.publisher
        genre.text = game.genre
        description.text = game.description
    }

    private fun openHomeWindow() {
        val bundle = Bundle()
        bundle.putInt("last_opened_game", game.id)
        findNavController().navigate(R.id.HomeFragment,bundle)
    }

    private fun addToFavorites(){
        if(!isConnected())
            return
        runBlocking {
            AccountGamesRepository.setHash("3b6569c0-c0b5-4426-a05a-e2b0813408ee")
            AccountGamesRepository.saveGame(game)
        }
    }

    private fun getGameReviews(){
        if(!isConnected())
            return
        runBlocking {
            AccountGamesRepository.setHash("3b6569c0-c0b5-4426-a05a-e2b0813408ee")
            gameReviews = GameReviewsRepository.getReviewsForGame(game.id)
        }
    }

    private fun addReview(){
        if(!isConnected())
            return
        runBlocking {
            val gameReview = GameReview(ratingInput.text.toString(),reviewInput.text.toString(),game.id,false)
            AccountGamesRepository.setHash("3b6569c0-c0b5-4426-a05a-e2b0813408ee")
            context?.let { GameReviewsRepository.sendReview(it,gameReview) }
        }
    }

    private fun removeFromFavorites(){
        if(!isConnected())
            return
        runBlocking {
            AccountGamesRepository.setHash("3b6569c0-c0b5-4426-a05a-e2b0813408ee")
            if(!AccountGamesRepository.removeGame(game.id))
                Toast.makeText(context, "Game is not in the favorites list", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getGameById(id: Int){
        if(!isConnected())
            return
        runBlocking {
            game = GamesRepository.getGameById(id)
            if (game == null)
                onError()
        }
    }

    fun onError() {
        val toast = Toast.makeText(context, "Search error", Toast.LENGTH_SHORT)
        toast.show()
    }

    private fun isConnected():Boolean{
        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo?.isConnected == true && networkInfo.isAvailable
    }
}