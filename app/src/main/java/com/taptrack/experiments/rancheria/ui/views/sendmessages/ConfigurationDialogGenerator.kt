package com.taptrack.experiments.rancheria.ui.views.sendmessages

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputLayout
import com.taptrack.experiments.rancheria.R
import com.taptrack.experiments.rancheria.business.CommandDataSource
import com.taptrack.experiments.rancheria.business.CommandOption
import com.taptrack.experiments.rancheria.business.TappyService
import com.taptrack.experiments.rancheria.ui.hexStringToByteArray
import com.taptrack.experiments.rancheria.ui.isTextValidHex
import com.taptrack.tcmptappy.tappy.constants.NdefUriCodes
import com.taptrack.tcmptappy2.TCMPMessage
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.PollingModes
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands.*
import com.taptrack.tcmptappy2.commandfamilies.mifareclassic.KeySetting
import com.taptrack.tcmptappy2.commandfamilies.mifareclassic.commands.DetectMifareClassicCommand
import com.taptrack.tcmptappy2.commandfamilies.mifareclassic.commands.ReadMifareClassicCommand
import com.taptrack.tcmptappy2.commandfamilies.ntag21x.commands.*
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.commands.ConfigureOnboardScanCooldownCommand
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.commands.SetConfigItemCommand
import com.taptrack.tcmptappy2.commandfamilies.type4.commands.*
import org.jetbrains.anko.*
import kotlin.reflect.full.createInstance
import kotlin.reflect.typeOf


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

    private fun makeNdefDialogPair(ctx: Context, option: CommandOption): Pair<View, ConfirmListener> {
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
                        TappyService.broadcastSendTcmp(message,ctx)
                    } else {
                        val message = ScanNdefCommand(timeoutAdjusted.toByte(), PollingModes.MODE_GENERAL)
                        TappyService.broadcastSendTcmp(message,ctx)
                    }

                } catch (ignored: Exception) {
                    // cant instantiate
                }
                return true
            }
        }

        return Pair(cl, listener)
    }

    private fun makeScanDialogPair(ctx: Context, option: CommandOption): Pair<View, ConfirmListener> {
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
                        TappyService.broadcastSendTcmp((message),ctx)
                    } else {
                        val message = ScanTagCommand(timeoutAdjusted.toByte(), PollingModes.MODE_GENERAL)
                        TappyService.broadcastSendTcmp((message),ctx)
                    }

                } catch (ignored: Exception) {
                    // cant instantiate
                }
                return true
            }
        }

        return Pair(cl, listener)
    }

    private fun makeType4DialogPair(ctx: Context, option: CommandOption): Pair<View, ConfirmListener> {
        val cl = wrapInConstraintLayout(ctx, R.layout.timeout_modulation_afi_command_data_view)

        cl.findOptional<TextView>(R.id.tv_command_title)?.textResource = option.titleRes
        cl.findOptional<TextView>(R.id.tv_command_description)?.textResource = option.descriptionRes

        applyTimeoutListener(ctx,cl)

        val afiIcon = cl.findOptional<ImageView>(R.id.iv_afi_icon)
        val afiLabel = cl.findOptional<TextView>(R.id.tv_afi_label)
        val afiTil = cl.findOptional<TextInputLayout>(R.id.til_afi_container)
        val afiEt = cl.findOptional<EditText>(R.id.et_afi)

        cl.findOptional<SwitchCompat>(R.id.swc_modulation)?.setOnCheckedChangeListener { buttonView, isChecked ->
            val afiVisibility = if (isChecked) {
                View.VISIBLE
            } else {
                View.GONE
            }

            afiIcon?.visibility = afiVisibility
            afiLabel?.visibility = afiVisibility
            afiTil?.visibility = afiVisibility
        }

        if (afiEt != null && afiTil != null) {
            afiEt.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (s?.toString()?.trim()?.isTextValidHex() == false) {
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
                            TappyService.broadcastSendTcmp((message),ctx)
                        } else if (afi.isTextValidHex()) {
                            val ba = afi.hexStringToByteArray()
                            val message = DetectType4BSpecificAfiCommand(timeoutAdjusted.toByte(),ba[0])
                            TappyService.broadcastSendTcmp((message),ctx)
                            return true
                        } else {
                            return false
                        }
                    } else {
                        val message = DetectType4Command(timeoutAdjusted.toByte())
                        TappyService.broadcastSendTcmp((message),ctx)
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
        val command: Class<out TCMPMessage>?
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
            SetBLEPinCommand::class.java -> {
                val cl = wrapInConstraintLayout(ctx, R.layout.message_command_data_view)

                cl.findOptional<TextView>(R.id.tv_message_label)?.textResource = R.string.parameter_label_ble_pin
                cl.findOptional<ImageView>(R.id.iv_message_icon)?.imageResource = R.drawable.ic_lock_black_24dp


                cl.findOptional<TextView>(R.id.tv_command_title)?.textResource = option.titleRes
                cl.findOptional<TextView>(R.id.tv_command_description)?.textResource = option.descriptionRes


                val messageTil = cl.find<TextInputLayout>(R.id.til_message_container)
                val messageEt = cl.find<EditText>(R.id.et_message)
                messageEt.inputType = InputType.TYPE_CLASS_NUMBER
                messageEt.filters = messageEt.filters.plus(
                        arrayOf(InputFilter.LengthFilter(6)))

                messageEt.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        if (s != null) {
                            val str = s.toString()
                            val modified = ("[^0-9]").toRegex().replace(str,"")
                            if(str != modified) {
                                messageEt.setText(modified)
                            } else {
                                if (str.isNotEmpty() && str.length != 6) {
                                    messageTil.error = ctx.getString(R.string.error_pin_must_be_six_numeric)
                                } else {
                                    messageTil.error = null
                                }
                            }
                        }
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    }
                })

