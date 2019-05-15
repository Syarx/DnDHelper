package com.imd.dndhelper

import java.io.Serializable

class Battle(var char1: Character? = null, var char2: Character? = null, var battleID: String = "") : Serializable {

	fun getTitle(): String {
		return "${char1!!.name} vs ${char2!!.name}"
	}

	override fun toString(): String {
		return "Battle(char1=$char1, char2=$char2, battleID='$battleID')"
	}

}