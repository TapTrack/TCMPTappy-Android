package com.taptrack.experiments.rancheria.ui.views.sendmessages

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.annotation.LayoutRes
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatSeekBar
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
import com.taptrack.experiments.rancheria.wristcoinpos.commands.*
import com.taptrack.tcmptappy.tappy.constants.NdefUriCodes
import com.taptrack.tcmptappy2.TCMPMessage
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.PollingModes
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands.*
import com.taptrack.tcmptappy2.commandfamilies.mifareclassic.KeySetting
import com.taptrack.tcmptappy2.commandfamilies.mifareclassic.commands.DetectMifareClassicCommand
import com.taptrack.tcmptappy2.commandfamilies.mifareclassic.commands.ReadMifareClassicCommand
import com.taptrack.tcmptappy2.commandfamilies.ntag21x.commands.*
import com.taptrack.tcmptappy2.commandfamilies.ntag21x.commands.ReadNdefWithPasswordCommand
import com.taptrack.tcmptappy2.commandfamilies.standalonecheckin.commands.*
import com.taptrack.tcmptappy2.commandfamilies.stmicroM24SR02.commands.*
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.commands.ConfigureOnboardScanCooldownCommand
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.commands.SetBootConfigCommand
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.commands.SetConfigItemCommand
import com.taptrack.tcmptappy2.commandfamilies.type4.commands.*
import org.jetbrains.anko.*
import java.util.*
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
                    .setNegativeButton(act.getString(android.R.string.cancel)) { dialog, which -> dialog.dismiss() }
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
        sb.max = 11

        val listener = object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (progress >= 11) {
                    valueDesc.text = ctx.getString(R.string.parameter_value_infinite)
                } else {
                    val adjusted = progress + 5
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
                    val timeout = 5 + (cl.findOptional<SeekBar>(R.id.sb_timeout_selection)?.progress ?: 11)
                    val stream = cl.findOptional<SwitchCompat>(R.id.swc_continuous)?.isChecked ?: false

                    val timeoutAdjusted = if (timeout < 0 ) 0 else if (timeout == 16) 0 else timeout

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
                    val timeout = 5 + (cl.findOptional<SeekBar>(R.id.sb_timeout_selection)?.progress ?: 11)
                    val stream = cl.findOptional<SwitchCompat>(R.id.swc_continuous)?.isChecked ?: false

                    val timeoutAdjusted = if (timeout < 0 ) 0 else if (timeout == 16) 0 else timeout

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
                    val timeout = 5 + (cl.findOptional<SeekBar>(R.id.sb_timeout_selection)?.progress ?: 11)
                    val isTypeB = cl.findOptional<SwitchCompat>(R.id.swc_modulation)?.isChecked ?: false

                    val timeoutAdjusted = if (timeout < 0 ) 0 else if (timeout == 16) 0 else timeout

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


    @RequiresApi(Build.VERSION_CODES.O)
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
            InitiateTappyTagHandshakeCommand::class.java -> {
                val cl = wrapInConstraintLayout(ctx, R.layout.timeout_message_command_data_view)

                cl.findOptional<TextView>(R.id.tv_command_title)?.textResource = option.titleRes
                cl.findOptional<TextView>(R.id.tv_command_description)?.textResource = option.descriptionRes


                val parameterTil = cl.find<TextInputLayout>(R.id.til_message_container)
                val parameterEt = cl.find<EditText>(R.id.et_message)

                parameterEt.filters = parameterEt.filters.plus(
                    arrayOf(InputFilter.AllCaps()))
                parameterEt.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        if (s != null) {
                            val str = s.toString()
                            val modified = ("[^A-F0-9]").toRegex().replace(str.toUpperCase(Locale.ROOT),"")
                            if(str != modified) {
                                parameterEt.setText(modified)
                            } else {
                                if (!str.trim().isTextValidHex()) {
                                    val errorText = ctx.getString(R.string.error_must_be_hex_byte)
                                    parameterTil.error = errorText
                                } else {
                                    parameterTil.error = null
                                }
                            }
                        }
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    }
                })

                applyTimeoutListener(ctx, cl)

                val listener = object : ConfirmListener {
                    override fun didConfirm(v: View): Boolean {
                        try {
                            val rawParameter = cl.findOptional<EditText>(R.id.et_message)?.text?.toString()
                            var timeout = 5 + (cl.findOptional<AppCompatSeekBar>(R.id.sb_timeout_selection)?.progress ?: 11)

                            val timeoutAdjusted = if (timeout < 0 ) 0 else if (timeout >= 16) 0 else timeout


                            if((rawParameter == null || rawParameter.isEmpty()) && timeoutAdjusted == 0){
                                val cmd = InitiateTappyTagHandshakeCommand()
                                TappyService.broadcastSendTcmp(cmd,ctx)
                                return true
                            }else if(rawParameter == null || rawParameter.isEmpty()){
                                val cmd = InitiateTappyTagHandshakeCommand(duration = (timeoutAdjusted))
                                TappyService.broadcastSendTcmp(cmd,ctx)
                                return true
                            }else if (!rawParameter.isTextValidHex()) {
                                return false
                            }

                            val hexParameter = rawParameter.hexStringToByteArray()

                            val cmd = InitiateTappyTagHandshakeCommand(responseData = hexParameter, duration = timeoutAdjusted)
                            TappyService.broadcastSendTcmp(cmd,ctx)
                        } catch (ignored: Exception) {
                            // cant instantiate
                        }
                        // TODO: Implement verification when more settings are added
                        return true
                    }
                }
                return Pair(cl, listener)
            }
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
                                val modified = ("[^A-F0-9]").toRegex().replace(str.toUpperCase(Locale.ROOT),"")
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
            WriteNdefUriRecordCommand::class.java, EmulateUriRecordCommand::class.java -> {
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
                            val rawTimeout = 5 + (cl.findOptional<SeekBar>(R.id.sb_timeout_selection)?.progress ?: 11)
                            val uri = cl.findOptional<EditText>(R.id.et_message)?.text?.toString() ?: ""

                            val timeoutAdjusted = if (rawTimeout < 0) 0 else if (rawTimeout == 16) 0 else rawTimeout

                            var uriMessage: TCMPMessage

                            val (uriCode, uriBytes) = parseUriCodeAndUriBytes(uri)
                            if(command == WriteNdefUriRecordCommand::class.java){
                                uriMessage = WriteNdefUriRecordCommand(
                                    timeoutAdjusted.toByte(),
                                    false,
                                    uriCode,
                                    uriBytes
                                )
                            } else {
                                uriMessage = EmulateUriRecordCommand(
                                    timeoutAdjusted.toByte(),
                                    0x00,
                                    uriCode,
                                    uriBytes
                                )
                            }
                            TappyService.broadcastSendTcmp(uriMessage, ctx)
                        } catch (ignored: Exception) {
                            // cant instantiate
                        }
                        return true
                    }
                }

                return Pair(cl, listener)
            }
            WriteNdefTextRecordCommand::class.java, EmulateTextRecordCommand::class.java -> {
                val cl = wrapInConstraintLayout(ctx, R.layout.timeout_message_command_data_view)

                cl.findOptional<TextView>(R.id.tv_command_title)?.textResource = option.titleRes
                cl.findOptional<TextView>(R.id.tv_command_description)?.textResource = option.descriptionRes

                applyTimeoutListener(ctx,cl)

                val listener = object : ConfirmListener {
                    override fun didConfirm(v: View):Boolean {
                        try {
                            val timeout = 5 + (cl.findOptional<SeekBar>(R.id.sb_timeout_selection)?.progress ?: 11)
                            val msg = cl.findOptional<EditText>(R.id.et_message)?.text?.toString() ?: ""

                            val timeoutAdjusted = if (timeout < 0 ) 0 else if (timeout == 16) 0 else timeout

                            var message: TCMPMessage

                            if(command == WriteNdefTextRecordCommand::class.java) {
                                message = WriteNdefTextRecordCommand(
                                    timeoutAdjusted.toByte(),
                                    false,
                                    msg.toByteArray()
                                )
                            } else {
                                message = EmulateTextRecordCommand(
                                    timeoutAdjusted.toByte(),
                                    0x00,
                                    msg.toByteArray()
                                )
                            }
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
                            val timeout = 5 + (cl.findOptional<SeekBar>(R.id.sb_timeout_selection)?.progress ?: 11)

                            val timeoutAdjusted = if (timeout < 0 ) 0 else if (timeout == 16) 0 else timeout

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
            LockTagCommand::class.java, DetectMifareClassicCommand::class.java, DetectActiveHCETargetCommand::class.java,
            GetI2CSettingCommand::class.java, ReadCheckinCardUidCommand::class.java, GetWristbandStatusCommand::class.java, CloseoutWristbandCommand::class.java -> {
                val cl = wrapInConstraintLayout(ctx, R.layout.timeout_command_data_view)

                cl.findOptional<TextView>(R.id.tv_command_title)?.textResource = option.titleRes
                cl.findOptional<TextView>(R.id.tv_command_description)?.textResource = option.descriptionRes

                applyTimeoutListener(ctx,cl)

                val listener = object : ConfirmListener {
                    override fun didConfirm(v: View): Boolean {
                        try {
                            val timeout = 5 + (cl.findOptional<SeekBar>(R.id.sb_timeout_selection)?.progress ?: 11)

                            val timeoutAdjusted = if (timeout < 0) 0 else if (timeout == 16) 0 else timeout

                            val tcmpMessage: TCMPMessage
                            if (command == LockTagCommand::class.java) {
                                tcmpMessage = LockTagCommand(timeoutAdjusted.toByte(), ByteArray(0))
                            } else if (command == DetectActiveHCETargetCommand::class.java) {
                                tcmpMessage = DetectActiveHCETargetCommand(timeoutAdjusted.toByte())
                            } else if (command == GetI2CSettingCommand::class.java) {
                                tcmpMessage = GetI2CSettingCommand(timeoutAdjusted.toByte())
                            } else if (command == ReadCheckinCardUidCommand::class.java) {
                                tcmpMessage = ReadCheckinCardUidCommand(timeoutAdjusted.toByte())
                            } else if (command == GetWristbandStatusCommand::class.java) {
                                tcmpMessage = GetWristbandStatusCommand(timeoutAdjusted.toByte())
                            } else if (command == CloseoutWristbandCommand::class.java) {
                                tcmpMessage = CloseoutWristbandCommand(timeoutAdjusted.toByte())
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
                                val modified = "[^A-F0-9]".toRegex().replace(str.toUpperCase(Locale.ROOT), "")
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

                        val timeout = 1 + (cl.findOptional<SeekBar>(R.id.sb_timeout_selection)?.progress ?: 15)

                        val timeoutAdjusted = when {
                            timeout < 0 || timeout == 16 -> 0
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
                passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                passwordEditText.maxLines = 1

                val listener = object : ConfirmListener {
                    override fun didConfirm(v: View): Boolean {
                        val password = passwordEditText.text.toString().trim()

                        if (password.isEmpty()) {
                            return false
                        }

                        val timeout = 5 + (cl.findOptional<SeekBar>(R.id.sb_timeout_selection)?.progress ?: 11)

                        val timeoutAdjusted = when {
                            timeout < 0 || timeout == 16 -> 0
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
                                val modified = "[^A-F0-9]".toRegex().replace(str.toUpperCase(Locale.ROOT), "")
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
                        val timeout = 5 + (cl.findOptional<SeekBar>(R.id.sb_timeout_selection)?.progress ?: 11)

                        val timeoutAdjusted = when {
                            timeout < 0 || timeout == 16 -> 0
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
                passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                passwordEditText.maxLines = 1

                val listener = object : ConfirmListener {
                    override fun didConfirm(v: View): Boolean {
                        val password = passwordEditText.text.toString().trim()

                        if (password.isEmpty()) {
                            return false
                        }

                        val timeout = 5 + (cl.findOptional<SeekBar>(R.id.sb_timeout_selection)?.progress ?: 11)

                        val timeoutAdjusted = when {
                            timeout < 0 || timeout == 16 -> 0
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
                cl.findOptional<TextView>(R.id.tv_message_label)?.textResource = R.string.parameter_label_url
                cl.findOptional<ImageView>(R.id.iv_message_icon)?.imageResource = R.drawable.ic_link_black_24dp

                val uriEditText = cl.find<EditText>(R.id.et_message)
                uriEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_URI

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
                                val modified = "[^A-F0-9]".toRegex().replace(str.toUpperCase(Locale.ROOT), "")
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
                        val timeout = 5 + (cl.findOptional<SeekBar>(R.id.sb_timeout_selection)?.progress ?: 11)

                        val timeoutAdjusted = when {
                            timeout < 0 || timeout == 16 -> 0
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
                cl.findOptional<TextView>(R.id.tv_message_label)?.textResource = R.string.parameter_label_url
                cl.findOptional<ImageView>(R.id.iv_message_icon)?.imageResource = R.drawable.ic_link_black_24dp

                val uriEditText = cl.find<EditText>(R.id.et_message)
                uriEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_URI

                val passwordEditText = cl.find<EditText>(R.id.et_parameter)
                passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                passwordEditText.maxLines = 1

                val listener = object : ConfirmListener {
                    override fun didConfirm(v: View): Boolean {
                        val password = passwordEditText.text.toString().trim()

                        if (password.isEmpty()) {
                            return false
                        }

                        val timeout = 5 + (cl.findOptional<SeekBar>(R.id.sb_timeout_selection)?.progress ?: 11)

                        val timeoutAdjusted = when {
                            timeout < 0 || timeout == 16 -> 0
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
            SetBootConfigCommand::class.java -> {
                val cl = wrapInConstraintLayout(ctx, R.layout.message_command_data_view)

                cl.findOptional<TextView>(R.id.tv_message_label)?.textResource = R.string.parameter_label_config_param_id
                cl.findOptional<ImageView>(R.id.iv_message_icon)?.imageResource = R.drawable.ic_config_parameter_black_24dp
                cl.findOptional<EditText>(R.id.et_message)?.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
                val paramEt = cl.findOptional<EditText>(R.id.et_parameter)
                paramEt?.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
                paramEt?.maxLines = 1


                cl.findOptional<TextView>(R.id.tv_command_title)?.textResource = option.titleRes
                cl.findOptional<TextView>(R.id.tv_command_description)?.textResource = option.descriptionRes

                val parameterTil = cl.find<TextInputLayout>(R.id.til_message_container)
                val parameterEt = cl.find<EditText>(R.id.et_message)

                parameterEt.filters = parameterEt.filters.plus(
                    arrayOf(InputFilter.AllCaps()))
                parameterEt.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        if (s != null) {
                            val str = s.toString()
                            val modified = ("[^A-F0-9]").toRegex().replace(str.toUpperCase(Locale.ROOT),"")
                            if(str != modified) {
                                parameterEt.setText(modified)
                            } else {
                                if (!str.trim().isTextValidHex()) {
                                    val errorText = ctx.getString(R.string.error_must_be_hex_byte)
                                    parameterTil.error = errorText
                                } else {
                                    parameterTil.error = null
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
                        try {
                            val rawParameter = cl.findOptional<EditText>(R.id.et_message)?.text?.toString()
                            if (rawParameter == null || rawParameter.isEmpty() || !rawParameter.isTextValidHex()) {
                                return false
                            }

                            val hexParameter = rawParameter.hexStringToByteArray()

                            val cmd = SetBootConfigCommand(hexParameter)
                            TappyService.broadcastSendTcmp(cmd,ctx)

                        } catch (ignored: Exception) {
                            // cant instantiate
                        }
                        return true
                    }
                }

                return Pair(cl, listener)
            }
            AutoPollCommand::class.java ->{
                val cl = wrapInConstraintLayout(ctx, R.layout.toggle_command_data_view)

                cl.findOptional<TextView>(R.id.tv_command_title)?.textResource = option.titleRes
                cl.findOptional<TextView>(R.id.tv_command_description)?.textResource = option.descriptionRes
                cl.findOptional<TextView>(R.id.tv_toggle_label)?.textResource = R.string.enable_heartbeat

                val listener = object : ConfirmListener {
                    override fun didConfirm(v: View): Boolean {
                        val heartBeatEnabled = cl.findViewById<SwitchCompat>(R.id.swc_toggle).isChecked

                        if(heartBeatEnabled){
                            val cmd = AutoPollCommand(0x00,0x05, false)
                            TappyService.broadcastSendTcmp(cmd,ctx)
                        }else{
                            val cmd = AutoPollCommand()
                            TappyService.broadcastSendTcmp(cmd, ctx)
                        }
                        return true
                    }
                }

                return Pair(cl, listener)
            }
            AutoPollNdefCommand::class.java ->{
                val cl = wrapInConstraintLayout(ctx, R.layout.toggle_command_data_view)

                cl.findOptional<TextView>(R.id.tv_command_title)?.textResource = option.titleRes
                cl.findOptional<TextView>(R.id.tv_command_description)?.textResource = option.descriptionRes
                cl.findOptional<TextView>(R.id.tv_toggle_label)?.textResource = R.string.enable_heartbeat

                val listener = object : ConfirmListener {
                    override fun didConfirm(v: View): Boolean {
                        val heartBeatEnabled = cl.findViewById<SwitchCompat>(R.id.swc_toggle).isChecked

                        if(heartBeatEnabled){
                            val cmd = AutoPollNdefCommand(0x05)
                            TappyService.broadcastSendTcmp(cmd,ctx)
                        }else{
                            val cmd = AutoPollNdefCommand()
                            TappyService.broadcastSendTcmp(cmd, ctx)
                        }
                        return true
                    }
                }

                return Pair(cl, listener)
            }
            DispatchTagsCommand::class.java ->{
                val cl = wrapInConstraintLayout(ctx, R.layout.timeout_continuous_command_data_view)
                cl.findOptional<TextView>(R.id.tv_command_title)?.textResource = option.titleRes
                cl.findOptional<TextView>(R.id.tv_command_description)?.textResource = option.descriptionRes
                applyTimeoutListener(ctx, cl)
                val listener = object : ConfirmListener {
                    override fun didConfirm(v: View): Boolean {
                        val continuous = cl.findViewById<SwitchCompat>(R.id.swc_continuous).isChecked
                        val timeout = 5 + (cl.findOptional<SeekBar>(R.id.sb_timeout_selection)?.progress ?: 11)
                        val timeoutAdjusted = if (timeout < 0 ) 0 else if (timeout == 16) 0 else timeout

                        if(continuous){
                            val cmd = DispatchTagsCommand(timeoutAdjusted.toByte())
                            TappyService.broadcastSendTcmp(cmd, ctx)
                        }else{
                            val cmd = DispatchTagCommand(timeoutAdjusted.toByte())
                            TappyService.broadcastSendTcmp(cmd, ctx)
                        }
                        return true
                    }
                }
                return Pair(cl, listener)
            }
            ChangeReadNdefPasswordCommand::class.java, ChangeWriteNdefPasswordCommand::class.java -> {
                val cl = wrapInConstraintLayout(ctx, R.layout.timeout_parameter_message_data_view)
                applyTimeoutListener(ctx, cl)

                cl.findOptional<TextView>(R.id.tv_command_title)?.textResource = option.titleRes
                cl.findOptional<TextView>(R.id.tv_command_description)?.textResource = option.descriptionRes
                cl.findOptional<TextView>(R.id.tv_parameter_label)?.textResource = R.string.parameter_label_current_password
                cl.findOptional<TextView>(R.id.tv_value_label)?.textResource = R.string.parameter_label_new_password
                cl.findOptional<ImageView>(R.id.iv_value_icon)?.imageResource = R.drawable.ic_password_black_24dp
                cl.findOptional<ImageView>(R.id.iv_parameter_icon)?.imageResource = R.drawable.ic_password_black_24dp

                val passwordTextInputLayout = cl.find<TextInputLayout>(R.id.til_parameter_container)
                val messageTil = cl.find<TextInputLayout>(R.id.til_value_container)
                val messageEditText = cl.find<EditText>(R.id.et_value)
                val passwordEditText = cl.find<EditText>(R.id.et_parameter)

                passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
                messageEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
                passwordEditText.maxLines = 1
                val passwordLength = 32

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
                            if (str.length > 32) {
                                val truncated = str.substring(startIndex = 0, endIndex = passwordLength - 1)
                                passwordEditText.setText(truncated)
                            } else {
                                val modified = "[^A-F0-9]".toRegex().replace(str.toUpperCase(Locale.ROOT), "")
                                if (str != modified) {
                                    passwordEditText.setText(modified)
                                } else {
                                    if (!str.trim().isTextValidHex()) {
                                        val errorText = ctx.getString(R.string.error_must_be_hex_byte)
                                        passwordTextInputLayout.error = errorText
                                    } else if (str.length < 32){
                                        val errorText = ctx.getString(R.string.error_password_must_be_32)
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

                messageEditText.filters = messageEditText.filters.plus(
                    arrayOf(InputFilter.AllCaps()))
                messageEditText.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        if (s != null) {
                            val str = s.toString()
                            if (str.length > 32) {
                                val truncated = str.substring(startIndex = 0, endIndex = passwordLength - 1)
                                messageEditText.setText(truncated)
                            } else {
                                val modified = "[^A-F0-9]".toRegex().replace(str.toUpperCase(Locale.ROOT), "")
                                if (str != modified) {
                                    messageEditText.setText(modified)
                                } else {
                                    if (!str.trim().isTextValidHex()) {
                                        val errorText = ctx.getString(R.string.error_must_be_hex_byte)
                                        messageTil.error = errorText
                                    } else if (str.length < 32){
                                        val errorText = ctx.getString(R.string.error_password_must_be_32)
                                        messageTil.error = errorText
                                    } else {
                                        messageTil.error = null
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
                        val currentPassword = passwordEditText.text.toString().trim()

                        if (!currentPassword.isTextValidHex() || currentPassword.length != 32) {
                            return false
                        }

                        val newPassword = messageEditText.text.toString().trim()

                        if (!newPassword.isTextValidHex() || newPassword.length != 32) {
                            return false
                        }

                        val currentPasswordBytes = currentPassword.hexStringToByteArray()
                        val newPasswordBytes = newPassword.hexStringToByteArray()

                        val timeout = 5 + (cl.findOptional<SeekBar>(R.id.sb_timeout_selection)?.progress ?: 11)

                        val timeoutAdjusted = when {
                            timeout < 0 || timeout == 16 -> 0
                            else -> timeout
                        }

                        try {
                            var message: TCMPMessage
                            message = if(command == ChangeReadNdefPasswordCommand::class.java) {
                                ChangeReadNdefPasswordCommand(
                                    timeoutAdjusted.toByte(),
                                    currentPasswordBytes,
                                    newPasswordBytes
                                )
                            }else{
                                ChangeWriteNdefPasswordCommand(
                                    timeoutAdjusted.toByte(),
                                    currentPasswordBytes,
                                    newPasswordBytes
                                )
                            }
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
            LockNdefReadAccessCommand::class.java, LockNdefWriteAccessCommand::class.java, UnlockNdefReadAccessCommand::class.java,
            UnlockNdefWriteAccessCommand::class.java, PermanentlyLockNdefWriteAccessCommand::class.java, ReadNdefMsgWithPasswordCommand::class.java -> {
                val cl = wrapInConstraintLayout(ctx, R.layout.timeout_message_command_data_view)
                applyTimeoutListener(ctx, cl)

                cl.findOptional<TextView>(R.id.tv_command_title)?.textResource = option.titleRes
                cl.findOptional<TextView>(R.id.tv_command_description)?.textResource = option.descriptionRes
                if(command == LockNdefReadAccessCommand::class.java || command == UnlockNdefReadAccessCommand::class.java || command == ReadNdefMsgWithPasswordCommand::class.java){
                    cl.findOptional<TextView>(R.id.tv_message_label)?.textResource = R.string.parameter_label_read_password
                }else{
                    cl.findOptional<TextView>(R.id.tv_message_label)?.textResource = R.string.parameter_label_write_password
                }
                cl.findOptional<ImageView>(R.id.iv_message_icon)?.imageResource = R.drawable.ic_password_black_24dp

                val passwordTextInputLayout = cl.find<TextInputLayout>(R.id.til_message_container)
                val passwordEditText = cl.find<EditText>(R.id.et_message)
                passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
                passwordEditText.maxLines = 1

                val passwordLength = 32

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
                            if (str.length > 32) {
                                val truncated = str.substring(startIndex = 0, endIndex = passwordLength - 1)
                                passwordEditText.setText(truncated)
                            } else {
                                val modified = "[^A-F0-9]".toRegex().replace(str.toUpperCase(Locale.ROOT), "")
                                if (str != modified) {
                                    passwordEditText.setText(modified)
                                } else {
                                    if (!str.trim().isTextValidHex()) {
                                        val errorText = ctx.getString(R.string.error_must_be_hex_byte)
                                        passwordTextInputLayout.error = errorText
                                    } else if (str.length < 32){
                                        val errorText = ctx.getString(R.string.error_password_must_be_32)
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

                        if (!pwdPackText.isTextValidHex() || pwdPackText.length != 32) {
                            return false
                        }

                        val passwordBytes = pwdPackText.hexStringToByteArray()

                        val timeout = 5 + (cl.findOptional<SeekBar>(R.id.sb_timeout_selection)?.progress ?: 11)

                        val timeoutAdjusted = when {
                            timeout < 0 || timeout == 16 -> 0
                            else -> timeout
                        }

                        try {
                            var message: TCMPMessage
                            message = when(command){
                                LockNdefReadAccessCommand::class.java -> {
                                    LockNdefReadAccessCommand(
                                        timeoutAdjusted.toByte(),
                                        passwordBytes,
                                    )
                                }
                                LockNdefWriteAccessCommand::class.java -> {
                                    LockNdefWriteAccessCommand(
                                        timeoutAdjusted.toByte(),
                                        passwordBytes,
                                    )
                                }
                                UnlockNdefReadAccessCommand::class.java -> {
                                    UnlockNdefReadAccessCommand(
                                        timeoutAdjusted.toByte(),
                                        passwordBytes,
                                    )
                                }
                                UnlockNdefWriteAccessCommand::class.java -> {
                                    UnlockNdefWriteAccessCommand(
                                        timeoutAdjusted.toByte(),
                                        passwordBytes,
                                    )
                                }
                                PermanentlyLockNdefWriteAccessCommand::class.java -> {
                                    PermanentlyLockNdefWriteAccessCommand(
                                        timeoutAdjusted.toByte(),
                                        passwordBytes,
                                    )
                                }
                                else -> {
                                    ReadNdefMsgWithPasswordCommand(
                                        timeoutAdjusted.toByte(),
                                        passwordBytes,
                                    )
                                }
                            }
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
            WriteNdefWithPasswordCommand::class.java -> {
                val cl = wrapInConstraintLayout(ctx, R.layout.timeout_parameter_message_data_view)
                applyTimeoutListener(ctx, cl)

                cl.findOptional<TextView>(R.id.tv_command_title)?.textResource = option.titleRes
                cl.findOptional<TextView>(R.id.tv_command_description)?.textResource = option.descriptionRes
                cl.findOptional<TextView>(R.id.tv_parameter_label)?.textResource = R.string.parameter_label_write_password
                cl.findOptional<TextView>(R.id.tv_value_label)?.textResource = R.string.parameter_label_ndef_message
                cl.findOptional<ImageView>(R.id.iv_parameter_icon)?.imageResource = R.drawable.ic_password_black_24dp

                val passwordTextInputLayout = cl.find<TextInputLayout>(R.id.til_parameter_container)
                val messageTil = cl.find<TextInputLayout>(R.id.til_value_container)
                val messageEditText = cl.find<EditText>(R.id.et_value)
                val passwordEditText = cl.find<EditText>(R.id.et_parameter)

                passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
                messageEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
                passwordEditText.maxLines = 1
                val passwordLength = 32

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
                            if (str.length > 32) {
                                val truncated = str.substring(startIndex = 0, endIndex = passwordLength - 1)
                                passwordEditText.setText(truncated)
                            } else {
                                val modified = "[^A-F0-9]".toRegex().replace(str.toUpperCase(Locale.ROOT), "")
                                if (str != modified) {
                                    passwordEditText.setText(modified)
                                } else {
                                    if (!str.trim().isTextValidHex()) {
                                        val errorText = ctx.getString(R.string.error_must_be_hex_byte)
                                        passwordTextInputLayout.error = errorText
                                    } else if (str.length < 32){
                                        val errorText = ctx.getString(R.string.error_password_must_be_32)
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

                messageEditText.filters = messageEditText.filters.plus(
                    arrayOf(InputFilter.AllCaps()))
                messageEditText.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        if (s != null) {
                            val str = s.toString()
                            val modified = ("[^A-F0-9]").toRegex().replace(str,"")
                            if(str != modified) {
                                messageEditText.setText(modified)
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

                val listener = object : ConfirmListener {
                    override fun didConfirm(v: View): Boolean {
                        val password = passwordEditText.text.toString().trim()


                        if (!password.isTextValidHex() || password.length != 32) {
                            return false
                        }

                        val passwordBytes = password.hexStringToByteArray()

                        val text = messageEditText.text.toString().trim()

                        if (text.isNotEmpty() && !text.isTextValidHex()) {
                            return false
                        }
                        val hexValue = if (text.isEmpty()) {
                            byteArrayOf()
                        } else {
                            text.hexStringToByteArray()
                        }

                        val timeout = 5 + (cl.findOptional<SeekBar>(R.id.sb_timeout_selection)?.progress ?: 11)

                        val timeoutAdjusted = when {
                            timeout < 0 || timeout == 16 -> 0
                            else -> timeout
                        }

                        try {
                            val message = WriteNdefWithPasswordCommand(
                                timeoutAdjusted.toByte(),
                                passwordBytes,
                                hexValue
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
            GetCheckinsCommand::class.java -> {
                val cl = wrapInConstraintLayout(ctx, R.layout.parameter_message_data_view)

                cl.findOptional<TextView>(R.id.tv_command_title)?.textResource = option.titleRes
                cl.findOptional<TextView>(R.id.tv_command_description)?.textResource = option.descriptionRes
                cl.findOptional<TextView>(R.id.tv_parameter_label)?.textResource = R.string.parameter_label_first_checkin
                cl.findOptional<TextView>(R.id.tv_value_label)?.textResource = R.string.parameter_label_second_checkin
                cl.findOptional<ImageView>(R.id.iv_parameter_icon)?.imageResource = R.drawable.ic_config_item_value_black_24dp

                val firstCheckinTil = cl.find<TextInputLayout>(R.id.til_parameter_container)
                val secondCheckinTil = cl.find<TextInputLayout>(R.id.til_value_container)
                val firstCheckinEditText = cl.find<EditText>(R.id.et_parameter)
                val secondCheckinEditText = cl.find<EditText>(R.id.et_value)

                firstCheckinEditText.inputType = InputType.TYPE_CLASS_NUMBER
                secondCheckinEditText.inputType = InputType.TYPE_CLASS_NUMBER
                firstCheckinEditText.maxLines = 1

                val integerLength = 5

                firstCheckinEditText.filters = firstCheckinEditText.filters.plus(
                    arrayOf(
                        InputFilter.LengthFilter(integerLength)
                    )
                )

                secondCheckinEditText.filters = secondCheckinEditText.filters.plus(
                    arrayOf(
                        InputFilter.LengthFilter(integerLength)
                    )
                )

                firstCheckinEditText.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        if (s != null) {
                            val str = s.toString()
                            if (str.length > integerLength) {
                                val truncated = str.substring(startIndex = 0, endIndex = integerLength - 1)
                                firstCheckinEditText.setText(truncated)
                            } else {
                                val modified = "[^0-9]".toRegex().replace(str.toUpperCase(Locale.ROOT), "")
                                if (str != modified) {
                                    firstCheckinEditText.setText(modified)
                                } else {
                                    if (str.toInt() < 0 || str.toInt() > 65535) {
                                        val errorText = ctx.getString(R.string.error_must_be_integer)
                                        firstCheckinTil.error = errorText
                                    } else {
                                        firstCheckinTil.error = null
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

                secondCheckinEditText.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        if (s != null) {
                            val str = s.toString()
                            if (str.length > integerLength) {
                                val truncated = str.substring(startIndex = 0, endIndex = integerLength - 1)
                                secondCheckinEditText.setText(truncated)
                            } else {
                                val modified = "[^0-9]".toRegex().replace(str.toUpperCase(Locale.ROOT), "")
                                if (str != modified) {
                                    secondCheckinEditText.setText(modified)
                                } else {
                                    if (str.toInt() < 0 || str.toInt() > 65535) {
                                        val errorText = ctx.getString(R.string.error_must_be_integer)
                                        secondCheckinTil.error = errorText
                                    } else {
                                        secondCheckinTil.error = null
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
                        val first = firstCheckinEditText.text.toString()
                        val second = secondCheckinEditText.text.toString()

                        if(first.isEmpty()|| second.isEmpty()) {
                            return false
                        }

                        if (first.toInt() < 0 || first.toInt() > 65535) {
                            return false
                        }


                        if (second.toInt() < 0 || second.toInt() > 65535) {
                            return false
                        }

                        try {
                            val message = GetCheckinsCommand(
                                first.toInt(),
                                second.toInt()
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
            SetStationIdCommand::class.java -> {
                val cl = wrapInConstraintLayout(ctx, R.layout.message_command_data_view)

                cl.findOptional<TextView>(R.id.tv_command_title)?.textResource = option.titleRes
                cl.findOptional<TextView>(R.id.tv_command_description)?.textResource = option.descriptionRes
                cl.findOptional<TextView>(R.id.tv_parameter_label)?.textResource = R.string.parameter_label_station_id
                cl.findOptional<ImageView>(R.id.iv_parameter_icon)?.imageResource = R.drawable.ic_config_item_value_black_24dp
                cl.findOptional<TextView>(R.id.tv_message_label)?.textResource = R.string.parameter_label_station_id

                val stationIdTil = cl.find<TextInputLayout>(R.id.til_message_container)
                val stationIdEditText = cl.find<EditText>(R.id.et_message)

                stationIdEditText.inputType = InputType.TYPE_CLASS_NUMBER
                stationIdEditText.maxLines = 1

                val integerLength = 5

               stationIdEditText.filters = stationIdEditText.filters.plus(
                    arrayOf(
                        InputFilter.LengthFilter(integerLength)
                    )
                )

               stationIdEditText.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        if (s != null) {
                            val str = s.toString()
                            if (str.length > integerLength) {
                                val truncated = str.substring(startIndex = 0, endIndex = integerLength - 1)
                                stationIdEditText.setText(truncated)
                            } else {
                                val modified = "[^0-9]".toRegex().replace(str.toUpperCase(Locale.ROOT), "")
                                if (str != modified) {
                                    stationIdEditText.setText(modified)
                                } else {
                                    if (str.toInt() < 0 || str.toInt() > 65535) {
                                        val errorText = ctx.getString(R.string.error_must_be_integer)
                                        stationIdTil.error = errorText
                                    } else {
                                        stationIdTil.error = null
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
                        val id = stationIdEditText.text.toString()

                        if (id.isEmpty()) {
                            return false
                        }

                        if (id.toInt() < 0 || id.toInt() > 65535) {
                            return false
                        }

                        try {
                            val message = SetStationIdCommand(
                                id.toInt(),
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
            SetStationNameCommand::class.java -> {
                val cl = wrapInConstraintLayout(ctx, R.layout.message_command_data_view)

                cl.findOptional<TextView>(R.id.tv_command_title)?.textResource = option.titleRes
                cl.findOptional<TextView>(R.id.tv_command_description)?.textResource = option.descriptionRes
                cl.findOptional<TextView>(R.id.tv_message_label)?.textResource = R.string.parameter_label_station_name


                val messageTil = cl.find<TextInputLayout>(R.id.til_message_container)
                val messageEt = cl.find<EditText>(R.id.et_message)

                val stationNameLength = 16

                messageEt.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
                messageEt.filters = messageEt.filters.plus(
                    arrayOf(InputFilter.LengthFilter(stationNameLength)))

                messageEt.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        if (s != null) {
                            val str = s.toString()
                            if (str.isNotEmpty() && str.length > stationNameLength) {
                                messageTil.error = ctx.getString(R.string.error_must_be_less_than_16)
                            } else {
                                messageTil.error = null
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

                            if (rawValue.length > stationNameLength || rawValue.isEmpty()) {
                                return false
                            }

                            val cmd = SetStationNameCommand(rawValue)
                            TappyService.broadcastSendTcmp(cmd,ctx)

                        } catch (ignored: Exception) {
                            // cant instantiate
                        }
                        return true
                    }
                }

                return Pair(cl, listener)
            }
            SetTimeAndDateCommand::class.java -> {
                val cl = wrapInConstraintLayout(ctx, R.layout.set_time_and_date_command_data_view)

                cl.findOptional<TextView>(R.id.tv_command_title)?.textResource = option.titleRes
                cl.findOptional<TextView>(R.id.tv_command_description)?.textResource = option.descriptionRes

                val datePicker = cl.find<DatePicker>(R.id.date_picker)
                val hourPicker = cl.find<NumberPicker>(R.id.numpicker_hours)
                val minutesPicker = cl.find<NumberPicker>(R.id.numpicker_minutes)
                val secondsPicker = cl.find<NumberPicker>(R.id.numpicker_seconds)
                hourPicker.setMaxValue(24)
                minutesPicker.setMaxValue(59)
                secondsPicker.setMaxValue(59)
                var displayedValues = arrayOf<String>()
                for(i in 0..60){
                    if(i < 10) {
                        displayedValues += "0$i"
                    } else{
                        displayedValues += "$i"
                    }
                }
                hourPicker.setDisplayedValues(displayedValues.sliceArray(0..24))
                minutesPicker.setDisplayedValues(displayedValues)
                secondsPicker.setDisplayedValues(displayedValues)

                val listener = object : ConfirmListener {
                    override fun didConfirm(v: View): Boolean {
                        val timeAndDate = ByteArray(7)
                        timeAndDate[0] = (datePicker.year-2000).toByte()
                        timeAndDate[1] = (datePicker.month+1).toByte()
                        timeAndDate[2] = datePicker.dayOfMonth.toByte()
                        timeAndDate[3] = hourPicker.value.toByte()
                        timeAndDate[4] = minutesPicker.value.toByte()
                        timeAndDate[5] = secondsPicker.value.toByte()
                        timeAndDate[6] = datePicker.firstDayOfWeek.toByte()
                        try {
                            val message = SetTimeAndDateCommand(timeAndDate)
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
            DebitWristbandFullRespCommand::class.java, DebitWristbandShortRespCommand::class.java,
            TopupWristbandFullRespCommand::class.java, TopupWristbandShortRespCommand::class.java-> {
                val cl = wrapInConstraintLayout(ctx, R.layout.timeout_message_command_data_view)

                cl.findOptional<TextView>(R.id.tv_command_title)?.textResource = option.titleRes
                cl.findOptional<TextView>(R.id.tv_command_description)?.textResource = option.descriptionRes
                cl.findOptional<ImageView>(R.id.iv_message_icon)?.imageResource = R.drawable.ic_attach_money_black_24dp
                cl.findOptional<TextView>(R.id.tv_message_label)?.textResource = R.string.parameter_label_dollar

                val parameterTil = cl.find<TextInputLayout>(R.id.til_message_container)
                val parameterEt = cl.find<EditText>(R.id.et_message)

                parameterEt.inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_CLASS_NUMBER
                parameterEt.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        if (s != null) {
                            val str = s.toString()
                            var modified = ("[^0-9.]").toRegex().replace(str.toUpperCase(Locale.ROOT),"")
                            if (modified.indexOf('.') != -1 && modified.length-modified.indexOf('.') > 3) {
                                modified = modified.substring(0..modified.indexOf('.')+2)
                            }
                            if(str != modified) {
                                parameterEt.setText(modified)
                            }
                        }
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    }
                })

                applyTimeoutListener(ctx, cl)

                val listener = object : ConfirmListener {
                    override fun didConfirm(v: View): Boolean {
                        try {
                            val rawParameter = cl.findOptional<EditText>(R.id.et_message)?.text?.toString()
                            var timeout = 5 + (cl.findOptional<AppCompatSeekBar>(R.id.sb_timeout_selection)?.progress ?: 11)

                            val timeoutAdjusted = if (timeout < 0 ) 0 else if (timeout >= 16) 0 else timeout

                            if(rawParameter == null || rawParameter.isEmpty()){
                                return false
                            }
                            var amount: Int
                            try{
                               amount = ((rawParameter.toDouble()*100).toInt())
                            } catch (e: NumberFormatException){
                                return false
                            }
                            var cmd: TCMPMessage
                            if(command == DebitWristbandShortRespCommand::class.java){
                                cmd = DebitWristbandShortRespCommand(amount, timeoutAdjusted.toByte())
                            } else if (command == DebitWristbandFullRespCommand::class.java) {
                                cmd = DebitWristbandFullRespCommand(amount, timeoutAdjusted.toByte())
                            } else if(command == TopupWristbandShortRespCommand::class.java){
                                cmd = TopupWristbandShortRespCommand(amount, timeoutAdjusted.toByte())
                            } else {
                                cmd = TopupWristbandFullRespCommand(amount, timeoutAdjusted.toByte())
                            }
                            TappyService.broadcastSendTcmp(cmd,ctx)
                        } catch (ignored: Exception) {
                            // cant instantiate
                        }
                        // TODO: Implement verification when more settings are added
                        return true
                    }
                }
                return Pair(cl, listener)
            }
            SetEventIdCommand::class.java -> {
                val cl = wrapInConstraintLayout(ctx, R.layout.message_command_data_view)

                cl.findOptional<TextView>(R.id.tv_message_label)?.textResource = R.string.parameter_label_eventID
                cl.findOptional<ImageView>(R.id.iv_message_icon)?.imageResource = R.drawable.ic_config_parameter_black_24dp
                cl.findOptional<EditText>(R.id.et_message)?.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS

                cl.findOptional<TextView>(R.id.tv_command_title)?.textResource = option.titleRes
                cl.findOptional<TextView>(R.id.tv_command_description)?.textResource = option.descriptionRes

                val parameterTil = cl.find<TextInputLayout>(R.id.til_message_container)
                val parameterEt = cl.find<EditText>(R.id.et_message)

                val eventIdLength = 32

                parameterEt.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        if (s != null) {
                            val str = s.toString()
                            if (str.length > eventIdLength && !str.contains('-')) {
                                val truncated = str.substring(startIndex = 0, endIndex = eventIdLength - 1)
                                parameterEt.setText(truncated)
                            } else if (str.length > 36 && str.contains('-')) {
                                val truncated = str.substring(startIndex = 0, endIndex = 35)
                                parameterEt.setText(truncated)
                            } else {
                                val modified = "[^A-F0-9-]".toRegex().replace(str.toUpperCase(Locale.ROOT), "")
                                if (str != modified) {
                                    parameterEt.setText(modified)
                                } else {
                                    if (!str.trim().isTextValidHex() && !str.contains('-')) {
                                        val errorText = ctx.getString(R.string.error_must_be_hex_byte)
                                        parameterTil.error = errorText
                                    } else if (str.contains('-') && str.length < 36) {
                                        val errorText = ctx.getString(R.string.error_must_be_valid_uuid)
                                        parameterTil.error = errorText
                                    } else if (str.length < eventIdLength){
                                        val errorText = ctx.getString(R.string.error_must_be_16_bytes)
                                        parameterTil.error = errorText
                                    } else {
                                        parameterTil.error = null
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
                        val eventId = parameterEt.text.toString().trim()
                        var uuid: UUID = UUID.randomUUID()
                        var eventIdBytes: ByteArray = ByteArray(16)

                        if (eventId == null) {
                            return false
                        }
                        if ((!eventId.isTextValidHex() || eventId.length != 32) && !eventId.contains('-')) {
                            return false
                        } else if (eventId.contains('-')) {
                            if (eventId.length != 36) {
                                return false
                            } else {
                                try {
                                    uuid = UUID.fromString(eventId)
                                } catch (e: IllegalArgumentException) {
                                    return false
                                }
                            }
                        } else {
                            eventIdBytes = eventId.hexStringToByteArray()
                        }


                        try {
                            val message: TCMPMessage
                            if (eventId.contains('-')) {
                                message = SetEventIdCommand(uuid)
                            } else {
                                message = SetEventIdCommand(eventIdBytes)
                            }
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
                .setPositiveButton(android.R.string.ok) { dialog, _ -> dialog?.dismiss() }
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
