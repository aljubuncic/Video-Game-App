package ba.unsa.etf.rma.videogameapp

import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.PositionAssertions.*
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class OwnEspressoTestPortrait {
    @get:Rule
    var activityScenarioRule: ActivityScenarioRule<HomeActivity> = ActivityScenarioRule(HomeActivity::class.java)
    @get:Rule
    var activityTestRule: ActivityTestRule<HomeActivity> = ActivityTestRule(HomeActivity::class.java)

    /**
     * Test koji provjerava tačan raspored elemenata u portret orijentaciji, kada se tek uđe
     * u aplikaciju (u home fragmnetu), te kada se odabere neka igra (u ovom slučaju se radi o konkretno specificiranoj igri - FIFA 23)
     * u recylcer view-u (u game details fragmentu)
     */
    @Test
    fun portraitTest() {
        onView(withId(R.id.search_query_edittext)).check(isCompletelyLeftOf(withId(R.id.search_button)))
        onView(withId(R.id.game_list)).check(isCompletelyBelow(withId(R.id.search_query_edittext)))
        onView(withId(R.id.game_list)).check(isCompletelyBelow(withId(R.id.search_button)))
        onView(withId(R.id.bottom_nav)).check(isCompletelyBelow(withId(R.id.game_list)))
        onView(withId(R.id.gameDetailsItem)).check(isCompletelyRightOf(withId(R.id.homeItem)))

        val games = GameData.getAll()
        games.forEach {
            onView(withId(R.id.game_list)).perform(
                RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(
                    CoreMatchers.allOf(
                        hasDescendant(withText(it.title)),
                        hasDescendant(withText(it.releaseDate)),
                        hasDescendant(withText(it.rating.toString())),
                        hasDescendant(withText(it.platform))
                    )
                )
            )
        }

        val fifa23=GameData.getAll()[1]
        onView(withId(R.id.game_list)).perform(RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(allOf(
            hasDescendant(withText(fifa23.title)),
            hasDescendant(withText(fifa23.releaseDate)),
            hasDescendant(withText(fifa23.rating.toString()))
        ),click()))

        onView(allOf(withId(R.id.item_title_textview), withText("FIFA 23")
        )).check(isCompletelyAbove(withId(R.id.description_textview)))
        onView(withId(R.id.cover_imageview)).check(isCompletelyBelow(withId(R.id.description_textview)))
        onView(withId(R.id.details_linear)).check(isCompletelyBelow(withId(R.id.cover_imageview)))
        onView(withId(R.id.details_linear)).check(isCompletelyAbove(withId(R.id.impression_list)))
        onView(withId(R.id.details_linear)).check(isCompletelyAbove(withId(R.id.impression_list)))

        onView(withId(R.id.platform_textview)).check(isCompletelyLeftOf(withId(R.id.developer_textview)))
        onView(withId(R.id.platform_textview)).check(isCompletelyLeftOf(withId(R.id.publisher_textview)))
        onView(withId(R.id.platform_textview)).check(isCompletelyLeftOf(withId(R.id.release_date_textview)))

        onView(withId(R.id.esrb_rating_textview)).check(isCompletelyLeftOf(withId(R.id.developer_textview)))
        onView(withId(R.id.esrb_rating_textview)).check(isCompletelyLeftOf(withId(R.id.publisher_textview)))
        onView(withId(R.id.esrb_rating_textview)).check(isCompletelyLeftOf(withId(R.id.release_date_textview)))

        onView(withId(R.id.genre_textview)).check(isCompletelyLeftOf(withId(R.id.developer_textview)))
        onView(withId(R.id.genre_textview)).check(isCompletelyLeftOf(withId(R.id.publisher_textview)))
        onView(withId(R.id.genre_textview)).check(isCompletelyLeftOf(withId(R.id.release_date_textview)))

        onView(withId(R.id.bottom_nav)).check(isCompletelyBelow(withId(R.id.impression_list)))

    }

    /**
     *  Testira da li se bottomNavigationView ponaša kako treba u različitim situacijama:
     *  kada se pokuša kliknuti na opciju u meniju koja je već saglasna sa trenutnim fragmentom,
     *  klik na details opciju u meniju a nijedna igra nije prethodno otvorena,
     *  na game details fragmentu kada se odebere home opcija u meniju za povratak na home fragment,
     *  ponovni prelazak na game details fragment (a postoji igra koja je prethodno otvorena)
     */
    @Test
    fun bottomNavigationViewTest(){
        val homeFragmentName=HomeFragment::class.simpleName
        val gameDetailsFragmentName=GameDetailsFragment::class.simpleName
        val navController= activityTestRule.activity.findNavController(R.id.nav_host_fragment)

        //home fragment
        checkMenuItem(navController,R.id.homeItem,homeFragmentName!!)
        checkMenuItem(navController,R.id.gameDetailsItem, homeFragmentName)

        //prelazak u game details fragment
        val game = GameData.getAll()[0]
        onView(withId(R.id.game_list)).perform(RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(allOf(
            hasDescendant(withText(game.title)),
        ),click()))
        assert(navController.currentDestination!!.label==gameDetailsFragmentName)
        checkMenuItem(navController,R.id.gameDetailsItem,gameDetailsFragmentName!!)

        //prelazak u home fragment
        checkMenuItem(navController,R.id.homeItem,homeFragmentName)

        //prelazak u game details fragemnt i provjera naslova igre koja je posljednji put otvorena
        checkMenuItem(navController,R.id.gameDetailsItem,gameDetailsFragmentName!!)
        onView(allOf(withId(R.id.item_title_textview), withText(game.title)))
    }
    private fun checkMenuItem(navController: NavController,menuItem: Int,fragmentName:String){
        onView(withId(menuItem)).perform(click())
        assert(navController.currentDestination!!.label==fragmentName)
    }
}


