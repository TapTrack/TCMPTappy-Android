package com.taptrack.experiments.rancheria.business

import android.content.Context
import com.taptrack.experiments.rancheria.R
import com.taptrack.tcmptappy.tcmp.TCMPMessage
import com.taptrack.tcmptappy.tcmp.commandfamilies.basicnfc.BasicNfcCommandLibrary
import com.taptrack.tcmptappy.tcmp.commandfamilies.basicnfc.commands.*
import com.taptrack.tcmptappy.tcmp.commandfamilies.mifareclassic.MifareClassicCommandLibrary
import com.taptrack.tcmptappy.tcmp.commandfamilies.mifareclassic.commands.DetectMifareClassicCommand
import com.taptrack.tcmptappy.tcmp.commandfamilies.mifareclassic.commands.GetMifareClassicLibraryVersionCommand
import com.taptrack.tcmptappy.tcmp.commandfamilies.mifareclassic.commands.ReadMifareClassicCommand
import com.taptrack.tcmptappy.tcmp.commandfamilies.systemfamily.SystemCommandLibrary
import com.taptrack.tcmptappy.tcmp.commandfamilies.systemfamily.commands.GetBatteryLevelCommand
import com.taptrack.tcmptappy.tcmp.commandfamilies.systemfamily.commands.GetFirmwareVersionCommand
import com.taptrack.tcmptappy.tcmp.commandfamilies.systemfamily.commands.GetHardwareVersionCommand
import com.taptrack.tcmptappy.tcmp.commandfamilies.systemfamily.commands.PingCommand
import com.taptrack.tcmptappy.tcmp.commandfamilies.type4.Type4CommandLibrary
import com.taptrack.tcmptappy.tcmp.commandfamilies.type4.commands.*
import kotlin.reflect.full.createInstance

data class CommandFamilyOption(
        val id: Int,
        val imageRes: Int,
        val descriptionRes: Int)

