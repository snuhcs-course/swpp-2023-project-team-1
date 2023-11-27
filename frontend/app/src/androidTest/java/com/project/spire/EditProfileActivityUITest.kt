package com.project.spire


import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4

import com.example.spire.R
import com.project.spire.ui.MainActivity
import com.project.spire.ui.auth.LoginActivity
import com.project.spire.ui.profile.EditProfileActivity
import org.junit.After
import org.junit.Before

import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EditProfileUITest {

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

        onView(withId(R.id.tab_profile))
            .perform(click())

        onView(withId(R.id.profile_large_button))
            .perform(click())
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun test_checkEmail() {
        onView(withId(R.id.edit_profile_email_edit_text))
            .check(matches(withText("uitest@google.com")))
    }

//    // TODO
//    @Test
//    fun test_editProfileUsername_wrong_duplicated() {
//        onView(withId(R.id.edit_profile_username_edit_text))
//            .perform(clearText(), typeText("test0000"), ViewActions.closeSoftKeyboard())
//
//        onView(withId(R.id.edit_profile_save_btn))
//            .perform(click())
//    }
//
//    // TODO
//    @Test
//    fun test_editProfileBio() {
//        onView(withId(R.id.edit_profile_bio_edit_text))
//            .perform(clearText(), typeText("test user for UI testing"), ViewActions.closeSoftKeyboard())
//
//        onView(withId(R.id.edit_profile_save_btn))
//            .perform(click())
//
//        Thread.sleep(500)
//
//        onView(withId(R.id.tab_profile))
//            .perform(click())
//
//        onView(withId(R.id.profile_bio))
//            .check(matches(withText("test user for UI testing")))
//    }

    @Test
    fun test_backButton() {
        onView(withId(R.id.edit_profile_toolbar))
            .perform(click())

        onView(withId(R.id.back_button))
            .perform(click())

        intended(hasComponent(MainActivity::class.java.name))
    }

    @Test
    fun test_logout() {
        onView(withId(R.id.edit_profile_dropdown_menu))
            .perform(click())

        onView(withText("Log out")) // doesn't work with R.id.edit_profile_logout_btn
            .perform(click())

        onView(withText("LOG OUT"))
            .perform(click())

        intended(hasComponent(LoginActivity::class.java.name))
    }
}