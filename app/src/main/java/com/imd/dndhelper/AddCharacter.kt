package com.imd.dndhelper

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_character.*
import java.util.*
import kotlin.collections.HashMap

class AddCharacter : AppCompatActivity() {
	companion object {
		const val TAG = "AddCharacter"
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_add_character)

		addChar_add.setOnClickListener {
			val character = HashMap<String, Any>()
			val id = UUID.randomUUID().toString()
			character["name"] = addChar_nameInput.text.toString()
			character["p"] = Integer.parseInt(addChar_pInput.text.toString())
			character["x"] = Integer.parseInt(addChar_xInput.text.toString())
			character["id"] = id
			character["special"] = addChar_specialInput.isChecked
			FirebaseFirestore.getInstance().collection("characters").document(id)
				.set(character)
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
