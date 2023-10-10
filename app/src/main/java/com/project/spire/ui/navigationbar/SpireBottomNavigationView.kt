package com.project.spire.ui.navigationbar

import android.content.Context
import android.util.AttributeSet
import com.example.spire.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class SpireBottomNavigationView : BottomNavigationView {

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context) : super(context) {
        init()
    }

    private fun init() {
            inflateMenu(R.menu.bottom_nav_menu)
    }

}