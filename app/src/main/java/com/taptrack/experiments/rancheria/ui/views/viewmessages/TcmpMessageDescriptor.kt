package com.taptrack.experiments.rancheria.ui.views.viewmessages

import android.content.Context
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import androidx.annotation.StringRes
import com.taptrack.experiments.rancheria.R
import com.taptrack.experiments.rancheria.ui.toHex
import com.taptrack.experiments.rancheria.ui.toUnsigned
import com.taptrack.experiments.rancheria.ui.views.sendmessages.SetBLEPinCommand
import com.taptrack.tcmptappy.tappy.constants.TagTypes
import com.taptrack.tcmptappy2.StandardErrorResponse
import com.taptrack.tcmptappy2.StandardLibraryVersionResponse
import com.taptrack.tcmptappy2.TCMPMessage
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.AbstractBasicNfcMessage
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.AutoPollingConstants
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands.*
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.responses.*
import com.taptrack.tcmptappy2.commandfamilies.mifareclassic.AbstractMifareClassicMessage
import com.taptrack.tcmptappy2.commandfamilies.mifareclassic.KeySetting
import com.taptrack.tcmptappy2.commandfamilies.mifareclassic.commands.DetectMifareClassicCommand
import com.taptrack.tcmptappy2.commandfamilies.mifareclassic.commands.GetMifareClassicLibraryVersionCommand
import com.taptrack.tcmptappy2.commandfamilies.mifareclassic.commands.ReadMifareClassicCommand
import com.taptrack.tcmptappy2.commandfamilies.mifareclassic.responses.*
import com.taptrack.tcmptappy2.commandfamilies.ntag21x.AbstractNtag21xMessage
import com.taptrack.tcmptappy2.commandfamilies.ntag21x.commands.*
import com.taptrack.tcmptappy2.commandfamilies.ntag21x.responses.*
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.AbstractSystemMessage
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.commands.*
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.responses.*
import com.taptrack.tcmptappy2.commandfamilies.type4.AbstractType4Message
import com.taptrack.tcmptappy2.commandfamilies.type4.commands.*
import com.taptrack.tcmptappy2.commandfamilies.type4.responses.*
import timber.log.Timber
import java.nio.charset.Charset
import java.util.*
import kotlin.experimental.and

object TcmpMessageDescriptor {

    fun getCommandDescription(command: TCMPMessage,
                              ctx: Context): String {
        return when (command) {
            is AbstractBasicNfcMessage -> {
                getCommandDescriptionBasicNfc(command, ctx)
            }
            is AbstractSystemMessage -> {
                getCommandDescriptionSystem(command, ctx)
            }
            is AbstractMifareClassicMessage -> {
                getCommandDescriptionClassic(command, ctx)
            }
            is AbstractType4Message -> {
                getCommandDescriptionType4(command, ctx)
            }
            is AbstractNtag21xMessage -> {
                getCommandDescriptionNtag21x(command, ctx)
            }
            else -> {
                ctx.getString(R.string.unknown_command)
            }
        }
    }

    fun getCommandDescriptionBasicNfc(command: TCMPMessage, ctx: Context): String {
        return when (command) {
            is GetBasicNfcLibraryVersionCommand -> {
                ctx.getString(R.string.get_basic_nfc_lib_version)
            }
            is ScanNdefCommand -> {
                if (command.timeout.toInt() != 0) {
                    val form = ctx.getString(R.string.scan_ndef_seconds)
                    String.format(form, command.timeout.toUnsigned())
                } else {
                    ctx.getString(R.string.scan_ndef_indefinite)
                }
            }
            is StreamNdefCommand -> {
                if (command.timeout.toInt() != 0) {
                    val form = ctx.getString(R.string.stream_ndef_seconds)
                    String.format(form, (command.timeout.toUnsigned()))
                } else {
                    ctx.getString(R.string.stream_ndef_indefinite)
                }
            }
            is ScanTagCommand -> {
                if (command.timeout.toInt() != 0) {
                    val form = ctx.getString(R.string.scan_tag_seconds)
                    String.format(form, command.timeout.toUnsigned())
                } else {
                    ctx.getString(R.string.scan_tag_indefinitely)
                }

            }
            is StreamTagsCommand -> {
                if (command.timeout.toInt() != 0) {
                    val form = ctx.getString(R.string.stream_tag_seconds)
                    String.format(form, command.timeout.toUnsigned())
                } else {
                    ctx.getString(R.string.stream_tag_indefinitely)
                }
            }
            is WriteNdefTextRecordCommand -> {
                if (command.timeout.toInt() != 0) {
                    val form = ctx.getString(R.string.write_ndef_txt_seconds)
                    String.format(form, command.timeout.toUnsigned(), command.text)
                } else {
                    val form = ctx.getString(R.string.write_ndef_txt_indefinite)
                    String.format(form, command.text)
                }
            }
            is WriteNdefUriRecordCommand -> {
                val uri = NdefUriCodeUtils.decodeNdefUri(command.uriCode, command.uriBytes)
                if (command.timeout.toInt() != 0) {
                    val form = ctx.getString(R.string.write_ndef_uri_seconds)
                    String.format(form, command.timeout.toUnsigned(), uri)
                } else {
                    val form = ctx.getString(R.string.write_ndef_uri_indefinite)
                    String.format(form, uri)
                }
            }
            is AutoPollCommand -> {
                String.format(
                    ctx.getString(R.string.autopoll_for_tags),
                    when (command.scanModeIndicator) {
                        AutoPollingConstants.ScanModes.TYPE_1 -> ctx.getString(R.string.autopoll_tag_t1)
                        AutoPollingConstants.ScanModes.TYPE_2 -> ctx.getString(R.string.autopoll_tag_t2)
                        AutoPollingConstants.ScanModes.FELICIA -> ctx.getString(R.string.autopoll_tag_t3)
                        AutoPollingConstants.ScanModes.TYPE_4A -> ctx.getString(R.string.autopoll_tag_t4a)
                        AutoPollingConstants.ScanModes.TYPE_4B -> ctx.getString(R.string.autopoll_tag_t4b)
                        AutoPollingConstants.ScanModes.ALL -> ctx.getString(R.string.autopoll_tag_all)
                        else -> ""
                    },
                    when (command.heartBeatPeriod) {
                        0x00.toByte() -> ctx.getString(R.string.autopoll_for_tags_hb_disabled)
                        else -> String.format("%ds", command.heartBeatPeriod.toUnsigned())
                    },
                    if (command.isBuzzerDisabled) {
                        ctx.getString(R.string.autopoll_for_tags_buzzer_disabled)
                    } else {
                        ctx.getString(R.string.autopoll_for_tags_buzzer_enabled)
                    }
                )
            }
            is StopCommand -> {
                ctx.getString(R.string.stop_command)
            }
            is LockTagCommand -> {
                if (command.timeout.toInt() != 0) {
                    val form = ctx.getString(R.string.lock_tags_seconds)
                    String.format(form, command.timeout.toUnsigned())
                } else {
                    ctx.getString(R.string.lock_tags_indefinite)
                }
            }
            is InitiateTappyTagHandshakeCommand -> {
                ctx.getString(R.string.initiate_tappytag_handshake)
            }
            else -> {
                ctx.getString(R.string.unknown_command)
            }
        }
    }

