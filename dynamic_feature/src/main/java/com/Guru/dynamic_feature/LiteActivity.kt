package com.Guru.dynamic_feature

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.Guru.dynamic_feature.R
import com.google.android.play.core.splitcompat.SplitCompat
import com.squareup.picasso.Picasso

import kotlinx.android.synthetic.main.activity_lite.*

class LiteActivity : AppCompatActivity() {

    var imageFilePath = "file:///android_asset/images/img1.jpg"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lite)
        Picasso.get().load(R.drawable.img1).fit().into(imageView)

    }

    override fun attachBaseContext(newBase: Context?) {

        super.attachBaseContext(newBase)

        SplitCompat.install(this)

    }

}
