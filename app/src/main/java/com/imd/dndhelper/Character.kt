package com.imd.dndhelper

import android.util.Log


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
	var magic: Double = 0.0

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
		this.magic = mg
		this.speed = sp
	}

	fun getHp(): Double {
		return 150000 + ((this.pLevel / 2) * 10)
	}

	fun getAttackPower(MagicMultiplier: Int, SpellMultiplier: Int): Double {    //to attack tou kathe spell
		return this.pLevel * MagicMultiplier * SpellMultiplier
	}

	fun Damage(PowerLevel: Int, Triforce: Int, ExtraMultiplier: Int, Borg: Int): IntArray {
		//to damage stin zoi tou pou tha kani ean xtipisi kapion, to ExtraMultipliers bori na eine polloi multipliers opos black/white h super-effective elements
		val result = ((Triforce * ExtraMultiplier - Borg) / PowerLevel).toDouble()
		val damage = intArrayOf(0, 0)                                             //[damage,borg remaining]
		if (result < 0) {
			damage[1] = result.toInt() * PowerLevel
		}
		if (result >= 1 && result < 2) {
			damage[0] = 6000
		} else if (result >= 2 && result < 3) {
			damage[0] = 12000
		} else if (result >= 3 && result < 5) {
			damage[0] = 18000
		} else if (result >= 5 && result < 7) {
			damage[0] = 48000
		} else if (result >= 7 && result < 9) {
			damage[0] = 72000
		} else if (result >= 9 && result < 10) {
			damage[0] = 144000
		} else if (result >= 10) {
			damage[0] = 1000000
		}
		return damage
	}

	override fun toString(): String {
		return "Character(name='$name $pLevel')"
	}
}