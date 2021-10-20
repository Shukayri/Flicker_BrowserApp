package com.example.flickerbrowser


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.flickerbrowser.image.Image
import com.example.flickerbrowser.databinding.EditRowBinding

class MyAdapter(private val activity: MainActivity, private val photos: ArrayList<Image>): RecyclerView.Adapter<MyAdapter.ItemViewHolder>() {
    class ItemViewHolder(val binding: EditRowBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(EditRowBinding.inflate

            (LayoutInflater.from(parent.context),
            parent,
            false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val photo = photos[position]

        holder.binding.apply {
            tvImageText.text = photo.title
            Glide.with(activity).load(photo.link).into(ivThumbnail)
            itemRow.setOnClickListener { activity.openImg(photo.link) }
        }
    }

    override fun getItemCount() = photos.size
}