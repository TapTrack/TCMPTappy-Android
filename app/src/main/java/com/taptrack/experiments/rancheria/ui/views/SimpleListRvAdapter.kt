package com.taptrack.experiments.rancheria.ui.views

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView

abstract class SimpleListRvAdapter<T, V : RecyclerView.ViewHolder> : RecyclerView.Adapter<V> {
    private var items : List<T> = listOf()

    inner class DiffCb(val newList: List<T>, val oldList: List<T>) : DiffUtil.Callback() {

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val newVal = newList[newItemPosition]
            val oldVal = oldList[oldItemPosition]

            return this@SimpleListRvAdapter.areItemsTheSame(oldItem = oldVal, newItem = newVal)
        }

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val newVal = newList[newItemPosition]
            val oldVal = oldList[oldItemPosition]

            return this@SimpleListRvAdapter.areContentsTheSame(oldItem = oldVal, newItem = newVal)
        }
    }

    constructor() : super()

    constructor(initialItems: List<T>) : super() {
        items = initialItems
    }

    fun setItems(newItems: List<T>) {
        val result = DiffUtil.calculateDiff(DiffCb(newList = newItems, oldList = items))
        items = newItems
        result.dispatchUpdatesTo(this)
    }

    abstract fun areItemsTheSame(oldItem: T, newItem: T) : Boolean
    abstract fun areContentsTheSame(oldItem: T, newItem: T) : Boolean

    override fun onBindViewHolder(holder: V, position: Int) {
        val item = items.get(position) ?: return
        onBindViewHolder(holder,item)
    }

    abstract fun onBindViewHolder(holder: V, item: T)

    override fun getItemCount(): Int {
        return items.size
    }
}
