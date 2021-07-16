package com.taptrack.experiments.rancheria.business


// In the Tappy Demo App the wristcoinpos command family is vendored in the project to avoid build conflicts
// For developers who want to use the wristcoinpos command family please make sure to include the dependency in your
// projects build.gradle file and DO NOT copy paste the "wristcoinpos" package into your own project
import android.content.Context
import com.taptrack.experiments.rancheria.R
import com.taptrack.experiments.rancheria.wristcoinpos.WristCoinPOSCommandResolver
import com.taptrack.experiments.rancheria.wristcoinpos.commands.*
import com.taptrack.experiments.rancheria.ui.views.sendmessages.ClearBondingCacheCommand
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
import com.taptrack.tcmptappy2.commandfamilies.standalonecheckin.StandaloneCheckinCommandResolver
import com.taptrack.tcmptappy2.commandfamilies.standalonecheckin.commands.*
import com.taptrack.tcmptappy2.commandfamilies.stmicroM24SR02.STMicroCommandResolver
import com.taptrack.tcmptappy2.commandfamilies.stmicroM24SR02.commands.*
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

    private val sortedStMicroCommands by lazy {
        ST_MICRO_M24SR02_COMMANDS.sortedBy { context.getString(it.titleRes) }
    }

    private val sortedStandaloneCheckinCommands by lazy {
        STANDALONE_CHECKIN_COMMANDS.sortedBy {context.getString(it.titleRes)}
    }

    private val sortedWristCoinPOSCommands by lazy {
        WRISTCON_POS_COMMANDS.sortedBy { context.getString(it.titleRes) }
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
            msg.commandFamily contentEquals STMicroCommandResolver.FAMILY_ID -> {
                sortedStMicroCommands
            }
            msg.commandFamily contentEquals StandaloneCheckinCommandResolver.FAMILY_ID -> {
                sortedStandaloneCheckinCommands
            }
            msg.commandFamily contentEquals WristCoinPOSCommandResolver.FAMILY_ID -> {
                sortedWristCoinPOSCommands
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
            FAM_OPTION_ID_STMICRO -> sortedStMicroCommands
            FAM_OPTION_ID_STANDALONE -> sortedStandaloneCheckinCommands
            FAM_OPTION_ID_WRISTCOINPOS -> sortedWristCoinPOSCommands

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
        const val FAM_OPTION_ID_STMICRO = 6
        const val FAM_OPTION_ID_STANDALONE = 7
        const val FAM_OPTION_ID_WRISTCOINPOS = 8

        private val ALL_FAMILY_OPTIONS: List<CommandFamilyOption> = listOf(
            CommandFamilyOption(FAM_OPTION_ID_ALL, R.drawable.ic_all_inclusive_black_48dp, R.string.desc_all_families),
            CommandFamilyOption(FAM_OPTION_ID_SYS, R.drawable.ic_settings_black_48dp, R.string.desc_system_family),
            CommandFamilyOption(FAM_OPTION_ID_BASIC, R.drawable.ic_nfc_black_48dp, R.string.desc_basic_nfc_family),
            CommandFamilyOption(FAM_OPTION_ID_CLASSIC, R.drawable.ic_classic_black_48dp, R.string.desc_classic_family),
            CommandFamilyOption(FAM_OPTION_ID_T4, R.drawable.ic_type4_black_48dp, R.string.desc_type_4_family),
            CommandFamilyOption(FAM_OPTION_ID_NTAG21X, R.drawable.ic_lock_black_48dp, R.string.desc_ntag_21x_family),
            CommandFamilyOption(FAM_OPTION_ID_STMICRO, R.drawable.ic_developer_board_black_24dp, R.string.desc_st_micro_family),
            CommandFamilyOption(FAM_OPTION_ID_STANDALONE, R.drawable.ic_how_to_vote_black_24dp, R.string.desc_standalone_family),
            CommandFamilyOption(FAM_OPTION_ID_WRISTCOINPOS, R.drawable.ic_wclogo_appicon, R.string.desc_wristcoinpos_family),
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
        private const val COM_OPT_SET_BOOT_CONFIG = 37
        private const val COM_OPT_GET_BOOT_CONFIG = 38
        private const val COM_OPT_CLEAR_BONDING_CACHE = 39
        private const val COM_OPT_AUTO_POLL = 40
        private const val COM_OPT_AUTO_POLL_NDEF = 41
        private const val COM_OPT_DISPATCH_TAGS = 42
        private const val COM_OPT_CHANGE_READ_NDEF_PASS = 43
        private const val COM_OPT_CHANGE_WRITE_NDEF_PASS = 44
        private const val COM_OPT_GET_COMFAM_V  = 45
        private const val COM_OPT_GET_I2C_SET = 46
        private const val COM_OPT_LOCK_NDEF_READ = 47
        private const val COM_OPT_LOCK_NDEF_WRITE = 48
        private const val COM_OPT_PERM_LOCK_NDEF_WRITE = 49
        private const val COM_OPT_READ_NDEF_PASS = 50
        private const val COM_OPT_WRITE_NDEF_PASS = 51
        private const val COM_OPT_UNLOCK_NDEF_READ = 52
        private const val COM_OPT_UNLOCK_NDEF_WRITE = 53
        private const val COM_OPT_GET_CHECKINS_COUNT = 54
        private const val COM_OPT_GET_CHECKINS = 55
        private const val COM_OPT_GET_STANDALONE_FAMV = 56
        private const val COM_OPT_GET_STATION_INFO = 57
        private const val COM_OPT_GET_TIME_DATE = 58
        private const val COM_OPT_READ_CHECKIN_UID = 59
        private const val COM_OPT_RESET_CHECKINS = 60
        private const val COM_OPT_SET_STATION_ID = 61
        private const val COM_OPT_SET_STATION_NAME = 62
        private const val COM_OPT_SET_TIME_DATE = 63
        private const val COM_OPT_EMULATE_TEXT = 64
        private const val COM_OPT_EMULATE_URI = 65
        private const val COM_OPT_DEBIT_WB_FULL = 66
        private const val COM_OPT_DEBIT_WB_SHORT = 67
        private const val COM_OPT_GET_WB_STATUS = 68
        private const val COM_OPT_GET_WRISTCOINPOS_COMFAMV = 69
        private const val COM_OPT_SET_EVENTID = 70
        private const val COM_OPT_TOPUP_WB_FULL = 71
        private const val COM_OPT_TOPUP_WB_SHORT = 72
        private const val COM_OPT_CLOSEOUT_WB = 73

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
                    R.drawable.ic_n_mark_solid_black,
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
                    R.drawable.ic_nfc_unlocked,
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
                    R.drawable.ic_description_locked,
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
                    R.drawable.ic_link_locked,
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
                    R.drawable.ic_nfc_unlocked,
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
                    R.drawable.ic_description_locked,
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
                    R.drawable.ic_link_locked,
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
            ),

            Pair(
                COM_OPT_SET_BOOT_CONFIG,
                CommandOption(
                    COM_OPT_SET_BOOT_CONFIG,
                    FAM_OPTION_ID_SYS,
                    R.drawable.ic_settings_suggest_24dp,
                    R.string.syscommand_set_boot_configuration_title,
                    R.string.syscommand_set_boot_configuration_description,
                    SetBootConfigCommand::class.java
                )
            ),

            Pair(
                COM_OPT_GET_BOOT_CONFIG,
                CommandOption(
                    COM_OPT_GET_BOOT_CONFIG,
                    FAM_OPTION_ID_SYS,
                    R.drawable.ic_settings_black_48dp,
                    R.string.syscommand_get_boot_configuration_title,
                    R.string.syscommand_get_boot_configuration_description,
                    GetBootConfigCommand::class.java
                )
            ),

            Pair(
                COM_OPT_CLEAR_BONDING_CACHE,
                CommandOption(
                    COM_OPT_CLEAR_BONDING_CACHE,
                    FAM_OPTION_ID_SYS,
                    R.drawable.ic_settings_bluetooth,
                    R.string.syscommand_clear_bonding_cache_title,
                    R.string.syscommand_clear_bonding_cache_description,
                    ClearBondingCacheCommand::class.java
                )
            ),

            Pair(
                COM_OPT_AUTO_POLL,
                CommandOption(
                    COM_OPT_AUTO_POLL,
                    FAM_OPTION_ID_BASIC,
                    R.drawable.ic_contactless_24dp,
                    R.string.nfccommand_auto_poll_title,
                    R.string.nfccommand_auto_poll_description,
                    AutoPollCommand::class.java
                )
            ),

            Pair(
                COM_OPT_AUTO_POLL_NDEF,
                CommandOption(
                    COM_OPT_AUTO_POLL_NDEF,
                    FAM_OPTION_ID_BASIC,
                    R.drawable.ic_auto_poll_ndef_icon,
                    R.string.nfccommand_auto_poll_ndef_title,
                    R.string.nfccommand_auto_poll_ndef_description,
                    AutoPollNdefCommand::class.java
                )
            ),

            Pair(
                COM_OPT_DISPATCH_TAGS,
                CommandOption(
                    COM_OPT_DISPATCH_TAGS,
                    FAM_OPTION_ID_BASIC,
                    R.drawable.ic_scan_nfc,
                    R.string.nfccommand_dispatch_tags_title,
                    R.string.nfccommand_dispatch_tags_description,
                    listOf(DispatchTagsCommand::class.java, DispatchTagCommand::class.java)
                )
            ),

            Pair(
                COM_OPT_CHANGE_READ_NDEF_PASS,
                CommandOption(
                    COM_OPT_CHANGE_READ_NDEF_PASS,
                    FAM_OPTION_ID_STMICRO,
                    R.drawable.ic_password_visible,
                    R.string.STMicroM24SR02_change_read_ndef_password_title,
                    R.string.STMicroM24SR02_change_read_ndef_password_description,
                    ChangeReadNdefPasswordCommand::class.java
                )
            ),

            Pair(
                COM_OPT_CHANGE_WRITE_NDEF_PASS,
                CommandOption(
                    COM_OPT_CHANGE_WRITE_NDEF_PASS,
                    FAM_OPTION_ID_STMICRO,
                    R.drawable.ic_password_edit,
                    R.string.STMicroM24SR02_change_write_ndef_password_title,
                    R.string.STMicroM24SR02_change_write_ndef_password_description,
                    ChangeWriteNdefPasswordCommand::class.java
                )
            ),

            Pair(
                COM_OPT_GET_COMFAM_V,
                CommandOption(
                    COM_OPT_GET_COMFAM_V,
                    FAM_OPTION_ID_STMICRO,
                    R.drawable.ic_library_version_48dp,
                    R.string.STMicroM24SR02_get_command_family_title,
                    R.string.STMicroM24SR02_get_command_family_description,
                    GetCommandFamilyVersionCommand::class.java
                )
            ),
            Pair(
                COM_OPT_GET_I2C_SET,
                CommandOption(
                    COM_OPT_GET_I2C_SET,
                    FAM_OPTION_ID_STMICRO,
                    R.drawable.ic_memory_vpn_key,
                    R.string.STMicroM24SR02_get_I2C_setting_title,
                    R.string.STMicroM24SR02_get_I2C_setting_description,
                    GetI2CSettingCommand::class.java
                )
            ),
            Pair(
                COM_OPT_LOCK_NDEF_READ,
                CommandOption(
                    COM_OPT_LOCK_NDEF_READ,
                    FAM_OPTION_ID_STMICRO,
                    R.drawable.ic_lock_visible,
                    R.string.STMicroM24SR02_password_lock_read_access_title,
                    R.string.STMicroM24SR02_password_lock_read_access_description,
                    LockNdefReadAccessCommand::class.java
                )
            ),
            Pair(
                COM_OPT_LOCK_NDEF_WRITE,
                CommandOption(
                    COM_OPT_LOCK_NDEF_WRITE,
                    FAM_OPTION_ID_STMICRO,
                    R.drawable.ic_lock_edit,
                    R.string.STMicroM24SR02_password_lock_write_access_title,
                    R.string.STMicroM24SR02_password_lock_write_access_description,
                    LockNdefWriteAccessCommand::class.java
                )
            ),
            Pair(
                COM_OPT_PERM_LOCK_NDEF_WRITE,
                CommandOption(
                    COM_OPT_PERM_LOCK_NDEF_WRITE,
                    FAM_OPTION_ID_STMICRO,
                    R.drawable.ic_enhanced_encryption_edit,
                    R.string.STMicroM24SR02_perma_lock_ndef_write_access_title,
                    R.string.STMicroM24SR02_perma_lock_ndef_write_access_description,
                    PermanentlyLockNdefWriteAccessCommand::class.java
                )
            ),
            Pair(
                COM_OPT_READ_NDEF_PASS,
                CommandOption(
                    COM_OPT_READ_NDEF_PASS,
                    FAM_OPTION_ID_STMICRO,
                    R.drawable.ic_lock_open_visible,
                    R.string.STMicroM24SR02_read_ndef_with_password_title,
                    R.string.STMicroM24SR02_read_ndef_with_password_description,
                    ReadNdefMsgWithPasswordCommand::class.java
                )
            ),
            Pair(
                COM_OPT_WRITE_NDEF_PASS,
                CommandOption(
                    COM_OPT_WRITE_NDEF_PASS,
                    FAM_OPTION_ID_STMICRO,
                    R.drawable.ic_lock_open_edit,
                    R.string.STMicroM24SR02_write_ndef_with_password_title,
                    R.string.STMicroM24SR02_write_ndef_with_password_description,
                    WriteNdefWithPasswordCommand::class.java
                )
            ),
            Pair(
                COM_OPT_UNLOCK_NDEF_READ,
                CommandOption(
                    COM_OPT_UNLOCK_NDEF_READ,
                    FAM_OPTION_ID_STMICRO,
                    R.drawable.ic_no_encryption_visible,
                    R.string.STMicroM24SR02_password_unlock_read_access_title,
                    R.string.STMicroM24SR02_password_unlock_read_access_description,
                    UnlockNdefReadAccessCommand::class.java
                )
            ),
            Pair(
                COM_OPT_UNLOCK_NDEF_WRITE,
                CommandOption(
                    COM_OPT_UNLOCK_NDEF_WRITE,
                    FAM_OPTION_ID_STMICRO,
                    R.drawable.ic_no_encryption_edit,
                    R.string.STMicroM24SR02_password_unlock_write_access_title,
                    R.string.STMicroM24SR02_password_unlock_write_access_description,
                    UnlockNdefWriteAccessCommand::class.java
                )
            ),
            Pair(
                COM_OPT_GET_CHECKINS_COUNT,
                CommandOption(
                    COM_OPT_GET_CHECKINS_COUNT,
                    FAM_OPTION_ID_STANDALONE,
                    R.drawable.ic_query_stats_black_24dp,
                    R.string.StandaloneCheckin_get_checkin_count_title,
                    R.string.StandaloneCheckin_get_checkin_count_description,
                    GetCheckinCountCommand::class.java
                )
            ),
            Pair(
                COM_OPT_GET_CHECKINS,
                CommandOption(
                    COM_OPT_GET_CHECKINS,
                    FAM_OPTION_ID_STANDALONE,
                    R.drawable.ic_download_black_24dp,
                    R.string.StandaloneCheckin_get_checkins_title,
                    R.string.StandaloneCheckin_get_checkins_description,
                    GetCheckinsCommand::class.java
                )
            ),
            Pair(
                COM_OPT_GET_STANDALONE_FAMV,
                CommandOption(
                    COM_OPT_GET_STANDALONE_FAMV,
                    FAM_OPTION_ID_STANDALONE,
                    R.drawable.ic_library_version_48dp,
                    R.string.StandaloneCheckin_get_family_version_title,
                    R.string.StandaloneCheckin_get_family_version_description,
                    GetStandaloneCheckinFamilyVersionCommand::class.java
                )
            ),
            Pair(
                COM_OPT_GET_STATION_INFO,
                CommandOption(
                    COM_OPT_GET_STATION_INFO,
                    FAM_OPTION_ID_STANDALONE,
                    R.drawable.ic_station_info,
                    R.string.StandaloneCheckin_get_station_info_title,
                    R.string.StandaloneCheckin_get_station_info_description,
                    GetStationInfoCommand::class.java
                )
            ),
            Pair(
                COM_OPT_GET_TIME_DATE,
                CommandOption(
                    COM_OPT_GET_TIME_DATE,
                    FAM_OPTION_ID_STANDALONE,
                    R.drawable.ic_today_black_24dp,
                    R.string.StandaloneCheckin_get_time_and_date_title,
                    R.string.StandaloneCheckin_get_time_and_date_description,
                    GetTimeAndDateCommand::class.java
                )
            ),
            Pair(
                COM_OPT_READ_CHECKIN_UID,
                CommandOption(
                    COM_OPT_READ_CHECKIN_UID,
                    FAM_OPTION_ID_STANDALONE,
                    R.drawable.ic_badge_black_24dp,
                    R.string.StandaloneCheckin_read_checkin_card_uid_title,
                    R.string.StandaloneCheckin_read_checkin_card_uid_description,
                    ReadCheckinCardUidCommand::class.java
                )
            ),
            Pair(
                COM_OPT_RESET_CHECKINS,
                CommandOption(
                    COM_OPT_RESET_CHECKINS,
                    FAM_OPTION_ID_STANDALONE,
                    R.drawable.ic_restart_alt_black_24dp,
                    R.string.StandaloneCheckin_reset_checkins_title,
                    R.string.StandaloneCheckin_reset_checkins_description,
                    ResetCheckinsCommand::class.java
                )
            ),
            Pair(
                COM_OPT_SET_STATION_ID,
                CommandOption(
                    COM_OPT_SET_STATION_ID,
                    FAM_OPTION_ID_STANDALONE,
                    R.drawable.ic_pin_black_24dp,
                    R.string.StandaloneCheckin_set_station_id_title,
                    R.string.StandaloneCheckin_set_station_id_description,
                    SetStationIdCommand::class.java
                )
            ),
            Pair(
                COM_OPT_SET_STATION_NAME,
                CommandOption(
                    COM_OPT_SET_STATION_NAME,
                    FAM_OPTION_ID_STANDALONE,
                    R.drawable.ic_business_black_24dp,
                    R.string.StandaloneCheckin_set_station_name_title,
                    R.string.StandaloneCheckin_set_station_name_description,
                    SetStationNameCommand::class.java
                )
            ),
            Pair(
                COM_OPT_SET_TIME_DATE,
                CommandOption(
                    COM_OPT_SET_TIME_DATE,
                    FAM_OPTION_ID_STANDALONE,
                    R.drawable.ic_edit_calendar_black_24dp,
                    R.string.StandaloneCheckin_set_time_and_date_title,
                    R.string.StandaloneCheckin_set_time_and_date_description,
                    SetTimeAndDateCommand::class.java
                )
            ),
            Pair(
                COM_OPT_EMULATE_TEXT,
                CommandOption(
                    COM_OPT_EMULATE_TEXT,
                    FAM_OPTION_ID_BASIC,
                    R.drawable.ic_description_black_48dp,
                    R.string.nfccommand_emulate_text_title,
                    R.string.nfccommand_emulate_text_description,
                    EmulateTextRecordCommand::class.java
                )
            ),
            Pair(
                COM_OPT_EMULATE_URI,
                CommandOption(
                    COM_OPT_EMULATE_URI,
                    FAM_OPTION_ID_BASIC,
                    R.drawable.ic_link_black_48dp,
                    R.string.nfccommand_emulate_uri_title,
                    R.string.nfccommand_emulate_uri_description,
                    EmulateUriRecordCommand::class.java
                )
            ),
            Pair(
                COM_OPT_DEBIT_WB_FULL,
                CommandOption(
                    COM_OPT_DEBIT_WB_FULL,
                    FAM_OPTION_ID_WRISTCOINPOS,
                    R.drawable.ic_debit_wristband_full_resp,
                    R.string.wristcoinPOS_debit_wristband_full_title,
                    R.string.wristcoinPOS_debit_wristband_full_description,
                    DebitWristbandFullRespCommand::class.java
                )
            ),
            Pair(
                COM_OPT_DEBIT_WB_SHORT,
                CommandOption(
                    COM_OPT_DEBIT_WB_SHORT,
                    FAM_OPTION_ID_WRISTCOINPOS,
                    R.drawable.ic_shopping_cart_black_24dp,
                    R.string.wristcoinPOS_debit_wristband_short_title,
                    R.string.wristcoinPOS_debit_wristband_short_description,
                    DebitWristbandShortRespCommand::class.java
                )
            ),
            Pair(
                COM_OPT_GET_WB_STATUS,
                CommandOption(
                    COM_OPT_GET_WB_STATUS,
                    FAM_OPTION_ID_WRISTCOINPOS,
                    R.drawable.ic_info_black_24dp,
                    R.string.wristcoinPOS_get_wristband_status_title,
                    R.string.wristcoinPOS_get_wristband_status_description,
                    GetWristbandStatusCommand::class.java
                )
            ),
            Pair(
                COM_OPT_GET_WRISTCOINPOS_COMFAMV,
                CommandOption(
                    COM_OPT_GET_WRISTCOINPOS_COMFAMV,
                    FAM_OPTION_ID_WRISTCOINPOS,
                    R.drawable.ic_library_version_48dp,
                    R.string.wristcoinPOS_get_command_family_version_title,
                    R.string.wristcoinPOS_get_command_family_version_description,
                    GetWristCoinPOSCommandFamilyVersionCommand::class.java
                )
            ),
            Pair(
                COM_OPT_SET_EVENTID,
                CommandOption(
                    COM_OPT_SET_EVENTID,
                    FAM_OPTION_ID_WRISTCOINPOS,
                    R.drawable.ic_event_black_24dp,
                    R.string.wristcoinPOS_set_event_id_title,
                    R.string.wristcoinPOS_set_event_id_description,
                    SetEventIdCommand::class.java
                )
            ),
            Pair(
                COM_OPT_TOPUP_WB_FULL,
                CommandOption(
                    COM_OPT_TOPUP_WB_FULL,
                    FAM_OPTION_ID_WRISTCOINPOS,
                    R.drawable.ic_topup_wb_full_resp,
                    R.string.wristcoinPOS_topup_wristband_full_title,
                    R.string.wristcoinPOS_topup_wristband_full_description,
                    TopupWristbandFullRespCommand::class.java
                )
            ),
            Pair(
                COM_OPT_TOPUP_WB_SHORT,
                CommandOption(
                    COM_OPT_TOPUP_WB_SHORT,
                    FAM_OPTION_ID_WRISTCOINPOS,
                    R.drawable.ic_local_atm_black_24dp,
                    R.string.wristcoinPOS_topup_wristband_short_title,
                    R.string.wristcoinPOS_topup_wristband_short_description,
                    TopupWristbandShortRespCommand::class.java
                )
            ),
            Pair(
                COM_OPT_CLOSEOUT_WB,
                CommandOption(
                    COM_OPT_CLOSEOUT_WB,
                    FAM_OPTION_ID_WRISTCOINPOS,
                    R.drawable.ic_cancel_black_24dp,
                    R.string.wristcoinPOS_closeout_wristband_title,
                    R.string.wristcoinPOS_closeout_wristband_description,
                    CloseoutWristbandCommand::class.java
                )
            ),
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

        private val ST_MICRO_M24SR02_COMMANDS by lazy {
            ALL_COMMAND_OPTIONS.filter {
                it.commandFamilyId == FAM_OPTION_ID_STMICRO
            }
        }

        private val STANDALONE_CHECKIN_COMMANDS by lazy {
            ALL_COMMAND_OPTIONS.filter {
                it.commandFamilyId == FAM_OPTION_ID_STANDALONE
            }
        }

        private val WRISTCON_POS_COMMANDS by lazy {
            ALL_COMMAND_OPTIONS.filter {
                it.commandFamilyId == FAM_OPTION_ID_WRISTCOINPOS
            }
        }

    }
}
