package com.Guru.marvelhandbook

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.play.core.splitcompat.SplitCompat
import kotlinx.android.synthetic.main.activity_scrolling.*

open class SplitCompatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SplitCompat .install ( this )
    }
}
