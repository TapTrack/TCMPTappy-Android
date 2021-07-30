package com.taptrack.experiments.rancheria.ui.views.viewmessages

// In the Tappy Demo App the wristcoinpos command family is vendored in the project to avoid build conflicts
// For developers who want to use the wristcoinpos command family please make sure to include the dependency in your
// projects build.gradle file and DO NOT copy paste the "wristcoinpos" package into your own project
import android.content.Context
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import androidx.annotation.StringRes
import com.taptrack.experiments.rancheria.R
import com.taptrack.experiments.rancheria.ui.toHex
import com.taptrack.experiments.rancheria.ui.toUnsigned
import com.taptrack.experiments.rancheria.ui.views.sendmessages.SetBLEPinCommand
import com.taptrack.experiments.rancheria.wristcoinpos.AbstractWristCoinPOSMessage
import com.taptrack.experiments.rancheria.wristcoinpos.AppScratchState
import com.taptrack.experiments.rancheria.wristcoinpos.commands.*
import com.taptrack.experiments.rancheria.wristcoinpos.responses.*
import com.taptrack.kotlin_tlv.ByteUtils.arrayToInt
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
import com.taptrack.tcmptappy2.commandfamilies.ntag21x.responses.Ntag21xApplicationErrorResponse
import com.taptrack.tcmptappy2.commandfamilies.ntag21x.responses.Ntag21xPollingTimeoutResponse
import com.taptrack.tcmptappy2.commandfamilies.ntag21x.responses.Ntag21xReadSuccessResponse
import com.taptrack.tcmptappy2.commandfamilies.ntag21x.responses.Ntag21xWriteSuccessResponse
import com.taptrack.tcmptappy2.commandfamilies.standalonecheckin.AbstractStandaloneCheckinMessage
import com.taptrack.tcmptappy2.commandfamilies.standalonecheckin.commands.*
import com.taptrack.tcmptappy2.commandfamilies.standalonecheckin.responses.*
import com.taptrack.tcmptappy2.commandfamilies.standalonecheckin.utils.MicrochipRtcFormatter
import com.taptrack.tcmptappy2.commandfamilies.stmicroM24SR02.AbstractStMicroMessage
import com.taptrack.tcmptappy2.commandfamilies.stmicroM24SR02.commands.*
import com.taptrack.tcmptappy2.commandfamilies.stmicroM24SR02.responses.*
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.AbstractSystemMessage
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.commands.*
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.responses.*
import com.taptrack.tcmptappy2.commandfamilies.type4.AbstractType4Message
import com.taptrack.tcmptappy2.commandfamilies.type4.commands.*
import com.taptrack.tcmptappy2.commandfamilies.type4.responses.*
import com.taptrack.tcmptappy2.commandfamilies.type4.responses.Type4ErrorResponse
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
            is AbstractStMicroMessage -> {
                getCommandDescriptionSTMicro(command, ctx)
            }
            is AbstractStandaloneCheckinMessage -> {
                getCommandDescriptionStandaloneCheckin(command, ctx)
            }
            is AbstractWristCoinPOSMessage -> {
                getCommandDescriptionWristcoinPOS(command, ctx)
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
            is EmulateTextRecordCommand -> {
                if (command.timeout.toInt() != 0) {
                    val form = ctx.getString(R.string.emulate_ndef_txt_seconds)
                    String.format(form, command.timeout.toUnsigned(), command.text)
                } else {
                    val form = ctx.getString(R.string.emulate_ndef_txt_indefinite)
                    String.format(form, command.text)
                }
            }
            is EmulateUriRecordCommand -> {
                val uri = NdefUriCodeUtils.decodeNdefUri(command.uriPrefixCode, command.uriBytes)
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
            is AutoPollNdefCommand -> {
                String.format(
                    ctx.getString(R.string.autopollndef_for_tags),
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
                if(command.duration.toInt() == 0){
                    ctx.getString(R.string.initiate_tappytag_handshake, "Indefinite", command.responseData.decodeToString(), command.responseData.toHex())
                }else{
                    ctx.getString(R.string.initiate_tappytag_handshake, command.duration.toString(), command.responseData.decodeToString(), command.responseData.toHex())
                }
            }
            is DispatchTagCommand -> {
                if (command.timeout.toInt() != 0) {
                    val form = ctx.getString(R.string.dispatch_tag_second)
                    String.format(form, command.timeout.toUnsigned())
                } else {
                    ctx.getString(R.string.dispatch_tag_indefinite)
                }
            }
            is DispatchTagsCommand -> {
                if (command.timeout.toInt() != 0) {
                    val form = ctx.getString(R.string.dispatch_tags_seconds)
                    String.format(form, command.timeout.toUnsigned())
                } else {
                    ctx.getString(R.string.dispatch_tags_indefinite)
                }
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
                SetConfigItemCommand.ParameterBytes.CLEAR_BLUETOOTH_BONDING_CACHE -> {
                    ctx.getString(R.string.clear_ble_bonding_cache)
                }
                else -> if (command.multibyteValue.isEmpty()) {
                    ctx.getString(
                            R.string.set_config_item_no_value,
                            byteArrayOf(command.parameter).toHex()
                    )
                } else {
                    ctx.getString(
                            R.string.set_config_item_with_value,
                            byteArrayOf(command.parameter),
                            command.multibyteValue.toHex()
                    )
                }
            }
        } else if (command is GetHardwareVersionCommand) {
            return ctx.getString(R.string.get_hardware_version)
        } else if (command is PingCommand) {
            return ctx.getString(R.string.ping_command)
        } else if (command is GetBootConfigCommand) {
            return ctx.getString(R.string.get_boot_config)
        } else if (command is SetBootConfigCommand) {
            return ctx.getString(R.string.set_boot_config, command.payload.toHex())
        } else {
            return ctx.getString(R.string.unknown_command)
        }
    }

    fun getCommandDescriptionClassic(command: TCMPMessage, ctx: Context): String {
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

    fun getCommandDescriptionSTMicro (command: TCMPMessage, ctx: Context): String {
        return when(command){
            is ChangeReadNdefPasswordCommand -> {
                ctx.getString(R.string.change_read_ndef_password, command.timeout.toString(), command.currentPassword.toHex(), command.newPassword.toHex())
            }
            is ChangeWriteNdefPasswordCommand -> {
                ctx.getString(R.string.change_write_ndef_password, command.timeout.toString(), command.currentPassword.toHex(), command.newPassword.toHex())
            }
            is GetCommandFamilyVersionCommand -> {
                ctx.getString(R.string.get_command_family_version)
            }
            is GetI2CSettingCommand -> {
                ctx.getString(R.string.get_I2C_setting, command.timeout.toString())
            }
            is LockNdefReadAccessCommand -> {
                ctx.getString(R.string.lock_ndef_read_access, command.timeout.toString(), command.password.toHex())
            }
            is LockNdefWriteAccessCommand -> {
                ctx.getString(R.string.lock_ndef_write_access, command.timeout.toString(), command.password.toHex())
            }
            is UnlockNdefReadAccessCommand -> {
                ctx.getString(R.string.unlock_ndef_read_access, command.timeout.toString(), command.password.toHex())
            }
            is UnlockNdefWriteAccessCommand -> {
                ctx.getString(R.string.unlock_ndef_write_access, command.timeout.toString(), command.password.toHex())
            }
            is PermanentlyLockNdefWriteAccessCommand -> {
                ctx.getString(R.string.perma_lock_ndef_write_access, command.timeout.toString(), command.password.toHex())
            }
            is ReadNdefMsgWithPasswordCommand -> {
                ctx.getString(R.string.read_ndef_msg_with_password, command.timeout.toString(), command.password.toHex())
            }
            is WriteNdefWithPasswordCommand -> {
                ctx.getString(R.string.write_ndef_msg_with_password, command.timeout.toString(), command.password.toHex(), command.content.toHex())
            }
            else -> {
                ctx.getString(R.string.unknown_command)
            }
        }
    }

    fun getCommandDescriptionStandaloneCheckin (command: TCMPMessage, ctx: Context): String {
        return when(command){
            is GetCheckinCountCommand -> {
                ctx.getString(R.string.get_checkin_count)
            }
            is GetCheckinsCommand -> {
                ctx.getString(R.string.get_checkins, command.firstCheckin.toString(), command.secondCheckin.toString())
            }
            is GetStandaloneCheckinFamilyVersionCommand -> {
                ctx.getString(R.string.get_standalone_checkin_fam_version)
            }
            is GetStationInfoCommand -> {
                ctx.getString(R.string.get_station_info)
            }
            is GetTimeAndDateCommand -> {
                ctx.getString(R.string.get_time_and_date)
            }
            is ReadCheckinCardUidCommand -> {
                if(command.timeout == 0x00.toByte()){
                    ctx.getString(R.string.read_checkin_card_uid_indefinitely)
                } else {
                    ctx.getString(R.string.read_checkin_card_uid_seconds, command.timeout.toString())
                }
            }
            is ResetCheckinsCommand -> {
                ctx.getString(R.string.reset_checkins)
            }
            is SetStationIdCommand -> {
                ctx.getString(R.string.set_station_id, command.stationId.toString())
            }
            is SetStationNameCommand -> {
                ctx.getString(R.string.set_station_name, command.name)
            }
            is SetTimeAndDateCommand -> {
                val bytes: ByteArray = command.payload
                ctx.getString(
                    R.string.set_time_and_date, formatTimeAndDate(bytes[0].toString()), formatTimeAndDate(bytes[1].toString()), formatTimeAndDate(bytes[2].toString()),
                    formatTimeAndDate(bytes[3].toString()), formatTimeAndDate(bytes[4].toString()),
                    formatTimeAndDate(bytes[5].toString()), formatTimeAndDate(bytes[6].toString())
                )
            }
            else -> {
                ctx.getString(R.string.unknown_command)
            }
        }
    }

    fun getCommandDescriptionWristcoinPOS (command: TCMPMessage, ctx: Context): String {
        return when(command){
            is DebitWristbandFullRespCommand -> {
                ctx.getString(R.string.debit_wristband_full, formatCentavosAmount(command.debitAmountCentavos))
            }
            is DebitWristbandShortRespCommand -> {
                ctx.getString(R.string.debit_wristband_short, formatCentavosAmount(command.debitAmountCentavos))
            }
            is GetWristCoinPOSCommandFamilyVersionCommand -> {
                ctx.getString(R.string.get_wristcoinPOS_command_family_version)
            }
            is GetWristbandStatusCommand -> {
                ctx.getString(R.string.get_wristband_status)
            }
            is SetEventIdCommand -> {
                ctx.getString(R.string.set_event_ID, command.eventId.toHex())
            }
            is TopupWristbandFullRespCommand -> {
                ctx.getString(R.string.topup_wristband_full, formatCentavosAmount(command.topupAmountCentavos))
            }
            is TopupWristbandShortRespCommand -> {
                ctx.getString(R.string.topup_wristband_short, formatCentavosAmount(command.topupAmountCentavos))
            }
            is CloseoutWristbandCommand -> {
                ctx.getString(R.string.closeout_wristband)
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
                    is StandaloneCheckinLibraryVersionResponse -> {
                        parseStandardLibraryVersionResponse(ctx, R.string.family_standalone, response)
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
            is AbstractStMicroMessage -> {
                return getSTMicroM24SR02ResponseDescription(response, ctx)
            }
            is AbstractStandaloneCheckinMessage -> {
                return getStandaloneCheckinResponseDescription(response, ctx)
            }
            is AbstractWristCoinPOSMessage -> {
                return getWristCoinPOSResponseDescription(response, ctx)
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
        } else if (response is SetBootConfigResponse) {
            return ctx.getString(R.string.set_boot_config_response)
        } else if (response is GetBootConfigResponse) {
            return ctx.getString(R.string.get_boot_config_response, response.payload.toHex())
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
                if(response.dataReceived.isNotEmpty() && response.timeout.isNotEmpty()){
                    return ctx.getString(R.string.tappytag_response,  response.dataReceived.decodeToString(), response.dataReceived.toHex(), arrayToInt(response.timeout).toString())
                } else if(response.dataReceived.isNotEmpty()){
                    return ctx.getString(R.string.tappytag_response2,  response.dataReceived.decodeToString(), response.dataReceived.toHex())
                } else if(response.timeout.isNotEmpty()){
                    return ctx.getString(R.string.tappytag_response3, arrayToInt(response.timeout).toString())
                } else{
                    return ctx.getString(R.string.tappytag_response4)
                }
            }
            is ResponseDataTransmitted -> {
                return ctx.getString(R.string.tappytag_response_transmitted, response.dataOffset.toString(), response.dataLength.toString())
            }
            is EmulationSuccessResponse -> {
                return ctx.getString(R.string.emulation_success_response)
            }
            is EmulationStoppedResponse -> {
                return ctx.getString(R.string.emulation_stopped_response)
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

    private fun getSTMicroM24SR02ResponseDescription(response: TCMPMessage, ctx: Context): String {
        return when(response){
            is PollingTimeoutResponse -> {
                ctx.getString(R.string.polling_timeout_response)
            }
            is com.taptrack.tcmptappy2.commandfamilies.stmicroM24SR02.responses.Type4ErrorResponse -> {
                ctx.getString(R.string.type4_error_response, byteArrayOf(response.sw2).toHex(), byteArrayOf(response.sw1).toHex())
            }
            is ChangeReadNdefPasswordResponse -> {
                ctx.getString(R.string.change_read_ndef_password_response, response.uid.size.toString(), response.uid.toHex())
            }
            is ChangeWriteNdefPasswordResponse -> {
                ctx.getString(R.string.change_write_ndef_password_response, response.uid.size.toString(), response.uid.toHex())
            }
            is GetCommandFamilyVersionResponse -> {
                ctx.getString(R.string.get_command_family_version_response, response.majorVersion.toString(),response.minorVersion.toString())
            }
            is GetI2CProtectionSettingResponse -> {
                ctx.getString(R.string.get_I2C_setting_response, response.protecSetting.toString(), response.uid.size.toString(), response.uid.toHex())
            }
            is PasswordLockNdefReadResponse -> {
                ctx.getString(R.string.lock_ndef_read_access_response, response.uid.size.toString(), response.uid.toHex())
            }
            is PasswordLockNdefWriteResponse -> {
                ctx.getString(R.string.lock_ndef_write_access_response, response.uid.size.toString(), response.uid.toHex())
            }
            is PasswordUnlockNdefReadResponse -> {
                ctx.getString(R.string.unlock_ndef_read_access_response, response.uid.size.toString(), response.uid.toHex())
            }
            is PasswordUnlockNdefWriteResponse -> {
                ctx.getString(R.string.unlock_ndef_write_access_response, response.uid.size.toString(), response.uid.toHex())
            }
            is PermanentNdefWriteLockResponse -> {
                ctx.getString(R.string.perma_lock_ndef_write_access_response, response.uid.size.toString(), response.uid.toHex())
            }
            is FoundNdefMessageResponse -> {
                ctx.getString(R.string.found_ndef_message_response, response.uid.size.toString(), response.uid.toHex(), response.content.size.toString(), response.content.decodeToString())
            }
            is NdefMessageWrittenResponse -> {
                ctx.getString(R.string.ndef_message_written_response, response.uid.size.toString(), response.uid.toHex())
            }
            else -> {
                ctx.getString(R.string.unknown_response)
            }
        }
    }
    private fun getStandaloneCheckinResponseDescription(response: TCMPMessage, ctx: Context): String  {
        return when (response){
            is CheckinDataResponse -> {
                try {
                    var responseStr: String = ""
                    for(i in 0 until (response.size)/12) {
                        val uid = response.uids[i]
                        val timestamp = response.timestamp[i]
                        val formatter = MicrochipRtcFormatter(timestamp)
                        val timeBytes = formatter.getMicrochipRtcFormatted(false,false)
                        responseStr += "Checkin $i\n" + ctx.getString(R.string.checkin_data_response,
                            uid.toHex(), formatTimeAndDate(timeBytes[0].toString()), formatTimeAndDate(timeBytes[1].toString()),
                            formatTimeAndDate(timeBytes[2].toString()), formatTimeAndDate(timeBytes[3].toString()), formatTimeAndDate(timeBytes[4].toString())
                        )
                        if(i != response.size/12-1){
                            responseStr += "\n"
                        }
                    }
                    responseStr
                } catch (e: Exception){
                    "Error forming response message"
                }
            }
            is CheckinsResetResponse -> {
                ctx.getString(R.string.checkins_reset_reponse)
            }
            is CheckinTagUidResponse -> {
                ctx.getString(R.string.checkin_tag_uid_response, response.uid.toHex())
            }
            is NoCheckinsPresentResponse -> {
                ctx.getString(R.string.no_checkins_present_response)
            }
            is NumberOfCheckinsResponse -> {
                ctx.getString(R.string.number_of_checkins_response, response.numberOfCheckins.toString())
            }
            is StandaloneCheckinErrorResponse -> {
                ctx.getString(R.string.standalone_checkin_error_response)
            }
            is StandaloneCheckinLibraryVersionResponse -> {
                ctx.getString(R.string.standalone_checkin_library_version_response, response.majorVersion.toString(), response.minorVersion.toString())
            }
            is StationIdSetSuccessResponse -> {
                ctx.getString(R.string.station_id_set_success_response)
            }
            is StationInfoResponse -> {
                ctx.getString(R.string.station_info_response, response.id.toString(), response.name)
            }
            is StationNameSetSuccessResponse -> {
                ctx.getString(R.string.station_name_set_success_response)
            }
            is TagDetectionTimedOut -> {
                ctx.getString(R.string.tag_detection_timeout_response)
            }
            is TimeAndDateResponse -> {
                val bytes: ByteArray = response.payload
                ctx.getString(
                    R.string.time_and_date_response, formatTimeAndDate(bytes[0].toString()), formatTimeAndDate(bytes[1].toString()), formatTimeAndDate(bytes[2].toString()),
                    formatTimeAndDate(bytes[3].toString()), formatTimeAndDate(bytes[4].toString()),
                    formatTimeAndDate(bytes[5].toString()), formatTimeAndDate(bytes[6].toString())
                )
            }
            is TimeAndDateSetResponse -> {
                ctx.getString(R.string.time_and_date_set_response)
            }
            else -> {
                ctx.getString(R.string.unknown_response)
            }
        }
    }

    private fun getWristCoinPOSResponseDescription(response: TCMPMessage, ctx: Context): String  {
        return when (response){
            is WristCoinPOSApplicationErrorMessage -> {
                ctx.getString(R.string.wristcoinPOS_application_error_message, response.appErrorCode.toString(), response.internalErrorCode.toString(), response.readerStatusCode.toString(), response.errorDescription)
            }
            is DebitWristbandFullRespResponse -> {
                val resultingWristbandState = response.getResultingWristbandState()
                ctx.getString(R.string.wristcoinPOS_resulting_wristband_state_response,
                    formatCentavosAmount(resultingWristbandState.balance),
                    resultingWristbandState.rewardBalance,
                    resultingWristbandState.uid.toHex(),
                    resultingWristbandState.aeonCount,
                    if(resultingWristbandState.isClosedOut) "Closed" else "Not closed",
                    when(resultingWristbandState.scratchState){
                        AppScratchState.OnlineScratched -> "Online"
                        AppScratchState.OfflineScratched -> "Offline"
                        AppScratchState.Unscratched -> "Unscratched"
                        else -> if(resultingWristbandState.isConfiguredForOnlineOperation){
                            "Online"
                        } else{
                            "Offline"
                        }},
                    formatCentavosAmount((resultingWristbandState.offlineCreditTotal ?: 0) + (resultingWristbandState.onlineCreditTotal ?: 0)),
                    formatCentavosAmount(resultingWristbandState.debitTotal),
                    formatCentavosAmount(resultingWristbandState.reversalTotal),
                    formatCentavosAmount(resultingWristbandState.preloadedCreditTotal),
                    resultingWristbandState.rewardPointCreditTotal,
                    resultingWristbandState.rewardPointDebitTotal,
                    resultingWristbandState.preloadedPointsTotal,
                    resultingWristbandState.majorVersion.toString() + "." + resultingWristbandState.minorVersion.toString()
                )

            }
            is DebitWristbandShortRespResponse -> {
                ctx.getString(R.string.wristcoinPOS_debit_wristband_short_response, formatCentavosAmount(response.remainingBalanceCentavos ?: 0))
            }
            is GetWristbandStatusResponse -> {
                val wristbandState = response.getWristbandState()
                ctx.getString(R.string.wristcoinPOS_get_wristband_status_response,
                    formatCentavosAmount(wristbandState.balance),
                    wristbandState.rewardBalance,
                    wristbandState.uid.toHex(),
                    wristbandState.aeonCount,
                    if(wristbandState.isClosedOut) "Closed" else "Not closed",
                    when(wristbandState.scratchState){
                        AppScratchState.OnlineScratched -> "Online"
                        AppScratchState.OfflineScratched -> "Offline"
                        AppScratchState.Unscratched -> "Unscratched"
                        else -> if(wristbandState.isConfiguredForOnlineOperation){
                            "Online"
                        } else{
                            "Offline"
                        }},
                    formatCentavosAmount((wristbandState.offlineCreditTotal ?: 0) + (wristbandState.onlineCreditTotal ?: 0)),
                    formatCentavosAmount(wristbandState.debitTotal),
                    formatCentavosAmount(wristbandState.reversalTotal),
                    formatCentavosAmount(wristbandState.preloadedCreditTotal),
                    wristbandState.rewardPointCreditTotal,
                    wristbandState.rewardPointDebitTotal,
                    wristbandState.preloadedPointsTotal,
                    wristbandState.majorVersion.toString() + "." + wristbandState.minorVersion.toString()
                )
            }
            is GetWristCoinPOSCommandFamilyVersionResponse -> {
                ctx.getString(R.string.wristcoinPOS_library_version_response, response.majorVersion.toString(), response.minorVersion.toString())
            }
            is SetEventIdResponse -> {
                ctx.getString(R.string.wristcoinPOS_set_event_ID_response)
            }
            is TopupWristbandFullRespResponse -> {
                val resultingWristbandState = response.getResultingWristbandState()
                ctx.getString(R.string.wristcoinPOS_resulting_wristband_state_response,
                    formatCentavosAmount(resultingWristbandState.balance),
                    resultingWristbandState.rewardBalance,
                    resultingWristbandState.uid.toHex(),
                    resultingWristbandState.aeonCount,
                    if(resultingWristbandState.isClosedOut) "Closed" else "Not closed",
                    when(resultingWristbandState.scratchState){
                        AppScratchState.OnlineScratched -> "Online"
                        AppScratchState.OfflineScratched -> "Offline"
                        AppScratchState.Unscratched -> "Unscratched"
                        else -> if(resultingWristbandState.isConfiguredForOnlineOperation){
                            "Online"
                        } else{
                            "Offline"
                        }},
                    formatCentavosAmount((resultingWristbandState.offlineCreditTotal ?: 0) + (resultingWristbandState.onlineCreditTotal ?: 0)),
                    formatCentavosAmount(resultingWristbandState.debitTotal),
                    formatCentavosAmount(resultingWristbandState.reversalTotal),
                    formatCentavosAmount(resultingWristbandState.preloadedCreditTotal),
                    resultingWristbandState.rewardPointCreditTotal,
                    resultingWristbandState.rewardPointDebitTotal,
                    resultingWristbandState.preloadedPointsTotal,
                    resultingWristbandState.majorVersion.toString() + "." + resultingWristbandState.minorVersion.toString()
                )

            }
            is TopupWristbandShortRespResponse -> {
                ctx.getString(R.string.wristcoinPOS_topup_wristband_short_response, formatCentavosAmount(response.remainingBalanceCentavos ?: 0))
            }
            is CloseoutWristbandResponse -> {
                val wristbandState = response.getWristbandState()
                ctx.getString(R.string.wristcoinPOS_closeout_wristband_response,
                    formatCentavosAmount(wristbandState.balance),
                    wristbandState.rewardBalance,
                    wristbandState.uid.toHex(),
                    wristbandState.aeonCount,
                    if(wristbandState.isClosedOut) "Closed" else "Not closed",
                    when(wristbandState.scratchState){
                        AppScratchState.OnlineScratched -> "Online"
                        AppScratchState.OfflineScratched -> "Offline"
                        AppScratchState.Unscratched -> "Unscratched"
                        else -> if(wristbandState.isConfiguredForOnlineOperation){
                            "Online"
                        } else{
                            "Offline"
                        }},
                    formatCentavosAmount((wristbandState.offlineCreditTotal ?: 0) + (wristbandState.onlineCreditTotal ?: 0)),
                    formatCentavosAmount(wristbandState.debitTotal),
                    formatCentavosAmount(wristbandState.reversalTotal),
                    formatCentavosAmount(wristbandState.preloadedCreditTotal),
                    wristbandState.rewardPointCreditTotal,
                    wristbandState.rewardPointDebitTotal,
                    wristbandState.preloadedPointsTotal,
                    wristbandState.majorVersion.toString() + "." + wristbandState.minorVersion.toString()
                )
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

private fun formatTimeAndDate(string: String) : String {
    return if(string.length == 1) "0$string" else string
}
private fun formatCentavosAmount(amount: Int) : String {
    val amountStr = (amount.toDouble()/100).toString()
    return "$" + amountStr + if(amountStr[amountStr.length-2] == '.') "0" else ""
}
