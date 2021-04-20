package com.taptrack.experiments.rancheria.business

import android.content.Context
import com.taptrack.experiments.rancheria.R
import com.taptrack.experiments.rancheria.ui.views.sendmessages.DisableBlePinCommand
import com.taptrack.experiments.rancheria.ui.views.sendmessages.SetBLEPinCommand
import com.taptrack.tcmptappy2.TCMPMessage
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.BasicNfcCommandResolver
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands.*
import com.taptrack.tcmptappy2.commandfamilies.mifareclassic.MifareClassicCommandResolver
import com.taptrack.tcmptappy2.commandfamilies.mifareclassic.commands.DetectMifareClassicCommand
import com.taptrack.tcmptappy2.commandfamilies.mifareclassic.commands.GetMifareClassicLibraryVersionCommand
import com.taptrack.tcmptappy2.commandfamilies.mifareclassic.commands.ReadMifareClassicCommand
import com.taptrack.tcmptappy2.commandfamilies.ntag21x.commands.*
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.SystemCommandResolver
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.commands.*
import com.taptrack.tcmptappy2.commandfamilies.type4.Type4CommandResolver
import com.taptrack.tcmptappy2.commandfamilies.type4.commands.*
import kotlin.reflect.full.createInstance

data class CommandFamilyOption(
    val id: Int,
    val imageRes: Int,
    val descriptionRes: Int
)

data class CommandOption(
    val commandOptionId: Int,
    val commandFamilyId: Int,
    val imageRes: Int,
    val titleRes: Int,
    val descriptionRes: Int,
    val clazzes: List<Class<out TCMPMessage>>
) {
    constructor(
        commandOptionId: Int,
        commandFamilyId: Int,
        imageRes: Int,
        titleRes: Int,
        descriptionRes: Int,
        clazz: Class<out TCMPMessage>
    ) : this(commandOptionId, commandFamilyId, imageRes, titleRes, descriptionRes, listOf(clazz))
}

class CommandDataSource(val context: Context) {
    private val sortedAllCommands by lazy {
        ALL_COMMAND_OPTIONS.sortedBy { context.getString(it.titleRes) }
    }

    private val sortedSystemCommands by lazy {
        SYSTEM_COMMANDS.sortedBy { context.getString(it.titleRes) }
    }

    private val sortedBasicNfcCommands by lazy {
        BASIC_NFC_COMMANDS.sortedBy { context.getString(it.titleRes) }
    }

    private val sortedClassicCommands by lazy {
        CLASSIC_COMMANDS.sortedBy { context.getString(it.titleRes) }
    }

    private val sortedType4Commands by lazy {
        TYPE_4_COMMANDS.sortedBy { context.getString(it.titleRes) }
    }

    private val sortedNtag21xCommands by lazy {
        NTAG_21X_COMMANDS // Ignoring sort by title since string commands take priority
    }


    // TODO: improve this procedure
    fun retrieveCommandOptionForMessage(msg: TCMPMessage): CommandOption? {
        val toSearch = when {
            msg.commandFamily contentEquals SystemCommandResolver.FAMILY_ID -> {
                sortedSystemCommands
            }
            msg.commandFamily contentEquals BasicNfcCommandResolver.FAMILY_ID -> {
                sortedBasicNfcCommands
            }
            msg.commandFamily contentEquals MifareClassicCommandResolver.FAMILY_ID -> {
                sortedClassicCommands
            }
            msg.commandFamily contentEquals Type4CommandResolver.FAMILY_ID -> {
                sortedType4Commands
            }
            else -> {
                sortedAllCommands
            }
        }

        for (option in toSearch) {
            val clazzes = option.clazzes
            for (clazz in clazzes) {
                try {
                    val constructed = clazz.kotlin.createInstance()
                    if (constructed.commandFamily contentEquals msg.commandFamily
                            && constructed.commandCode == msg.commandCode) {
                        return option
                    }
                } catch (ignored: Exception) {
                    // cant instantiate
                }
            }
        }
        return null
    }

    fun retrieveCommand(id: Int): CommandOption? {
        return ALL_COMMAND_OPTIONS_MAP[id]
    }

    fun retrieveFamilyOptions(): List<CommandFamilyOption> {
        return ALL_FAMILY_OPTIONS
    }

