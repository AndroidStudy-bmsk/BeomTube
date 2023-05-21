package org.bmsk.beomtube

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import org.bmsk.beomtube.adapter.VideoAdapter
import org.bmsk.beomtube.data.VideoItem
import org.bmsk.beomtube.data.VideoList
import org.bmsk.beomtube.databinding.ActivityMainBinding
import org.bmsk.beomtube.util.readData
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val videoList: VideoList by lazy {
        readData("videos.json", VideoList::class.java) ?: VideoList(emptyList())
    }
    private lateinit var videoAdapter: VideoAdapter

    private var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setUpMotionLayout()
        setUpVideoRecyclerView()

        setUpControlButton()
        binding.hideButton.setOnClickListener {
            binding.motionLayout.transitionToState(R.id.hide)
            player?.pause()
        }
    }

    private fun setUpControlButton() {
        binding.controlButton.setOnClickListener {
            player?.let {
                if (it.isPlaying) {
                    it.pause()
                } else {
                    it.play()
                }
            }
        }
    }

    private fun setUpVideoRecyclerView() {
        videoAdapter = VideoAdapter(context = this) { videoItem ->
            binding.motionLayout.setTransition(R.id.collapse, R.id.expand)
            binding.motionLayout.transitionToEnd()

            play(videoItem)
        }

        binding.videoListRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = videoAdapter
        }

        videoAdapter.submitList(videoList.videos)
    }

    private fun setUpMotionLayout() {
        binding.motionLayout.targetView = binding.videoPlayerContainer
        binding.motionLayout.jumpToState(R.id.hide)
        binding.motionLayout.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int
            ) {
                Log.d("MotionLayout", "Transition Started")
            }

            override fun onTransitionChange(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int,
                progress: Float
            ) {
                Log.d("MotionLayout", "Transition Changing: $progress")
                binding.playerView.useController = false
            }

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                Log.d("MotionLayout", "Transition Completed")
                binding.playerView.useController = (currentId == R.id.expand)
            }

            override fun onTransitionTrigger(
                motionLayout: MotionLayout?,
                triggerId: Int,
                positive: Boolean,
                progress: Float
            ) {
                Log.d("MotionLayout", "Transition Triggered")
            }
        })
    }

    private fun play(videoItem: VideoItem) {
        if (videoItem.sources.isNotEmpty()) {
            player?.setMediaItem(MediaItem.fromUri(Uri.parse(videoItem.sources[0])))
            player?.prepare()
            player?.play()

            binding.videoTitleTextView.text = videoItem.title
        }
    }

    // 플레이어의 초기화가 오래걸릴 수 있으므로 onStart에서부터 초기화를 진행한다.
    override fun onStart() {
        super.onStart()

        initExoPlayer()
    }

    // 이후 사용자와의 상호작용이 가능해지는 onResume에서도 ExoPlayer를 초기화한다.
    override fun onResume() {
        super.onResume()

        initExoPlayer()
    }

    override fun onStop() {
        super.onStop()

        player?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()

        player?.release()
    }

    private fun initExoPlayer() {
        if (player == null) {
            player = ExoPlayer.Builder(this).build()
                .also { exoPlayer ->
                    binding.playerView.player = exoPlayer
                    binding.playerView.useController = false

                    exoPlayer.addListener(object: Player.Listener {
                        override fun onIsPlayingChanged(isPlaying: Boolean) {
                            super.onIsPlayingChanged(isPlaying)

                            if(isPlaying) {
                                binding.controlButton.setImageResource(R.drawable.baseline_pause_24)
                            } else {
                                binding.controlButton.setImageResource(R.drawable.baseline_play_arrow_24)
                            }
                        }
                    })
                }
        }
    }
}