package com.example.fireapplicatioin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.fireapplicatioin.databinding.ActivityNewsFlashBinding
import androidx.core.app.ActivityCompat.startActivityForResult

import android.content.Intent
import android.view.MenuItem


class NewsFlashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityNewsFlashBinding>(this, R.layout.activity_news_flash)
        setContentView(binding.root)
        binding.image.setImageResource(intent.getIntExtra("imageId", 1))
        binding.title.setText(intent.getIntExtra("titleId", 1))
        val backButton = actionBar
        backButton?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val myIntent = Intent(applicationContext, MainActivity::class.java)
        myIntent.putExtra("activeItem", 0)
        startActivity(myIntent)
        return true
    }
}