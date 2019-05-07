package com.imd.dndhelper

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_character.*

class AddCharacter : AppCompatActivity() {
    companion object {
        const val TAG = "AddCharacter"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_character)

        addChar_add.setOnClickListener {
            val character = HashMap<String, Any>()
            character["name"] = addChar_nameInput.text.toString()
            FirebaseFirestore.getInstance().collection("characters")
                .add(character)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, task.toString())
                        finish()
                    } else {
                        Log.d(TAG, task.toString())
                        Toast.makeText(this, "Failed adding", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Log.d(TAG, e.message)
                }
        }
    }
}