data class CommandOption(
        val commandOptionId: Int,
        val commandFamilyId: Int,
        val imageRes: Int,
        val titleRes: Int,
        val descriptionRes: Int,
        val clazzes: List<Class<out TCMPMessage>>) {
    constructor(
            commandOptionId: Int,
            commandFamilyId: Int,
            imageRes: Int,
            titleRes: Int,
            descriptionRes: Int,
            clazz: Class<out TCMPMessage>) : this(commandOptionId,commandFamilyId,imageRes,titleRes,descriptionRes, listOf(clazz))
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


    // TODO: improve this procedure
    fun retrieveCommandOptionForMessage(msg: TCMPMessage): CommandOption? {
        var toSearch: List<CommandOption>
        if (msg.commandFamily contentEquals SystemCommandLibrary.FAMILY_ID) {
            toSearch = sortedSystemCommands
        } else if (msg.commandFamily contentEquals BasicNfcCommandLibrary.FAMILY_ID) {
            toSearch = sortedBasicNfcCommands
        } else if (msg.commandFamily contentEquals MifareClassicCommandLibrary.FAMILY_ID) {
            toSearch = sortedClassicCommands
        } else if (msg.commandFamily contentEquals Type4CommandLibrary.FAMILY_ID) {
            toSearch = sortedType4Commands
        } else {
            toSearch = sortedAllCommands
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
        when(familyId) {
            FAM_OPTION_ID_ALL -> {
                return sortedAllCommands
            }
            FAM_OPTION_ID_SYS -> {
                return sortedSystemCommands
            }
            FAM_OPTION_ID_BASIC -> {
                return sortedBasicNfcCommands
            }
            FAM_OPTION_ID_CLASSIC -> {
                return sortedClassicCommands
            }
            FAM_OPTION_ID_T4 -> {
                return sortedType4Commands
            }
            else -> {
                return emptyList()
            }
        }
    }

    companion object {
        val FAM_OPTION_ID_ALL = 0
        val FAM_OPTION_ID_SYS = 1
        val FAM_OPTION_ID_BASIC = 2
        val FAM_OPTION_ID_CLASSIC = 3
        val FAM_OPTION_ID_T4 = 4

        private val ALL_FAMILY_OPTIONS: List<CommandFamilyOption> = listOf(
                CommandFamilyOption(FAM_OPTION_ID_ALL, R.drawable.ic_all_inclusive_black_48dp, R.string.desc_all_families),
                CommandFamilyOption(FAM_OPTION_ID_SYS, R.drawable.ic_settings_black_48dp, R.string.desc_system_family),
                CommandFamilyOption(FAM_OPTION_ID_BASIC, R.drawable.ic_nfc_black_48dp, R.string.desc_basic_nfc_family),
                CommandFamilyOption(FAM_OPTION_ID_CLASSIC, R.drawable.ic_classic_black_48dp, R.string.desc_classic_family),
                CommandFamilyOption(FAM_OPTION_ID_T4, R.drawable.ic_type4_black_48dp, R.string.desc_type_4_family)
        )

        private val COM_OPT_GET_BATT = 0
        private val COM_OPT_HARDWARE_V = 1
        private val COM_OPT_SYSTEM_LIBV = 2
        private val COM_OPT_PING = 3
        private val COM_OPT_BASICNFC_LIBV = 4
        private val COM_OPT_SCAN_NDEF = 5
        private val COM_OPT_SCAN_TAG = 6
        private val COM_OPT_STOP = 7
        private val COM_OPT_WRITE_TEXT = 8
        private val COM_OPT_WRITE_URI = 9
        private val COM_OPT_LOCK_TAG = 10
        private val COM_OPT_DETECT_CLASS = 11
        private val COM_OPT_CLASS_LIBV = 12
        private val COM_OPT_READ_CLASS = 13
        private val COM_OPT_T4_DETECT = 14
        private val COM_OPT_T4_LIBV = 15
        private val COM_OPT_T4_TRANS = 16

        private val ALL_COMMAND_OPTIONS_MAP: Map<Int, CommandOption> = mapOf(
                Pair(COM_OPT_GET_BATT,
                        CommandOption(COM_OPT_GET_BATT, FAM_OPTION_ID_SYS, R.drawable.ic_get_battery_48dp, R.string.syscommand_get_battery_title, R.string.syscommand_get_battery_description, GetBatteryLevelCommand::class.java)),
                Pair(COM_OPT_HARDWARE_V,
                        CommandOption(COM_OPT_HARDWARE_V, FAM_OPTION_ID_SYS, R.drawable.ic_hardware_version_48dp, R.string.syscommand_get_hardware_title, R.string.syscommand_get_hardware_description, GetHardwareVersionCommand::class.java)),
                Pair(COM_OPT_PING,
                        CommandOption(COM_OPT_PING, FAM_OPTION_ID_SYS, R.drawable.ic_library_version_48dp, R.string.syscommand_get_libraryv_title, R.string.syscommand_get_libraryv_description, GetFirmwareVersionCommand::class.java)),
                Pair(COM_OPT_SYSTEM_LIBV,
                        CommandOption(COM_OPT_SYSTEM_LIBV, FAM_OPTION_ID_SYS, R.drawable.ic_ping_black_48dp, R.string.syscommand_ping_title, R.string.syscommand_ping_description, PingCommand::class.java)),

                Pair(COM_OPT_BASICNFC_LIBV,
                        CommandOption(COM_OPT_BASICNFC_LIBV, FAM_OPTION_ID_BASIC, R.drawable.ic_library_version_48dp, R.string.nfccommand_get_libraryv_title, R.string.nfccommand_get_libraryv_description, GetBasicNfcLibraryVersionCommand::class.java)),
                Pair(COM_OPT_SCAN_NDEF,
                        CommandOption(COM_OPT_SCAN_NDEF, FAM_OPTION_ID_BASIC, R.drawable.ic_scan_ndef_48dp, R.string.nfccommand_scan_ndef_title, R.string.nfccommand_scan_ndef_description, listOf(ScanNdefCommand::class.java, StreamNdefCommand::class.java))),

                Pair(COM_OPT_SCAN_TAG,
                        CommandOption(COM_OPT_SCAN_TAG, FAM_OPTION_ID_BASIC, R.drawable.ic_nfc_black_48dp, R.string.nfccommand_scan_tag_title, R.string.nfccommand_scan_tag_description, listOf(ScanTagCommand::class.java, StreamTagsCommand::class.java))),

                Pair(COM_OPT_STOP,
                        CommandOption(COM_OPT_STOP, FAM_OPTION_ID_BASIC, R.drawable.ic_stop_black_48dp, R.string.nfccommand_stop_title, R.string.nfccommand_stop_description, StopCommand::class.java)),
                Pair(COM_OPT_WRITE_TEXT,
                        CommandOption(COM_OPT_WRITE_TEXT, FAM_OPTION_ID_BASIC, R.drawable.ic_description_black_48dp, R.string.nfccommand_write_text_title, R.string.nfccommand_write_text_description, WriteNdefTextRecordCommand::class.java)),
                Pair(COM_OPT_WRITE_URI,
                        CommandOption(COM_OPT_WRITE_URI, FAM_OPTION_ID_BASIC, R.drawable.ic_link_black_48dp, R.string.nfccommand_write_uri_title, R.string.nfccommand_write_uri_description, listOf(WriteNdefUriRecordCommand::class.java))),
                Pair(COM_OPT_LOCK_TAG,
                        CommandOption(COM_OPT_LOCK_TAG, FAM_OPTION_ID_BASIC, R.drawable.ic_lock_black_48dp, R.string.nfccommand_lock_tag_title, R.string.nfccommand_lock_tag_description, LockTagCommand::class.java)),

                Pair(COM_OPT_DETECT_CLASS,
                        CommandOption(COM_OPT_DETECT_CLASS, FAM_OPTION_ID_CLASSIC, R.drawable.ic_nfc_black_48dp, R.string.classiccommand_detect_title, R.string.classiccommand_detect_description, DetectMifareClassicCommand::class.java)),
                Pair(COM_OPT_CLASS_LIBV,
                        CommandOption(COM_OPT_CLASS_LIBV, FAM_OPTION_ID_CLASSIC, R.drawable.ic_library_version_48dp, R.string.classiccommand_get_version_title, R.string.classiccommand_get_version_description, GetMifareClassicLibraryVersionCommand::class.java)),
                Pair(COM_OPT_READ_CLASS,
                        CommandOption(COM_OPT_READ_CLASS, FAM_OPTION_ID_CLASSIC, R.drawable.ic_read_classic_48dp, R.string.classiccommand_read_classic_title, R.string.classiccommand_read_classic_description, ReadMifareClassicCommand::class.java)),

                Pair(COM_OPT_T4_DETECT,
                        CommandOption(COM_OPT_T4_DETECT, FAM_OPTION_ID_T4, R.drawable.ic_nfc_black_48dp, R.string.type4command_detect_title, R.string.type4command_detect_description, listOf(DetectType4Command::class.java,DetectType4BCommand::class.java,DetectType4BSpecificAfiCommand::class.java))),
                Pair(COM_OPT_T4_LIBV,
                        CommandOption(COM_OPT_T4_LIBV, FAM_OPTION_ID_T4, R.drawable.ic_library_version_48dp, R.string.type4command_get_version_title, R.string.type4command_get_version_description, GetType4LibraryVersionCommand::class.java)),
                Pair(COM_OPT_T4_TRANS,
                        CommandOption(COM_OPT_T4_TRANS, FAM_OPTION_ID_T4, R.drawable.ic_transceive_48dp, R.string.type4command_transceive_apdu_title, R.string.type4command_transceive_apdu_description, TransceiveApduCommand::class.java))
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


    }
}

