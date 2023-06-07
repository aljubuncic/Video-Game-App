package ba.etf.rma23.projekat

import android.content.pm.ActivityInfo
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.PositionAssertions.*
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class OwnEspressoTestLandscape {
    @get:Rule
    var activityTestRule: ActivityTestRule<HomeActivity> = ActivityTestRule(HomeActivity::class.java)
    @Before
    fun switchOrientationToLandscape(){
        activityTestRule.activity.requestedOrientation= ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }
    /**
     * Test koji provjerava tačan raspored elemenata (home fragment i game details fragment)
     * u landscape orijentaciji za svaku igru koja se odabere iz recycler view-a
     */
    @Test
    fun landscapeTest() {
        onView(withId(R.id.search_query_edittext)).check(isCompletelyLeftOf(withId(R.id.search_button)))
        onView(withId(R.id.game_list)).check(isCompletelyBelow(withId(R.id.search_query_edittext)))
        onView(withId(R.id.game_list)).check(isCompletelyBelow(withId(R.id.search_button)))

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
            openAGame(it)
        }
    }

    private fun openAGame(game:Game){
        onView(withId(R.id.game_list)).perform(RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(allOf(
            hasDescendant(withText(game.title)),
            hasDescendant(withText(game.releaseDate)),
            hasDescendant(withText(game.rating.toString()))
        ),click()))

        onView(allOf(withId(R.id.item_title_textview), withText(game.title), isDescendantOfA(withId(R.id.nav_host_fragment))))
            .check(isCompletelyAbove(withId(R.id.cover_imageview)))
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

    }
}


