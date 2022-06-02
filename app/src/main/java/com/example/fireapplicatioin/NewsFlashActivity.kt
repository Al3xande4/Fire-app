package com.example.fireapplicatioin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.fireapplicatioin.databinding.ActivityNewsFlashBinding


class NewsFlashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityNewsFlashBinding>(this, R.layout.activity_news_flash)
        setContentView(binding.root)
        binding.image.setImageResource(intent.getIntExtra("imageId", 1))
        binding.title.setText(intent.getIntExtra("titleId", 1))
        binding.btnBack.setOnClickListener{ onBackPressed() }
    }

}