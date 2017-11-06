package com.taptrack.experiments.rancheria.ui.views.sendmessages

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.constraint.ConstraintLayout
import android.support.design.widget.TextInputLayout
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.SwitchCompat
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.taptrack.experiments.rancheria.R
import com.taptrack.experiments.rancheria.business.CommandDataSource
import com.taptrack.experiments.rancheria.business.CommandOption
import com.taptrack.experiments.rancheria.business.TappyService
import com.taptrack.experiments.rancheria.ui.hexStringToByteArray
import com.taptrack.experiments.rancheria.ui.isTextValidHex
import com.taptrack.tcmptappy.tappy.constants.NdefUriCodes
import com.taptrack.tcmptappy.tcmp.TCMPMessage
import com.taptrack.tcmptappy.tcmp.commandfamilies.basicnfc.PollingModes
import com.taptrack.tcmptappy.tcmp.commandfamilies.basicnfc.commands.*
import com.taptrack.tcmptappy.tcmp.commandfamilies.mifareclassic.KeySetting
import com.taptrack.tcmptappy.tcmp.commandfamilies.mifareclassic.commands.DetectMifareClassicCommand
import com.taptrack.tcmptappy.tcmp.commandfamilies.mifareclassic.commands.ReadMifareClassicCommand
import com.taptrack.tcmptappy.tcmp.commandfamilies.type4.commands.DetectType4BCommand
import com.taptrack.tcmptappy.tcmp.commandfamilies.type4.commands.DetectType4BSpecificAfiCommand
import com.taptrack.tcmptappy.tcmp.commandfamilies.type4.commands.DetectType4Command
import com.taptrack.tcmptappy.tcmp.commandfamilies.type4.commands.TransceiveApduCommand
import com.taptrack.tcmptappy2.tcmpconverter.TcmpConverter
import org.jetbrains.anko.*
import kotlin.reflect.full.createInstance


private interface ConfirmListener {
    fun didConfirm(v: View): Boolean
}

object DialogGenerator {
    private val NDEF_OPTIONS = listOf(ScanNdefCommand::class.java, StreamNdefCommand::class.java)
    private val SCAN_OPTIONS = listOf(ScanTagCommand::class.java, StreamTagsCommand::class.java)
    private val TYPE4_OPTIONS = listOf(DetectType4Command::class.java, DetectType4BCommand::class.java, DetectType4BSpecificAfiCommand::class.java)

    fun configureCommandAlertDialog(act: Activity, option: CommandOption): Dialog? {
        val d = AlertDialog.Builder(act, R.style.AppTheme_Dialog)
        val configuration = getConfigurationView(d.context,option)

        if (configuration == null) {
            return null
        } else {
            val dialog = d.setCancelable(true)
                    .setView(configuration.first)
                    .setNegativeButton(act.getString(android.R.string.cancel)
                    ) { dialog, which -> dialog.dismiss() }
                    .setPositiveButton(act.getString(android.R.string.ok),null)
                    .create()
            dialog.setOnShowListener {
                val b = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                b?.setOnClickListener {
                    if (configuration.second.didConfirm(configuration.first)) {
                        dialog.dismiss()
                    }
                }
            }

            return dialog

        }
    }

    private fun wrapInConstraintLayout(ctx: Context, @LayoutRes layoutInt: Int): View {
        // this is a strange hack to get the dialog to behave in an acceptable (although not ideal) fashion
        val rl = RelativeLayout(ctx)
        rl.layoutParams = RelativeLayout.LayoutParams(matchParent, wrapContent)
        rl.padding = ctx.dip(16)

        val cl = ConstraintLayout(ctx)
        val clp = RelativeLayout.LayoutParams(matchParent, wrapContent)
        cl.layoutParams =clp
        val inf = LayoutInflater.from(ctx)
        inf.inflate(layoutInt,cl)
        rl.addView(cl)
        return rl
    }

