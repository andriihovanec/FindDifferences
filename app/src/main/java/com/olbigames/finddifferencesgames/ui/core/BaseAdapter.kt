package com.olbigames.finddifferencesgames.ui.core

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class BaseAdapter<VH : BaseAdapter.BaseViewHolder> : RecyclerView.Adapter<VH>() {

    var items: ArrayList<Any> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    fun getItem(position: Int): Any {
        return items[position]
    }

    abstract class BaseViewHolder(protected val view: View) : RecyclerView.ViewHolder(view) {
        var item: Any? = null

        protected abstract fun onBind(item: Any)

        fun bind(item: Any) {
            this.item = item
            onBind(item)
        }
    }
}