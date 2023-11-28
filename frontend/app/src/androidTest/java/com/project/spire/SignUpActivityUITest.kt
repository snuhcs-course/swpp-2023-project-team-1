package com.project.spire


import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
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
import com.project.spire.ui.auth.SignUpActivity
import com.project.spire.ui.auth.VerifyEmailActivity
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.endsWith
import org.junit.After
import org.junit.Before

import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SignUpActivityUITest {

    @get:Rule
    val rule = ActivityScenarioRule(SignUpActivity::class.java)

    @Before
    fun setUp() {
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun test_register_wrong_duplicated_username() {
        onView(withId(R.id.email_edit_text))
            .perform(replaceText("uitest2@google.com")) // can't really use the email

        onView(withId(R.id.password_edit_text))
            .perform(typeText("uitest1234!"), ViewActions.closeSoftKeyboard())

        onView(withId(R.id.username_edit_text))
            .perform(typeText("ui_test_user"), ViewActions.closeSoftKeyboard())

        onView(withId(R.id.sign_up_btn))
            .perform(click())

        Thread.sleep(1000)
    }

    @Test
    fun test_register_correct() {
        onView(withId(R.id.email_edit_text))
            .perform(replaceText("uitest2@google.com")) // can't really use the email

        onView(withId(R.id.password_edit_text))
            .perform(typeText("uitest1234!"), ViewActions.closeSoftKeyboard())

        onView(withId(R.id.username_edit_text))
            .perform(typeText("ui_test_user_2"), ViewActions.closeSoftKeyboard())

        onView(withId(R.id.sign_up_btn))
            .perform(click())

        Thread.sleep(1000)

        intended(hasComponent(MainActivity::class.java.name))
    }
}