    private fun applyTimeoutListener(ctx: Context, v: View) {
        val valueDesc = v.findOptional<TextView>(R.id.tv_timeout_value)
        val sb = v.findOptional<SeekBar>(R.id.sb_timeout_selection)

        if (valueDesc == null || sb == null) {
            return
        }

        val listener = object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (progress >= 10) {
                    valueDesc.text = ctx.getString(R.string.parameter_value_infinite)
                } else {
                    val adjusted = progress + 1
                    valueDesc.text = ctx.getString(R.string.parameter_value_seconds,adjusted)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        }

        listener.onProgressChanged(sb,sb.progress,false)

        sb.setOnSeekBarChangeListener(listener)
    }

    private fun makeNdefDialogPair(ctx: Context, option: CommandOption): Pair<View, ConfirmListener>? {
        val cl = wrapInConstraintLayout(ctx, R.layout.timeout_continuous_command_data_view)

        cl.findOptional<TextView>(R.id.tv_command_title)?.textResource = option.titleRes
        cl.findOptional<TextView>(R.id.tv_command_description)?.textResource = option.descriptionRes

        applyTimeoutListener(ctx,cl)

        val listener = object : ConfirmListener {
            override fun didConfirm(v: View): Boolean {
                try {
                    val timeout = 1 + (cl.findOptional<SeekBar>(R.id.sb_timeout_selection)?.progress ?: 10)
                    val stream = cl.findOptional<SwitchCompat>(R.id.swc_continuous)?.isChecked ?: false

                    val timeoutAdjusted = if (timeout < 0 ) 0 else if (timeout == 11) 0 else timeout

                    if (stream) {
                        val message = StreamNdefCommand(timeoutAdjusted.toByte(), PollingModes.MODE_GENERAL)
                        TappyService.broadcastSendTcmp(TcmpConverter.toVersionTwo(message),ctx)
                    } else {
                        val message = ScanNdefCommand(timeoutAdjusted.toByte(), PollingModes.MODE_GENERAL)
                        TappyService.broadcastSendTcmp(TcmpConverter.toVersionTwo(message),ctx)
                    }

                } catch (ignored: Exception) {
                    // cant instantiate
                }
                return true
            }
        }

        return Pair(cl, listener)
    }

    private fun makeScanDialogPair(ctx: Context, option: CommandOption): Pair<View, ConfirmListener>? {
        val cl = wrapInConstraintLayout(ctx, R.layout.timeout_continuous_command_data_view)

        cl.findOptional<TextView>(R.id.tv_command_title)?.textResource = option.titleRes
        cl.findOptional<TextView>(R.id.tv_command_description)?.textResource = option.descriptionRes

        applyTimeoutListener(ctx,cl)

        val listener = object : ConfirmListener {
            override fun didConfirm(v: View): Boolean {
                try {
                    val timeout = 1 + (cl.findOptional<SeekBar>(R.id.sb_timeout_selection)?.progress ?: 10)
                    val stream = cl.findOptional<SwitchCompat>(R.id.swc_continuous)?.isChecked ?: false

                    val timeoutAdjusted = if (timeout < 0 ) 0 else if (timeout == 11) 0 else timeout

                    if (stream) {
                        val message = StreamTagsCommand(timeoutAdjusted.toByte(), PollingModes.MODE_GENERAL)
                        TappyService.broadcastSendTcmp(TcmpConverter.toVersionTwo(message),ctx)
                    } else {
                        val message = ScanTagCommand(timeoutAdjusted.toByte(), PollingModes.MODE_GENERAL)
                        TappyService.broadcastSendTcmp(TcmpConverter.toVersionTwo(message),ctx)
                    }

                } catch (ignored: Exception) {
                    // cant instantiate
                }
                return true
            }
        }

        return Pair(cl, listener)
    }