//                applyTimeoutListener(ctx,cl)

                val listener = object : ConfirmListener {
                    override fun didConfirm(v: View): Boolean {
                        try {


                            val rawValue = cl.find<EditText>(R.id.et_message).text.toString()

                            val cleaned = ("[^0-9]").toRegex().replace(rawValue,"")
                            if (cleaned != rawValue) {
                                return false
                            }

                            if (cleaned.length != 6) {
                                return false
                            }

                            val cmd = SetBLEPinCommand(cleaned)
                            TappyService.broadcastSendTcmp(cmd,ctx)

                        } catch (ignored: Exception) {
                            // cant instantiate
                        }
                        return true
                    }
                }

                return Pair(cl, listener)

            }
            ConfigureOnboardScanCooldownCommand::class.java -> {
                val cl = wrapInConstraintLayout(ctx, R.layout.configure_scan_cooldown_view)

                cl.findOptional<TextView>(R.id.tv_command_title)?.textResource = option.titleRes
                cl.findOptional<TextView>(R.id.tv_command_description)?.textResource = option.descriptionRes

                val valueDesc = cl.find<TextView>(R.id.tv_cardcache_value)
                val sb = cl.find<SeekBar>(R.id.sb_cardcache_selection)

                val sbListener = object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                        val adjusted = progress + 1
                        valueDesc.text = adjusted.toString(10)
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    }

                }

                sbListener.onProgressChanged(sb,sb.progress,false)

                sb.setOnSeekBarChangeListener(sbListener)

                val listener = object : ConfirmListener {
                    override fun didConfirm(v: View): Boolean {
                        try {
                            val cardCache = 1 + (cl.findOptional<SeekBar>(R.id.sb_cardcache_selection)?.progress ?: 0)
                            val isEnabled = cl.findOptional<SwitchCompat>(R.id.swc_enablecooldown)?.isChecked ?: false

                            val message = ConfigureOnboardScanCooldownCommand(
                                    if (isEnabled) {
                                        ConfigureOnboardScanCooldownCommand.CooldownSettings.ENABLE_COOLDOWN
                                    } else {
                                        ConfigureOnboardScanCooldownCommand.CooldownSettings.DISABLE_COOLDOWN
                                    },
                                    cardCache
                            )
                            TappyService.broadcastSendTcmp((message as TCMPMessage),ctx)
                            return true
                        } catch (ignored: Exception) {
                            // cant instantiate
                        }
                        return true
                    }
                }

                return Pair(cl, listener)

            }
            SetConfigItemCommand::class.java -> {
                val cl = wrapInConstraintLayout(ctx, R.layout.parameter_message_data_view)

                cl.findOptional<TextView>(R.id.tv_message_label)?.textResource = R.string.parameter_label_config_param_id
                cl.findOptional<ImageView>(R.id.iv_message_icon)?.imageResource = R.drawable.ic_link_black_24dp
                cl.findOptional<EditText>(R.id.et_message)?.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
                val paramEt = cl.findOptional<EditText>(R.id.et_parameter)
                paramEt?.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
                paramEt?.maxLines = 1


                cl.findOptional<TextView>(R.id.tv_command_title)?.textResource = option.titleRes
                cl.findOptional<TextView>(R.id.tv_command_title)?.textResource = option.titleRes
                cl.findOptional<TextView>(R.id.tv_command_description)?.textResource = option.descriptionRes

                val parameterTil = cl.find<TextInputLayout>(R.id.til_parameter_container)
                val parameterEt = cl.find<EditText>(R.id.et_parameter)

                parameterEt.filters = parameterEt.filters.plus(
                        arrayOf(InputFilter.AllCaps(),InputFilter.LengthFilter(2)))
                parameterEt.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        if (s != null) {
                            val str = s.toString()
                            if ((str.length)> 2) {
                                val truncated = str.substring(
                                                startIndex = 0,
                                                endIndex = 2
                                )
                                parameterEt.setText(truncated)
                            } else {
                                val modified = ("[^A-F0-9]").toRegex().replace(str.toUpperCase(),"")
                                if(str != modified) {
                                    parameterEt.setText(modified)
                                } else {
                                    if (!str.trim().isTextValidHex()) {
                                        val errorText = ctx.getString(R.string.error_must_be_hex_byte)
                                        parameterTil.error = errorText
    //                                    parameterPusherTil.error = errorText
                                    } else {
                                        parameterTil.error = null
    //                                    parameterPusherTil.error = null
                                    }
                                }
                            }
                        }
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    }
                })

                val messageTil = cl.find<TextInputLayout>(R.id.til_value_container)
                val messageEt = cl.find<EditText>(R.id.et_value)
                messageEt.filters = messageEt.filters.plus(
                        arrayOf(InputFilter.AllCaps()))
                messageEt.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        if (s != null) {
                            val str = s.toString()
                            val modified = ("[^A-F0-9]").toRegex().replace(str,"")
                            if(str != modified) {
                                messageEt.setText(modified)
                            } else {
                                if (str.isNotEmpty() && !str.isTextValidHex()) {
                                    messageTil.error = ctx.getString(R.string.error_must_be_hex)
                                } else {
                                    messageTil.error = null
                                }
                            }
                        }
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    }
                })

