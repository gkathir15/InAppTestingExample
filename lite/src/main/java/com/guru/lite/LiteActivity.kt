package com.guru.lite

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_lite.*

class LiteActivity : AppCompatActivity() {

    var imageFilePath = "file:///android_asset/images/img1.jpg"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lite)
        Picasso.get()
            .load(R.drawable.img1).fit()
            .into(imageView)
    }
}