    fun getCommandDescriptionSystem(command: TCMPMessage,
                                    ctx: Context): String {
        if (command is GetBatteryLevelCommand) {
            return ctx.getString(R.string.get_battery_level)
        } else if (command is GetFirmwareVersionCommand) {
            return ctx.getString(R.string.get_firmware_version)
        } else if (command is ConfigureOnboardScanCooldownCommand) {
            return when (command.cooldownSetting) {
                ConfigureOnboardScanCooldownCommand.CooldownSettings.DISABLE_COOLDOWN -> {
                    ctx.getString(R.string.disable_scan_cooldown)
                }
                ConfigureOnboardScanCooldownCommand.CooldownSettings.ENABLE_COOLDOWN -> {
                    ctx.getString(R.string.enable_scan_cooldown_with_tag_memory,command.bufferSize)
                }
                ConfigureOnboardScanCooldownCommand.CooldownSettings.NO_CHANGE-> {
                    ctx.getString(R.string.do_not_change_scan_cooldown)
                }
                else -> {
                    ctx.getString(R.string.unknown_command)
                }
            }
        } else if (command is ActivateGreenLEDCommand) {
            return ctx.getString(R.string.activate_green_led)
        } else if (command is DeactivateGreenLEDCommand) {
            return ctx.getString(R.string.deactivate_green_led)
        } else if (command is ActivateBlueLEDCommand) {
            return ctx.getString(R.string.activate_blue_led)
        } else if (command is DeactivateBlueLEDCommand) {
            return ctx.getString(R.string.deactivate_blue_led)
        } else if (command is ActivateRedLEDCommand) {
            return ctx.getString(R.string.activate_red_led)
        } else if (command is DeactivateRedLEDCommand) {
            return ctx.getString(R.string.deactivate_red_led)
        } else if (command is ActivateBuzzerCommand) {
            return ctx.getString(R.string.activate_buzzer)
        } else if (command is DeactivateBuzzerCommand) {
            return ctx.getString(R.string.deactivate_buzzer)
        } else if (command is SetConfigItemCommand) {
            return when (command.parameter) {
                SetConfigItemCommand.ParameterBytes.ENABLE_BLUETOOTH_PIN_PARING -> {
                    Timber.d("pin to set %s",command.multibyteValue.toHex())
                    val cmd = SetBLEPinCommand()
                    return try {
                        cmd.parsePayload(command.payload)
                        ctx.getString(R.string.enable_ble_pin_pairing,cmd.getPin())
                    } catch (e : Exception) {
                        Timber.e(e)
                        ctx.getString(R.string.enable_ble_pin_pairing_invalid_pin)
                    }
                }
                SetConfigItemCommand.ParameterBytes.DISABLE_BLUETOOTH_PIN_PAIRING -> {
                    ctx.getString(R.string.disable_ble_pin_pairing)
                }
                else -> if (command.multibyteValue.isEmpty()) {
                    ctx.getString(
                            R.string.set_config_item_no_value,
                            byteArrayOf(command.parameter).toHex()
                    )
                } else {
                    ctx.getString(
                            R.string.set_config_item_with_value,
                            byteArrayOf(command.parameter).toHex(),
                            command.multibyteValue.toHex()
                    )
                }
            }
        } else if (command is GetHardwareVersionCommand) {
            return ctx.getString(R.string.get_hardware_version)
        } else if (command is PingCommand) {
            return ctx.getString(R.string.ping_command)
        } else {
            return ctx.getString(R.string.unknown_command)
        }
    }

