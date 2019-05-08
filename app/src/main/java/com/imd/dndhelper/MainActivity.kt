package com.imd.dndhelper

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.character_row.view.*

class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseAuth.getInstance().signInAnonymously()
            .addOnCompleteListener {
                if (!it.isSuccessful) {
                    Toast.makeText(this, "Failed $it", Toast.LENGTH_SHORT).show()
                    return@addOnCompleteListener
                }
                Toast.makeText(this, "Logged in", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }

        getCharacters()

        main_addCharacter.setOnClickListener {
            startActivity(Intent(this, AddCharacter::class.java))
        }
    }

    private fun getCharacters() {
        val ref = FirebaseFirestore.getInstance().collection("characters")
        ref.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }
            val adapter = GroupAdapter<ViewHolder>()
            snapshot?.forEach {
                val char = it.toObject(Character::class.java)

                Log.d("ForEach", char.toString())
                adapter.add(CharacterItem(this, char))
            }

            main_recycler.adapter = adapter
        }
    }

}

class CharacterItem(var context: Context, var char: Character) : Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        Log.d("TEST", char.name)
        viewHolder.itemView.characterRow_name.text = char.name
        viewHolder.itemView.characterRow_pLevel.text = char.pLevel.toString()
        viewHolder.itemView.setOnClickListener {
            Toast.makeText(context, "Clicked ${char.name}", Toast.LENGTH_SHORT).show()
        }
        viewHolder.itemView.characterRow_deleteBtn.setOnClickListener {
            FirebaseFirestore.getInstance().collection("characters").document(char.id).delete()
        }
    }

    override fun getLayout(): Int {
        return R.layout.character_row
    }
}