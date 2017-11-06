package com.taptrack.experiments.rancheria.ui.views.viewmessages

import android.content.Context
import android.graphics.Rect
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.taptrack.experiments.rancheria.R
import com.taptrack.experiments.rancheria.business.CommandDataSource
import com.taptrack.experiments.rancheria.business.TappyService
import com.taptrack.experiments.rancheria.model.RealmTcmpCommunique
import com.taptrack.experiments.rancheria.ui.views.getHostActivity
import com.taptrack.experiments.rancheria.ui.views.sendmessages.ConfigureCommandDialogFragment
import com.taptrack.experiments.rancheria.ui.views.sendmessages.DialogGenerator
import com.taptrack.tcmptappy.tcmp.MalformedPayloadException
import com.taptrack.tcmptappy.tcmp.RawTCMPMessage
import com.taptrack.tcmptappy.tcmp.TCMPMessage
import com.taptrack.tcmptappy.tcmp.TCMPMessageParseException
import com.taptrack.tcmptappy.tcmp.commandfamilies.basicnfc.BasicNfcCommandLibrary
import com.taptrack.tcmptappy.tcmp.commandfamilies.mifareclassic.MifareClassicCommandLibrary
import com.taptrack.tcmptappy.tcmp.commandfamilies.systemfamily.SystemCommandLibrary
import com.taptrack.tcmptappy.tcmp.commandfamilies.type4.Type4CommandLibrary
import com.taptrack.tcmptappy.tcmp.common.CommandCodeNotSupportedException
import com.taptrack.tcmptappy.tcmp.common.CommandFamilyMessageResolver
import com.taptrack.tcmptappy.tcmp.common.FamilyCodeNotSupportedException
import com.taptrack.tcmptappy.tcmp.common.ResponseCodeNotSupportedException
import com.taptrack.tcmptappy2.tcmpconverter.TcmpConverter
import io.realm.OrderedRealmCollection
import io.realm.Realm
import io.realm.RealmRecyclerViewAdapter
import io.realm.Sort
import org.jetbrains.anko.dip
import org.jetbrains.anko.find
import timber.log.Timber
import java.text.DateFormat
import java.util.*


class ViewMessagesView : RecyclerView {
    var realm: Realm? = null

    inner class VerticalSpaceItemDecoration(private val verticalSpaceHeight: Int) : RecyclerView.ItemDecoration() {

        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView,
                           state: RecyclerView.State) {
            outRect.bottom = verticalSpaceHeight
        }
    }

    val newItemScroller = object : RecyclerView.AdapterDataObserver() {
        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            super.onItemRangeInserted(positionStart, itemCount)
            val llManager = this@ViewMessagesView.layoutManager as? LinearLayoutManager
            if(llManager != null) {
                val pos = llManager.findFirstVisibleItemPosition()
                if(pos != RecyclerView.NO_POSITION && pos < 2) {
                    this@ViewMessagesView.scrollToPosition(0)
                }
            }

            // fuzz the inserts so the surrounding items know to update
//            val endex = positionStart+itemCount
//
//            if(this@ViewMessagesView.adapter.itemCount > (endex)) {
//                this@ViewMessagesView.adapter.notifyItemRangeChanged(endex,1)
//            }
//
//            val startEx = positionStart - 1
//            if((startEx) > 0) {
//                this@ViewMessagesView.adapter.notifyItemRangeChanged(startEx,1)
//            }

            // There are some issues with properly tracking updates, so doing this sadly
            this@ViewMessagesView.adapter.notifyDataSetChanged()
        }
    }

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
        val act = getHostActivity()
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        layoutManager.stackFromEnd = true
        layoutManager.reverseLayout = true
        this.layoutManager = layoutManager
        addItemDecoration(VerticalSpaceItemDecoration(dip(4)))
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        realm = Realm.getDefaultInstance()
        adapter = MessageAdapter(
                this,
                realm?.where(RealmTcmpCommunique::class.java)?.findAllSorted("messageTime", Sort.DESCENDING),
                true
        )
        adapter.registerAdapterDataObserver(newItemScroller)
        scrollToPosition(0)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        realm?.close()
        realm = null

        adapter.unregisterAdapterDataObserver(newItemScroller)
        adapter = null
    }
}