    fun getCommandDescriptionClassic(command: TCMPMessage,
                                     ctx: Context): String {
        if (command is DetectMifareClassicCommand) {
            val cmd = command
            if (cmd.timeout.toInt() == 0x00) {
                return ctx.getString(R.string.detect_classic_indefinite)
            } else {
                return String.format(ctx.getString(R.string.detect_classic_seconds), command.timeout.toUnsigned())
            }
        } else if (command is GetMifareClassicLibraryVersionCommand) {
            return ctx.getString(R.string.get_classic_version)
        } else if (command is ReadMifareClassicCommand) {
            val cmd = command
            if (cmd.timeout.toInt() == 0x00) {
                return String.format(ctx.getString(R.string.read_classic_indefinite),
                        cmd.startBlock,
                        cmd.endBlock,
                        if (cmd.keySetting == KeySetting.KEY_A) "A" else "B",
                        (cmd.key.toHex()))
            } else {
                return String.format(ctx.getString(R.string.read_classic_seconds),
                        cmd.startBlock,
                        cmd.endBlock,
                        if (cmd.keySetting == KeySetting.KEY_A) "A" else "B",
                        (cmd.key.toHex()),
                        cmd.timeout and 0xff.toByte())
            }
        } else {
            return ctx.getString(R.string.unknown_command)
        }
    }

    fun getCommandDescriptionType4(command: TCMPMessage,
                                   ctx: Context): String {
        if (command is DetectType4Command) {
            val cmd = command
            if (cmd.timeout.toInt() == 0x00) {
                return ctx.getString(R.string.detect_type4_indefinite)
            } else {
                return String.format(ctx.getString(R.string.detect_type4_seconds), command.timeout.toUnsigned())
            }
        } else if (command is DetectType4BCommand) {
            val cmd = command
            if (cmd.timeout.toInt() == 0x00) {
                return ctx.getString(R.string.detect_type4b_indefinite)
            } else {
                return String.format(ctx.getString(R.string.detect_type4b_seconds), command.timeout.toUnsigned())
            }

        } else if (command is DetectType4BSpecificAfiCommand) {
            val cmd = command
            if (cmd.timeout.toInt() == 0x00) {
                return String.format(ctx.getString(R.string.detect_type4b_afi_indefinite),
                        (byteArrayOf(cmd.afi).toHex()))
            } else {
                return String.format(ctx.getString(R.string.detect_type4b_afi_seconds),
                        cmd.timeout and 0xff.toByte(),
                        (byteArrayOf(cmd.afi).toHex()))
            }
        } else if (command is DetectActiveHCETargetCommand) {

            val cmd = command
            if (cmd.timeout.toInt() == 0x00) {
                return String.format(ctx.getString(R.string.detect_hce_target_indefinite))
            } else {
                return String.format(ctx.getString(R.string.detect_hce_target_seconds),
                        cmd.timeout and 0xff.toByte())
            }
        } else if (command is GetType4LibraryVersionCommand) {
            return ctx.getString(R.string.get_type4_version)
        } else if (command is TransceiveApduCommand) {
            return String.format(ctx.getString(R.string.send_apdu), command.apdu.toHex())
        } else {
            return ctx.getString(R.string.unknown_command)
        }
    }

    fun getCommandDescriptionNtag21x(command: TCMPMessage, ctx: Context): String {
        return when (command) {
            is ReadNdefWithPasswordBytesCommand -> {
                val duration = parseDurationFromTimeout(command.timeout, ctx)
                ctx.getString(R.string.read_password_bytes, duration)
            }
            is ReadNdefWithPasswordCommand -> {
                val duration = parseDurationFromTimeout(command.timeout, ctx)
                ctx.getString(R.string.read_password_string, duration)
            }
            is WriteTextNdefWithPasswordBytesCommand -> {
                val duration = parseDurationFromTimeout(command.timeout, ctx).capitalize(Locale.getDefault())
                val readProtection = parseProtectionTypeFromBoolean(command.readProtectionEnabled, ctx)
                ctx.getString(R.string.write_ndef_txt_password_bytes, duration, readProtection, command.text)
            }
            is WriteTextNdefWithPasswordCommand -> {
                val duration = parseDurationFromTimeout(command.timeout, ctx)
                val readProtection = parseProtectionTypeFromBoolean(command.readProtectionEnabled, ctx)
                ctx.getString(R.string.write_ndef_txt_password_string, duration, readProtection, command.text)
            }
            is WriteUriNdefWithPasswordBytesCommand -> {
                val duration = parseDurationFromTimeout(command.timeout, ctx)
                val readProtection = parseProtectionTypeFromBoolean(command.readProtectionEnabled, ctx)
                val uri = NdefUriCodeUtils.decodeNdefUri(command.uriCode, command.uri.toByteArray())
                ctx.getString(R.string.write_ndef_uri_password_bytes, duration, readProtection, uri)
            }
            is WriteUriNdefWithPasswordCommand -> {
                val duration = parseDurationFromTimeout(command.timeout, ctx)
                val readProtection = parseProtectionTypeFromBoolean(command.readProtectionEnabled, ctx)
                val uri = NdefUriCodeUtils.decodeNdefUri(command.uriCode, command.uri.toByteArray())
                ctx.getString(R.string.write_ndef_uri_password_string, duration, readProtection, uri)
            }
            else -> {
                ctx.getString(R.string.unknown_command)
            }
        }
    }

