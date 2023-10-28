package com.project.spire.ui.create

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.spire.R
import com.example.spire.databinding.ActivityWriteTextBinding

class WriteTextActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWriteTextBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        binding = ActivityWriteTextBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val appbar = binding.writeTextAppBarLayout
        appbar.toolbarText.setText(R.string.title_toolbar_write_text)


    }
}