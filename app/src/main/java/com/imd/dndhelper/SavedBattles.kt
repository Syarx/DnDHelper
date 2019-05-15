package com.imd.dndhelper

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_saved_battles.*
import kotlinx.android.synthetic.main.battle_row.view.*

class SavedBattles : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_saved_battles)
		getBattles()
	}

	private fun getBattles() {
		val ref = FirebaseFirestore.getInstance().collection("saved_battles")
		val adapter = GroupAdapter<ViewHolder>()

		ref.addSnapshotListener { snapshot, e ->
			adapter.clear()
			if (e != null) {
				Log.w(MainActivity.TAG, "Listen failed.", e)
				return@addSnapshotListener
			}
			snapshot?.forEach {
				val battle = it.toObject(Battle::class.java)
//				Log.d("SavedBattle", battle.toString())
				adapter.add(BattleItem(this, battle))
			}
			savedBattle_recycler.adapter = adapter
			return@addSnapshotListener
		}
	}
}

class BattleItem(var context :Context, var battle: Battle) : Item<ViewHolder>() {
	override fun bind(viewHolder: ViewHolder, position: Int) {
		Log.d("SavedBattle", "Test")
		Log.d("SavedBattle", battle.getTitle())
		viewHolder.itemView.battleRow_name.text = battle.getTitle()
	}
	override fun getLayout(): Int {
		return R.layout.battle_row
	}
}