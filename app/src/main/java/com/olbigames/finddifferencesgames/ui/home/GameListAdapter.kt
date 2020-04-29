package com.olbigames.finddifferencesgames.ui.home

import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.olbigames.finddifferencesgames.R
import com.olbigames.finddifferencesgames.domain.game.GameEntity
import kotlinx.android.synthetic.main.item_level.view.*

class GameListAdapter(
    private val sourceList: List<GameEntity>,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<GameListAdapter.GamesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GamesViewHolder {
        val rowView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_level, parent, false)
        return GamesViewHolder(rowView)
    }

    override fun getItemCount(): Int {
        return sourceList.count()
    }

    override fun onBindViewHolder(holder: GamesViewHolder, position: Int) {
        holder.bind(sourceList[position], itemClickListener)
    }

    class GamesViewHolder(private val rowView: View) : RecyclerView.ViewHolder(rowView) {
        fun bind(game: GameEntity, clickListener: OnItemClickListener) {
            rowView.level_image_imageview.setImageURI(Uri.parse(game.pathToMainFile))
            rowView.level_textview.text =
                rowView.context.resources.getString(R.string.level, game.level)
            rowView.game_progress_textview.text =
                rowView.context.resources.getString(R.string._0_0, game.foundedCount)
            if (game.foundedCount == 10) {
                rowView.reload_iv.visibility = View.VISIBLE
                rowView.check_iv.visibility = View.VISIBLE
                rowView.level_textview.setTextColor(Color.argb(255, 60, 240, 60))
                rowView.game_progress_textview.setTextColor(Color.argb(255, 60, 240, 60))
            } else {
                rowView.reload_iv.visibility = View.GONE
                rowView.check_iv.visibility = View.GONE
                rowView.level_textview.setTextColor(Color.argb(255, 255, 255, 255))
                rowView.game_progress_textview.setTextColor(Color.argb(255, 255, 255, 255))
            }

            rowView.setOnClickListener {
                clickListener.onItemClicked(game)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClicked(game: GameEntity)
    }
}