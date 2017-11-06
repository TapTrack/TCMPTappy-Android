package com.taptrack.experiments.rancheria.ui.views.testsendmessages

import android.content.Context
import android.content.res.Configuration
import android.support.annotation.StringRes
import android.support.v7.util.DiffUtil
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.taptrack.experiments.rancheria.R
import com.taptrack.experiments.rancheria.business.TappyService
import com.taptrack.experiments.rancheria.ui.inflateChildren
import com.taptrack.tcmptappy.tcmp.TCMPMessage
import com.taptrack.tcmptappy.tcmp.commandfamilies.basicnfc.PollingModes
import com.taptrack.tcmptappy.tcmp.commandfamilies.basicnfc.commands.*
import com.taptrack.tcmptappy.tcmp.commandfamilies.systemfamily.commands.GetBatteryLevelCommand
import com.taptrack.tcmptappy.tcmp.commandfamilies.systemfamily.commands.PingCommand
import com.taptrack.tcmptappy2.tcmpconverter.TcmpConverter
import org.jetbrains.anko.find
import org.jetbrains.anko.textResource
import java.util.*

private data class CommandOption(@StringRes val descriptionRes: Int, val message: TCMPMessage);

class SendTcmpView : RecyclerView {
    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        init(context)
    }

    private fun init(context: Context) {
        val screenSize = resources?.configuration?.screenLayout?.and(Configuration.SCREENLAYOUT_SIZE_MASK)
        var columnCount = 2
        when(screenSize) {
            Configuration.SCREENLAYOUT_SIZE_LARGE -> columnCount = 3
            Configuration.SCREENLAYOUT_SIZE_XLARGE -> columnCount = 4
            Configuration.SCREENLAYOUT_SIZE_SMALL -> columnCount = 1
        }
        layoutManager = GridLayoutManager(context,columnCount)
        val sendAdapter = SendTcmpAdapter()
        adapter = sendAdapter
        sendAdapter.updateContents(COMMAND_OPTIONS)
    }


    companion object {
        private val COMMAND_OPTIONS: List<CommandOption> = listOf(
            CommandOption(R.string.desc_get_battery_level,GetBatteryLevelCommand()),
            CommandOption(R.string.desc_ping_command,PingCommand()),
            CommandOption(R.string.desc_stop_command,StopCommand()),
            CommandOption(R.string.desc_scan_ndef_5_seconds,ScanNdefCommand(5,PollingModes.MODE_GENERAL)),
            CommandOption(R.string.desc_scan_ndef_indefinite,ScanNdefCommand(0,PollingModes.MODE_GENERAL)),
            CommandOption(R.string.desc_stream_ndef_5_seconds,StreamNdefCommand(5,PollingModes.MODE_GENERAL)),
            CommandOption(R.string.desc_stream_ndef_indefinite,StreamNdefCommand(0,PollingModes.MODE_GENERAL)),
            CommandOption(R.string.desc_scan_tag_5_seconds,ScanTagCommand(5,PollingModes.MODE_GENERAL)),
            CommandOption(R.string.desc_scan_tag_indefinitely,ScanTagCommand(0,PollingModes.MODE_GENERAL)),
            CommandOption(R.string.desc_stream_tag_5_seconds,StreamTagsCommand(5,PollingModes.MODE_GENERAL)),
            CommandOption(R.string.desc_stream_tag_indefinitely,StreamTagsCommand(0,PollingModes.MODE_GENERAL))
        )
        
    }
}

private class SendTcmpAdapter : RecyclerView.Adapter<VH>() {
    private var elements: List<CommandOption> = listOf()

    class DiffCb(val newList: List<CommandOption>, val oldList: List<CommandOption>) : DiffUtil.Callback() {

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val newVal = newList[newItemPosition]
            val oldVal = oldList[oldItemPosition]

            return newVal.descriptionRes == oldVal.descriptionRes
        }

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val newVal = newList[newItemPosition]
            val oldVal = oldList[oldItemPosition]

            return newVal.descriptionRes == oldVal.descriptionRes && Arrays.equals(newVal.message.toByteArray(), oldVal.message.toByteArray())
        }
    }

    fun updateContents(newElements: List<CommandOption>) {
        val result = DiffUtil.calculateDiff(DiffCb(newList = newElements,oldList = elements))
        elements = newElements
        result.dispatchUpdatesTo(this)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(elements[position])
    }

    override fun getItemCount(): Int = elements.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(parent.inflateChildren(R.layout.tester_tcmp_send,false))
    }

}

private class VH(private val rootView: View) : RecyclerView.ViewHolder(rootView) {
    private val descriptionView = itemView.find<TextView>(R.id.tv_message_description)
    private val context: Context = rootView.context
    private var currentTcmp: TCMPMessage? = null

    init {
        itemView.setOnClickListener {
            if(currentTcmp != null) {
                TappyService.broadcastSendTcmp(TcmpConverter.toVersionTwo(currentTcmp),context)
            }
        }
    }

    fun bind(option: CommandOption) {
        descriptionView.textResource = option.descriptionRes
        currentTcmp = option.message
    }
}