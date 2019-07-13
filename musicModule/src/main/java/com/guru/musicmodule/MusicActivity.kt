package com.guru.musicmodule

import android.os.Bundle
import com.Guru.marvelhandbook.SplitCompatActivity
import kotlinx.android.synthetic.main.activity_music.*


class MusicActivity : SplitCompatActivity() {

   // private lateinit var  lookAtMe:LookAtMe

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music)

       // actionBar.setTitle("Dynamic Feature module")




        val fd = assets.openFd("Dog.mp4")
         stats.text = ((fd.length.toInt() /1024).toString()+" kb")

//        lookAtMe = lookme
//
//        lookAtMe.init(this)
//        lookAtMe.setVideoURI(Uri.parse("/assets/Dog.mp4"))
//        // lookAtMe.setVideoPath("http://website.com/video/mp4/62000/62792m.mp4"); to use video from a url
//
//        lookAtMe.start()
//        lookAtMe.setLookMe()
    }
}
