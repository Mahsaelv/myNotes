package ir.shariati.notes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.shariati.notes.databinding.NoteItemBinding

class NotesAdapter(
    private val notes: List<Note>,
    private val listener: OnNoteClickListener
) : RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {

    interface OnNoteClickListener {
        fun onEdit(note: Note)
        fun onDelete(note: Note)
    }

    inner class NoteViewHolder(private val binding: NoteItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(note: Note) {
            binding.titleTextView.text = note.title
            binding.descriptionTextView.text = note.description
            binding.dateTextView.text = note.date

            binding.editButton.setOnClickListener {
                listener.onEdit(note)
            }

            binding.deleteButton.setOnClickListener {
                listener.onDelete(note)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = NoteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(notes[position])
    }

    override fun getItemCount(): Int = notes.size
}
