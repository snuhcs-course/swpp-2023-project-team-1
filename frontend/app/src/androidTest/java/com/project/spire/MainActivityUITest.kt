package com.project.spire


import androidx.compose.ui.res.integerResource
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.hasFocus
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withClassName
import androidx.test.espresso.matcher.ViewMatchers.withHint
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4

import com.example.spire.R
import com.project.spire.ui.MainActivity
import com.project.spire.ui.auth.LoginActivity
import com.project.spire.ui.auth.VerifyEmailActivity
import com.project.spire.ui.create.CameraActivity
import com.project.spire.ui.create.ImageEditActivity
import com.project.spire.ui.create.WriteTextActivity
import com.project.spire.ui.profile.EditProfileActivity
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.endsWith
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before

import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityUITest {

    @get:Rule
    val rule = ActivityScenarioRule(LoginActivity::class.java)

    @Before
    fun setUp() {
        Intents.init()
        onView(withId(R.id.email_edit_text))
            .perform(typeText("uitest@google.com"), ViewActions.closeSoftKeyboard())

        onView(withId(R.id.password_edit_text))
            .perform(typeText("uitest1234!"), ViewActions.closeSoftKeyboard())

        onView(withId(R.id.login_btn))
            .perform(click())

        Thread.sleep(1000)
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun test_postLikeButton() {
        onView(withId(R.id.recycler_view_feed))
            .perform(RecyclerViewActions.actionOnItemAtPosition<>(hasDescendant(withText("whatever")), click()))
    }

    @Test
    fun test_editProfileButton() {
        onView(withId(R.id.tab_profile))
            .perform(click())

        onView(withId(R.id.profile_large_button))
            .perform(click())

        intended(hasComponent(EditProfileActivity::class.java.name))
    }

    @Test
    fun test_createButton() {
        onView(withId(R.id.fab))
            .perform(click())

        onView(withId(R.id.bottom_sheet_image_source_layout))
            .check(matches(isDisplayed()))
    }

    @Test
    fun test_createButton_picture() {
        onView(withId(R.id.fab))
            .perform(click())

        onView(withId(R.id.bottom_sheet_layout_1))
            .perform(click())

        intended(hasComponent(CameraActivity::class.java.name))
    }

//    // TODO
//    @Test
//    fun test_createButton_gallery() {
//        onView(withId(R.id.fab))
//            .perform(click())
//
//        onView(withId(R.id.bottom_sheet_layout_2))
//            .perform(click())
//
//    }

    @Test
    fun test_createButton_prompt() {
        onView(withId(R.id.fab))
            .perform(click())

        onView(withId(R.id.bottom_sheet_layout_3))
            .perform(click())

        onView(withId(R.id.prompt_input_layout))
            .check(matches(isDisplayed()))

        onView(withId(R.id.prompt_input))
            .perform(typeText("a cat sitting on a bench"), ViewActions.closeSoftKeyboard())

        onView(withId(R.id.generate_button))
            .perform(click())

        intended(hasComponent(WriteTextActivity::class.java.name))
    }
}