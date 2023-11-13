package com.dreamteam.sharedream.view.adapter

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.dreamteam.sharedream.adapter.DifferCallback
import com.dreamteam.sharedream.adapter.PostClick
import com.dreamteam.sharedream.databinding.FragmentMyPageFavoritePostBinding
import com.dreamteam.sharedream.databinding.WriteItemBinding
import com.dreamteam.sharedream.model.PostRcv
import com.dreamteam.sharedream.view.MyPageFavoritePost

@Suppress("DEPRECATION")
class MyPageFavoritePostAdapter(private val postClick: PostClick) :
    ListAdapter<PostRcv, MyPageFavoritePostAdapter.MyPageFavoritePostViewHolder>(DifferCallback.differCallback) {

    inner class MyPageFavoritePostViewHolder(binding: WriteItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val title = binding.writeTittle
        val subtitle = binding.writeSubtittle
        val value = binding.writePrice
        val category = binding.writeCategory
        val image = binding.writeImage

        val postStateBgClosed: ImageView = binding.itemImgStateClosed
        val postStateBgPutOff: ImageView = binding.itemImgStatePutOff
        val postStateBgReservation: ImageView = binding.itemImgStateReservation

        fun bind(imagePath: Uri) {
            image.load(imagePath)

            when (currentList[layoutPosition].state) {
                "교환 가능" -> {
                    postStateBgClosed.visibility = View.INVISIBLE
                    postStateBgPutOff.visibility = View.INVISIBLE
                    postStateBgReservation.visibility = View.INVISIBLE
                }

                "교환 보류" -> postStateBgPutOff.visibility = View.VISIBLE
                "예약 중" -> postStateBgReservation.visibility = View.VISIBLE
                "교환 완료" -> postStateBgClosed.visibility = View.VISIBLE
                else -> return
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyPageFavoritePostViewHolder {
        return MyPageFavoritePostViewHolder(
            WriteItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MyPageFavoritePostViewHolder, position: Int) {
        val post = currentList[position]
        holder.itemView.setOnClickListener {
            postClick?.postClick(post)
        }

        val positionItem = currentList[position]

        holder.apply {
            category.text = "카테고리 : ${positionItem.category}"
            title.text = positionItem.title
            subtitle.text = positionItem.desc
            value.text = positionItem.price.toString()
            bind(positionItem.imgs.first())
            Log.d("xxxx", "onBindViewHolder: bind")
        }
    }
}