package com.example.consumerapp

import android.content.Intent
import android.database.ContentObserver
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.PersistableBundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.example.consumerapp.adapter.NoteAdapter
import com.example.consumerapp.db.DatabaseContract.NoteColumns.Companion.CONTENT_URI
import com.example.consumerapp.entity.Note
import com.example.consumerapp.helper.MappingHelper
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: NoteAdapter

    companion object {
        private const val EXTRA_STATE = "EXTRA_STATE"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.title = "Consumer Notes"

        rv_notes.layoutManager = LinearLayoutManager(this)
        rv_notes.setHasFixedSize(true)

        adapter = NoteAdapter(this)
        rv_notes.adapter = adapter

        fabb_add.setOnClickListener {
            val intent = Intent(this@MainActivity, NoteAddUpdateActivity::class.java)
            startActivityForResult(intent, NoteAddUpdateActivity.REQUEST_ADD)
        }

        val handlerThread = HandlerThread("DataObserver")
        handlerThread.start()
        val handler = Handler(handlerThread.looper)

        val myObserver = object : ContentObserver(handler) {
            override fun onChange(selfChange: Boolean) {
                loadNotesAsync()
            }
        }

        contentResolver.registerContentObserver(CONTENT_URI, true, myObserver)

    /*

        Cek jika savedInstaceState null makan akan melakukan proses asynctask nya
        jika tidak,akan mengambil arraylist nya dari yang sudah di simpan

    */

        if (savedInstanceState == null) {
            // Proses ambil data
            loadNotesAsync()
        } else {
            val list = savedInstanceState.getParcelableArrayList<Note>(EXTRA_STATE)
            if (list != null) {
                adapter.listNotes = list
            }
        }
    }

    private fun loadNotesAsync() {
        GlobalScope.launch(Dispatchers.Main) {
            progress_bar.visibility = View.VISIBLE
            val deferredNotes = async(Dispatchers.IO) {
                val cursor = contentResolver?.query(CONTENT_URI, null,null,null,null)
                MappingHelper.mapCursorToArrayList(cursor)
            }
            progress_bar.visibility = View.INVISIBLE
            val notes = deferredNotes.await()
            if (notes.size > 0) {
                adapter.listNotes = notes
            } else {
                adapter.listNotes = ArrayList()
                showSnackbarMessage("Tidak ada data saat ini")
            }
        }
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (data != null) {
//            when (requestCode) {
//                // Akan dipanggil jika request codenya ADD
//                NoteAddUpdateActivity.REQUEST_ADD -> if (resultCode == NoteAddUpdateActivity.RESULT_ADD) {
//                    val note = data.getParcelableExtra<Note>(NoteAddUpdateActivity.EXTRA_NOTE)
//
//                    adapter.addItem(note)
//                    rv_notes.smoothScrollToPosition(adapter.itemCount - 1)
//
//                    showSnackbarMessage("Satu item behasil ditambahkan")
//                }
//                // Update dan Delete memiliki request code sama akan tetapi result codenya berbeda
//                NoteAddUpdateActivity.REQUEST_UPDATE ->
//                    when (resultCode) {
//                        NoteAddUpdateActivity.RESULT_UPDATE -> {
//                            val note =
//                                data.getParcelableExtra<Note>(NoteAddUpdateActivity.EXTRA_NOTE)
//                            val position = data.getIntExtra(NoteAddUpdateActivity.EXTRA_POSITION, 0)
//
//                            adapter.updateItem(position, note)
//                            rv_notes.smoothScrollToPosition(position)
//
//                            showSnackbarMessage("Satu item berhasil diubah")
//                        }
//                        /*
//                        Akan dipanggil jika result codenya DELETE
//                        Delete akan menghapus data dari list berdasarkan dari position
//                        */
//                        NoteAddUpdateActivity.RESULT_DELETE -> {
//                            val position = data.getIntExtra(NoteAddUpdateActivity.EXTRA_POSITION, 0)
//
//                            adapter.removeItem(position)
//
//                            showSnackbarMessage("Satu item berhasil dihapus")
//                        }
//                    }
//            }
//        }
//    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putParcelableArrayList(EXTRA_STATE, adapter.listNotes)
    }

    private fun showSnackbarMessage(s: String) {
        Snackbar.make(rv_notes, s, Snackbar.LENGTH_SHORT).show()

    }

//    override fun onDestroy() {
//        super.onDestroy()
//        noteHelper.close()
//    }
}