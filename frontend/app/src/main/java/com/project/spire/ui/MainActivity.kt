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
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.spire.R
import com.example.spire.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.project.spire.ui.create.CameraActivity
import com.project.spire.ui.create.ImageEditActivity
import com.project.spire.ui.create.PromptDialogFragment
import com.project.spire.ui.feed.FeedFragment
import com.project.spire.ui.notifications.NotificationsFragment
import com.project.spire.ui.profile.ProfileFragment
import com.project.spire.ui.search.SearchFragment
import com.project.spire.utils.InferenceUtils
import java.lang.NullPointerException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var toolbar: Toolbar
    private var currentTab: String? = null

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

        if (intent.getStringExtra("fragment").equals("profile")) {
            // refresh profile on update
            Log.i("MainActivity", "${navController.currentDestination}, ${navController.currentBackStack}")
            navController.popBackStack(R.id.tab_profile, false)
            navController.navigate(R.id.tab_profile)
        }

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration.Builder(
            setOf(
                R.id.tab_feed,
                R.id.tab_search,
                R.id.tab_notification,
                R.id.tab_profile
            )
        ).build()
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navView.setOnItemReselectedListener {
            Log.i("MainActivity", "Reselected item: ${it.title}")
            // Resets back stack when re-selecting the same tab
            navController.popBackStack(it.itemId, false)
            navController.navigate(it.itemId)
        }

        // New Post Button
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_image_source, null)
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.hide()
        val bottomSheetCamera =
            bottomSheetView.findViewById<LinearLayout>(R.id.bottom_sheet_layout_1)
        val bottomSheetGallery =
            bottomSheetView.findViewById<LinearLayout>(R.id.bottom_sheet_layout_2)
        val bottomSheetNew = bottomSheetView.findViewById<LinearLayout>(R.id.bottom_sheet_layout_3)
        val createPostBtn: FloatingActionButton = binding.fab
        val inferenceViewModel = InferenceUtils.inferenceViewModel

        createPostBtn.setOnClickListener {
            bottomSheetDialog.show()
        }

        bottomSheetCamera.setOnClickListener {
            inferenceViewModel.resetViewModel()
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
            bottomSheetDialog.hide()
        }

        bottomSheetGallery.setOnClickListener {
            inferenceViewModel.resetViewModel()
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        bottomSheetNew.setOnClickListener {
            inferenceViewModel.resetViewModel()
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
        if (Build.VERSION.SDK_INT >= 30) {    // API 30 에 적용
            WindowCompat.setDecorFitsSystemWindows(window, false)
        }
    }

    fun replaceFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_fragment_activity_main, fragment)
        if (addToBackStack) {
            transaction.addToBackStack(null)
        }
        transaction.commit()
    }
}