    private fun makeType4DialogPair(ctx: Context, option: CommandOption): Pair<View, ConfirmListener>? {
        val cl = wrapInConstraintLayout(ctx, R.layout.timeout_modulation_afi_command_data_view)

        cl.findOptional<TextView>(R.id.tv_command_title)?.textResource = option.titleRes
        cl.findOptional<TextView>(R.id.tv_command_description)?.textResource = option.descriptionRes

        applyTimeoutListener(ctx,cl)

        val afiIcon = cl.findOptional<ImageView>(R.id.iv_afi_icon)
        val afiLabel = cl.findOptional<TextView>(R.id.tv_afi_label)
        val afiTil = cl.findOptional<TextInputLayout>(R.id.til_afi_container)
        val afiEt = cl.findOptional<EditText>(R.id.et_afi)

        cl.findOptional<SwitchCompat>(R.id.swc_modulation)?.setOnCheckedChangeListener { buttonView, isChecked ->
            val afiVisibility: Int
            if (isChecked) {
                afiVisibility = View.VISIBLE
            } else {
                afiVisibility = View.GONE
            }

            afiIcon?.visibility = afiVisibility
            afiLabel?.visibility = afiVisibility
            afiTil?.visibility = afiVisibility
        }

        if (afiEt != null && afiTil != null) {
            afiEt.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (!(s?.toString()?.trim()?.isTextValidHex() ?: true)) {
                        afiTil.error = ctx.getString(R.string.error_must_be_hex_byte)
                    } else {
                        afiTil.error = null
                    }
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            })
        }

        val listener = object : ConfirmListener {
            override fun didConfirm(v: View): Boolean {
                try {
                    val timeout = 1 + (cl.findOptional<SeekBar>(R.id.sb_timeout_selection)?.progress ?: 10)
                    val isTypeB = cl.findOptional<SwitchCompat>(R.id.swc_modulation)?.isChecked ?: false

                    val timeoutAdjusted = if (timeout < 0 ) 0 else if (timeout == 11) 0 else timeout

                    if (isTypeB) {
                        val afi = cl.findOptional<EditText>(R.id.et_afi)?.text?.toString()?.trim() ?: ""

                        if (afi == "") {
                            val message = DetectType4BCommand(timeoutAdjusted.toByte())
                            TappyService.broadcastSendTcmp(TcmpConverter.toVersionTwo(message),ctx)
                        } else if (afi.isTextValidHex()) {
                            val ba = afi.hexStringToByteArray()
                            val message = DetectType4BSpecificAfiCommand(timeoutAdjusted.toByte(),ba[0])
                            TappyService.broadcastSendTcmp(TcmpConverter.toVersionTwo(message),ctx)
                            return true
                        } else {
                            return false
                        }
                    } else {
                        val message = DetectType4Command(timeoutAdjusted.toByte())
                        TappyService.broadcastSendTcmp(TcmpConverter.toVersionTwo(message),ctx)
                    }

                } catch (ignored: Exception) {
                    // cant instantiate
                }
                return true
            }
        }

        return Pair(cl, listener)
    }


    private fun getConfigurationView(ctx: Context, option: CommandOption): Pair<View, ConfirmListener>? {
        // this is super inefficient
        val commands = option.clazzes

        var command: Class<out TCMPMessage>? = null
        if (commands.isEmpty()) {
            return null
        } else if (commands.size > 1) {
            if (commands.size == 2) {
                if (commands.containsAll(NDEF_OPTIONS)) {
                    return makeNdefDialogPair(ctx,option)
                } else if (commands.containsAll(SCAN_OPTIONS)) {
                    return makeScanDialogPair(ctx,option)
                } else {
                    command = commands[0]
                }
            } else if (commands.size ==3) {
                if (commands.containsAll(TYPE4_OPTIONS)) {
                    return makeType4DialogPair(ctx,option)
                } else {
                    command = commands[0]
                }
            } else {
                command = commands[0]
            }
        } else {
            command = commands[0]
        }

        when (command) {
            WriteNdefUriRecordCommand::class.java -> {
                val cl = wrapInConstraintLayout(ctx, R.layout.timeout_message_command_data_view)

                cl.findOptional<TextView>(R.id.tv_message_label)?.textResource = R.string.parameter_label_url
                cl.findOptional<ImageView>(R.id.iv_message_icon)?.imageResource = R.drawable.ic_link_black_24dp
                cl.findOptional<EditText>(R.id.et_message)?.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_URI

                cl.findOptional<TextView>(R.id.tv_command_title)?.textResource = option.titleRes
                cl.findOptional<TextView>(R.id.tv_command_description)?.textResource = option.descriptionRes

                applyTimeoutListener(ctx,cl)

                val listener = object : ConfirmListener {
                    override fun didConfirm(v: View): Boolean {
                        try {
                            val rawTimeout = 1 + (cl.findOptional<SeekBar>(R.id.sb_timeout_selection)?.progress ?: 10)
                            val uri = cl.findOptional<EditText>(R.id.et_message)?.text?.toString() ?: ""

                            val timeoutAdjusted = if (rawTimeout < 0 ) 0 else if (rawTimeout == 11) 0 else rawTimeout

                            val uriMessage: WriteNdefUriRecordCommand
                            if (uri.startsWith("https://www.")) {
                                uriMessage = WriteNdefUriRecordCommand(timeoutAdjusted.toByte(),
                                        false,
                                        NdefUriCodes.URICODE_HTTPSWWW,
                                        uri.substring("https://www.".length).toByteArray())
                            } else if (uri.startsWith("http://www.")) {
                                uriMessage = WriteNdefUriRecordCommand(timeoutAdjusted.toByte(),
                                        false,
                                        NdefUriCodes.URICODE_HTTPWWW,
                                        uri.substring("http://www.".length).toByteArray())
                            } else if (uri.startsWith("http://")) {
                                uriMessage = WriteNdefUriRecordCommand(timeoutAdjusted.toByte(),
                                        false,
                                        NdefUriCodes.URICODE_HTTP,
                                        uri.substring("http://".length).toByteArray())
                            } else if (uri.startsWith("https://")) {
                                uriMessage = WriteNdefUriRecordCommand(timeoutAdjusted.toByte(),
                                        false,
                                        NdefUriCodes.URICODE_HTTPS,
                                        uri.substring("https://".length).toByteArray())
                            } else if (uri.startsWith("tel:")) {
                                uriMessage = WriteNdefUriRecordCommand(timeoutAdjusted.toByte(),
                                        false,
                                        NdefUriCodes.URICODE_TEL,
                                        uri.substring("tel:".length).toByteArray())
                            } else if (uri.startsWith("mailto:")) {
                                uriMessage = WriteNdefUriRecordCommand(timeoutAdjusted.toByte(),
                                        false,
                                        NdefUriCodes.URICODE_MAILTO,
                                        uri.substring("mailto:".length).toByteArray())
                            } else {
                                uriMessage = WriteNdefUriRecordCommand(timeoutAdjusted.toByte(),
                                        false,
                                        NdefUriCodes.URICODE_NOPREFIX,
                                        uri.toByteArray())
                            }

                            TappyService.broadcastSendTcmp(TcmpConverter.toVersionTwo(uriMessage),ctx)

                        } catch (ignored: Exception) {
                            // cant instantiate
                        }
                        return true
                    }
                }

                return Pair(cl, listener)
            }
            WriteNdefTextRecordCommand::class.java -> {
                val cl = wrapInConstraintLayout(ctx, R.layout.timeout_message_command_data_view)

                cl.findOptional<TextView>(R.id.tv_command_title)?.textResource = option.titleRes
                cl.findOptional<TextView>(R.id.tv_command_description)?.textResource = option.descriptionRes

                applyTimeoutListener(ctx,cl)

                val listener = object : ConfirmListener {
                    override fun didConfirm(v: View):Boolean {
                        try {
                            val timeout = 1 + (cl.findOptional<SeekBar>(R.id.sb_timeout_selection)?.progress ?: 10)
                            val msg = cl.findOptional<EditText>(R.id.et_message)?.text?.toString() ?: ""

                            val timeoutAdjusted = if (timeout < 0 ) 0 else if (timeout == 11) 0 else timeout

                            val message = WriteNdefTextRecordCommand(timeoutAdjusted.toByte(), false, msg.toByteArray())
                            TappyService.broadcastSendTcmp(TcmpConverter.toVersionTwo(message),ctx)

                        } catch (ignored: Exception) {
                            // cant instantiate
                        }
                        return true
                    }
                }

                return Pair(cl, listener)
            }
            ReadMifareClassicCommand::class.java -> {
                val cl = wrapInConstraintLayout(ctx, R.layout.timeout_byte_range_command_data_view)

                cl.findOptional<TextView>(R.id.tv_command_title)?.textResource = option.titleRes
                cl.findOptional<TextView>(R.id.tv_command_description)?.textResource = option.descriptionRes

                applyTimeoutListener(ctx,cl)

                val startPicker = cl.findOptional<BytePickerView>(R.id.bpv_start_page)
                val endPicker = cl.findOptional<BytePickerView>(R.id.bpv_end_page)

                startPicker?.setOnValueChangedListener { picker, oldVal, newVal ->
                    if (endPicker != null) {
                        val endValue = endPicker.value
                        val difference = newVal - endValue
                        if (difference > 1) {
                            endPicker.value = newVal - 1
                            endPicker.changeCurrentByOne(true)
                        } else if (difference > 0) {
                            endPicker.changeCurrentByOne(true)
                        }
                    }
                }
                
                endPicker?.setOnValueChangedListener { picker, oldVal, newVal -> 
                    if (startPicker != null) {
                        val startValue = startPicker.value

                        val difference = startValue - newVal
                        if (difference > 1) {
                            startPicker.value = newVal + 1
                            startPicker.changeCurrentByOne(false)
                        } else if (difference > 0) {
                            startPicker.changeCurrentByOne(false)
                        }
                    }
                }

                val listener = object : ConfirmListener {
                    override fun didConfirm(v: View):Boolean {
                        try {
                            val timeout = 1 + (cl.findOptional<SeekBar>(R.id.sb_timeout_selection)?.progress ?: 10)

                            val timeoutAdjusted = if (timeout < 0 ) 0 else if (timeout == 11) 0 else timeout

                            val startPage = cl.findOptional<BytePickerView>(R.id.bpv_start_page)?.value ?: 0
                            val endPage = cl.findOptional<BytePickerView>(R.id.bpv_end_page)?.value ?: 0

                            val message = ReadMifareClassicCommand(
                                    timeoutAdjusted.toByte(),
                                    startPage.toByte(),
                                    endPage.toByte(),
                                    KeySetting.KEY_A,
                                    kotlin.ByteArray(6)
                            )
                            TappyService.broadcastSendTcmp(TcmpConverter.toVersionTwo(message),ctx)
                        } catch (ignored: Exception) {
                            // cant instantiate
                        }
                        return true
                    }
                }

                return Pair(cl, listener)
            }
            TransceiveApduCommand::class.java -> {
                val cl = wrapInConstraintLayout(ctx, R.layout.timeout_message_command_data_view)

                cl.findOptional<TextView>(R.id.tv_message_label)?.textResource = R.string.parameter_label_apdu
                cl.findOptional<ImageView>(R.id.iv_message_icon)?.imageResource = R.drawable.ic_apdu_content_black_24dp

                cl.findOptional<TextView>(R.id.tv_command_title)?.textResource = option.titleRes
                cl.findOptional<TextView>(R.id.tv_command_description)?.textResource = option.descriptionRes

                val apduET = cl.findOptional<TextView>(R.id.et_message)
                apduET?.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
                val apduTIL = cl.findOptional<TextInputLayout>(R.id.til_message_container)

                if (apduET != null && apduTIL != null) {
                    apduET.addTextChangedListener(object : TextWatcher {
                        override fun afterTextChanged(s: Editable?) {
                            if (!(s?.toString()?.trim()?.isTextValidHex() ?: true)) {
                                apduTIL.error = ctx.getString(R.string.error_must_be_hex)
                            } else {
                                apduTIL.error = null
                            }
                        }

                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                        }

                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        }
                    })
                }

                val listener = object : ConfirmListener {
                    override fun didConfirm(v: View):Boolean {
                        try {
                            val msg = cl.findOptional<EditText>(R.id.et_message)?.text?.toString()?.trim() ?: ""

                            if (msg.isTextValidHex()) {
                                val message = TransceiveApduCommand(msg.hexStringToByteArray())
                                TappyService.broadcastSendTcmp(TcmpConverter.toVersionTwo(message),ctx)
                                return true
                            } else {
                                return false
                            }
                        } catch (ignored: Exception) {
                            // cant instantiate
                        }
                        return true
                    }
                }

                return Pair(cl, listener)
            }
            LockTagCommand::class.java, DetectMifareClassicCommand::class.java -> {
                val cl = wrapInConstraintLayout(ctx, R.layout.timeout_command_data_view)

                cl.findOptional<TextView>(R.id.tv_command_title)?.textResource = option.titleRes
                cl.findOptional<TextView>(R.id.tv_command_description)?.textResource = option.descriptionRes

                applyTimeoutListener(ctx,cl)

                val listener = object : ConfirmListener {
                    override fun didConfirm(v: View): Boolean {
                        try {
                            val timeout = 1 + (cl.findOptional<SeekBar>(R.id.sb_timeout_selection)?.progress ?: 10)

                            val timeoutAdjusted = if (timeout < 0 ) 0 else if (timeout == 11) 0 else timeout

                            val tcmpMessage: TCMPMessage
                            if (command == LockTagCommand::class.java) {
                                tcmpMessage = LockTagCommand(timeoutAdjusted.toByte(), kotlin.ByteArray(0))
                            } else {
                                tcmpMessage = DetectMifareClassicCommand(timeoutAdjusted.toByte())
                            }
                            TappyService.broadcastSendTcmp(TcmpConverter.toVersionTwo(tcmpMessage),ctx)

                        } catch (ignored: Exception) {
                            // cant instantiate
                        }
                        return true
                    }
                }

                return Pair(cl, listener)
            }
            else -> {
                val cl = wrapInConstraintLayout(ctx, R.layout.noargs_command_data_view)

                cl.findOptional<TextView>(R.id.tv_command_title)?.textResource = option.titleRes
                cl.findOptional<TextView>(R.id.tv_command_description)?.textResource = option.descriptionRes

                val listener = object : ConfirmListener {
                    override fun didConfirm(v: View): Boolean {
                        try {
                            val message = command?.kotlin?.createInstance()
                            if (message != null) {
                                TappyService.broadcastSendTcmp(TcmpConverter.toVersionTwo(message),ctx)
                            }
                        } catch (ignored: Exception) {
                            // cant instantiate
                        }
                        return true
                    }
                }

                return Pair(cl, listener)
            }
        }
    }
}

class ConfigureCommandDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the Builder class for convenient dialog construction
        val builder = AlertDialog.Builder(this.activity)
        val commandId = arguments?.getInt(KEY_COMMAND_ID) ?: -1
        val dataSource = CommandDataSource(this.context)
        val command = dataSource.retrieveCommand(commandId)

        if (command != null) {
            val dialog = DialogGenerator.configureCommandAlertDialog(this.activity, command)
            if (dialog != null) {
                return dialog
            }
        }
        // this should be impossible
        return AlertDialog.Builder(this.activity,R.style.AppTheme_Dialog)
                .setTitle(R.string.error)
                .setCancelable(true)
                .setPositiveButton(android.R.string.ok, object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        dialog?.dismiss()
                    }
                })
                .create()
    }

    companion object {
        private val KEY_COMMAND_ID = "TAPPY_COMMAND_ID"

        fun createConfigureCommandFragment(commandOption: CommandOption): ConfigureCommandDialogFragment {
            val args = Bundle()
            args.putInt(KEY_COMMAND_ID,commandOption.commandOptionId)
            val frag = ConfigureCommandDialogFragment()
            frag.arguments = args

            return frag
        }
    }
}
