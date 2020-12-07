package com.test.firestore

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    companion object {
        const val COLLECTION_KEY = "Chat"
        const val DOCUMENT_KEY = "Message"
        const val NAME_FIELD = "Name"
        const val TEXT_FIELD = "Text"
    }

    private val firestoreChat by lazy {
        FirebaseFirestore.getInstance()
            .collection(COLLECTION_KEY).document(DOCUMENT_KEY)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btn_send_message).setOnClickListener { sendMessage() }
        realtimeUpdateListener()
    }

    private fun sendMessage() {
        val newMessage = mapOf(
            NAME_FIELD to findViewById<EditText>(R.id.et_name).text.toString(),
            TEXT_FIELD to findViewById<EditText>(R.id.et_message).text.toString()
        )
        firestoreChat.set(newMessage)
            .addOnSuccessListener {
                Toast.makeText(this@MainActivity, "Message Sent", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e -> Log.e("ERROR", e.message) }
    }

    @SuppressLint("SetTextI18n")
    private fun realtimeUpdateListener() {
        firestoreChat.addSnapshotListener { documentSnapshot, e ->
            when {
                e != null -> Log.e("ERROR", e.message)
                documentSnapshot != null && documentSnapshot.exists() -> {
                    with(documentSnapshot) {
                        data?.let {
                            it[NAME_FIELD]?.let { name ->
                                findViewById<TextView>(R.id.tv_received_sender).text = "$name :"
                            }

                            it[TEXT_FIELD]?.let { content ->
                                findViewById<TextView>(R.id.tv_received_content).text = "$content"
                            }
                        }

                    }
                }
            }
        }
    }
}

