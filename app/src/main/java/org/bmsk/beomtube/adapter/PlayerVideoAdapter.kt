package org.bmsk.beomtube.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.bmsk.beomtube.R
import org.bmsk.beomtube.data.player.PlayerHeader
import org.bmsk.beomtube.data.player.PlayerVideo
import org.bmsk.beomtube.data.player.PlayerVideoModel
import org.bmsk.beomtube.databinding.ItemVideoBinding
import org.bmsk.beomtube.databinding.ItemVideoHeaderBinding
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class PlayerVideoAdapter(
    private val context: Context,
    private val onClick: (PlayerVideo) -> Unit,
) : ListAdapter<PlayerVideoModel, RecyclerView.ViewHolder>(diffUtil) {

    private val now = LocalDate.now()

    inner class HeaderVideHolder(
        private val binding: ItemVideoHeaderBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PlayerHeader) {
            val date = LocalDate.parse(item.date, DateTimeFormatter.ofPattern("yyyy.MM.dd"))
            val monthsDiff = ChronoUnit.MONTHS.between(date, now)
            val yearsDiff = ChronoUnit.YEARS.between(date, now)
            val formattedDiff = if (monthsDiff < 12) {
                "%d개월 전".format(monthsDiff)
            } else {
                "%d년 전".format(yearsDiff)
            }

            binding.titleTextView.text = item.title
            binding.subTitleTextView.text = context.getString(
                R.string.header_sub_title_video_info, formatViewCount(item.viewCount), formattedDiff
            )
            binding.channelNameTextView.text = item.channelName
            Glide.with(binding.channelLogoImageView)
                .load(item.channelThumb)
                .circleCrop()
                .into(binding.channelLogoImageView)
        }

        private fun formatViewCount(count: Long): String {
            return when {
                count >= 1_000_0 -> DecimalFormat("#.#만회").format(count / 1_000_0.0)
                count >= 1_000 -> DecimalFormat("#.#천회").format(count / 1_000.0)
                else -> count.toString()
            }
        }
    }

    inner class VideoViewHolder(
        private val binding: ItemVideoBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PlayerVideo) {
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

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_HEADER) {
            HeaderVideHolder(
                ItemVideoHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else {
            VideoViewHolder(
                ItemVideoBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            VIEW_TYPE_HEADER -> {
                (holder as HeaderVideHolder).bind(currentList[position] as PlayerHeader)
            }

            VIEW_TYPE_VIDEO -> {
                (holder as VideoViewHolder).bind(currentList[position] as PlayerVideo)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) VIEW_TYPE_HEADER else VIEW_TYPE_VIDEO
    }

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_VIDEO = 1

        val diffUtil = object : DiffUtil.ItemCallback<PlayerVideoModel>() {
            override fun areItemsTheSame(oldItem: PlayerVideoModel, newItem: PlayerVideoModel): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: PlayerVideoModel, newItem: PlayerVideoModel): Boolean {
                return if(oldItem is PlayerVideo && newItem is PlayerVideo) {
                    oldItem == newItem
                } else if(oldItem is PlayerHeader && newItem is PlayerHeader) {
                    oldItem == newItem
                } else false
            }
        }
    }
}