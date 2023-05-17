package org.bmsk.beomtube

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import org.bmsk.beomtube.adapter.VideoAdapter
import org.bmsk.beomtube.data.VideoList
import org.bmsk.beomtube.databinding.ActivityMainBinding
import org.bmsk.beomtube.util.readData

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var videoAdapter: VideoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        videoAdapter = VideoAdapter(context = this) { videoItem ->
            binding.motionLayout.setTransition(R.id.collapse, R.id.expand)
            binding.motionLayout.transitionToEnd()
        }

        binding.motionLayout.jumpToState(R.id.collapse)

        binding.videoListRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = videoAdapter
        }

        val videoList = readData("videos.json", VideoList::class.java) ?: VideoList(emptyList())
        videoAdapter.submitList(videoList.videos)
    }
}