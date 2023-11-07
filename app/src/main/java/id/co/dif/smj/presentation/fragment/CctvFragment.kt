package id.co.dif.smj.presentation.fragment

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.core.view.isInvisible
import androidx.databinding.DataBindingUtil.setContentView
import androidx.fragment.app.FragmentContainer
import androidx.lifecycle.ViewModelProvider
import id.co.dif.smj.R
import id.co.dif.smj.base.BaseFragment
import id.co.dif.smj.base.BaseViewModel
import id.co.dif.smj.databinding.FragmentCctvBinding

class CctvFragment : BaseFragment<BaseViewModel, FragmentCctvBinding>() {
    override val layoutResId = R.layout.fragment_cctv

    private val videoURL1 = "https://media.lewatmana.com/cam/mirslipi/133/video-20000113-102049.384.mp4"
    private val videoURL2 = "https://media.lewatmana.com/cam/sotisresidence/332/videoclip19700101_001500.384.mp4"
    private val videoURL3 = "https://media.lewatmana.com/cam/infovista/231/mp420231012_112834.384.mp4"
    private lateinit var videoUri1: Uri
    private lateinit var videoUri2: Uri
    private lateinit var videoUri3: Uri
    override fun onViewBindingCreated(savedInstanceState: Bundle?) {

        videoUri1 = Uri.parse(videoURL1)
        binding.videoView1.setVideoURI(videoUri1)

        videoUri2 = Uri.parse(videoURL2)
        binding.videoView2.setVideoURI(videoUri2)

        videoUri3 = Uri.parse(videoURL3)
        binding.videoView3.setVideoURI(videoUri3)


//        val mediaController = MediaController(context)
//        mediaController.setAnchorView(binding.videoView1)
//        mediaController.setAnchorView(binding.videoView2)
//        mediaController.setAnchorView(binding.videoView3)
//        binding.videoView1.setMediaController(mediaController)
//        binding.videoView2.setMediaController(mediaController)
//        binding.videoView3.setMediaController(mediaController)
        binding.videoView1.setOnCompletionListener {
            binding.videoView1.start()
        }
        binding.videoView2.setOnCompletionListener {
            binding.videoView2.start()
        }
        binding.videoView3.setOnCompletionListener {
            binding.videoView3.start()
        }

        binding.videoView1.start()
        binding.videoView2.start()
        binding.videoView3.start()

        val handler = Handler()
        val runnable = object : Runnable {
            override fun run() {
                if (binding.videoView1.isPlaying && binding.videoView2.isPlaying && binding.videoView3.isPlaying) {
                    val currentPosition = binding.videoView1.currentPosition
                    val currentPosition2 = binding.videoView2.currentPosition
                    val currentPosition3 = binding.videoView3.currentPosition
                    val duration = binding.videoView1.duration
                    val duration2 = binding.videoView2.duration
                    val duration3 = binding.videoView3.duration
                    val loopThreshold = 1000

                    if (duration - currentPosition < loopThreshold) {
                        binding.videoView1.seekTo(0)
                        binding.videoView1.start()
                    }
                    else if (duration2 - currentPosition2 < loopThreshold){
                        binding.videoView2.seekTo(0)
                        binding.videoView2.start()
                    }
                    else if (duration3 - currentPosition3 < loopThreshold){
                        binding.videoView3.seekTo(0)
                        binding.videoView3.start()
                    }

                    }
                    handler.postDelayed(this, 1000) // Check every second
                }
            }
            handler.postDelayed(runnable, 1000)

        }
}