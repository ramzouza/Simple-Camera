package com.simplemobiletools.camera.activities
import com.simplemobiletools.camera.Adapter.PostsAdapter

import android.support.*
import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.simplemobiletools.camera.R
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.IsInstanceOf
import com.simplemobiletools.camera.R.id.settings_swipe
import kotlinx.android.synthetic.main.activity_main.view.*
import com.simplemobiletools.camera.extensions.config
import kotlinx.android.synthetic.main.activity_settings.*


import org.junit.Assert.*
import org.junit.Assert.assertEquals
import org.junit.runner.RunWith
import com.simplemobiletools.camera.extensions.config
import org.hamcrest.Matchers
import org.junit.*
import kotlin.concurrent.thread

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @Rule
    @JvmField
    val rule : ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    @Before
    fun setUp() {
    }

    @After
    fun tearDown() {
    }

    @Test
    fun advanceHubTest(){
        /*

        This Test make sure that the smartHub menu appear once the button Advanced Camera is pressed

        */

        //assert that the button is display
        onView(withId(R.id.advanced_camera)).check(matches(isDisplayed()))

        //click on the button
        onView(withId(R.id.advanced_camera)).perform(click())


        // Once in the smart hub mode, the container that contains all the options appears
        onView(withId(R.id.smart_hub_scroll)).check(matches(isDisplayed()))


    }

    @Test
    fun qrCodeTest(){

        val appCompatImageView = onView(
                allOf(withId(R.id.advanced_camera),
                        childAtPosition(
                                allOf(withId(R.id.TopMenu),
                                        childAtPosition(
                                                withId(R.id.view_holder),
                                                6)),
                                3),
                        isDisplayed()))

        appCompatImageView.perform(click())

        onView(withId(R.id.smart_hub_scroll)).perform(click())
}

    @Test
    fun imageFiltersTest() {

        val appCompatImageView = onView(
                allOf(withId(R.id.advanced_camera),
                        childAtPosition(
                                allOf(withId(R.id.TopMenu),
                                        childAtPosition(
                                                withId(R.id.view_holder),
                                                6)),
                                3),
                        isDisplayed()))
        appCompatImageView.perform(click())

        onView(withId(R.id.smart_hub_scroll)).perform(swipeLeft())


        Thread.sleep(1500)

        onView(withId(R.id.smart_hub_scroll)).perform(click())


        val cardView = onView(
                allOf(withId(R.id.btn_filters),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(`is`("android.widget.HorizontalScrollView")),
                                        0),
                                0)))
        cardView.perform(scrollTo(), click())

        val appCompatImageView2 = onView(
                allOf(withId(R.id.thumbnail),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.recycler_view),
                                        2),
                                1),
                        isDisplayed()))
        appCompatImageView2.perform(click())

        val view = onView(
                allOf(withId(R.id.touch_outside),
                        childAtPosition(
                                allOf(withId(R.id.coordinator),
                                        childAtPosition(
                                                withId(R.id.container),
                                                0)),
                                0),
                        isDisplayed()))
        view.perform(click())

        val cardView2 = onView(
                allOf(withId(R.id.tabs),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.coordinator),
                                        0),
                                3),
                        isDisplayed()))
        cardView2.perform(click())




        onView(withText("Save")).perform(click())

        //Image saved Succesfully in the gallery with the Text
        onView(withText("Image saved to gallery")).check(matches(isDisplayed()))



    }


    @Test
    fun detectObjectTest(){
        onView(withId(R.id.advanced_camera)).check(matches(isDisplayed()))

        //click on the button
        onView(withId(R.id.advanced_camera)).perform(click())
        onView(withId(R.id.smart_hub_scroll)).perform(swipeLeft())
        onView(withId(R.id.smart_hub_scroll)).perform(swipeLeft())

        //click on the image filter button
        onView(withId(R.id.smart_hub_scroll)).perform(click())


    }

    @Test
    fun hyperlinkTest(){

        onView(withId(R.id.advanced_camera)).check(matches(isDisplayed()))

        //click on the button
        onView(withId(R.id.advanced_camera)).perform(click())
        onView(withId(R.id.smart_hub_scroll)).perform(swipeLeft())
        onView(withId(R.id.smart_hub_scroll)).perform(swipeLeft())
        onView(withId(R.id.smart_hub_scroll)).perform(swipeLeft())
        onView(withId(R.id.smart_hub_scroll)).perform(swipeLeft())

        Thread.sleep(1500)
        //click on the image filter button
        onView(withId(R.id.smart_hub_scroll)).perform(click())

    }




    @Test
    fun MemeTextTest(){

        val appCompatImageView = onView(
                allOf(withId(R.id.advanced_camera),
                        childAtPosition(
                                allOf(withId(R.id.TopMenu),
                                        childAtPosition(
                                                withId(R.id.view_holder),
                                                6)),
                                3),
                        isDisplayed()))
        appCompatImageView.perform(click())

        onView(withId(R.id.smart_hub_scroll)).perform(swipeLeft())
        onView(withId(R.id.smart_hub_scroll)).perform(swipeLeft())
        onView(withId(R.id.smart_hub_scroll)).perform(swipeLeft())
        Thread.sleep(1500)

        onView(withId(R.id.smart_hub_scroll)).perform(click())

        val cardView = onView(
                allOf(withId(R.id.btn_text),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(`is`("android.widget.HorizontalScrollView")),
                                        0),
                                1)))
        cardView.perform(scrollTo(), click())

        val appCompatEditText = onView(
                allOf(withId(R.id.edt_add_text),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.design_bottom_sheet),
                                        0),
                                0),
                        isDisplayed()))
        appCompatEditText.perform(click())

        val appCompatEditText2 = onView(
                allOf(withId(R.id.edt_add_text),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.design_bottom_sheet),
                                        0),
                                0),
                        isDisplayed()))
        appCompatEditText2.perform(replaceText("Test display"), closeSoftKeyboard())

        val view = onView(
                allOf(withId(R.id.touch_outside),
                        childAtPosition(
                                allOf(withId(R.id.coordinator),
                                        childAtPosition(
                                                withId(R.id.container),
                                                0)),
                                0),
                        isDisplayed()))
        view.perform(click())

        val cardView2 = onView(
                allOf(withId(R.id.btn_text),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(`is`("android.widget.HorizontalScrollView")),
                                        0),
                                1)))
        cardView2.perform(scrollTo(), click())

        val cardView3 = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.recycler_font),
                                childAtPosition(
                                        withClassName(`is`("android.widget.LinearLayout")),
                                        1)),
                        0),
                        isDisplayed()))
        cardView3.perform(click())

        val appCompatButton = onView(
                allOf(withId(R.id.btn_done), withText("Done"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.design_bottom_sheet),
                                        0),
                                2),
                        isDisplayed()))
        appCompatButton.perform(click())

        val view2 = onView(
                allOf(withId(R.id.touch_outside),
                        childAtPosition(
                                allOf(withId(R.id.coordinator),
                                        childAtPosition(
                                                withId(R.id.container),
                                                0)),
                                0),
                        isDisplayed()))
        view2.perform(click())


        //At this point , if this Assert return True it means that the text is added successfully on the image
        onView(withText("Test display")).check(matches(isDisplayed()))

        onView(withText("Save")).perform(click())

        //Image saved Succesfully in the gallery with the Text
        onView(withText("Image saved to gallery")).check(matches(isDisplayed()))





    }

    private fun childAtPosition(
            parentMatcher: Matcher<View>, position: Int): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }


}