    fun getResponseDescription(response: TCMPMessage,
                               ctx: Context): String {
        when (response) {
            is StandardLibraryVersionResponse -> {
                return when (response) {
                    is HardwareVersionResponse -> {
                        parseStandardLibraryVersionResponse(ctx, R.string.resp_hardware, response)
                    }
                    is FirmwareVersionResponse -> {
                        parseStandardLibraryVersionResponse(ctx, R.string.resp_firmware, response)
                    }
                    is BasicNfcLibraryVersionResponse -> {
                        parseStandardLibraryVersionResponse(ctx, R.string.family_basicnfc, response)
                    }
                    is MifareClassicLibraryVersionResponse -> {
                        parseStandardLibraryVersionResponse(ctx, R.string.family_classic, response)
                    }
                    is Type4LibraryVersionResponse -> {
                        parseStandardLibraryVersionResponse(ctx, R.string.family_type4, response)
                    }
//                    is GetNtag21xCommandFamilyVersionResponse -> {
//                        // TODO: Implement this
//                    }
                    else -> {
                        parseStandardLibraryVersionResponse(ctx, R.string.family_unknown, response)
                    }
                }
            }
            is AbstractSystemMessage -> {
                return getSystemResponseDescription(response, ctx)
            }
            is AbstractBasicNfcMessage -> {
                return getBasicNfcResponseDescription(response, ctx)
            }
            is AbstractType4Message -> {
                return getType4ResponseDescription(response, ctx)
            }
            is AbstractMifareClassicMessage -> {
                return getClassicResponseDescription(response, ctx)
            }
            is AbstractNtag21xMessage -> {
                return getNtag21xResponseDescription(response, ctx)
            }
            is StandardErrorResponse -> {
                return parseStandardErrorResponse(
                    ctx,
                    R.string.family_unknown,
                    response
                )
            }
            else -> {
                return ctx.getString(R.string.unknown_response)
            }
        }
    }

    private fun getSystemResponseDescription(response: TCMPMessage,
                                             ctx: Context): String {
        if (response is CrcMismatchErrorResponse) {
            return ctx.getString(R.string.crc_error)
        } else if (response is GetBatteryLevelResponse) {
            return String.format(
                    ctx.getString(R.string.get_batt_response),
                    response.batteryLevelPercent)

        } else if (response is ImproperMessageFormatResponse) {
            return ctx.getString(R.string.improper_message_format_response)

        } else if (response is LcsMismatchErrorResponse) {
            return ctx.getString(R.string.lcs_mismatch_response)

        } else if (response is LengthMismatchErrorResponse) {
            return ctx.getString(R.string.length_check_failed)

        } else if (response is PingResponse) {
            return ctx.getString(R.string.ping_response)
        } else if (response is GreenLEDActivatedResponse) {
            return ctx.getString(R.string.activate_green_led)
        } else if (response is GreenLEDDeactivatedResponse) {
            return ctx.getString(R.string.deactivate_green_led)
        } else if (response is BlueLEDActivatedResponse) {
            return ctx.getString(R.string.blue_led_activated_response)
        } else if (response is BlueLEDDeactivatedResponse) {
            return ctx.getString(R.string.blue_led_deactivated_response)
        } else if (response is RedLEDActivatedResponse) {
            return ctx.getString(R.string.red_led_activated_response)
        } else if (response is RedLEDDeactivatedResponse) {
            return ctx.getString(R.string.red_led_deactivated_response)
        } else if (response is BuzzerActivatedResponse) {
            return ctx.getString(R.string.buzzer_activated_response)
        } else if (response is BuzzerDeactivatedResponse) {
            return ctx.getString(R.string.buzzer_deactivated_response)
        } else if (response is ConfigureOnboardScanCooldownResponse) {
            return if (response.isCooldownEnabled) {
                ctx.getString(
                        R.string.enabled_scan_cooldown_with_tag_memory_response,
                        response.bufferSize
                )
            } else {
                ctx.getString(
                        R.string.disabled_scan_cooldown_response
                )
            }
        } else if (response is ConfigItemResponse) {
            return ctx.getString(R.string.config_item_response)
        } else if (response is SystemErrorResponse) {
            val resp = response
            val errorRes: Int
            if (resp.errorCode == SystemErrorResponse.ErrorCodes.INVALID_PARAMETER) {
                errorRes = R.string.err_invalid_parameter
            } else if (resp.errorCode == SystemErrorResponse.ErrorCodes.UNSUPPORTED_COMMAND_FAMILY) {
                errorRes = R.string.err_unsupported_command_family
            } else if (resp.errorCode == SystemErrorResponse.ErrorCodes.TOO_FEW_PARAMETERS) {
                errorRes = R.string.err_too_few_parameters
            } else {
                return parseStandardErrorResponse(ctx,
                        R.string.family_system,
                        response as StandardErrorResponse)
            }

            return parseStandardErrorResponse(ctx,
                    R.string.family_system,
                    errorRes,
                    response as StandardErrorResponse)
        } else {
            return ctx.getString(R.string.unknown_response)
        }
    }

