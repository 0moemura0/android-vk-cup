package com.omoemurao.android_vk_cup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.omoemurao.android_vk_cup.news.NewsMainActivity

class MainActivity : AppCompatActivity(), View.OnClickListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btn_taxi = findViewById<Button>(R.id.btn_taxi)
        val btn_news = findViewById<Button>(R.id.btn_news)
        val btn_video = findViewById<Button>(R.id.btn_video)
        val btn_voice = findViewById<Button>(R.id.btn_voice)
        val btn_xo = findViewById<Button>(R.id.btn_xo)

        btn_taxi.setOnClickListener(this)
        btn_news.setOnClickListener(this)
        btn_video.setOnClickListener(this)
        btn_voice.setOnClickListener(this)
        btn_xo.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_taxi -> {
            }
            R.id.btn_news -> {
                startActivity(Intent(this, NewsMainActivity::class.java))
            }
            R.id.btn_video -> {
            }
            R.id.btn_voice -> {
            }
            R.id.btn_xo -> {
            }
        }
    }
}