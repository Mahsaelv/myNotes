package ir.shariati.notes

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import ir.shariati.notes.databinding.ActivityAddNoteBinding
import java.text.SimpleDateFormat
import java.util.*

class AddNoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddNoteBinding
    private lateinit var database: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var noteId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()

        noteId = intent.getStringExtra("noteId")
        if (noteId != null) {
            binding.noteTitleEditText.setText(intent.getStringExtra("noteTitle"))
            binding.noteDescriptionEditText.setText(intent.getStringExtra("noteDescription"))
        }

        binding.saveNoteButton.setOnClickListener {
            saveNote()
        }
    }

    private fun saveNote() {
        val title = binding.noteTitleEditText.text.toString()
        val description = binding.noteDescriptionEditText.text.toString()
        val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        if (title.isNotEmpty() && description.isNotEmpty()) {
            val userId = auth.currentUser?.uid
            if (userId != null) {
                val noteCollection = database.collection("notes").document(userId).collection("userNotes")
                val id = noteId ?: noteCollection.document().id
                val note = Note(
                    id = id,
                    title = title,
                    description = description,
                    date = date
                )
                noteCollection.document(id).set(note)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Note saved", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, "Failed to save note", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        } else {
            Toast.makeText(this, "Title and description cannot be empty", Toast.LENGTH_SHORT).show()
        }
    }
}