    private fun getBasicNfcResponseDescription(response: TCMPMessage,
                                               ctx: Context): String {
        when (response) {
            is NdefFoundResponse -> {
                return parseNdefFoundResponse(ctx, response)
            }
            is ScanTimeoutResponse -> {
                return ctx.getString(R.string.scan_timeout_response)
            }
            is TagFoundResponse -> {
                val resp = response
                return String.format(
                    ctx.getString(R.string.tag_found_response),
                    (resp.tagCode).toHex(),
                    parseTagType(ctx, resp.tagType))
            }
            is TagWrittenResponse -> {
                return String.format(
                    ctx.getString(R.string.tag_written_response),
                    (response.tagCode).toHex())
            }
            is TagLockedResponse -> {
                return String.format(
                    ctx.getString(R.string.tag_locked_response),
                    (response.tagCode).toHex())
            }
            is AutoPollTagEnteredResponse -> {
                return String.format(
                    ctx.getString(R.string.tag_entered_response),
                    when (response.detectedTagType) {
                        AutoPollingConstants.ResponseTagTypes.TYPE_1 -> ctx.getString(R.string.autopoll_tag_t1)
                        AutoPollingConstants.ResponseTagTypes.TYPE_2 -> ctx.getString(R.string.autopoll_tag_t2)
                        AutoPollingConstants.ResponseTagTypes.FELICIA -> ctx.getString(R.string.autopoll_tag_t3)
                        AutoPollingConstants.ResponseTagTypes.TYPE_4A -> ctx.getString(R.string.autopoll_tag_t4a)
                        AutoPollingConstants.ResponseTagTypes.TYPE_4B -> ctx.getString(R.string.autopoll_tag_t4b)
                        else -> ""
                    },
                    response.tagMetadata.toHex()
                )
            }
            is AutoPollTagExitedResponse -> {
                return String.format(
                    ctx.getString(R.string.tag_exited_response),
                    when (response.detectedTagType) {
                        AutoPollingConstants.ResponseTagTypes.TYPE_1 -> ctx.getString(R.string.autopoll_tag_t1)
                        AutoPollingConstants.ResponseTagTypes.TYPE_2 -> ctx.getString(R.string.autopoll_tag_t2)
                        AutoPollingConstants.ResponseTagTypes.FELICIA -> ctx.getString(R.string.autopoll_tag_t3)
                        AutoPollingConstants.ResponseTagTypes.TYPE_4A -> ctx.getString(R.string.autopoll_tag_t4a)
                        AutoPollingConstants.ResponseTagTypes.TYPE_4B -> ctx.getString(R.string.autopoll_tag_t4b)
                        else -> ""
                    },
                    response.tagMetadata.toHex()
                )
            }
            is BasicNfcErrorResponse -> {
                val resp = response
                val errorRes: Int
                if (resp.errorCode == BasicNfcErrorResponse.ErrorCodes.INVALID_PARAMETER) {
                    errorRes = R.string.err_invalid_parameter
                } else if (resp.errorCode == BasicNfcErrorResponse.ErrorCodes.POLLING_ERROR) {
                    errorRes = R.string.err_polling_error
                } else if (resp.errorCode == BasicNfcErrorResponse.ErrorCodes.TOO_FEW_PARAMETERS) {
                    errorRes = R.string.err_too_few_parameters
                } else if (resp.errorCode == BasicNfcErrorResponse.ErrorCodes.NDEF_MESSAGE_TOO_LARGE) {
                    errorRes = R.string.err_ndef_too_large
                } else if (resp.errorCode == BasicNfcErrorResponse.ErrorCodes.ERROR_CREATING_NDEF_CONTENT) {
                    errorRes = R.string.err_ndef_creation_error
                } else if (resp.errorCode == BasicNfcErrorResponse.ErrorCodes.ERROR_WRITING_NDEF_CONTENT) {
                    errorRes = R.string.err_ndef_writing_error
                } else if (resp.errorCode == BasicNfcErrorResponse.ErrorCodes.ERROR_LOCKING_TAG) {
                    errorRes = R.string.err_locking_error
                } else {
                    return parseStandardErrorResponse(ctx,
                        R.string.family_basicnfc,
                        response as StandardErrorResponse)
                }

                return parseStandardErrorResponse(ctx,
                    R.string.family_basicnfc,
                    errorRes,
                    response as StandardErrorResponse)
            }
            is TappyTagDataReceivedResponse -> {
                return ctx.getString(R.string.tappytag_response, response.payload.decodeToString())
            }
            else -> {
                return ctx.getString(R.string.unknown_response)
            }
        }
    }

