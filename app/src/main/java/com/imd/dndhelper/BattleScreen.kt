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

							title = "${char1!!.name} vs ${char2.name}"
							char1!!.pcPowerLevel()
							char2.pcPowerLevel()
							giveStats(char1!!, char2)
							battle_attack1.setOnClickListener {
								val mMod: Double = battle_left_mMod.text.toString().toDouble()
								val sMod = battle_left_sMod.text.toString().toDouble()
								char2 = startBattle(char1!!, char2!!, sMod, mMod)

								giveStats(char1!!, char2!!)
							}
							battle_attack2.setOnClickListener {
								val mMod: Double = battle_right_mMod.text.toString().toDouble()
								val sMod = battle_right_sMod.text.toString().toDouble()
								char1 = startBattle(char2!!, char1!!, sMod, mMod)

								giveStats(char1!!, char2!!)
							}
							battle_left_resetBorg.setOnClickListener {
								char1!!.resetBorg()
								giveStats(char1!!, char2!!)
							}
							battle_right_resetBorg.setOnClickListener {
								char2!!.resetBorg()
								giveStats(char1!!, char2!!)
							}
						} else {
							Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
							finish()
						}
					}
			}
	}

	private fun startBattle(attacker: Character, defender: Character, sMod: Double, mMod: Double): Character {
		Toast.makeText(this, "Triforce ${attacker.getAttackPower(mMod, sMod)}", Toast.LENGTH_LONG).show()
		return attacker.damage(attacker.getAttackPower(mMod, sMod), 1, defender) //returns defender
	}

	private fun giveStats(char1: Character, char2: Character) {
		battle_left_name.text = char1.name
		battle_left_pLevel.text = char1.pLevel.toString()
		battle_left_health.text = char1.hp.toString()
		battle_left_mana.text = char1.mana.toString()
		battle_left_borg.text = char1.borg.toString()
		battle_left_state.text = char1.getState().toString()
		//right
		battle_right_name.text = char2.name
		battle_right_pLevel.text = char2.pLevel.toString()
		battle_right_health.text = char2.hp.toString()
		battle_right_mana.text = char2.mana.toString()
		battle_right_borg.text = char2.borg.toString()
		battle_right_state.text = char2.getState().toString()

	}
}
