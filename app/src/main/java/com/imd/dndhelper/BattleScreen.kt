package com.imd.dndhelper

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_battle_screen.*
import java.util.*

class BattleScreen : AppCompatActivity() {
	companion object {
		const val TAG = "BattleScreen"
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_battle_screen)

		if (intent.getStringExtra("From") == "SavedBattles") {
			val battleID = intent.getStringExtra("battleID")
			FirebaseFirestore.getInstance().collection("saved_battles").document(battleID).get()
				.addOnCompleteListener {
					if (it.isSuccessful) {
						val battle = it.result?.toObject(Battle::class.java)
						if (battle != null) {
							title = "${battle.char1?.name} vs ${battle.char2?.name}"
							updateUI(this, battle.char1!!, battle.char2!!, battleID)
						}
					}
				}
		} else {
			val charsIds: ArrayList<String> = intent.getStringArrayListExtra("chars")
			FirebaseFirestore.getInstance().collection("characters").document(charsIds[0]).get()
				.addOnCompleteListener {
					val char1 = it.result?.toObject(Character::class.java)
					FirebaseFirestore.getInstance().collection("characters").document(charsIds[1]).get()
						.addOnCompleteListener { it2 ->
							val char2 = it2.result?.toObject(Character::class.java)
							if (char1 != null && char2 != null) {
								title = "${char1.name} vs ${char2.name}"
								char1.pcPowerLevel()
								char2.pcPowerLevel()
								updateUI(this, char1, char2)
							} else {
								Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
								finish()
							}
						}
				}
		}

	}

	private fun updateUI(battleScreen: BattleScreen, _char1: Character, _char2: Character, battleID: String = "") {
		var char1 = _char1
		var char2 = _char2
		giveStats(char1, char2)
		battle_attack1.setOnClickListener {
			val mMod: Double = battle_left_mMod.text.toString().toDouble()
			val sMod = battle_left_sMod.text.toString().toDouble()
			char2 = startBattle(char1, char2, sMod, mMod)

			giveStats(char1, char2)
		}
		battle_attack2.setOnClickListener {
			val mMod: Double = battle_right_mMod.text.toString().toDouble()
			val sMod = battle_right_sMod.text.toString().toDouble()
			char1 = startBattle(char2, char1, sMod, mMod)

			giveStats(char1, char2)
		}
		battle_left_resetBorg.setOnClickListener {
			char1.resetBorg()
			giveStats(char1, char2)
		}
		battle_right_resetBorg.setOnClickListener {
			char2.resetBorg()
			giveStats(char1, char2)
		}
		battle_left_forEffect.setOnClickListener {
			val mMod: Double = battle_left_mMod.text.toString().toDouble()
			val sMod = battle_left_sMod.text.toString().toDouble()
			val res = char1.isEffective(char1.getAttackPower(sMod, mMod), 1.0, char2)
			giveStats(char1, char2)
			Toast.makeText(this, res, Toast.LENGTH_LONG).show()
		}
		battle_right_forEffect.setOnClickListener {
			val mMod: Double = battle_right_mMod.text.toString().toDouble()
			val sMod = battle_right_sMod.text.toString().toDouble()
			val res = char2.isEffective(char2.getAttackPower(sMod, mMod), 1.0, char1)
			giveStats(char1, char2)
			Toast.makeText(this, res, Toast.LENGTH_LONG).show()
		}
		battle_saveButton.setOnClickListener {
			saveBattle(char1, char2, battleID)
		}
		battle_collide.setOnClickListener {
			val mModLeft: Double = battle_left_mMod.text.toString().toDouble()
			val sModLeft = battle_left_sMod.text.toString().toDouble()
			val mModRight: Double = battle_right_mMod.text.toString().toDouble()
			val sModRight = battle_right_sMod.text.toString().toDouble()
			val result = Character.simultaneousAttack(
				char1.getAttackPower(mModLeft, sModLeft),
				1.0,
				char2.getAttackPower(mModRight, sModRight),
				1.0
			)
			if (result["Winner"] == 1) {
				char2 = char1.attack(result["Power"] as Int, char2)
				giveStats(char1, char2)
			} else {
				char1 = char1.attack(result["Power"] as Int, char1)
				giveStats(char1, char2)
			}
		}
	}

	private fun saveBattle(char1: Character, char2: Character, id: String) {
		var uuid = UUID.randomUUID().toString()
		if (id != "") {
			uuid = id
		}
		FirebaseFirestore.getInstance().collection("saved_battles").document(uuid).set(Battle(char1, char2, uuid))
	}

	private fun startBattle(attacker: Character, defender: Character, sMod: Double, mMod: Double): Character {
		Toast.makeText(this, "Triforce ${attacker.getAttackPower(mMod, sMod)}", Toast.LENGTH_LONG).show()
		return attacker.attack(attacker.getAttackPower(mMod, sMod), 1.0, defender) //returns defender
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