    private fun getType4ResponseDescription(response: TCMPMessage,
                                            ctx: Context): String {
        if (response is APDUTransceiveSuccessfulResponse) {
            return String.format(ctx.getString(R.string.apdu_transceive_successful),
                    (response.apdu).toHex())
        } else if (response is Type4DetectedResponse) {
            val resp = response
            if (resp.ats != null && resp.ats.size != 0) {
                return String.format(ctx.getString(R.string.type4_detected_with_ats),
                        (resp.uid.toHex()),
                        (resp.ats.toHex()))
            } else {
                return String.format(ctx.getString(R.string.type4_detected_no_ats),
                        resp.uid.toHex())
            }
        } else if (response is Type4BDetectedResponse) {
            val resp = response
            val atqb = resp.atqb
            val attrib = resp.attrib
            if (atqb == null || attrib == null) {
                //this should be impossible
                return ctx.getString(R.string.type4b_detected_nothing)
            } else if (attrib.size == 0) {
                return String.format(ctx.getString(R.string.type4b_detected_no_attrib),
                        (resp.atqb.toHex()))
            } else {
                return String.format(ctx.getString(R.string.type4b_detected_w_attrib),
                        (resp.atqb.toHex()),
                        (resp.attrib.toHex()))
            }
        } else if (response is Type4TimeoutResponse) {
            return ctx.getString(R.string.type4_timeout)
        } else if (response is Type4ErrorResponse) {
            val resp = response
            val errorRes: Int
            if (resp.errorCode == Type4ErrorResponse.ErrorCodes.INVALID_PARAMETER) {
                errorRes = R.string.err_invalid_parameter
            } else if (resp.errorCode == Type4ErrorResponse.ErrorCodes.TOO_FEW_PARAMETERS) {
                errorRes = R.string.err_too_few_parameters
            } else if (resp.errorCode == Type4ErrorResponse.ErrorCodes.TOO_MANY_PARAMETERS) {
                errorRes = R.string.err_too_many_parameters
            } else if (resp.errorCode == Type4ErrorResponse.ErrorCodes.TRANSCEIVE_ERROR) {
                errorRes = R.string.err_transceive_error
            } else if (resp.errorCode == Type4ErrorResponse.ErrorCodes.NO_TAG_PRESENT) {
                errorRes = R.string.err_no_tag_present
            } else if (resp.errorCode == Type4ErrorResponse.ErrorCodes.NFC_CHIP_ERROR) {
                errorRes = R.string.err_nfc_chip_error
            } else {
                return parseStandardErrorResponse(ctx,
                        R.string.family_type4,
                        response as StandardErrorResponse)
            }

            return parseStandardErrorResponse(ctx,
                    R.string.family_type4,
                    errorRes,
                    response as StandardErrorResponse)
        } else if (response is ActiveHCETargetDetectedResponse) {
            val resp = response
            if (resp.firstCommand != null && resp.firstCommand.size != 0) {
                return String.format(ctx.getString(R.string.type4_hce_targetdetected_with_cmd),
                        (resp.firstCommand.toHex()))
            } else {
                return String.format(ctx.getString(R.string.type4_hce_targetdetected_no_cmd))
            }
        } else {
            return ctx.getString(R.string.unknown_response)
        }
    }

    private fun getClassicResponseDescription(response: TCMPMessage,
                                              ctx: Context): String {
        if (response is MifareClassicDetectedResponse) {
            val resp = response
            if (resp.type == MifareClassicDetectedResponse.ClassicType.CLASSIC_1K) {
                return String.format(ctx.getString(R.string.mifareclassic_1k_detected), (resp.uid.toHex()))
            } else if (resp.type == MifareClassicDetectedResponse.ClassicType.CLASSIC_4K) {
                return String.format(ctx.getString(R.string.mifareclassic_4k_detected), (resp.uid.toHex()))
            } else {
                return ctx.getString(R.string.mifareclassic_unkcap_detected)
            }

        } else if (response is MifareClassicTimeoutResponse) {
            return ctx.getString(R.string.mifareclassic_timeout)
        } else if (response is MifareClassicReadSuccessResponse) {
            val res = response
            return String.format(ctx.getString(R.string.mifareclassic_read_success),
                    res.uid.toHex(),
                    res.startBlock,
                    res.endBlock,
                    res.data.toHex())
        } else if (response is MifareClassicLibraryErrorResponse) {
            val resp = response
            val errorRes: Int
            if (resp.errorCode == MifareClassicLibraryErrorResponse.ErrorCodes.INVALID_PARAMETER) {
                errorRes = R.string.err_invalid_parameter
            } else if (resp.errorCode == MifareClassicLibraryErrorResponse.ErrorCodes.TOO_FEW_PARAMETERS) {
                errorRes = R.string.err_too_few_parameters
            } else if (resp.errorCode == MifareClassicLibraryErrorResponse.ErrorCodes.TOO_MANY_PARAMETERS) {
                errorRes = R.string.err_too_many_parameters
            } else if (resp.errorCode == MifareClassicLibraryErrorResponse.ErrorCodes.POLLING_ERROR) {
                errorRes = R.string.err_polling_error
            } else if (resp.errorCode == MifareClassicLibraryErrorResponse.ErrorCodes.TAG_READ_ERROR) {
                errorRes = R.string.err_tag_read_error
            } else if (resp.errorCode == MifareClassicLibraryErrorResponse.ErrorCodes.INVALID_BLOCK_ORDER) {
                errorRes = R.string.err_invalid_block_order
            } else if (resp.errorCode == MifareClassicLibraryErrorResponse.ErrorCodes.AUTHENTICATION_ERROR) {
                errorRes = R.string.err_authentication_error
            } else if (resp.errorCode == MifareClassicLibraryErrorResponse.ErrorCodes.INVALID_BLOCK_NO) {
                errorRes = R.string.err_invalid_block_number
            } else if (resp.errorCode == MifareClassicLibraryErrorResponse.ErrorCodes.INVALID_KEY_NO) {
                errorRes = R.string.err_invalid_key_number
            } else {
                return parseStandardErrorResponse(ctx,
                        R.string.family_classic,
                        response as StandardErrorResponse)
            }

            return parseStandardErrorResponse(ctx,
                    R.string.family_classic,
                    errorRes,
                    response as StandardErrorResponse)
        } else {
            return ctx.getString(R.string.unknown_response)
        }
    }

