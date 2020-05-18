package com.olbigames.finddifferencesgames.ui.home

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.olbigames.finddifferencesgames.R
import com.olbigames.finddifferencesgames.domain.game.GameEntity
import com.olbigames.finddifferencesgames.extension.invisible
import com.olbigames.finddifferencesgames.extension.setCorrectImage
import com.olbigames.finddifferencesgames.extension.visible
import kotlinx.android.synthetic.main.item_level.view.*

class GameListAdapter(
    private val itemClickListener: OnItemClickListener
) : ListAdapter<GameEntity, GameListAdapter.GamesViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GamesViewHolder {
        val rowView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_level, parent, false)
        return GamesViewHolder(rowView)
    }

    override fun onBindViewHolder(holder: GamesViewHolder, position: Int) {
        holder.bind(getItem(position), itemClickListener)
    }

    class GamesViewHolder(private val rowView: View) : RecyclerView.ViewHolder(rowView) {
        fun bind(game: GameEntity, clickListener: OnItemClickListener) {
            rowView.level_image_imageview.setCorrectImage(layoutPosition, game)
            rowView.level_textview.text =
                rowView.context.resources.getString(R.string.level, game.level)
            rowView.game_progress_textview.text =
                rowView.context.resources.getString(R.string._0_0, game.foundedCount)
            if (game.gameCompleted) {
                rowView.reload_iv.visible()
                rowView.check_iv.visible()
                rowView.level_textview.setTextColor(Color.argb(255, 60, 240, 60))
                rowView.game_progress_textview.setTextColor(Color.argb(255, 60, 240, 60))
            } else {
                rowView.reload_iv.invisible()
                rowView.check_iv.invisible()
                rowView.level_textview.setTextColor(Color.argb(255, 255, 255, 255))
                rowView.game_progress_textview.setTextColor(Color.argb(255, 255, 255, 255))
            }

            rowView.setOnClickListener {
                clickListener.onItemClicked(game)
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<GameEntity>() {

        override fun areItemsTheSame(oldItem: GameEntity, newItem: GameEntity): Boolean {
            return oldItem.level == newItem.level
        }

        override fun areContentsTheSame(oldItem: GameEntity, newItem: GameEntity): Boolean {
            return oldItem == newItem
        }
    }

    interface OnItemClickListener {
        fun onItemClicked(game: GameEntity)
    }
}