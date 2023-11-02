//package com.dreamteam.sharedream.home.Edit
//
//import android.annotation.SuppressLint
//import android.net.Uri
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide
//import com.dreamteam.sharedream.databinding.EditImageItemBinding
//
//class EditImageAdapter(val context: EditActivity, val items: ArrayList<Uri>) :
//    RecyclerView.Adapter<EditImageAdapter.ViewHolder>() {
//
//
//
//    // onItemClickListener 인터페이스 선언하기
//    interface onItemClickListener {
//        fun onItemClick(position: Int)
//    }
//
//    // onItemClickListener 참조 변수 선언하기
//    private var itemClickListener: onItemClickListener
//
//
//
//    init {
//        itemClickListener = object : onItemClickListener{
//            @SuppressLint("NotifyDataSetChanged")
//            override fun onItemClick(position: Int) {
//                items.removeAt(position)
//                    notifyDataSetChanged()
//                context.printCount()
//            }
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val inflater = LayoutInflater.from(parent.context)
//        val binding = EditImageItemBinding.inflate(inflater, parent, false)
//        return ViewHolder(binding)
//    }
//
//    override fun getItemCount(): Int {
//        return items.size
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val item = items[position]
//        holder.bindItem(item)
//    }
//
//    inner class ViewHolder(private val binding: EditImageItemBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//
//        fun bindItem(item: Uri) {
//            val imageArea = binding.editImage
//            val delete = binding.btnDelete
//
//            // ViewHolder에서 delete 버튼을 눌렀을 때 Listener에 onItemClick 등록하기
//            delete.setOnClickListener {
//                val position = adapterPosition
//                itemClickListener.onItemClick(position)
//            }
//            Glide.with(context)
//                .load(item)
//                .into(imageArea)
//        }
//
//    }
//}