//                applyTimeoutListener(ctx,cl)

                val listener = object : ConfirmListener {
                    override fun didConfirm(v: View): Boolean {
                        try {
                            val rawParameter = cl.findOptional<EditText>(R.id.et_parameter)?.text?.toString()
                            if (rawParameter == null || rawParameter.isEmpty() || !rawParameter.isTextValidHex()) {
                                return false
                            }

                            val hexParameter = rawParameter.hexStringToByteArray()
                            if (hexParameter.size != 1) {
                                return false
                            }

                            val rawValue = cl.findOptional<EditText>(R.id.et_value)?.text?.toString() ?: ""

                            if (rawValue.isNotEmpty() && !rawValue.isTextValidHex()) {
                                return false
                            }
                            val hexValue = if (rawValue.isEmpty()) {
                                byteArrayOf()
                            } else {
                                rawValue.hexStringToByteArray()
                            }


                            val cmd = SetConfigItemCommand(hexParameter[0],hexValue)
                            TappyService.broadcastSendTcmp(cmd,ctx)

                        } catch (ignored: Exception) {
                            // cant instantiate
                        }
                        return true
                    }
                }

                return Pair(cl, listener)
            }
            WriteNdefUriRecordCommand::class.java -> {
                val cl = wrapInConstraintLayout(ctx, R.layout.timeout_message_command_data_view)

                cl.findOptional<TextView>(R.id.tv_message_label)?.textResource = R.string.parameter_label_url
                cl.findOptional<ImageView>(R.id.iv_message_icon)?.imageResource = R.drawable.ic_link_black_24dp
                cl.findOptional<EditText>(R.id.et_message)?.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_URI

                cl.findOptional<TextView>(R.id.tv_command_title)?.textResource = option.titleRes
                cl.findOptional<TextView>(R.id.tv_command_description)?.textResource = option.descriptionRes

                applyTimeoutListener(ctx, cl)

                val listener = object : ConfirmListener {
                    override fun didConfirm(v: View): Boolean {
                        try {
                            val rawTimeout = 1 + (cl.findOptional<SeekBar>(R.id.sb_timeout_selection)?.progress ?: 10)
                            val uri = cl.findOptional<EditText>(R.id.et_message)?.text?.toString() ?: ""

                            val timeoutAdjusted = if (rawTimeout < 0) 0 else if (rawTimeout == 11) 0 else rawTimeout

                            val (uriCode, uriBytes) = parseUriCodeAndUriBytes(uri)
                            val uriMessage = WriteNdefUriRecordCommand(
                                timeoutAdjusted.toByte(),
                                false,
                                uriCode,
                                uriBytes
                            )
                            TappyService.broadcastSendTcmp(uriMessage, ctx)
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
                            TappyService.broadcastSendTcmp((message),ctx)

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
                                    ByteArray(6) { 0xFF.toByte() }
                            )
                            TappyService.broadcastSendTcmp((message),ctx)
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
                            if (s?.toString()?.trim()?.isTextValidHex() == false) {
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
                                TappyService.broadcastSendTcmp((message),ctx)
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
            LockTagCommand::class.java, DetectMifareClassicCommand::class.java, DetectActiveHCETargetCommand::class.java -> {
                val cl = wrapInConstraintLayout(ctx, R.layout.timeout_command_data_view)

                cl.findOptional<TextView>(R.id.tv_command_title)?.textResource = option.titleRes
                cl.findOptional<TextView>(R.id.tv_command_description)?.textResource = option.descriptionRes

                applyTimeoutListener(ctx,cl)

                val listener = object : ConfirmListener {
                    override fun didConfirm(v: View): Boolean {
                        try {
                            val timeout = 1 + (cl.findOptional<SeekBar>(R.id.sb_timeout_selection)?.progress
                                    ?: 10)

                            val timeoutAdjusted = if (timeout < 0) 0 else if (timeout == 11) 0 else timeout

                            val tcmpMessage: TCMPMessage
                            if (command == LockTagCommand::class.java) {
                                tcmpMessage = LockTagCommand(timeoutAdjusted.toByte(), ByteArray(0))
                            } else if (command == DetectActiveHCETargetCommand::class.java) {
                                tcmpMessage = DetectActiveHCETargetCommand(timeoutAdjusted.toByte())
                            } else {
                                tcmpMessage = DetectMifareClassicCommand(timeoutAdjusted.toByte())
                            }
                            TappyService.broadcastSendTcmp((tcmpMessage),ctx)

                        } catch (ignored: Exception) {
                            // cant instantiate
                        }
                        return true
                    }
                }

                return Pair(cl, listener)
            }
            ReadNdefWithPasswordBytesCommand::class.java -> {
                val cl = wrapInConstraintLayout(ctx, R.layout.timeout_message_command_data_view)
                applyTimeoutListener(ctx, cl)

                cl.findOptional<TextView>(R.id.tv_command_title)?.textResource = option.titleRes
                cl.findOptional<TextView>(R.id.tv_command_description)?.textResource = option.descriptionRes
                cl.findOptional<TextView>(R.id.tv_message_label)?.textResource = R.string.parameter_label_pwd_pack
                cl.findOptional<ImageView>(R.id.iv_message_icon)?.imageResource = R.drawable.ic_password_black_24dp

                val passwordTextInputLayout = cl.find<TextInputLayout>(R.id.til_message_container)
                val passwordEditText = cl.find<EditText>(R.id.et_message)
                passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
                passwordEditText.maxLines = 1

                val passwordLength = 12

                passwordEditText.filters = passwordEditText.filters.plus(
                    arrayOf(
                        InputFilter.AllCaps(),
                        InputFilter.LengthFilter(passwordLength)
                    )
                )

                passwordEditText.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        if (s != null) {
                            val str = s.toString()
                            if (str.length > 12) {
                                val truncated = str.substring(startIndex = 0, endIndex = passwordLength - 1)
                                passwordEditText.setText(truncated)
                            } else {
                                val modified = "[^A-F0-9]".toRegex().replace(str.toUpperCase(), "")
                                if (str != modified) {
                                    passwordEditText.setText(modified)
                                } else {
                                    if (!str.trim().isTextValidHex()) {
                                        val errorText = ctx.getString(R.string.error_must_be_hex_byte)
                                        passwordTextInputLayout.error = errorText
                                    } else {
                                        passwordTextInputLayout.error = null
                                    }
                                }
                            }
                        }
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    }
                })

                val listener = object : ConfirmListener {
                    override fun didConfirm(v: View): Boolean {
                        val pwdPackText = passwordEditText.text.toString().trim()

                        if (!pwdPackText.isTextValidHex() || pwdPackText.length != 12) {
                            return false
                        }

                        val passwordBytes = pwdPackText.substring(0..7).hexStringToByteArray()
                        val ackBytes = pwdPackText.substring(8..pwdPackText.lastIndex).hexStringToByteArray()

                        val timeout = 1 + (cl.findOptional<SeekBar>(R.id.sb_timeout_selection)?.progress ?: 10)

                        val timeoutAdjusted = when {
                            timeout < 0 || timeout == 11 -> 0
                            else -> timeout
                        }

                        try {
                            val message = ReadNdefWithPasswordBytesCommand(
                                timeoutAdjusted.toByte(),
                                passwordBytes,
                                ackBytes,
                            )
                            TappyService.broadcastSendTcmp(message, ctx)
                        } catch (ignored: Exception) {
                            ignored.printStackTrace()
                            // cant instantiate
                        }
                        return true
                    }
                }

                return Pair(cl, listener)
            }
            ReadNdefWithPasswordCommand::class.java -> {
                val cl = wrapInConstraintLayout(ctx, R.layout.timeout_message_command_data_view)
                applyTimeoutListener(ctx, cl)

                cl.findOptional<TextView>(R.id.tv_command_title)?.textResource = option.titleRes
                cl.findOptional<TextView>(R.id.tv_command_description)?.textResource = option.descriptionRes
                cl.findOptional<TextView>(R.id.tv_message_label)?.textResource = R.string.parameter_label_password
                cl.findOptional<ImageView>(R.id.iv_message_icon)?.imageResource = R.drawable.ic_password_black_24dp

                val passwordEditText = cl.find<EditText>(R.id.et_message)
                passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_NORMAL
                passwordEditText.maxLines = 1

                val listener = object : ConfirmListener {
                    override fun didConfirm(v: View): Boolean {
                        val password = passwordEditText.text.toString().trim()
                        val timeout = 1 + (cl.findOptional<SeekBar>(R.id.sb_timeout_selection)?.progress ?: 10)

                        val timeoutAdjusted = when {
                            timeout < 0 || timeout == 11 -> 0
                            else -> timeout
                        }

                        try {
                            val message = ReadNdefWithPasswordCommand(timeoutAdjusted.toByte(), password)
                            TappyService.broadcastSendTcmp(message, ctx)
                        } catch (ignored: Exception) {
                            ignored.printStackTrace()
                            // cant instantiate
                        }
                        return true
                    }
                }

                return Pair(cl, listener)
            }
            WriteTextNdefWithPasswordBytesCommand::class.java -> {
                val cl = wrapInConstraintLayout(ctx, R.layout.timeout_toggle_parameter_message_command_data_view)
                applyTimeoutListener(ctx, cl)

                cl.findOptional<TextView>(R.id.tv_command_title)?.textResource = option.titleRes
                cl.findOptional<TextView>(R.id.tv_command_description)?.textResource = option.descriptionRes
                cl.findOptional<TextView>(R.id.tv_parameter_label)?.textResource = R.string.parameter_label_pwd_pack

                val messageEditText = cl.find<EditText>(R.id.et_message)
                val passwordTextInputLayout = cl.find<TextInputLayout>(R.id.til_parameter_container)
                val passwordEditText = cl.find<EditText>(R.id.et_parameter)
                passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
                passwordEditText.maxLines = 1

                val passwordLength = 12

                passwordEditText.filters = passwordEditText.filters.plus(
                    arrayOf(
                        InputFilter.AllCaps(),
                        InputFilter.LengthFilter(passwordLength)
                    )
                )

                passwordEditText.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        if (s != null) {
                            val str = s.toString()
                            if (str.length > 12) {
                                val truncated = str.substring(startIndex = 0, endIndex = passwordLength - 1)
                                passwordEditText.setText(truncated)
                            } else {
                                val modified = "[^A-F0-9]".toRegex().replace(str.toUpperCase(), "")
                                if (str != modified) {
                                    passwordEditText.setText(modified)
                                } else {
                                    if (!str.trim().isTextValidHex()) {
                                        val errorText = ctx.getString(R.string.error_must_be_hex_byte)
                                        passwordTextInputLayout.error = errorText
                                    } else {
                                        passwordTextInputLayout.error = null
                                    }
                                }
                            }
                        }
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    }
                })

                val listener = object : ConfirmListener {
                    override fun didConfirm(v: View): Boolean {
                        val pwdPackText = passwordEditText.text.toString().trim()

                        if (!pwdPackText.isTextValidHex() || pwdPackText.length != 12) {
                            return false
                        }

                        val passwordBytes = pwdPackText.substring(0..7).hexStringToByteArray()
                        val ackBytes = pwdPackText.substring(8..pwdPackText.lastIndex).hexStringToByteArray()
                        val timeout = 1 + (cl.findOptional<SeekBar>(R.id.sb_timeout_selection)?.progress ?: 10)

                        val timeoutAdjusted = when {
                            timeout < 0 || timeout == 11 -> 0
                            else -> timeout
                        }

                        val text = messageEditText.text.toString().trim()

                        val readProtectionEnabled = cl.findViewById<SwitchCompat>(R.id.swc_toggle).isChecked

                        try {
                            val message = WriteTextNdefWithPasswordBytesCommand(
                                timeoutAdjusted.toByte(),
                                readProtectionEnabled,
                                passwordBytes,
                                ackBytes,
                                text
                            )
                            TappyService.broadcastSendTcmp(message, ctx)
                        } catch (ignored: Exception) {
                            ignored.printStackTrace()
                            // cant instantiate
                        }
                        return true
                    }
                }

                return Pair(cl, listener)
            }
            WriteTextNdefWithPasswordCommand::class.java -> {
                val cl = wrapInConstraintLayout(ctx, R.layout.timeout_toggle_parameter_message_command_data_view)
                applyTimeoutListener(ctx, cl)

                cl.findOptional<TextView>(R.id.tv_command_title)?.textResource = option.titleRes
                cl.findOptional<TextView>(R.id.tv_command_description)?.textResource = option.descriptionRes
                cl.findOptional<TextView>(R.id.tv_parameter_label)?.textResource = R.string.parameter_label_password

                val messageEditText = cl.find<EditText>(R.id.et_message)
                val passwordEditText = cl.find<EditText>(R.id.et_parameter)
                passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_NORMAL
                passwordEditText.maxLines = 1

                val listener = object : ConfirmListener {
                    override fun didConfirm(v: View): Boolean {
                        val password = passwordEditText.text.toString().trim()
                        val timeout = 1 + (cl.findOptional<SeekBar>(R.id.sb_timeout_selection)?.progress ?: 10)

                        val timeoutAdjusted = when {
                            timeout < 0 || timeout == 11 -> 0
                            else -> timeout
                        }

                        val text = messageEditText.text.toString().trim()

                        val readProtectionEnabled = cl.findViewById<SwitchCompat>(R.id.swc_toggle).isChecked

                        try {
                            val message = WriteTextNdefWithPasswordCommand(
                                timeoutAdjusted.toByte(),
                                readProtectionEnabled,
                                password,
                                text
                            )
                            TappyService.broadcastSendTcmp(message, ctx)
                        } catch (ignored: Exception) {
                            ignored.printStackTrace()
                            // cant instantiate
                        }
                        return true
                    }
                }

                return Pair(cl, listener)
            }
            WriteUriNdefWithPasswordBytesCommand::class.java -> {
                val cl = wrapInConstraintLayout(ctx, R.layout.timeout_toggle_parameter_message_command_data_view)
                applyTimeoutListener(ctx, cl)

                cl.findOptional<TextView>(R.id.tv_command_title)?.textResource = option.titleRes
                cl.findOptional<TextView>(R.id.tv_command_description)?.textResource = option.descriptionRes
                cl.findOptional<TextView>(R.id.tv_parameter_label)?.textResource = R.string.parameter_label_pwd_pack

                val uriEditText = cl.find<EditText>(R.id.et_message)
                val passwordTextInputLayout = cl.find<TextInputLayout>(R.id.til_parameter_container)
                val passwordEditText = cl.find<EditText>(R.id.et_parameter)
                passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_URI
                passwordEditText.maxLines = 1

                val passwordLength = 12

                passwordEditText.filters = passwordEditText.filters.plus(
                    arrayOf(
                        InputFilter.AllCaps(),
                        InputFilter.LengthFilter(passwordLength)
                    )
                )

                passwordEditText.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        if (s != null) {
                            val str = s.toString()
                            if (str.length > 12) {
                                val truncated = str.substring(startIndex = 0, endIndex = passwordLength - 1)
                                passwordEditText.setText(truncated)
                            } else {
                                val modified = "[^A-F0-9]".toRegex().replace(str.toUpperCase(), "")
                                if (str != modified) {
                                    passwordEditText.setText(modified)
                                } else {
                                    if (!str.trim().isTextValidHex()) {
                                        val errorText = ctx.getString(R.string.error_must_be_hex_byte)
                                        passwordTextInputLayout.error = errorText
                                    } else {
                                        passwordTextInputLayout.error = null
                                    }
                                }
                            }
                        }
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    }
                })

                val listener = object : ConfirmListener {
                    override fun didConfirm(v: View): Boolean {
                        val pwdPackText = passwordEditText.text.toString().trim()

                        if (!pwdPackText.isTextValidHex() || pwdPackText.length != 12) {
                            return false
                        }

                        val passwordBytes = pwdPackText.substring(0..7).hexStringToByteArray()
                        val ackBytes = pwdPackText.substring(8..pwdPackText.lastIndex).hexStringToByteArray()
                        val timeout = 1 + (cl.findOptional<SeekBar>(R.id.sb_timeout_selection)?.progress ?: 10)

                        val timeoutAdjusted = when {
                            timeout < 0 || timeout == 11 -> 0
                            else -> timeout
                        }

                        val readProtectionEnabled = cl.findViewById<SwitchCompat>(R.id.swc_toggle).isChecked
                        val fullUriString = uriEditText.text.toString().trim()
                        val (uriCode, uriBytes) = parseUriCodeAndUriBytes(fullUriString)

                        try {
                            val message = WriteUriNdefWithPasswordBytesCommand(
                                timeoutAdjusted.toByte(),
                                readProtectionEnabled,
                                passwordBytes,
                                ackBytes,
                                uriCode,
                                uriBytes
                            )
                            TappyService.broadcastSendTcmp(message, ctx)
                        } catch (ignored: Exception) {
                            ignored.printStackTrace()
                            // cant instantiate
                        }
                        return true
                    }
                }

                return Pair(cl, listener)
            }
            WriteUriNdefWithPasswordCommand::class.java -> {
                val cl = wrapInConstraintLayout(ctx, R.layout.timeout_toggle_parameter_message_command_data_view)
                applyTimeoutListener(ctx, cl)

                cl.findOptional<TextView>(R.id.tv_command_title)?.textResource = option.titleRes
                cl.findOptional<TextView>(R.id.tv_command_description)?.textResource = option.descriptionRes
                cl.findOptional<TextView>(R.id.tv_parameter_label)?.textResource = R.string.parameter_label_password

                val uriEditText = cl.find<EditText>(R.id.et_message)
                val passwordEditText = cl.find<EditText>(R.id.et_parameter)
                passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_NORMAL
                passwordEditText.maxLines = 1

                val listener = object : ConfirmListener {
                    override fun didConfirm(v: View): Boolean {
                        val password = passwordEditText.text.toString().trim()
                        val timeout = 1 + (cl.findOptional<SeekBar>(R.id.sb_timeout_selection)?.progress ?: 10)

                        val timeoutAdjusted = when {
                            timeout < 0 || timeout == 11 -> 0
                            else -> timeout
                        }

                        val readProtectionEnabled = cl.findViewById<SwitchCompat>(R.id.swc_toggle).isChecked
                        val fullUriString = uriEditText.text.toString().trim()
                        val (uriCode, uriBytes) = parseUriCodeAndUriBytes(fullUriString)

                        try {
                            val message = WriteUriNdefWithPasswordCommand(
                                timeoutAdjusted.toByte(),
                                readProtectionEnabled,
                                password,
                                uriCode,
                                uriBytes
                            )
                            TappyService.broadcastSendTcmp(message, ctx)
                        } catch (ignored: Exception) {
                            ignored.printStackTrace()
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
                            val message = command.kotlin.createInstance()
                            TappyService.broadcastSendTcmp((message),ctx)
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
        // TODO: find an alternative to this
        val act = this.activity!!
        val ctx = this.context!!
        val builder = AlertDialog.Builder(act)
        val commandId = arguments?.getInt(KEY_COMMAND_ID) ?: -1
        val dataSource = CommandDataSource(ctx)
        val command = dataSource.retrieveCommand(commandId)

        if (command != null) {
            val dialog = DialogGenerator.configureCommandAlertDialog(act, command)
            if (dialog != null) {
                return dialog
            }
        }
        // this should be impossible
        return AlertDialog.Builder(act,R.style.AppTheme_Dialog)
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
        private const val KEY_COMMAND_ID = "TAPPY_COMMAND_ID"

        fun createConfigureCommandFragment(commandOption: CommandOption): ConfigureCommandDialogFragment {
            val args = Bundle()
            args.putInt(KEY_COMMAND_ID,commandOption.commandOptionId)
            val frag = ConfigureCommandDialogFragment()
            frag.arguments = args

            return frag
        }
    }
}

private fun parseUriCodeAndUriBytes(fullUriString: String): Pair<Byte, ByteArray> = when {
    fullUriString.startsWith("https://www.") -> Pair(
        NdefUriCodes.URICODE_HTTPSWWW,
        fullUriString.substring("https://www.".length).toByteArray()
    )
    fullUriString.startsWith("http://www.") -> Pair(
        NdefUriCodes.URICODE_HTTPWWW,
        fullUriString.substring("http://www.".length).toByteArray()
    )
    fullUriString.startsWith("http://") -> Pair(
        NdefUriCodes.URICODE_HTTP,
        fullUriString.substring("http://".length).toByteArray()
    )
    fullUriString.startsWith("https://") -> Pair(
        NdefUriCodes.URICODE_HTTPS,
        fullUriString.substring("https://".length).toByteArray()
    )
    fullUriString.startsWith("tel:") -> Pair(
        NdefUriCodes.URICODE_TEL,
        fullUriString.substring("tel:".length).toByteArray()
    )

    fullUriString.startsWith("mailto:") -> Pair(
        NdefUriCodes.URICODE_MAILTO,
        fullUriString.substring("mailto:".length).toByteArray()
    )
    else -> Pair(
        NdefUriCodes.URICODE_NOPREFIX,
        fullUriString.toByteArray()
    )
}