private class MessageAdapter(private val hostView: RecyclerView, data: OrderedRealmCollection<RealmTcmpCommunique>?, autoUpdate: Boolean) :
        RealmRecyclerViewAdapter<RealmTcmpCommunique, MessageAdapter.VH>(data, autoUpdate) {

    private val commandDataSource: CommandDataSource

    init {
        setHasStableIds(true)
        commandDataSource = CommandDataSource(hostView.context)
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position) as? RealmTcmpCommunique
        if (item?.isCommand ?: false) {
            return TYPE_COMMAND
        } else {
            return TYPE_RESPONSE
        }
    }

    override fun getItemId(index: Int): Long {
        val uuidStr = getItem(index)?.communiqueId
        if(uuidStr != null) {
            val uuid = UUID.fromString(uuidStr)
            return uuid.mostSignificantBits xor uuid.leastSignificantBits
        } else {
            return 0
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): VH =
        when(viewType) {
            TYPE_RESPONSE -> ResponseVH.inflate(parent!!)
            TYPE_COMMAND -> CommandVH.inflate(commandDataSource, parent!!)
            else -> {
                throw IllegalArgumentException()
            }
        }


    override fun onBindViewHolder(holder: VH?, position: Int) {
        val item = getItem(position) ?: return

        when(holder) {
            is ResponseVH -> {
                var shouldShowName = false
                if (position == 0) {
                    // we're the last item
                    shouldShowName = true
                } else {
                    val next = getItem(position - 1)
                    if(next == null) {
                        shouldShowName = true
                    } else if(item.deviceId == null || next.deviceId == null){
                        shouldShowName = true
                    } else {
                        shouldShowName = item.deviceId != next.deviceId
                    }
                }

                holder.bind(item,shouldShowName)
            }
            is CommandVH -> {
                holder.bind(item)
            }
        }
    }

    abstract class VH constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        companion object {
            val resolver: CommandFamilyMessageResolver = CommandFamilyMessageResolver()

            val formatter: DateFormat

            init {
                resolver.registerCommandLibrary(SystemCommandLibrary())
                resolver.registerCommandLibrary(BasicNfcCommandLibrary())
                resolver.registerCommandLibrary(Type4CommandLibrary())
                resolver.registerCommandLibrary(MifareClassicCommandLibrary())

                formatter = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,DateFormat.MEDIUM)
            }

        }
    }

    class CommandVH(val commandDataSource: CommandDataSource, val rootView: View) : VH(rootView) {
        val ctx = rootView.context
        val messageView: TextView = rootView.find<TextView>(R.id.tv_message_content) as TextView
        val timeView: TextView = rootView.find<TextView>(R.id.tv_caption) as TextView

        var currentMessage: ByteArray? = null
        var currentTime: Long? = null

        var currentCommand: TCMPMessage? = null

        init {
            rootView.setOnClickListener {
                rootView.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                if(currentCommand != null) {
                    TappyService.broadcastSendTcmp(TcmpConverter.toVersionTwo(currentCommand),ctx)
                }
            }

            rootView.setOnLongClickListener {
                val cmd = currentCommand
                rootView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                if (cmd != null) {
                    val commandOpt = commandDataSource.retrieveCommandOptionForMessage(cmd)
                    if (commandOpt != null) {
                        val act = rootView.getHostActivity()

                        if (act is AppCompatActivity) {
                            val frag = ConfigureCommandDialogFragment.createConfigureCommandFragment(commandOpt)
                            val fm = act.supportFragmentManager
                            frag.show(fm,"configure_tcmp")
                        } else if (act != null) {
                            DialogGenerator.configureCommandAlertDialog(act,commandOpt)?.show()
                        }
                        true
                    } else {
                        false
                    }
                } else {
                    false
                }
            }
        }

        fun bind(communique: RealmTcmpCommunique) {
            if(communique.message != currentMessage) {
                currentCommand = null
                try {
                    val rawMessage = RawTCMPMessage(communique.message)
                    currentCommand = rawMessage
                    currentMessage = communique.message
                    val resolvedMessage = VH.resolver.parseCommand(rawMessage)
                    messageView.text = TcmpMessageDescriptor.getCommandDescription(resolvedMessage,ctx)
                } catch (e : TCMPMessageParseException) {
                    Timber.e(e)
                    messageView.setText(R.string.invalid_command)
                } catch (e : FamilyCodeNotSupportedException){
                    Timber.e(e)
                    messageView.setText(R.string.invalid_command)
                } catch (e : CommandCodeNotSupportedException) {
                    Timber.e(e)
                    messageView.setText(R.string.invalid_command)
                } catch (e : MalformedPayloadException) {
                    Timber.e(e)
                    messageView.setText(R.string.invalid_command)
                }
            }

            if(communique.messageTime != currentTime) {
                timeView.text = VH.formatter.format(Date(communique.messageTime ?: 0))
                currentTime = communique.messageTime
            }

        }

        companion object {
            fun inflate(commandDataSource: CommandDataSource, parent: ViewGroup) : CommandVH {
                val inflater = LayoutInflater.from(parent.context)
                val view = inflater.inflate(R.layout.chat_view_outgoing,parent,false)
                return CommandVH(commandDataSource, view)
            }
        }
    }

    class ResponseVH(val rootView: View) : VH(rootView) {
        val ctx = rootView.context
        val messageView: TextView = rootView.find<TextView>(R.id.tv_message_content)
        val captionView: TextView = rootView.find<TextView>(R.id.tv_caption)

        var currentCommunique: RealmTcmpCommunique? = null
        var isDisplayingName: Boolean = true

        fun bind(communique: RealmTcmpCommunique, displayName: Boolean) {
            currentCommunique = communique
            try {
                val rawMessage = RawTCMPMessage(communique.message)
                val resolvedMessage = VH.resolver.parseResponse(rawMessage)
                messageView.text = TcmpMessageDescriptor.getResponseDescription(resolvedMessage,ctx)
            } catch (e : TCMPMessageParseException) {
                Timber.e(e)
                messageView.setText(R.string.invalid_command)
            } catch (e : FamilyCodeNotSupportedException){
                Timber.e(e)
                messageView.setText(R.string.invalid_command)
            } catch (e : ResponseCodeNotSupportedException) {
                Timber.e(e)
                messageView.setText(R.string.invalid_command)
            } catch (e : MalformedPayloadException) {
                Timber.e(e)
                messageView.setText(R.string.invalid_command)
            }

            captionView.text = communique.deviceName

            if(displayName != isDisplayingName) {
                if(displayName){
                    captionView.visibility = View.VISIBLE
                } else {
                    captionView.visibility = View.GONE
                }
                isDisplayingName = displayName
            }

            rootView.invalidate()
        }


        companion object {
            fun inflate(parent: ViewGroup) : ResponseVH {
                val inflater = LayoutInflater.from(parent.context)
                val view = inflater.inflate(R.layout.chat_view_incoming,parent,false)

                return ResponseVH(view)
            }
        }
    }


    companion object {
        val TYPE_COMMAND = 0
        val TYPE_RESPONSE = 1
    }
}