package ru.tzhack.facegame.nearby.host.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import ru.tzhack.facegame.R
import ru.tzhack.facegame.common.adapter.ViewDataBindingHolder
import ru.tzhack.facegame.data.model.NearbyPlayer
import ru.tzhack.facegame.databinding.ItemPlayerNearbyBinding

class NearbyHostAdapter : RecyclerView.Adapter<NearbyHostAdapter.ViewHolder>() {

    private val playerList = mutableListOf<NearbyPlayer>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder.create(parent)
    override fun getItemCount(): Int = playerList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val playerItem = playerList[position]
        holder.bind(playerItem)
    }

    fun setItems(players: List<NearbyPlayer>) {
        playerList.clear()
        playerList.addAll(players)
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) :
        ViewDataBindingHolder<ItemPlayerNearbyBinding>(DataBindingUtil.bind(view)!!) {

        companion object {
            fun create(parent: ViewGroup) = ViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_player_nearby, parent, false)
            )
        }

        fun bind(player: NearbyPlayer) {
            dataBinding.viewModel = player
            dataBinding.executePendingBindings()
        }
    }
}