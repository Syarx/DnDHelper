package com.imd.dndhelper

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_battle_screen.*

class BattleScreen : AppCompatActivity() {
	companion object {
		const val TAG = "BattleScreen"
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_battle_screen)
		val charsIds: ArrayList<String> = intent.getStringArrayListExtra("chars")
		FirebaseFirestore.getInstance().collection("characters").document(charsIds[0]).get()
			.addOnCompleteListener {
				var char1 = it.result?.toObject(Character::class.java)
				FirebaseFirestore.getInstance().collection("characters").document(charsIds[1]).get()
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
//            }


			}

	}

	private fun startBatte(char1: Character, char2: Character) {
		char1.pcPowerLevel()
		battle_left_name.text = char1.name
		battle_left_pLevel.text = char1.pLevel.toString()
		battle_left_health.text = char1.hp.toString()
		battle_left_mana.text = char1.mana.toString()

		//right
		char2.pcPowerLevel()
		battle_right_name.text = char2.name
		battle_right_pLevel.text = char2.pLevel.toString()
		battle_right_health.text = char2.hp.toString()
		battle_right_mana.text = char2.mana.toString()

	}
}
