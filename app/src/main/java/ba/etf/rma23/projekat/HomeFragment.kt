package ba.etf.rma23.projekat

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ba.etf.rma23.projekat.data.repositories.AccountApiConfig
import ba.etf.rma23.projekat.data.repositories.AccountGamesRepository
import ba.etf.rma23.projekat.data.repositories.GamesRepository
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.*

class HomeFragment: Fragment() {

    private var bottomNavigationView: BottomNavigationView? = null
    private var homeMenuItem: MenuItem? = null
    private var gameDetailsMenuItem: MenuItem? = null
    private lateinit var searchQuery: EditText
    private lateinit var searchButton: Button
    private lateinit var favoritesButton: Button
    private lateinit var sortButton: Button
    private lateinit var ageSpinner: Spinner
    private lateinit var gameList: RecyclerView
    private lateinit var videoGameListAdapter: VideoGameListAdapter
    private var videoGameList: List<Game>? = null
    private var lastOpenedGame: Game? = null
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback

    @SuppressLint("RestrictedApi", "SuspiciousIndentation", "MissingInflatedId")
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
        favoritesButton = view.findViewById(R.id.favorites_button)
        sortButton = view.findViewById(R.id.sort_button)
        ageSpinner = view.findViewById(R.id.age_spinner)
        gameList = view.findViewById(R.id.game_list)
        gameList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        videoGameListAdapter =
            VideoGameListAdapter(listOf(), { game -> showGameDetails(game) })
        gameList.adapter = videoGameListAdapter
        getFavoriteGames()

        val numberList = (0..100).toList()
        val spinnerAdapter=ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, numberList)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        ageSpinner.adapter = spinnerAdapter
        favoritesButton.setOnClickListener {
            getFavoriteGames()
        }
        sortButton.setOnClickListener {
            sortGames()
        }
        searchButton.setOnClickListener{
            ageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val selectedAge = ageSpinner.selectedItem as Int
                    if(selectedAge>18){
                        GamesRepository.setAge(selectedAge)
                        getGamesFromApi()
                    }
                    else {
                        GamesRepository.setAge(selectedAge)
                        getSafeGamesFromApi()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    GamesRepository.setAge(null)
                    getSafeGamesFromApi()
                }
            }
        }

        if (homeMenuItem != null) {
            homeMenuItem!!.isChecked = true
            homeMenuItem!!.isEnabled = false
        }

        try {
            val extras = requireArguments()
            if(isConnected()) {
                runBlocking {
                    lastOpenedGame = try {
                        GamesRepository.getGameById(extras.getInt("last_opened_game"))
                    } catch (e: java.lang.Exception) {
                        null
                    }
                }
            }
            if(lastOpenedGame == null){
                if(homeMenuItem!=null)
                    homeMenuItem!!.isChecked = true
            }
            else {
                if(gameDetailsMenuItem!=null)
                    gameDetailsMenuItem!!.isEnabled = true
            }

            bottomNavigationView?.setOnItemSelectedListener {
                    when(it.itemId){
                        R.id.gameDetailsItem -> {
                            if (lastOpenedGame != null) {
                                showGameDetails(lastOpenedGame!!)
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

        // Create the network request
        /*val networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()

        // Create the network callback
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                // Network is available
                //isConnected = true
            }

            override fun onLost(network: Network) {
                // Network is lost
                //isConnected = false
            }
        }

        // Register the network callback
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
*/

        return view
    }

    private fun showGameDetails(game: Game) {
        val bundle = Bundle()
        bundle.putInt("game_id", game.id)
        findNavController().navigate(R.id.GameDetailsFragment,bundle)
    }
    private fun getFavoriteGames(){
        if(!isConnected())
            return
        try {
            runBlocking {
                AccountGamesRepository.setHash("3b6569c0-c0b5-4426-a05a-e2b0813408ee")
                videoGameList = AccountGamesRepository.getSavedGames()
                videoGameListAdapter.updateGames(videoGameList!!)
            }
        }
        catch (_: Exception){

        }
    }
    private fun sortGames(){
        if(!isConnected())
            return
        runBlocking {
            try {
                videoGameListAdapter.updateGames(GamesRepository.sortGames())
            }
            catch (e:java.lang.Exception){
                val toast = Toast.makeText(context, e.message, Toast.LENGTH_SHORT)
                toast.show()
            }
        }
    }
    fun getGamesFromApi(){
        if(!isConnected())
            return
        val scope = CoroutineScope(Job() + Dispatchers.Main)
        // Create a new coroutine on the UI thread
        try {
            scope.launch {
                // Opcija 1
                val result = GamesRepository.getGamesByName(searchQuery.text.toString())
                // Display result of the network request to the user
                when (result) {
                    is List<Game>? -> onSuccess(result)
                    else -> onError()
                }
            }
        }
        catch (_:Exception){

        }
    }
    fun getSafeGamesFromApi(){
        if(!isConnected())
            return
        val scope = CoroutineScope(Job() + Dispatchers.Main)
        // Create a new coroutine on the UI thread
        try {
            scope.launch {
                // Opcija 1
                val result = GamesRepository.getGamesSafe(searchQuery.text.toString())
                // Display result of the network request to the user
                when (result) {
                    is List<Game>? -> onSuccess(result)
                    else -> onError()
                }
            }
        }
        catch (_:Exception){

        }
    }
    fun onSuccess(games: List<Game>){
        val toast = Toast.makeText(context, "Games found", Toast.LENGTH_SHORT)
        toast.show()
        videoGameList = games;
        videoGameListAdapter.updateGames(games)
    }
    fun onError() {
        val toast = Toast.makeText(context, "Search error", Toast.LENGTH_SHORT)
    toast.show()
}
    private fun isConnected():Boolean{
        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo?.isConnected == true
    }
}