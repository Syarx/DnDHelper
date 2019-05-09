package com.imd.dndhelper

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore

class BattleScreen : AppCompatActivity() {
    companion object {
        const val TAG = "BattleScreen"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_battle_screen)
        val char1id = intent.getStringExtra("char0")
        val char2id = intent.getStringExtra("char1")
        FirebaseFirestore.getInstance().collection("characters").document(char1id).get()
            .addOnCompleteListener {
                var char1 = it.result?.toObject(Character::class.java)
                FirebaseFirestore.getInstance().collection("characters").document(char2id).get()
                    .addOnCompleteListener { it2 ->
                        var char2 = it2.result?.toObject(Character::class.java)
                        if (char1 != null && char2 != null) {
                            title = "${char1.name} vs ${char2.name}"
                            startBatte(char1, char2)
                        } else {
                            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }
            }


    }

    private fun startBatte(char1: Character, char2: Character) {

    }
}