    fun retrieveCommandOptions(familyId: Int): List<CommandOption> {
        return when (familyId) {
            FAM_OPTION_ID_ALL -> sortedAllCommands
            FAM_OPTION_ID_SYS -> sortedSystemCommands
            FAM_OPTION_ID_BASIC -> sortedBasicNfcCommands
            FAM_OPTION_ID_CLASSIC -> sortedClassicCommands
            FAM_OPTION_ID_T4 -> sortedType4Commands
            FAM_OPTION_ID_NTAG21X -> sortedNtag21xCommands
            else -> emptyList()
        }
    }

    companion object {
        const val FAM_OPTION_ID_ALL = 0
        const val FAM_OPTION_ID_SYS = 1
        const val FAM_OPTION_ID_BASIC = 2
        const val FAM_OPTION_ID_CLASSIC = 3
        const val FAM_OPTION_ID_T4 = 4
        const val FAM_OPTION_ID_NTAG21X = 5

        private val ALL_FAMILY_OPTIONS: List<CommandFamilyOption> = listOf(
            CommandFamilyOption(FAM_OPTION_ID_ALL, R.drawable.ic_all_inclusive_black_48dp, R.string.desc_all_families),
            CommandFamilyOption(FAM_OPTION_ID_SYS, R.drawable.ic_settings_black_48dp, R.string.desc_system_family),
            CommandFamilyOption(FAM_OPTION_ID_BASIC, R.drawable.ic_nfc_black_48dp, R.string.desc_basic_nfc_family),
            CommandFamilyOption(FAM_OPTION_ID_CLASSIC, R.drawable.ic_classic_black_48dp, R.string.desc_classic_family),
            CommandFamilyOption(FAM_OPTION_ID_T4, R.drawable.ic_type4_black_48dp, R.string.desc_type_4_family),
            CommandFamilyOption(FAM_OPTION_ID_NTAG21X, R.drawable.ic_lock_black_48dp, R.string.desc_ntag_21x_family)
        )

        private const val COM_OPT_GET_BATT = 0
        private const val COM_OPT_HARDWARE_V = 1
        private const val COM_OPT_SYSTEM_LIBV = 2
        private const val COM_OPT_PING = 3
        private const val COM_OPT_BASICNFC_LIBV = 4
        private const val COM_OPT_SCAN_NDEF = 5
        private const val COM_OPT_SCAN_TAG = 6
        private const val COM_OPT_STOP = 7
        private const val COM_OPT_WRITE_TEXT = 8
        private const val COM_OPT_WRITE_URI = 9
        private const val COM_OPT_LOCK_TAG = 10
        private const val COM_OPT_DETECT_CLASS = 11
        private const val COM_OPT_CLASS_LIBV = 12
        private const val COM_OPT_READ_CLASS = 13
        private const val COM_OPT_T4_DETECT = 14
        private const val COM_OPT_T4_LIBV = 15
        private const val COM_OPT_T4_TRANS = 16
        private const val COM_OPT_SET_CONFIGURATION_PARAM = 17
        private const val COM_OPT_ACTIVATE_RED_LED = 18
        private const val COM_OPT_DEACTIVATE_RED_LED = 19
        private const val COM_OPT_ACTIVATE_GREEN_LED = 20
        private const val COM_OPT_DEACTIVATE_GREEN_LED = 21
        private const val COM_OPT_ACTIVATE_BLUE_LED = 22
        private const val COM_OPT_DEACTIVATE_BLUE_LED = 23
        private const val COM_OPT_ACTIVATE_BUZZER = 24
        private const val COM_OPT_DEACTIVATE_BUZZER = 25
        private const val COM_OPT_CONFIGURE_COOLDOWN = 26
        private const val COM_OPT_ENABLE_BLE_PIN_PAIRING = 27
        private const val COM_OPT_DISABLE_BLE_PIN_PAIRING = 28
        private const val COM_OPT_T4_DETECT_HCE = 29
        private const val COM_OPT_NTAG21X_READ_PASSWORD_BYTES = 30
        private const val COM_OPT_NTAG21X_READ_PASSWORD_STRING = 31
        private const val COM_OPT_NTAG21X_WRITE_TEXT_PASSWORD_BYTES = 32
        private const val COM_OPT_NTAG21X_WRITE_TEXT_PASSWORD_STRING = 33
        private const val COM_OPT_NTAG21X_WRITE_URI_PASSWORD_BYTES = 34
        private const val COM_OPT_NTAG21X_WRITE_URI_PASSWORD_STRING = 35
        private const val COM_OPT_INITIATE_TAPPYTAG_HANDSHAKE = 36

        private val ALL_COMMAND_OPTIONS_MAP: Map<Int, CommandOption> = mapOf(
            Pair(
                COM_OPT_GET_BATT,
                CommandOption(
                    COM_OPT_GET_BATT,
                    FAM_OPTION_ID_SYS,
                    R.drawable.ic_get_battery_48dp,
                    R.string.syscommand_get_battery_title,
                    R.string.syscommand_get_battery_description,
                    GetBatteryLevelCommand::class.java
                )
            ),
            Pair(
                COM_OPT_HARDWARE_V,
                CommandOption(
                    COM_OPT_HARDWARE_V,
                    FAM_OPTION_ID_SYS,
                    R.drawable.ic_hardware_version_48dp,
                    R.string.syscommand_get_hardware_title,
                    R.string.syscommand_get_hardware_description,
                    GetHardwareVersionCommand::class.java
                )
            ),
            Pair(
                COM_OPT_PING,
                CommandOption(
                    COM_OPT_PING,
                    FAM_OPTION_ID_SYS,
                    R.drawable.ic_library_version_48dp,
                    R.string.syscommand_get_libraryv_title,
                    R.string.syscommand_get_libraryv_description,
                    GetFirmwareVersionCommand::class.java
                )
            ),
            Pair(
                COM_OPT_SYSTEM_LIBV,
                CommandOption(
                    COM_OPT_SYSTEM_LIBV,
                    FAM_OPTION_ID_SYS,
                    R.drawable.ic_ping_black_48dp,
                    R.string.syscommand_ping_title,
                    R.string.syscommand_ping_description,
                    PingCommand::class.java
                )
            ),
            Pair(
                COM_OPT_SET_CONFIGURATION_PARAM,
                CommandOption(
                    COM_OPT_SET_CONFIGURATION_PARAM,
                    FAM_OPTION_ID_SYS,
                    R.drawable.ic_set_config_item_48dp,
                    R.string.syscommand_set_configuration_item_title,
                    R.string.syscommand_set_configuration_item_description,
                    SetConfigItemCommand::class.java
                )
            ),

            Pair(
                COM_OPT_ACTIVATE_BLUE_LED,
                CommandOption(
                    COM_OPT_ACTIVATE_BLUE_LED,
                    FAM_OPTION_ID_SYS,
                    R.drawable.ic_led_on_48dp,
                    R.string.syscommand_activate_blue_led_title,
                    R.string.syscommand_activate_blue_led_description,
                    ActivateBlueLEDCommand::class.java
                )
            ),

            Pair(
                COM_OPT_ACTIVATE_RED_LED,
                CommandOption(
                    COM_OPT_ACTIVATE_RED_LED,
                    FAM_OPTION_ID_SYS,
                    R.drawable.ic_led_on_48dp,
                    R.string.syscommand_activate_red_led_title,
                    R.string.syscommand_activate_red_led_description,
                    ActivateRedLEDCommand::class.java
                )
            ),

            Pair(
                COM_OPT_ACTIVATE_GREEN_LED,
                CommandOption(
                    COM_OPT_ACTIVATE_GREEN_LED,
                    FAM_OPTION_ID_SYS,
                    R.drawable.ic_led_on_48dp,
                    R.string.syscommand_activate_green_led_title,
                    R.string.syscommand_activate_green_led_description,
                    ActivateGreenLEDCommand::class.java
                )
            ),


            Pair(
                COM_OPT_ACTIVATE_BUZZER,
                CommandOption(
                    COM_OPT_ACTIVATE_BUZZER,
                    FAM_OPTION_ID_SYS,
                    R.drawable.ic_buzzer_on_48dp,
                    R.string.syscommand_activate_buzzer_title,
                    R.string.syscommand_activate_buzzer_description,
                    ActivateBuzzerCommand::class.java
                )
            ),


            Pair(
                COM_OPT_DEACTIVATE_BUZZER,
                CommandOption(
                    COM_OPT_DEACTIVATE_BUZZER,
                    FAM_OPTION_ID_SYS,
                    R.drawable.ic_buzzer_off_48dp,
                    R.string.syscommand_deactivate_buzzer_title,
                    R.string.syscommand_deactivate_buzzer_description,
                    DeactivateBuzzerCommand::class.java
                )
            ),

            Pair(
                COM_OPT_DEACTIVATE_BLUE_LED,
                CommandOption(
                    COM_OPT_DEACTIVATE_BLUE_LED,
                    FAM_OPTION_ID_SYS,
                    R.drawable.ic_led_off_48dp,
                    R.string.syscommand_deactivate_blue_led_title,
                    R.string.syscommand_deactivate_blue_led_description,
                    DeactivateBlueLEDCommand::class.java
                )
            ),

            Pair(
                COM_OPT_DEACTIVATE_RED_LED,
                CommandOption(
                    COM_OPT_DEACTIVATE_RED_LED,
                    FAM_OPTION_ID_SYS,
                    R.drawable.ic_led_off_48dp,
                    R.string.syscommand_deactivate_red_led_title,
                    R.string.syscommand_deactivate_red_led_description,
                    DeactivateRedLEDCommand::class.java
                )
            ),

            Pair(
                COM_OPT_DEACTIVATE_GREEN_LED,
                CommandOption(
                    COM_OPT_DEACTIVATE_GREEN_LED,
                    FAM_OPTION_ID_SYS,
                    R.drawable.ic_led_off_48dp,
                    R.string.syscommand_deactivate_green_led_title,
                    R.string.syscommand_deactivate_green_led_description,
                    DeactivateGreenLEDCommand::class.java
                )
            ),

            Pair(
                COM_OPT_CONFIGURE_COOLDOWN,
                CommandOption(
                    COM_OPT_CONFIGURE_COOLDOWN,
                    FAM_OPTION_ID_SYS,
                    R.drawable.ic_enable_cooldown_black_48dp,
                    R.string.syscommand_configure_scan_cooldown_title,
                    R.string.syscommand_configure_scan_cooldown_description,
                    ConfigureOnboardScanCooldownCommand::class.java
                )
            ),

            Pair(
                COM_OPT_ENABLE_BLE_PIN_PAIRING,
                CommandOption(
                    COM_OPT_ENABLE_BLE_PIN_PAIRING,
                    FAM_OPTION_ID_SYS,
                    R.drawable.ic_enable_ble_pin_pairing_black_48dp,
                    R.string.syscommand_enable_ble_pin_title,
                    R.string.syscommand_enable_ble_pin_description,
                    SetBLEPinCommand::class.java
                )
            ),

            Pair(
                COM_OPT_DISABLE_BLE_PIN_PAIRING,
                CommandOption(
                    COM_OPT_DISABLE_BLE_PIN_PAIRING,
                    FAM_OPTION_ID_SYS,
                    R.drawable.ic_disable_ble_pin_pairing_black_24dp,
                    R.string.syscommand_disable_ble_pin_title,
                    R.string.syscommand_disable_ble_pin_description,
                    DisableBlePinCommand::class.java
                )
            ),

            Pair(
                COM_OPT_BASICNFC_LIBV,
                CommandOption(
                    COM_OPT_BASICNFC_LIBV,
                    FAM_OPTION_ID_BASIC,
                    R.drawable.ic_library_version_48dp,
                    R.string.nfccommand_get_libraryv_title,
                    R.string.nfccommand_get_libraryv_description,
                    GetBasicNfcLibraryVersionCommand::class.java
                )
            ),

            Pair(
                COM_OPT_SCAN_NDEF,
                CommandOption(
                    COM_OPT_SCAN_NDEF,
                    FAM_OPTION_ID_BASIC,
                    R.drawable.ic_scan_ndef_48dp,
                    R.string.nfccommand_scan_ndef_title,
                    R.string.nfccommand_scan_ndef_description,
                    listOf(ScanNdefCommand::class.java, StreamNdefCommand::class.java)
                )
            ),

            Pair(
                COM_OPT_SCAN_TAG,
                CommandOption(
                    COM_OPT_SCAN_TAG,
                    FAM_OPTION_ID_BASIC,
                    R.drawable.ic_nfc_black_48dp,
                    R.string.nfccommand_scan_tag_title,
                    R.string.nfccommand_scan_tag_description,
                    listOf(ScanTagCommand::class.java, StreamTagsCommand::class.java)
                )
            ),

            Pair(
                COM_OPT_STOP,
                CommandOption(
                    COM_OPT_STOP,
                    FAM_OPTION_ID_BASIC,
                    R.drawable.ic_stop_black_48dp,
                    R.string.nfccommand_stop_title,
                    R.string.nfccommand_stop_description,
                    StopCommand::class.java
                )
            ),
            Pair(
                COM_OPT_WRITE_TEXT,
                CommandOption(
                    COM_OPT_WRITE_TEXT,
                    FAM_OPTION_ID_BASIC,
                    R.drawable.ic_description_black_48dp,
                    R.string.nfccommand_write_text_title,
                    R.string.nfccommand_write_text_description,
                    WriteNdefTextRecordCommand::class.java
                )
            ),
            Pair(
                COM_OPT_WRITE_URI,
                CommandOption(
                    COM_OPT_WRITE_URI,
                    FAM_OPTION_ID_BASIC,
                    R.drawable.ic_link_black_48dp,
                    R.string.nfccommand_write_uri_title,
                    R.string.nfccommand_write_uri_description,
                    listOf(WriteNdefUriRecordCommand::class.java)
                )
            ),
            Pair(
                COM_OPT_LOCK_TAG,
                CommandOption(
                    COM_OPT_LOCK_TAG,
                    FAM_OPTION_ID_BASIC,
                    R.drawable.ic_lock_black_48dp,
                    R.string.nfccommand_lock_tag_title,
                    R.string.nfccommand_lock_tag_description,
                    LockTagCommand::class.java
                )
            ),

            Pair(
                COM_OPT_DETECT_CLASS,
                CommandOption(
                    COM_OPT_DETECT_CLASS,
                    FAM_OPTION_ID_CLASSIC,
                    R.drawable.ic_nfc_black_48dp,
                    R.string.classiccommand_detect_title,
                    R.string.classiccommand_detect_description,
                    DetectMifareClassicCommand::class.java
                )
            ),
            Pair(
                COM_OPT_CLASS_LIBV,
                CommandOption(
                    COM_OPT_CLASS_LIBV,
                    FAM_OPTION_ID_CLASSIC,
                    R.drawable.ic_library_version_48dp,
                    R.string.classiccommand_get_version_title,
                    R.string.classiccommand_get_version_description,
                    GetMifareClassicLibraryVersionCommand::class.java
                )
            ),
            Pair(
                COM_OPT_READ_CLASS,
                CommandOption(
                    COM_OPT_READ_CLASS,
                    FAM_OPTION_ID_CLASSIC,
                    R.drawable.ic_read_classic_48dp,
                    R.string.classiccommand_read_classic_title,
                    R.string.classiccommand_read_classic_description,
                    ReadMifareClassicCommand::class.java
                )
            ),

            Pair(
                COM_OPT_T4_DETECT,
                CommandOption(
                    COM_OPT_T4_DETECT,
                    FAM_OPTION_ID_T4,
                    R.drawable.ic_nfc_black_48dp,
                    R.string.type4command_detect_title,
                    R.string.type4command_detect_description,
                    listOf(
                        DetectType4Command::class.java,
                        DetectType4BCommand::class.java,
                        DetectType4BSpecificAfiCommand::class.java
                    )
                )
            ),
            Pair(
                COM_OPT_T4_LIBV,
                CommandOption(
                    COM_OPT_T4_LIBV,
                    FAM_OPTION_ID_T4,
                    R.drawable.ic_library_version_48dp,
                    R.string.type4command_get_version_title,
                    R.string.type4command_get_version_description,
                    GetType4LibraryVersionCommand::class.java
                )
            ),
            Pair(
                COM_OPT_T4_TRANS,
                CommandOption(
                    COM_OPT_T4_TRANS,
                    FAM_OPTION_ID_T4,
                    R.drawable.ic_transceive_48dp,
                    R.string.type4command_transceive_apdu_title,
                    R.string.type4command_transceive_apdu_description,
                    TransceiveApduCommand::class.java
                )
            ),
            Pair(
                COM_OPT_T4_DETECT_HCE,
                CommandOption(
                    COM_OPT_T4_DETECT_HCE,
                    FAM_OPTION_ID_T4,
                    R.drawable.ic_nfc_black_48dp,
                    R.string.type4command_detect_hce_target_title,
                    R.string.type4command_detect_hce_target_description,
                    DetectActiveHCETargetCommand::class.java
                )
            ),

            Pair(
                COM_OPT_NTAG21X_READ_PASSWORD_STRING,
                CommandOption(
                    COM_OPT_NTAG21X_READ_PASSWORD_STRING,
                    FAM_OPTION_ID_NTAG21X,
                    R.drawable.ic_scan_ndef_48dp,
                    R.string.ntag21xcommand_read_ndef_with_password_string_title,
                    R.string.ntag21xcommand_read_ndef_with_password_string_description,
                    ReadNdefWithPasswordCommand::class.java
                )
            ),

            Pair(
                COM_OPT_NTAG21X_WRITE_TEXT_PASSWORD_STRING,
                CommandOption(
                    COM_OPT_NTAG21X_WRITE_TEXT_PASSWORD_STRING,
                    FAM_OPTION_ID_NTAG21X,
                    R.drawable.ic_description_black_48dp,
                    R.string.ntag21xcommand_write_text_with_password_string_title,
                    R.string.ntag21xcommand_write_text_with_password_string_description,
                    WriteTextNdefWithPasswordCommand::class.java
                )
            ),

            Pair(
                COM_OPT_NTAG21X_WRITE_URI_PASSWORD_STRING,
                CommandOption(
                    COM_OPT_NTAG21X_WRITE_URI_PASSWORD_STRING,
                    FAM_OPTION_ID_NTAG21X,
                    R.drawable.ic_link_black_48dp,
                    R.string.ntag21xcommand_write_uri_with_password_string_title,
                    R.string.ntag21xcommand_write_uri_with_password_string_description,
                    WriteUriNdefWithPasswordCommand::class.java
                )
            ),

            Pair(
                COM_OPT_NTAG21X_READ_PASSWORD_BYTES,
                CommandOption(
                    COM_OPT_NTAG21X_READ_PASSWORD_BYTES,
                    FAM_OPTION_ID_NTAG21X,
                    R.drawable.ic_scan_ndef_48dp,
                    R.string.ntag21xcommand_read_ndef_with_password_bytes_title,
                    R.string.ntag21xcommand_read_ndef_with_password_bytes_description,
                    ReadNdefWithPasswordBytesCommand::class.java
                )
            ),

            Pair(
                COM_OPT_NTAG21X_WRITE_TEXT_PASSWORD_BYTES,
                CommandOption(
                    COM_OPT_NTAG21X_WRITE_TEXT_PASSWORD_BYTES,
                    FAM_OPTION_ID_NTAG21X,
                    R.drawable.ic_description_black_48dp,
                    R.string.ntag21xcommand_write_text_with_password_bytes_title,
                    R.string.ntag21xcommand_write_text_with_password_bytes_description,
                    WriteTextNdefWithPasswordBytesCommand::class.java
                )
            ),

            Pair(
                COM_OPT_NTAG21X_WRITE_URI_PASSWORD_BYTES,
                CommandOption(
                    COM_OPT_NTAG21X_WRITE_URI_PASSWORD_BYTES,
                    FAM_OPTION_ID_NTAG21X,
                    R.drawable.ic_link_black_48dp,
                    R.string.ntag21xcommand_write_uri_with_password_bytes_title,
                    R.string.ntag21xcommand_write_uri_with_password_bytes_description,
                    WriteUriNdefWithPasswordBytesCommand::class.java
                )
            ),

            Pair(
                COM_OPT_INITIATE_TAPPYTAG_HANDSHAKE,
                CommandOption(
                    COM_OPT_INITIATE_TAPPYTAG_HANDSHAKE,
                    FAM_OPTION_ID_BASIC,
                    R.drawable.ic_tappytag_black_48dp,
                    R.string.nfccommand_tappytag_handshake_title,
                    R.string.nfccommand_tappytag_handshake_description,
                    InitiateTappyTagHandshakeCommand::class.java
                )
            )
        )

        private val ALL_COMMAND_OPTIONS: List<CommandOption> by lazy {
            ALL_COMMAND_OPTIONS_MAP.values.toList()
        }

        private val SYSTEM_COMMANDS by lazy {
            ALL_COMMAND_OPTIONS.filter {
                it.commandFamilyId == FAM_OPTION_ID_SYS
            }
        }

        private val BASIC_NFC_COMMANDS by lazy {
            ALL_COMMAND_OPTIONS.filter {
                it.commandFamilyId == FAM_OPTION_ID_BASIC
            }
        }


        private val CLASSIC_COMMANDS by lazy {
            ALL_COMMAND_OPTIONS.filter {
                it.commandFamilyId == FAM_OPTION_ID_CLASSIC
            }
        }


        private val TYPE_4_COMMANDS by lazy {
            ALL_COMMAND_OPTIONS.filter {
                it.commandFamilyId == FAM_OPTION_ID_T4
            }
        }

        private val NTAG_21X_COMMANDS by lazy {
            ALL_COMMAND_OPTIONS.filter {
                it.commandFamilyId == FAM_OPTION_ID_NTAG21X
            }
        }

    }
}
