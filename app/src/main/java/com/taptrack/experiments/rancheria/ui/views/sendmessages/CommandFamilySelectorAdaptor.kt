package com.taptrack.experiments.rancheria.ui.views.sendmessages

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.taptrack.experiments.rancheria.R
import com.taptrack.experiments.rancheria.business.CommandFamilyOption
import com.taptrack.experiments.rancheria.ui.getColorResTintedDrawable
import com.taptrack.experiments.rancheria.ui.inflateChildren
import com.taptrack.experiments.rancheria.ui.views.SimpleListRvAdapter
import org.jetbrains.anko.find


interface CommandFamilySelectedListener {
    fun commandFamilySelected(family: CommandFamilyOption)
}

data class SelectableFamilyOption(val family: CommandFamilyOption,val isSelected: Boolean)

class CommandFamilySelectorAdaptor(
        val listener: CommandFamilySelectedListener) : SimpleListRvAdapter<SelectableFamilyOption, CommandFamilySelectorAdaptor.CommandFamilyVh>() {
    override fun areItemsTheSame(oldItem: SelectableFamilyOption, newItem: SelectableFamilyOption): Boolean = oldItem.family.id == newItem.family.id

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommandFamilyVh {
        return CommandFamilyVh(parent.inflateChildren(R.layout.command_family_option,false))
    }

    override fun onBindViewHolder(holder: CommandFamilyVh, item: SelectableFamilyOption) {
        holder.bind(item)
    }

    override fun areContentsTheSame(oldItem: SelectableFamilyOption, newItem: SelectableFamilyOption): Boolean = oldItem.isSelected == newItem.isSelected

    inner class CommandFamilyVh(rootView: View) : RecyclerView.ViewHolder(rootView) {
        val context = rootView.context
        val icon = rootView.find<ImageView>(R.id.iv_icon)
        val text = rootView.find<TextView>(R.id.tv_title)

        var option: SelectableFamilyOption? = null

        init {
            rootView.setOnClickListener {
                val localOption = option
                if(localOption!= null) {
                    this@CommandFamilySelectorAdaptor.listener.commandFamilySelected(localOption.family)
                }
            }
        }

        fun bind(option: SelectableFamilyOption) {
            val color = if (option.isSelected) R.color.colorAccent else R.color.colorUnselected

            icon?.setImageDrawable(context.getColorResTintedDrawable(option.family.imageRes,color))
            text?.setTextColor(ContextCompat.getColor(context,color))
            text?.setText(option.family.descriptionRes)

            this.option = option
        }
    }
}