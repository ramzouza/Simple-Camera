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
import com.simplemobiletools.camera.R.id.settings_swipe
import kotlinx.android.synthetic.main.activity_main.view.*
import com.simplemobiletools.camera.extensions.config
import kotlinx.android.synthetic.main.activity_settings.*

import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SwipingTest {

    @Rule
    @JvmField
    val rule : ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    @Before
    fun setUp() {
        // enabling swipe mode: open settings, click on enable swipe button and than return to main view
        onView(withId(R.id.settings)).perform(click())
        onView(withId(R.id.settings_swipe)).perform(click())
        Espresso.pressBack()

    }

    @After
    fun tearDown() {
        // disabling swipe mode
        onView(withId(R.id.settings)).perform(click())
        onView(withId(R.id.settings_swipe)).perform(click())
        Espresso.pressBack()
    }

    @Test
    fun swipingTest(){
        onView(withId(R.id.swipe)).perform(swipeRight())

        // Once the button advance camera is click, the smart hub appear with 4 buttons displayed
        onView(withId(R.id.smart_hub_scroll)).check(matches(isDisplayed()))

        onView(withId(R.id.swipe)).perform(swipeRight())
        onView(withId(R.id.smart_hub_scroll)).check(matches(isDisplayed()))

        onView(withId(R.id.swipe)).perform(swipeUp())
        onView(withId(R.id.settings_swipe)).check(matches(isDisplayed()))
        Espresso.pressBack()
    }

    @Test
    fun swipe(){
        onView(withId(R.id.doubleTap)).perform(doubleClick())
    }




}
