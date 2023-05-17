package org.bmsk.beomtube.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.bmsk.beomtube.R
import org.bmsk.beomtube.data.VideoItem
import org.bmsk.beomtube.databinding.ItemVideoBinding
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class VideoAdapter(
    private val context: Context,
    private val onClick: (VideoItem) -> Unit,
) : ListAdapter<VideoItem, VideoAdapter.ViewHolder>(difUtil) {
    inner class ViewHolder(
        private val binding: ItemVideoBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        private val now = LocalDate.now()

        fun bind(item: VideoItem) {
            binding.titleTextView.text = item.title

            val date = LocalDate.parse(item.date, DateTimeFormatter.ofPattern("yyyy.MM.dd"))
            val monthsDiff = ChronoUnit.MONTHS.between(date, now)
            val yearsDiff = ChronoUnit.YEARS.between(date, now)
            val formattedDiff = if (monthsDiff < 12) {
                "%d개월 전".format(monthsDiff)
            } else {
                "%d년 전".format(yearsDiff)
            }

            binding.subTitleTextView.text = context.getString(
                R.string.sub_title_video_info,
                item.channelName,
                formatViewCount(item.viewCount),
                formattedDiff
            )

            Glide.with(binding.videoThumbnailImageView)
                .load(item.videoThumb)
                .into(binding.videoThumbnailImageView)

            Glide.with(binding.channelLogoImageView)
                .load(item.channelThumb)
                .circleCrop()
                .into(binding.channelLogoImageView)

            binding.root.setOnClickListener {
                onClick.invoke(item)
            }
        }

        private fun formatViewCount(count: Long): String {
            return when {
                count >= 1_000_0 -> DecimalFormat("#.#만회").format(count / 1_000_0.0)
                count >= 1_000 -> DecimalFormat("#.#천회").format(count / 1_000.0)
                else -> count.toString()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemVideoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val difUtil = object : DiffUtil.ItemCallback<VideoItem>() {
            override fun areItemsTheSame(oldItem: VideoItem, newItem: VideoItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: VideoItem, newItem: VideoItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}