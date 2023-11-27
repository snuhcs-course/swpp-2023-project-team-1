package com.project.spire


import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.hasFocus
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.withClassName
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.project.spire.ui.auth.LoginActivity

import com.example.spire.R
import com.project.spire.ui.MainActivity
import com.project.spire.ui.auth.VerifyEmailActivity
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.endsWith
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
    fun test_input_default() {
        onView(withId(R.id.email_edit_text))
            .check(matches(ViewMatchers.withHint("Sign in with Email")))

        onView(withId(R.id.password_edit_text))
            .check(matches(ViewMatchers.withHint("Password")))
    }

    @Test
    fun test_emailInput_focus() {
        onView(withId(R.id.email_input))
            .perform(click())

        onView(withId(R.id.email_input))
            .check(matches(hasFocus()))

        onView(withId(R.id.email_input))
            .check(matches(hasDescendant(withText("Must be a valid email address"))))
    }

    @Test
    fun test_emailInput_empty() {
        onView(withId(R.id.login_btn))
            .perform(click())

        onView(withId(R.id.email_input))
            .check(matches(hasDescendant(withText("Email is required"))))
    }

    @Test
    fun test_emailInput_not_found() {
        onView(withId(R.id.email_edit_text))
            .perform(typeText("1234"), closeSoftKeyboard())

        onView(withId(R.id.password_edit_text))
            .perform(typeText("1234"), closeSoftKeyboard())

        onView(withId(R.id.login_btn))
            .perform(click())

        onView(withId(R.id.email_input))
            .check(matches(hasDescendant(withText("Email Not Found"))))
    }

    @Test
    fun test_passwordInput_focus() {
        // Type text and then press the button.
        onView(withId(R.id.password_input))
            .perform(click())

        // Check that the must be a valid email address text is displayed
        onView(withId(R.id.password_input))
            .check(matches(hasFocus()))

        onView(withId(R.id.password_input))
            .check(matches(hasDescendant(withText("Must be at least 8 characters"))))
    }

    @Test
    fun test_passwordInput_empty() {
        onView(withId(R.id.email_edit_text))
            .perform(typeText("1234"), closeSoftKeyboard())

        onView(withId(R.id.login_btn))
            .perform(click())

        onView(withId(R.id.password_input))
            .check(matches(hasDescendant(withText("Password is required"))))
    }

    @Test
    fun test_passwordInput_wrong_password() {
        onView(withId(R.id.email_edit_text))
            .perform(typeText("uitest@google.com"), closeSoftKeyboard())

        onView(withId(R.id.password_edit_text))
            .perform(typeText("1234"), closeSoftKeyboard())

        onView(withId(R.id.login_btn))
            .perform(click())

        Thread.sleep(1000)

        onView(withId(R.id.password_input))
            .check(matches(hasDescendant(withText("Wrong Password"))))
    }

    @Test
    fun test_login_success() {
        // uitest@google.com
        // ui_test_user
        // uitest1234!

        onView(withId(R.id.email_edit_text))
            .perform(typeText("uitest@google.com"), closeSoftKeyboard())

        onView(withId(R.id.password_edit_text))
            .perform(typeText("uitest1234!"), closeSoftKeyboard())

        onView(withId(R.id.login_btn))
            .perform(click())

        Thread.sleep(1000)

        intended(hasComponent(MainActivity::class.java.name))
    }

    @Test
    fun test_signUpButton() {
        // Navigate to VerifyEmailActivity
        onView(withId(R.id.go_to_sign_up_text_btn))
            .perform(click())

        intended(hasComponent(VerifyEmailActivity::class.java.name))
    }
}