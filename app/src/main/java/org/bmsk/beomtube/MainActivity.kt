package org.bmsk.beomtube

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import org.bmsk.beomtube.adapter.PlayerVideoAdapter
import org.bmsk.beomtube.adapter.VideoAdapter
import org.bmsk.beomtube.data.VideoList
import org.bmsk.beomtube.data.player.PlayerHeader
import org.bmsk.beomtube.data.player.transform
import org.bmsk.beomtube.databinding.ActivityMainBinding
import org.bmsk.beomtube.util.readData

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val videoList: VideoList by lazy {
        readData("videos.json", VideoList::class.java) ?: VideoList(emptyList())
    }
    private lateinit var videoAdapter: VideoAdapter
    private lateinit var playerVideoAdapter: PlayerVideoAdapter

    private var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setUpMotionLayout()
        setUpVideoRecyclerView()
        setUpPlayerVideoRecyclerView()
        setUpControlButton()
        setUpHideButton()
    }

    private fun setUpHideButton() {
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

            val headerModel = PlayerHeader(
                id = "H${videoItem.id}",
                title = videoItem.title,
                channelName = videoItem.channelName,
                viewCount = videoItem.viewCount,
                channelThumb = videoItem.channelThumb,
                date = videoItem.date
            )

            val list = listOf(headerModel) + videoList.videos.filter { it.id != videoItem.id }
                .map { it.transform() }
            playerVideoAdapter.submitList(list)

            play(videoItem.sources[0], videoItem.title)
        }

        binding.videoListRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = videoAdapter
        }

        videoAdapter.submitList(videoList.videos)
    }

    private fun setUpPlayerVideoRecyclerView() {
        playerVideoAdapter = PlayerVideoAdapter(context = this) { playerVideo ->
            if (playerVideo.sources.isNotEmpty()) {
                play(playerVideo.sources[0], playerVideo.title)

                val headerModel = PlayerHeader(
                    id = "H${playerVideo.id}",
                    title = playerVideo.title,
                    channelName = playerVideo.channelName,
                    viewCount = playerVideo.viewCount,
                    channelThumb = playerVideo.channelThumb,
                    date = playerVideo.date
                )

                val list = listOf(headerModel) + videoList.videos.filter { it.id != playerVideo.id }
                    .map { it.transform() }
                playerVideoAdapter.submitList(list) {
                    // submit callback
                    binding.playerRecyclerView.smoothScrollToPosition(0)
                }
            }
        }
        binding.playerRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = playerVideoAdapter
        }
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

    private fun play(videoUrl: String, videoTitle: String) {
        player?.setMediaItem(MediaItem.fromUri(Uri.parse(videoUrl)))
        player?.prepare()
        player?.play()

        binding.videoTitleTextView.text = videoTitle
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

                    exoPlayer.addListener(object : Player.Listener {
                        override fun onIsPlayingChanged(isPlaying: Boolean) {
                            super.onIsPlayingChanged(isPlaying)

                            if (isPlaying) {
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