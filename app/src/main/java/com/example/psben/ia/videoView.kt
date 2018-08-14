package com.example.psben.ia

import android.graphics.Color
import android.media.MediaPlayer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.MediaController
import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressFlower
import kotlinx.android.synthetic.main.activity_video_view.*

class videoView : AppCompatActivity() {

    var videoUrl: String? = null
    var dialog: ACProgressFlower? = null
    var mediaController: MediaController? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_view)
        videoUrl = intent.getStringExtra("videoUrl")
        dialog = ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Loading...")
                .fadeColor(Color.DKGRAY).build()
        dialog!!.show()
        configureVideoView(videoUrl.toString())
    }

    fun configureVideoView(videoUrl:String) {
        videoView.setVideoPath(
                videoUrl)
        videoView.setOnPreparedListener(object: MediaPlayer.OnPreparedListener{
            override fun onPrepared(p0: MediaPlayer?) {
                dialog!!.dismiss()
            }
        })
        mediaController = MediaController(this)
        mediaController?.setAnchorView(videoView)
        videoView.setMediaController(mediaController)
        videoView.start()
    }
}
