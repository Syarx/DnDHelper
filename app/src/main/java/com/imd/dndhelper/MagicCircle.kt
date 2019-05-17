package com.imd.dndhelper

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_magic_circle.*
import kotlinx.android.synthetic.main.character_row_circle.view.*

class MagicCircle : AppCompatActivity() {
	companion object {
		const val TAG = "MagicCircle"
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_magic_circle)
		CharacterItem.checkedChars.clear()
		getCharacters()

		circle_calculate.setOnClickListener {
			if (CharacterItem.checkedChars.count() < 2) {
				Toast.makeText(this, "Please select more than 2 characters.", Toast.LENGTH_LONG).show()
			} else {
				var circlePlv = 0.0
				val chars = CharacterItem.checkedChars
				chars.forEachIndexed { index, char ->
					circlePlv += if (index == 1) {
						(3 * char.pLevel) / 4
					} else {
						(3 * char.pLevel) / (2 * (index + 2))
					}
				}

				circle_pLevelResult.text = circlePlv.toString()
			}
		}
	}

	private fun getCharacters() {
		val ref = FirebaseFirestore.getInstance().collection("characters")

		val adapter = GroupAdapter<ViewHolder>()
		ref.addSnapshotListener { snapshot, e ->
			adapter.clear()
			if (e != null) {
				Log.w(MainActivity.TAG, "Listen failed.", e)
				return@addSnapshotListener
			}
			snapshot?.forEach {
				val char = it.toObject(Character::class.java)
				char.pcPowerLevel()
				adapter.add(CharacterItem(this, char))

			}

			circle_recycler.adapter = adapter
			return@addSnapshotListener
		}
	}

	private class CharacterItem(var context: Context, var char: Character) : Item<ViewHolder>() {
		companion object {
			var checkedChars: ArrayList<Character> = ArrayList()
		}

		override fun bind(viewHolder: ViewHolder, position: Int) {
			viewHolder.itemView.circle_name.text = char.name
			viewHolder.itemView.circle_pLevel.text = char.pLevel.toString()
			viewHolder.itemView.circle_check.setOnCheckedChangeListener { view, isChecked ->
				if (isChecked) {
					checkedChars.add(char)
				} else {
					checkedChars.remove(char)
				}
			}
		}

		override fun getLayout(): Int {
			return R.layout.character_row_circle
		}
	}
}
