package com.imd.dndhelper

class Character(var name: String = "", var id: String = "", var pLevel: String = "") {
    var test: String = ""

    init {
        testfun()
    }

    private fun testfun() {
        test = name
    }

    override fun toString(): String {
        return "Character(name='$name $test')"
    }
}