    private fun getNtag21xResponseDescription(response: TCMPMessage, ctx: Context): String {
        return when (response) {
            is Ntag21xReadSuccessResponse -> {
                parseNtag21xReadSuccessResponse(ctx, response)
            }
            is Ntag21xWriteSuccessResponse -> {
                ctx.getString(R.string.ntag_21x_password_tag_written_response, response.uid.toHex())
            }
            is Ntag21xPollingTimeoutResponse -> {
                ctx.getString(R.string.ntag_21x_polling_timeout)
            }
            is Ntag21xApplicationErrorResponse -> {
                ctx.getString(R.string.ntag_21x_application_error_generic, response.appErrorCode, response.errorDescription)
            }
            else -> {
                ctx.getString(R.string.unknown_response)
            }
        }
    }

    private fun parseStandardErrorResponse(ctx: Context,
                                           @StringRes libraryName: Int,
                                           @StringRes description: Int,
                                           response: StandardErrorResponse): String {
        return String.format(
                ctx.getString(R.string.standard_error_response_with_description),
                response.errorCode,
                response.internalErrorCode,
                response.readerStatus,
                response.errorMessage,
                ctx.getString(libraryName),
                ctx.getString(description))
    }

    private fun parseStandardErrorResponse(ctx: Context,
                                           @StringRes libraryName: Int,
                                           response: StandardErrorResponse): String {
        return String.format(
                ctx.getString(R.string.standard_error_response),
                response.errorCode,
                response.internalErrorCode,
                response.readerStatus,
                response.errorMessage,
                ctx.getString(libraryName))
    }

    private fun parseStandardLibraryVersionResponse(ctx: Context, @StringRes libraryName: Int, response: StandardLibraryVersionResponse): String {
        return String.format(
                ctx.getString(R.string.standard_version_response),
                response.majorVersion.toUnsigned(),
                response.minorVersion.toUnsigned(),
                ctx.getString(libraryName))
    }

    private fun parseNdefFoundResponse(ctx: Context, resp: NdefFoundResponse): String {
        val msg = resp.message
        val records = msg.records
        if (records.size == 0) {
            return ctx.getString(R.string.ndef_no_record)
        } else if (records.size == 1) {
            return String.format(ctx.getString(R.string.ndef_found_response_single_record),
                    resp.tagCode.toHex(),
                    parseTagType(ctx, resp.tagType),
                    parseNdefRecord(ctx, records[0]))
        } else {
            return String.format(ctx.getString(R.string.ndef_found_response_multi_record),
                    resp.tagCode.toHex(),
                    parseTagType(ctx, resp.tagType),
                    parseNdefRecord(ctx, records[0]))
        }
    }

    private fun parseNtag21xReadSuccessResponse(ctx: Context, resp: Ntag21xReadSuccessResponse): String {
        val raw = resp.ndefMessage
        val msg = NdefMessage(raw)
        val ndefFoundResp = NdefFoundResponse(resp.uid, resp.tagType, msg)
        return parseNdefFoundResponse(ctx, ndefFoundResp)
    }

    private fun parseNdefRecord(ctx: Context, record: NdefRecord): String {
        if (record.tnf == NdefRecord.TNF_WELL_KNOWN) {
            if (Arrays.equals(record.type, NdefRecord.RTD_URI)) {
                return parseWellKnownUriRecord(ctx, record)
            } else if (Arrays.equals(record.type, NdefRecord.RTD_TEXT)) {
                return parseWellKnownTextRecord(ctx, record)
            } else {
                return parseGenericNdefRecord(ctx, record)
            }
        } else {
            return parseGenericNdefRecord(ctx, record)
        }
    }

    private fun parseWellKnownTextRecord(ctx: Context, record: NdefRecord): String {

        val payload = record.payload

        val status = payload[0] and 0xff.toByte()
        val languageCodeLength = status and 0x1F
        //not needed currently
        //String languageCode = new String(payload, 1, languageCodeLength);

        val textEncoding = if ((status and 0x80.toByte()) != 0.toByte()) Charset.forName("UTF-16") else Charset.forName("UTF-8")
        val field = String(payload, 1 + languageCodeLength, payload.size - languageCodeLength - 1, textEncoding)
        return ctx.getString(R.string.ndef_record_text_display_format, field)
    }

    private fun parseWellKnownUriRecord(ctx: Context, record: NdefRecord): String {
        val payload = record.payload
        if (payload.size > 1) {
            val uriCode = payload[0]
            val uri = ByteArray(payload.size - 1)
            System.arraycopy(payload, 1, uri, 0, payload.size - 1)
            return ctx.getString(R.string.ndef_record_uri_display_format, NdefUriCodeUtils.decodeNdefUri(uriCode, uri))
        } else {
            return parseGenericNdefRecord(ctx, record)
        }
    }

    private fun parseGenericNdefRecord(ctx: Context, record: NdefRecord): String {
        return String.format(ctx.getString(R.string.ndef_record_generic_display_format),
                parseTnf(ctx, record),
                parseType(ctx, record),
                (record.payload.toHex()))
    }

