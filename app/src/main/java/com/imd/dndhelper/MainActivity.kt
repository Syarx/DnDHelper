package com.imd.dndhelper

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
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

        val adapter = getCharacters()

        main_addCharacter.setOnClickListener {
            startActivity(Intent(this, AddCharacter::class.java))
        }
        main_startBattle.setOnClickListener {
            if (CharacterItem.readyCount == 2) {
                val intent = Intent(this, BattleScreen::class.java)

                for (i in 0 until adapter.itemCount) {
                    val charItem = adapter.getItem(i) as CharacterItem
                    intent.putExtra("char$i", charItem.char.id)
                }

                startActivity(intent)
            } else {
                Toast.makeText(this, "Please select exactly 2 characters", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun getCharacters(): GroupAdapter<ViewHolder> {
        val ref = FirebaseFirestore.getInstance().collection("characters")

        val adapter = GroupAdapter<ViewHolder>()
        ref.addSnapshotListener { snapshot, e ->
            adapter.clear()
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }
            snapshot?.forEach {
                val char = it.toObject(Character::class.java)

                Log.d("ForEach", char.toString())
                adapter.add(CharacterItem(this, char))
            }

            main_recycler.adapter = adapter
            return@addSnapshotListener
        }
        return adapter
    }

}

class CharacterItem(var context: Context, var char: Character) : Item<ViewHolder>() {
    companion object {
        var readyCount = 0
    }

    var readyBattle = false
    override fun bind(viewHolder: ViewHolder, position: Int) {
        Log.d("TEST", char.name)
        viewHolder.itemView.characterRow_name.text = char.name
        viewHolder.itemView.characterRow_pLevel.text = char.pLevel
//        viewHolder.itemView.setOnClickListener {
//            Toast.makeText(context, "Clicked ${char.name}", Toast.LENGTH_SHORT).show()
//        }

        viewHolder.itemView.characterRow_deleteBtn.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Delete")
                .setMessage("Are you sure you want to delete?")
                .setPositiveButton("YES") { dialog, which ->
                    FirebaseFirestore.getInstance().collection("characters").document(char.id).delete()
                }
                .setNegativeButton("NO") { dialog, which ->
                }.create().show()
        }
        val battleButton = viewHolder.itemView.characterRow_toBattle
        battleButton.setOnClickListener {
            if (readyBattle) {
                readyBattle = false
                readyCount--
                battleButton.setImageResource(R.drawable.battle_off)
            } else {
                readyBattle = true
                readyCount++
                battleButton.setImageResource(R.drawable.battle_on)
            }
        }
    }

    override fun getLayout(): Int {
        return R.layout.character_row
    }


}