package com.project.spire


import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
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
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before

import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class VerifyEmailActivityUITest {

    @get:Rule
    val rule = ActivityScenarioRule(VerifyEmailActivity::class.java)

    @Before
    fun setUp() {
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun test_emailInput_default() {
        onView(withId(R.id.email_edit_text))
            .check(matches(withHint("Sign up with Email")))
    }

    @Test
    fun test_emailInput_wrong_format() {
        onView(withId(R.id.email_edit_text))
            .perform(typeText("1234"), ViewActions.closeSoftKeyboard())

        onView(withId(R.id.send_mail_button))
            .perform(click())

        onView(withId(R.id.email_input))
            .check(matches(hasDescendant(withText("Invalid email format"))))
    }

    @Test
    fun test_emailInput_duplicated_email() {
        onView(withId(R.id.email_edit_text))
            .perform(typeText("uitest@google.com"), ViewActions.closeSoftKeyboard())

        onView(withId(R.id.send_mail_button))
            .perform(click())

        onView(withId(R.id.email_input))
            .check(matches(hasDescendant(withText("Email already exists"))))
    }

    @Test
    fun test_emailInput_correct() {
        onView(withId(R.id.email_edit_text))
            .perform(typeText("uitest2@google.com"), ViewActions.closeSoftKeyboard())

        onView(withId(R.id.send_mail_button))
            .perform(click())

        onView(withId(R.id.verification_code_input))
            .check(matches(isDisplayed()))
    }

    @Test
    fun test_codeInput_wrong() {
        onView(withId(R.id.email_edit_text))
            .perform(typeText("uitest2@google.com"), ViewActions.closeSoftKeyboard())

        onView(withId(R.id.send_mail_button))
            .perform(click())

        onView(withId(R.id.send_mail_button))
            .check(matches(not(isDisplayed())))

        onView(withId(R.id.verification_code_input))
            .check(matches(isDisplayed()))

        onView(withId(R.id.verification_code_1_edit_text))
            .perform(typeText("1"))

        onView(withId(R.id.verification_code_2_edit_text))
            .perform(typeText("2"))

        onView(withId(R.id.verification_code_3_edit_text))
            .perform(typeText("3"))

        onView(withId(R.id.verification_code_4_edit_text))
            .perform(typeText("4"))

        onView(withId(R.id.verification_code_5_edit_text))
            .perform(typeText("5"))

        onView(withId(R.id.verification_code_6_edit_text))
            .perform(typeText("6"))

        Thread.sleep(1000)

        onView(withId(R.id.verify_error_text))
            .check(matches(withText("Check your code again")))
    }

    @Test
    fun test_loginButton() {
        // Navigate to LoginActivity
        onView(withId(R.id.go_to_login_text_btn))
            .perform(click())

        intended(hasComponent(LoginActivity::class.java.name))
    }


}