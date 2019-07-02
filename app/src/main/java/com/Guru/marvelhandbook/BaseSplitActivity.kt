package com.Guru.marvelhandbook

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.play.core.splitcompat.SplitCompat

abstract class BaseSplitActivity : AppCompatActivity() {



    override fun attachBaseContext(newBase: Context?) {

        super.attachBaseContext(newBase)

        SplitCompat.install(this)

    }



}