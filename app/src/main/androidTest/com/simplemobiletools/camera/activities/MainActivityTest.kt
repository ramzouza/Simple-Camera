package com.simplemobiletools.camera.activities

import org.junit.After
import org.junit.Before
import android.support.*
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.simplemobiletools.camera.R
import kotlinx.android.synthetic.main.activity_main.view.*
import com.simplemobiletools.camera.extensions.config

import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

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


        // Once the button advance camera is click, the smart hub appear with 4 buttons displayed
        onView(withId(R.id.qr_code)).check(matches(isDisplayed()))
        onView(withId(R.id.math_hub)).check(matches(isDisplayed()))
        onView(withId(R.id.detect_object)).check(matches(isDisplayed()))
        onView(withId(R.id.image_filter)).check(matches(isDisplayed()))

    }

    @Test
    fun detectObjectTest (){
        onView(withId(R.id.advanced_camera)).check(matches(isDisplayed()))

        //click on the button
        onView(withId(R.id.advanced_camera)).perform(click())


        // Once the button advance camera is click, the smart hub appear with 4 buttons displayed
        onView(withId(R.id.detect_object)).check(matches(isDisplayed()))

        // Once button is clicked check thats it clicked idk
        onView(withId(R.id.detect_object)).perform(click())

    }

    @Test
    fun swipingTest(){
        onView(withId(R.id.settings)).perform(click())
        onView(withId(R.id.settings_swipe)).perform(click())
        Espresso.pressBack()

        onView(withId(R.id.swipe)).perform(swipeRight())

        // Once the button advance camera is click, the smart hub appear with 4 buttons displayed
        onView(withId(R.id.qr_code)).check(matches(isDisplayed()))
        onView(withId(R.id.math_hub)).check(matches(isDisplayed()))
        onView(withId(R.id.detect_object)).check(matches(isDisplayed()))
        onView(withId(R.id.image_filter)).check(matches(isDisplayed()))

                onView(withId(R.id.settings)).perform(click())
                onView(withId(R.id.settings_swipe)).perform(click())
                Espresso.pressBack()
    }

    @Test
    fun enableSwipingUpDownTest(){

      //      if (context.config.isSwipingEnabled){

     //   }
        // enabling swipe mode: open settings, click on enable swipe button and than return to main view
        onView(withId(R.id.settings)).perform(click())
        onView(withId(R.id.settings_swipe)).perform(click())
        Espresso.pressBack()

        // ensure that swiping is working
        onView(withId(R.id.swipe)).perform(swipeRight())

        // Once the button advance camera is click, the smart hub appear with 4 buttons displayed
        onView(withId(R.id.qr_code)).check(matches(isDisplayed()))
        onView(withId(R.id.math_hub)).check(matches(isDisplayed()))
        onView(withId(R.id.detect_object)).check(matches(isDisplayed()))
        onView(withId(R.id.image_filter)).check(matches(isDisplayed()))


        onView(withId(R.id.swipe)).perform(swipeUp())
        onView(withId(R.id.settings_swipe)).check(matches(isDisplayed()))
        onView(withId(R.id.settings_swipe)).perform(click())
        Espresso.pressBack()

                onView(withId(R.id.settings)).perform(click())
                onView(withId(R.id.settings_swipe)).perform(click())
                Espresso.pressBack()

    }





}
