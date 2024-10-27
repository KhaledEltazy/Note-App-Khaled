package com.example.noteappkhaled.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.noteappkhaled.R
import com.example.noteappkhaled.databinding.GridViewSmallBinding
import com.example.noteappkhaled.model.Notes
import java.text.SimpleDateFormat
import java.util.Locale

class SmallGridAdapter : RecyclerView.Adapter<SmallGridAdapter.SmallGirdViewHolder>() {
    var noteList = listOf<Notes>()
    private val dataFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    inner class SmallGirdViewHolder(val  binding : GridViewSmallBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SmallGirdViewHolder {
        return SmallGirdViewHolder(GridViewSmallBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return noteList.size
    }

    override fun onBindViewHolder(holder: SmallGirdViewHolder, position: Int) {
        val currentList = noteList[position]

        //setTitle and description
        holder.binding.titleNoteItem.text = currentList.title
        holder.binding.descriptionNoteItem.text = currentList.description

        //set Date and Time
        holder.binding.dateItem.text = dataFormat.format(currentList.time)
        holder.binding.timeItem.text = timeFormat.format(currentList.time)

        //fav icon color
        holder.itemView.apply {
            if(currentList.fav != 0){
                holder.binding.favImage.setImageResource(R.drawable.baseline_favorite_24_red)
            } else{
                holder.binding.favImage.setImageResource(R.drawable.baseline_favorite_24)
            }
        }

        //click on fav Image
        holder.binding.favImage.setOnClickListener {
            onFavClickListener?.invoke(currentList)
        }

        //set clickListener update Function
        holder.itemView.setOnClickListener{
            onItemClickListener?.invoke(currentList)
        }

    }



    @SuppressLint("NotifyDataSetChanged")
    fun setNote(myNotes : List<Notes>){
        this.noteList = myNotes
        notifyDataSetChanged()
    }

    var onItemClickListener : ((Notes)-> Unit)? = null

    var onFavClickListener : ((Notes)-> Unit)? = null

    fun setFilteredList(NoteList : List<Notes>){
        this.noteList = NoteList
        notifyDataSetChanged()
    }

}