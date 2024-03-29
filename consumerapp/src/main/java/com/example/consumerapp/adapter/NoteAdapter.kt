package com.example.consumerapp.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.consumerapp.CustomOnclickListener
import com.example.consumerapp.NoteAddUpdateActivity
import com.example.consumerapp.R
import com.example.consumerapp.entity.Note
import kotlinx.android.synthetic.main.item_note.view.*


class NoteAdapter(private val activity: Activity) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {
    var listNotes = ArrayList<Note>()
        set(listNotes) {
            if (listNotes.size > 0) {
                this.listNotes.clear()
            }
            this.listNotes.addAll(listNotes)

            notifyDataSetChanged()
        }

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(note: Note) {
            with(itemView) {
                tv_item_title.text = note.title
                tv_item_description.text = note.description
                tv_item_date.text = note.date
                cv_item_note.setOnClickListener(
                    CustomOnclickListener(
                        adapterPosition,
                        object :
                            CustomOnclickListener.OnItemClickCallback {
                            override fun onItemClicked(view: View, position: Int) {
                                val intent = Intent(
                                    activity,
                                    NoteAddUpdateActivity::class.java
                                )
                                intent.putExtra(
                                    NoteAddUpdateActivity.EXTRA_POSITION,
                                    position
                                )
                                intent.putExtra(
                                    NoteAddUpdateActivity.EXTRA_NOTE,
                                    note
                                )
                                activity.startActivityForResult(
                                    intent,
                                    NoteAddUpdateActivity.REQUEST_UPDATE
                                )
                            }
                        })
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun getItemCount(): Int  = this.listNotes.size

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(listNotes[position])
    }

    fun addItem(note: Note) {
        this.listNotes.add(note)
        notifyItemInserted(this.listNotes.size - 1)
    }

    fun updateItem(position: Int, note: Note) {
        this.listNotes[position] = note
        notifyItemChanged(position, note)
    }

    fun removeItem(position: Int) {
        this.listNotes.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, this.listNotes.size)
    }
}