    private fun parseTnf(ctx: Context, record: NdefRecord): String {
        val tnf = record.tnf
        when (tnf) {
            NdefRecord.TNF_ABSOLUTE_URI -> return ctx.getString(R.string.tnf_abs_uri)
            NdefRecord.TNF_EMPTY -> return ctx.getString(R.string.tnf_empty)
            NdefRecord.TNF_EXTERNAL_TYPE -> return ctx.getString(R.string.tnf_external)
            NdefRecord.TNF_MIME_MEDIA -> return ctx.getString(R.string.tnf_mime_media)
            NdefRecord.TNF_UNCHANGED -> return ctx.getString(R.string.tnf_unchanged)
            NdefRecord.TNF_WELL_KNOWN -> return ctx.getString(R.string.tnf_well_known)
            else -> return ctx.getString(R.string.tnf_unknown)
        }
    }

    private fun parseType(ctx: Context, record: NdefRecord): String {
        val type = record.type
        if (Arrays.equals(type, NdefRecord.RTD_URI))
            return ctx.getString(R.string.rtd_uri)
        else if (Arrays.equals(type, NdefRecord.RTD_ALTERNATIVE_CARRIER))
            return ctx.getString(R.string.rtd_alt_carrier)
        else if (Arrays.equals(type, NdefRecord.RTD_HANDOVER_CARRIER))
            return ctx.getString(R.string.rtd_handover_carrier)
        else if (Arrays.equals(type, NdefRecord.RTD_HANDOVER_REQUEST))
            return ctx.getString(R.string.rtd_handover_request)
        else if (Arrays.equals(type, NdefRecord.RTD_HANDOVER_SELECT))
            return ctx.getString(R.string.rtd_handover_select)
        else if (Arrays.equals(type, NdefRecord.RTD_SMART_POSTER))
            return ctx.getString(R.string.rtd_smart_poster)
        else if (Arrays.equals(type, NdefRecord.RTD_TEXT))
            return ctx.getString(R.string.rtd_text)
        else if (record.tnf == NdefRecord.TNF_MIME_MEDIA)
            return String(type)
        else
            return type.toHex()
    }

    private fun parseTagType(ctx: Context, flag: Byte): String {
        when (flag) {
            TagTypes.MIFARE_ULTRALIGHT -> {
                return ctx.getString(R.string.ultralight_title)
            }
            TagTypes.NTAG203 -> {
                return ctx.getString(R.string.ntag203_title)
            }
            TagTypes.MIFARE_ULTRALIGHT_C -> {
                return ctx.getString(R.string.ultralight_c_title)
            }
            TagTypes.MIFARE_STD_1K -> {
                return ctx.getString(R.string.std_1k_title)
            }
            TagTypes.MIFARE_STD_4K -> {
                return ctx.getString(R.string.std_4k_title)
            }
            TagTypes.MIFARE_DESFIRE_EV1_2K -> {
                return ctx.getString(R.string.desfire_ev1_2k_title)
            }
            TagTypes.TYPE_2_TAG -> {
                return ctx.getString(R.string.unk_type2_title)
            }
            TagTypes.MIFARE_PLUS_2K_CL2 -> {
                return ctx.getString(R.string.plus_2k_title)
            }
            TagTypes.MIFARE_PLUS_4K_CL2 -> {
                return ctx.getString(R.string.plus_4k_title)
            }
            TagTypes.MIFARE_MINI -> {
                return ctx.getString(R.string.mini_title)
            }
            TagTypes.OTHER_TYPE4 -> {
                return ctx.getString(R.string.other_type4_title)
            }
            TagTypes.MIFARE_DESFIRE_EV1_4K -> {
                return ctx.getString(R.string.desfire_ev1_4k_title)
            }
            TagTypes.MIFARE_DESFIRE_EV1_8K -> {
                return ctx.getString(R.string.desfire_ev1_8k)
            }
            TagTypes.MIFARE_DESFIRE -> {
                return ctx.getString(R.string.desfire_title)
            }
            TagTypes.TOPAZ_512 -> {
                return ctx.getString(R.string.topaz_512_title)
            }
            TagTypes.NTAG_210 -> {
                return ctx.getString(R.string.ntag_210_title)
            }
            TagTypes.NTAG_212 -> {
                return ctx.getString(R.string.ntag_212_title)
            }
            TagTypes.NTAG_213 -> {
                return ctx.getString(R.string.ntag_213_title)
            }
            TagTypes.NTAG_215 -> {
                return ctx.getString(R.string.ntag_215_title)
            }
            TagTypes.NTAG_216 -> {
                return ctx.getString(R.string.ntag_216_title)
            }
            TagTypes.NO_TAG -> {
                return ctx.getString(R.string.no_tag_title)
            }
//            TagTypes.TAG_UNKNOWN,
            else -> {
                return ctx.getString(R.string.unk_type_title)
            }
        }
    }
}

private fun parseDurationFromTimeout(timeout: Byte, ctx: Context): String {
    return when (timeout.toInt()) {
        0x00 -> ctx.getString(R.string.indefinitely)
        0x01 -> ctx.getString(R.string.for_duration_singular)
        else -> ctx.getString(R.string.for_duration_plural, timeout.toUnsigned())
    }
}

private fun parseProtectionTypeFromBoolean(boolean: Boolean, ctx: Context): String {
    return if (boolean) ctx.getString(R.string.read_and_write_protection) else ctx.getString(R.string.write_protection)
}
