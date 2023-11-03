import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dreamteam.sharedream.databinding.FragmentYourDetailRecyclerViewBinding // Change this to your actual binding class
import com.dreamteam.sharedream.model.PostRcv
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class YourDetailRecyclerViewAdapter : RecyclerView.Adapter<YourDetailRecyclerViewAdapter.YourDetailViewHolder>() {
    private var itemList: List<PostRcv> = emptyList()
    val storage = Firebase.storage

    fun setItem(items: List<PostRcv>) {
        itemList = items
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(item: PostRcv, position: Int)
    }

    var onItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YourDetailViewHolder {
        val binding = FragmentYourDetailRecyclerViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return YourDetailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: YourDetailViewHolder, position: Int) {
        val item = itemList[position]

        holder.titleTextView.text = item.title
        holder.priceTextView.text = item.price.toString()
        holder.subtitleTextView.text = item.desc
        holder.endDateTextView.text = EndTime(item.endDate)
        holder.itemView.setOnClickListener {
            val clickedItem = item
            onItemClickListener?.onItemClick(clickedItem, position) // 클릭된 아이템과 위치를 전달
        }
        bindingImage(holder, item.imgs[0].toString())
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class YourDetailViewHolder(private val binding: FragmentYourDetailRecyclerViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val titleTextView = binding.writeTittle
        val subtitleTextView = binding.writeSubtittle
        val priceTextView = binding.writePrice
        val endDateTextView = binding.endDate
        val imageView = binding.writeImage

    }

    private fun bindingImage(holder: YourDetailViewHolder, image: String?) {
        if (image?.isNotEmpty() == true) {
            val imagePath = image
            storage.reference.child("post").child(imagePath).downloadUrl
                .addOnSuccessListener { uri ->
                    holder.imageView?.let { imageView ->
                        Glide.with(imageView.context)
                            .load(uri)
                            .into(imageView)
                    }
                }
        }

    }

    private fun EndTime(endTime: String): String {
        val dateFormat = SimpleDateFormat("E MMM dd HH:mm:ss z yyyy", Locale.US)
        try {
            val futureDate = dateFormat.parse(endTime)
            val currentDate = Date()
            val diff = futureDate.time - currentDate.time
            val days = TimeUnit.MILLISECONDS.toDays(diff)//시간을 밀리초로 변한한 뒤 일로변환
            val hours = TimeUnit.MILLISECONDS.toHours(diff)//시간을 밀리초로 변환한 뒤 시간으로변환
            val minites = TimeUnit.MILLISECONDS.toMinutes(diff)//시밀변뒤분
            return when {
                days >= 1 -> "$days 일 남음"
                hours >= 1 -> "$hours 시간 남음"
                minites >=0 -> "$minites 분 남음"
                else -> "마감되었습니다!"
            }
        } catch (e: ParseException) {
            return "날짜 형식 오류"
        }
    }
}
