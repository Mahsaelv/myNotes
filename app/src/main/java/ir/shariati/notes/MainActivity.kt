package ir.shariati.notes

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import ir.shariati.notes.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), NotesAdapter.OnNoteClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore
    private lateinit var adapter: NotesAdapter
    private val noteList = mutableListOf<Note>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()

        setupRecyclerView()

        binding.addNoteButton.setOnClickListener {
            startActivity(Intent(this, AddNoteActivity::class.java))
        }

        binding.logoutButton.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, WelcomeActivity::class.java))
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        loadNotes()
    }

    private fun setupRecyclerView() {
        adapter = NotesAdapter(noteList, this)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun loadNotes() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            database.collection("notes").document(userId).collection("userNotes")
                .get()
                .addOnSuccessListener { documents ->
                    noteList.clear()
                    for (document in documents) {
                        val note = document.toObject(Note::class.java)
                        noteList.add(note)
                    }
                    adapter.notifyDataSetChanged()
                }
                .addOnFailureListener { exception ->
                    Log.e("MainActivity", "Error getting documents: ", exception)
                }
        }
    }

    override fun onEdit(note: Note) {
        val intent = Intent(this, AddNoteActivity::class.java)
        intent.putExtra("noteId", note.id)
        intent.putExtra("noteTitle", note.title)
        intent.putExtra("noteDescription", note.description)
        startActivity(intent)
    }

    override fun onDelete(note: Note) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            database.collection("notes").document(userId).collection("userNotes").document(note.id)
                .delete()
                .addOnSuccessListener {
                    noteList.remove(note)
                    adapter.notifyDataSetChanged()
                }
                .addOnFailureListener { e ->
                    Log.e("MainActivity", "Error deleting document", e)
                }
        }
    }

    private fun navigateToLoginActivity() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
