package ba.unsa.etf.rma.videogameapp

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView

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
    private lateinit var userImpressions: RecyclerView
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

        try {
            val extras = requireArguments()
            game = GameData.getDetails(extras.getString("game_title",""))!!
            populateViewElements()
        }
        catch (e: IllegalStateException) {
            requireActivity().finish()
        }

        userImpressions.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
        userImpressionListAdapter = UserImpressionListAdapter(game.userImpressions.sortedByDescending { userImpression -> userImpression.timestamp })
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

    private fun openHomeWindow() {
        val bundle = Bundle()
        bundle.putString("last_opened_game", game.title)
        findNavController().navigate(R.id.HomeFragment,bundle)
    }
}