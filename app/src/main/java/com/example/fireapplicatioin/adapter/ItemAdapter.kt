package com.example.fireapplicatioin.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fireapplicatioin.NewsFlashActivity
import com.example.fireapplicatioin.R
import com.example.fireapplicatioin.data.Datasource
import com.example.fireapplicatioin.model.NewsFlash

class ItemAdapter(val context: Context) : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    private var dataset: ArrayList<NewsFlash>

    init{
        val datasource = Datasource()
        dataset = datasource.loadNewsFlashes()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataset[position]
        holder.textView.text = context.getString(item.titleId)
        holder.imageView.setImageResource(item.imageId)
        holder.imageView.setOnClickListener{
            val intent = Intent(context, NewsFlashActivity::class.java)
            intent.putExtra("imageId", item.imageId)
            intent.putExtra("titleId", item.titleId)
            context.startActivity(intent)

        }
        holder.textView.setOnClickListener{
            val intent = Intent(context, NewsFlashActivity::class.java)
            intent.putExtra("imageId", item.imageId)
            intent.putExtra("titleId", item.titleId)
            context.startActivity(intent)

        }
    }

    override fun getItemCount(): Int = dataset.size

    class ViewHolder(val itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.item_text)
        val imageView: ImageView = itemView.findViewById(R.id.item_image)

    }

}