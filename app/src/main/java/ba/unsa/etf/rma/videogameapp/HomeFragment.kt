package ba.unsa.etf.rma.videogameapp

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView

class HomeFragment: Fragment() {

    private var bottomNavigationView: BottomNavigationView? = null
    private var homeMenuItem: MenuItem? = null
    private var gameDetailsMenuItem: MenuItem? = null
    private lateinit var searchQuery: TextView
    private lateinit var searchButton: Button
    private lateinit var gameList: RecyclerView
    private lateinit var videoGameListAdapter: VideoGameListAdapter
    private var videoGameList = GameData.getAll()

    @SuppressLint("RestrictedApi", "SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.home_fragment, container, false)

        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            bottomNavigationView = requireActivity().findViewById(R.id.bottom_nav)
            homeMenuItem = bottomNavigationView?.menu?.get(0)
            gameDetailsMenuItem = bottomNavigationView?.menu?.get(1)
        }

        searchQuery = view.findViewById(R.id.search_query_edittext)
        searchButton = view.findViewById(R.id.search_button)
        gameList = view.findViewById(R.id.game_list)
        gameList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        videoGameListAdapter =
            VideoGameListAdapter(videoGameList, { game -> showGameDetails(game) })
        gameList.adapter = videoGameListAdapter
        if (homeMenuItem != null) {
            homeMenuItem!!.isChecked = true
            homeMenuItem!!.isEnabled = false
        }

        try {
            val extras = requireArguments()
            if(extras.getString("last_opened_game","").equals(" ")){
                if(homeMenuItem!=null)
                    homeMenuItem!!.isChecked = true
            }
            else {
                if(gameDetailsMenuItem!=null)
                    gameDetailsMenuItem!!.isEnabled = true
            }
            val game = GameData.getDetails(extras.getString("last_opened_game", ""))
            bottomNavigationView?.setOnItemSelectedListener {
                    when(it.itemId){
                        R.id.gameDetailsItem -> {
                            if (game != null) {
                                showGameDetails(game)
                                true
                            }
                            else
                                false
                        }
                        R.id.homeItem -> {
                            true
                        }
                        else -> false
                    }
                }
        } catch (_: IllegalStateException) {
            if (gameDetailsMenuItem != null) {
                gameDetailsMenuItem!!.isChecked = false
                gameDetailsMenuItem!!.isEnabled = false
            }
        }

        return view
    }

    private fun showGameDetails(game: Game) {
        val bundle = Bundle()
        bundle.putString("game_title", game.title)
        findNavController().navigate(R.id.GameDetailsFragment,bundle)
    }
}