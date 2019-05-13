package com.imd.dndhelper

import android.util.Log
import kotlin.math.abs


class Character(
	var name: String = "",
	var id: String = "",
	var p: Double = 50.0,
	var x: Double = 50.0,
	var special: Boolean = false
) {
	var pLevel: Double = 50.0
	var mana: Double = 0.0
	var mRegen: Double = 0.0
	var range: Int = 0
	var physique: Int = 0
	var speed: Int = 0
	var borg: Double = 0.0
	var hp = 0.0
	private var state = State.HEALTHY
	//sublironei ola ta parapano stats

	fun pcPowerLevel() {
		var message: String
		var z = 0.0
		var f = 0.0
		var mana = x * p / 10
		var mrege = x
		val bo: Double
		val ra: Int
		var ph: Int
		val sp: Int
		var mg = 0.0
		if (special) {
			mana = x * (p + 0.5 * p) / 10
			mrege = x + 0.5 * x
		}

		message = "6  Power Level= $x   mana= $mana   mana regen= $mrege\n"

		for (y in 7..20) {
			if (y < 13) {
				x = (x + 0.5 * x).toInt().toDouble()
				if (special) {
					mana = x * (p + 0.5 * p) / (17 - y)
					mrege = x + 0.5 * x
				} else {
					mana = x * p / (17 - y)
					mrege = x
				}
				message += "$y  Power Level= $x   mana= $mana   mana regen= $mrege\n"
				if (y == 12) {
					z = x
				}

			} else if (y in 13..16) {

				z += x
				mana = if (special) {
					z * (p + 0.5 * p) / (17 - y)
				} else {
					z * p / (17 - y)
				}
				message += "$y  Power Level= $z   mana= $mana   mana regen= $mrege\n"
				if (y == 16) {
					f = z
				}
			} else if (y in 17..20) {

				f += p / 100 * x
				mana = if (special) {
					f * (p + 0.5 * p)
				} else {
					f * p
				}
				message += "$y  Power Level= $f   mana= $mana   mana regen= $mrege\n"
			}
		}
		f = 100 * Math.floor((f + 50) / 100)
		if (f > 10000) {
			if (f % 1000 > 200 && f % 1000 < 700) {
				f = 1000 * Math.floor((f - 300) / 1000) + 500
			} else if (f % 1000 > 700) {
				f = 1000 * Math.floor((f + 500) / 1000)
			} else if (f % 1000 < 300) {
				f = 1000 * Math.floor((f - 300) / 1000) + 1000
			}
		}
		mana = 1000 * Math.floor((mana + 500) / 1000)
		if (mana > 100000) {
			mana = 10000 * Math.floor((mana + 5000) / 10000)
		}
		if (mana > 1500000) {
			mana = 100000 * Math.floor((mana + 50000) / 100000) + 100000
		}
		mrege = 10 * Math.floor((mrege + 5) / 10)
		if (mrege > 2000) {
			mrege = 100 * Math.floor((mrege + 50) / 100)
		}
		bo = 100 * Math.floor((f + 50) / 100)
		if (f < 2000) {
			ra = (f / 10).toInt()
			ph = 1 + bo.toInt() / 500
			mg = f / 5
			sp = 1 + bo.toInt() / 500
		} else {
			ra = bo.toInt() / 2
			ph = (5 * Math.pow(2.0, (bo.toInt() / 1000 - 2).toDouble())).toInt()
			ph = (ph * (1 + f % 1000 / 1000)).toInt()
			sp = 1 + bo.toInt() / 500
			if (f <= 10000) {
				for (v in 1..(bo / 1000).toInt() - 2) {
					mg += 0.5
				}
				mg = 100 * Math.floor((f / (5 - mg) + 50) / 100)
			} else {
				mg = 1.0
				for (v in 1..(bo / 1000).toInt() - 10) {
					mg += 0.25
				}
				mg = 100 * Math.floor((f * mg + 50) / 100)
			}
		}

		message += "Power Level= " + f + "   mana= " + mana + "   mana regen= " + mrege + "/1m out of combat   Range= " + ra + "m   Physique= " + ph + "x   Speed= " + sp + "x   Magic= " + mg + "/5s" + "\n"
		Log.d("Character", message)
		this.pLevel = f
		this.mana = mana
		this.mRegen = mrege
		this.range = ra
		this.physique = ph
		this.borg = mg
		this.speed = sp
		this.hp = 150000 + ((this.pLevel / 2) * 10)
	}

	fun calculateState(stateByAttacks: State) {
		val newHp = this.hp - stateByAttacks.getDamage()
		this.hp = newHp
		val stateByHp = when {
			newHp <= 0 -> State.DEAD
			newHp <= 6000 -> State.COMATOSE
			newHp <= 78000 -> State.FAINT
			newHp <= 102000 -> State.DEADLY_INJURED
			newHp <= 132000 -> State.SERIOUSLY_INJURED
			newHp <= 138000 -> State.MULTIPLY_INJURED
			newHp <= 144000 -> State.INJURED
			else -> State.HEALTHY
		}
		if (stateByHp.id >= stateByAttacks.id) {
			this.state = stateByHp
		} else {
			this.state = stateByAttacks
		}
	}

	fun getAttackPower(MagicMultiplier: Double, SpellMultiplier: Double): Double {    //to attack tou kathe spell
		return this.pLevel * MagicMultiplier * SpellMultiplier
	}

	fun damage(triforce: Double, extraMultiplier: Int, opponent: Character): Character {   //to damage stin zoi tou pou tha kani ean xtipisi kapion, to ExtraMultipliers bori na eine polloi multipliers opos black/white h super-effective elements
		val result = ((triforce * extraMultiplier) - opponent.borg) / opponent.pLevel
		//[damage,borg remaining]
		if (result < 0) {
			opponent.borg = abs(result * opponent.pLevel)
		} else {
			opponent.borg = 0.0
		}
		if (result >= 1 && result < 2) {
			opponent.calculateState(State.INJURED)
		} else if (result >= 2 && result < 3) {
			opponent.calculateState(State.MULTIPLY_INJURED)
		} else if (result >= 3 && result < 5) {
			opponent.calculateState(State.SERIOUSLY_INJURED)
		} else if (result >= 5 && result < 7) {
			opponent.calculateState(State.DEADLY_INJURED)
		} else if (result >= 7 && result < 9) {
			opponent.calculateState(State.FAINT)
		} else if (result >= 9 && result < 10) {
			opponent.calculateState(State.COMATOSE)
		} else if (result >= 10) {
			opponent.calculateState(State.DEAD)
		} else {
			opponent.calculateState(State.HEALTHY)
		}
		return opponent
	}

	fun getState(): State {
		return this.state
	}

	override fun toString(): String {
		return "Character(name='$name $pLevel')"
	}
}

enum class State {
	HEALTHY {
		override val id = 0
		override fun getDamage() = 0
		override fun getText() = "No Effect"
	},
	INJURED {
		override val id = 1
		override fun getDamage() = 6000
		override fun getText() = "No Effect"
	},
	MULTIPLY_INJURED {
		override val id = 2
		override fun getDamage() = 12000
		override fun getText() = "Can't channel Spells"
	},
	SERIOUSLY_INJURED {
		override val id = 3
		override fun getDamage() = 18000
		override fun getText() = "Can't channel Spells. Chance of not being able to act"
	},
	DEADLY_INJURED {
		override val id = 4
		override fun getDamage() = 48000
		override fun getText() = "Can't channel Spells. Chance of not being able to act. Get injury every 10 seconds"
	},
	FAINT {
		override val id = 5
		override fun getDamage() = 72000
		override fun getText() = "Faint. No Borg"
	},
	COMATOSE {
		override val id = 6
		override fun getDamage() = 144000
		override fun getText() = "Death If unattended for some time"
	},
	DEAD {
		override val id = 7
		override fun getDamage() = 1000000
		override fun getText() = "Death"
	};

	abstract val id: Int
	abstract fun getDamage(): Int
	abstract fun getText(): String
}