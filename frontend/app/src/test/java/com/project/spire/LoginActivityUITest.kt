package com.project.spire


import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.project.spire.ui.auth.LoginActivity

import com.example.spire.R
import com.project.spire.ui.auth.VerifyEmailActivity
import org.junit.After
import org.junit.Before

import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginActivityUITest {

    @get:Rule
    val rule = ActivityScenarioRule(LoginActivity::class.java)

    @Before
    fun setUp() {
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun test_signUpButton() {
        // Navigate to VerifyEmailActivity
        onView(withId(R.id.go_to_sign_up_text_btn))
            .perform(click())

        intended(hasComponent(VerifyEmailActivity::class.java.name))
    }
}