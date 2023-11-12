package com.project.spire.ui

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.Toolbar
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.spire.R
import com.example.spire.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.project.spire.ui.create.CameraActivity
import com.project.spire.ui.create.ImageEditActivity
import com.project.spire.ui.create.PromptDialogFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val metrics = resources.displayMetrics
        Log.d("Metrics", "Width: ${metrics.widthPixels} Height: ${metrics.heightPixels}")

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hide action bar
        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }

        // Navigation
        val navView: BottomNavigationView = binding.bottomNavigationView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_feed,
                R.id.navigation_search,
                R.id.navigation_notification,
                R.id.navigation_profile
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // New Post Button
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_image_source, null)
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.hide()
        val bottomSheetCamera = bottomSheetView.findViewById<LinearLayout>(R.id.bottom_sheet_layout_1)
        val bottomSheetGallery = bottomSheetView.findViewById<LinearLayout>(R.id.bottom_sheet_layout_2)
        val bottomSheetNew = bottomSheetView.findViewById<LinearLayout>(R.id.bottom_sheet_layout_3)
        val createPostBtn: FloatingActionButton = binding.fab

        createPostBtn.setOnClickListener {
            bottomSheetDialog.show()
        }

        bottomSheetCamera.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
            bottomSheetDialog.hide()
        }

        bottomSheetGallery.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        bottomSheetNew.setOnClickListener {
            PromptDialogFragment().show(supportFragmentManager, "PromptDialogFragment")
            bottomSheetDialog.hide()
        }
    }

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")
                val intent = Intent(this, ImageEditActivity::class.java)
                intent.putExtra("imageUri", uri.toString())
                startActivity(intent)
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

    fun Activity.setStatusBarTransparent() {
        window.apply {
            setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }
        if(Build.VERSION.SDK_INT >= 30) {	// API 30 에 적용
            WindowCompat.setDecorFitsSystemWindows(window, false)
        }
    }
    fun replaceFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_fragment_activity_main, fragment)
        if (addToBackStack)  {
            transaction.addToBackStack(null)
        }
        transaction.commit()
    }
}