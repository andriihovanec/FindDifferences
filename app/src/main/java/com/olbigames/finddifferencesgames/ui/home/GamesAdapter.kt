package com.olbigames.finddifferencesgames.ui.home

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.olbigames.finddifferencesgames.R
import com.olbigames.finddifferencesgames.db.GameEntity
import kotlinx.android.synthetic.main.item_level.view.*

class GamesAdapter(private val itemClickListener: OnItemClickListener) : RecyclerView.Adapter<GamesAdapter.GamesViewHolder>() {

    private var sourceList: ArrayList<GameEntity> = ArrayList()

    fun setupGames(games: List<GameEntity>) {
        sourceList.addAll(games)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GamesViewHolder {
        val rowView = LayoutInflater.from(parent.context).inflate(R.layout.item_level, parent, false)
        return GamesViewHolder(rowView)
    }

    override fun getItemCount(): Int {
        return sourceList.count()
    }

    override fun onBindViewHolder(holder: GamesViewHolder, position: Int) {
        holder.bind(sourceList[position], itemClickListener)
    }

    class GamesViewHolder(val rowView: View) : RecyclerView.ViewHolder(rowView) {
        fun bind(game: GameEntity, clickListener: OnItemClickListener) {
            rowView.level_image_imageview.setImageURI(Uri.parse(game.uri))
            rowView.level_textview.text = "${rowView.context.resources.getString(R.string.level)}${game.level}"

            rowView.setOnClickListener {
                clickListener.onItemClicked(game)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClicked(game: GameEntity)
    }
}