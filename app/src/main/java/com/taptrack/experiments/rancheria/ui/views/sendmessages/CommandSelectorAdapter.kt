package com.taptrack.experiments.rancheria.ui.views.sendmessages

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.taptrack.experiments.rancheria.R
import com.taptrack.experiments.rancheria.business.CommandOption
import com.taptrack.experiments.rancheria.ui.getColorResTintedDrawable
import com.taptrack.experiments.rancheria.ui.inflateChildren
import com.taptrack.experiments.rancheria.ui.views.SimpleListRvAdapter
import org.jetbrains.anko.find
import org.jetbrains.anko.textColor
import org.jetbrains.anko.textResource

interface CommandSelectedListener {
    fun commandSelected(command: CommandOption)
}

class CommandSelectorAdapter(val listener: CommandSelectedListener) : SimpleListRvAdapter<CommandOption, CommandSelectorAdapter.VH>() {
    override fun areItemsTheSame(oldItem: CommandOption, newItem: CommandOption): Boolean = oldItem == newItem

    override fun areContentsTheSame(oldItem: CommandOption, newItem: CommandOption): Boolean = oldItem == newItem

    override fun onBindViewHolder(holder: VH, item: CommandOption) {
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH = VH(parent.inflateChildren(R.layout.command_option,false))

    inner class VH(val rootView: View) : RecyclerView.ViewHolder(rootView) {
        val context = rootView.context
        val iconView = rootView.find<ImageView>(R.id.iv_icon)
        val titleView = rootView.find<TextView>(R.id.iv_title)

        var currentOption: CommandOption? = null

        init {
            rootView.setOnClickListener {
                val localOption = currentOption
                if(localOption != null) {
                    this@CommandSelectorAdapter.listener.commandSelected(localOption)
                }
            }
        }

        fun bind(option: CommandOption) {
            iconView.setImageDrawable(context.getColorResTintedDrawable(option.imageRes,R.color.colorUnselected))
            titleView.textResource = option.titleRes
            titleView.textColor = ContextCompat.getColor(context,R.color.colorUnselected)

            currentOption = option
        }
    }
}
