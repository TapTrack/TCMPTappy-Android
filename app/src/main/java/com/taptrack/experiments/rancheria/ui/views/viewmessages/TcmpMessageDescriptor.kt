package com.taptrack.experiments.rancheria.ui.views.viewmessages

import android.content.Context
import android.nfc.NdefRecord
import android.support.annotation.StringRes
import com.taptrack.experiments.rancheria.R
import com.taptrack.experiments.rancheria.ui.toHex
import com.taptrack.experiments.rancheria.ui.toUnsigned
import com.taptrack.tcmptappy.tappy.constants.TagTypes
import com.taptrack.tcmptappy.tcmp.StandardErrorResponse
import com.taptrack.tcmptappy.tcmp.StandardLibraryVersionResponse
import com.taptrack.tcmptappy.tcmp.TCMPMessage
import com.taptrack.tcmptappy.tcmp.commandfamilies.basicnfc.AbstractBasicNfcMessage
import com.taptrack.tcmptappy.tcmp.commandfamilies.basicnfc.commands.*
import com.taptrack.tcmptappy.tcmp.commandfamilies.basicnfc.responses.*
import com.taptrack.tcmptappy.tcmp.commandfamilies.mifareclassic.AbstractMifareClassicMessage
import com.taptrack.tcmptappy.tcmp.commandfamilies.mifareclassic.KeySetting
import com.taptrack.tcmptappy.tcmp.commandfamilies.mifareclassic.commands.DetectMifareClassicCommand
import com.taptrack.tcmptappy.tcmp.commandfamilies.mifareclassic.commands.GetMifareClassicLibraryVersionCommand
import com.taptrack.tcmptappy.tcmp.commandfamilies.mifareclassic.commands.ReadMifareClassicCommand
import com.taptrack.tcmptappy.tcmp.commandfamilies.mifareclassic.responses.*
import com.taptrack.tcmptappy.tcmp.commandfamilies.systemfamily.AbstractSystemMessage
import com.taptrack.tcmptappy.tcmp.commandfamilies.systemfamily.commands.GetBatteryLevelCommand
import com.taptrack.tcmptappy.tcmp.commandfamilies.systemfamily.commands.GetFirmwareVersionCommand
import com.taptrack.tcmptappy.tcmp.commandfamilies.systemfamily.commands.GetHardwareVersionCommand
import com.taptrack.tcmptappy.tcmp.commandfamilies.systemfamily.commands.PingCommand
import com.taptrack.tcmptappy.tcmp.commandfamilies.systemfamily.responses.*
import com.taptrack.tcmptappy.tcmp.commandfamilies.type4.AbstractType4Message
import com.taptrack.tcmptappy.tcmp.commandfamilies.type4.commands.*
import com.taptrack.tcmptappy.tcmp.commandfamilies.type4.responses.*
import java.nio.charset.Charset
import java.util.*
import kotlin.experimental.and

object TcmpMessageDescriptor {

    fun getCommandDescription(command: TCMPMessage,
                              ctx: Context): String {
        if (command is AbstractBasicNfcMessage) {
            return getCommandDescriptionBasicNfc(command, ctx)
        } else if (command is AbstractSystemMessage) {
            return getCommandDescriptionSystem(command, ctx)
        } else if (command is AbstractMifareClassicMessage) {
            return getCommandDescriptionClassic(command, ctx)
        } else if (command is AbstractType4Message) {
            return getCommandDescriptionType4(command, ctx)
        } else {
            return ctx.getString(R.string.unknown_command)
        }
    }

    fun getCommandDescriptionBasicNfc(command: TCMPMessage,
                                                ctx: Context): String {
        if (command is GetBasicNfcLibraryVersionCommand) {
            return ctx.getString(R.string.get_basic_nfc_lib_version)
        } else if (command is ScanNdefCommand) {
            if (command.timeout.toInt() != 0) {
                val form = ctx.getString(R.string.scan_ndef_seconds)
                return String.format(form, command.timeout.toUnsigned())
            } else {
                return ctx.getString(R.string.scan_ndef_indefinite)
            }
        } else if (command is StreamNdefCommand) {
            if (command.timeout.toInt() != 0) {
                val form = ctx.getString(R.string.stream_ndef_seconds)
                return String.format(form, (command.timeout.toUnsigned()))
            } else {
                return ctx.getString(R.string.stream_ndef_indefinite)
            }
        } else if (command is ScanTagCommand) {
            if (command.timeout.toInt() != 0) {
                val form = ctx.getString(R.string.scan_tag_seconds)
                return String.format(form, command.timeout.toUnsigned())
            } else {
                return ctx.getString(R.string.scan_tag_indefinitely)
            }

        } else if (command is StreamTagsCommand) {
            if (command.timeout.toInt() != 0) {
                val form = ctx.getString(R.string.stream_tag_seconds)
                return String.format(form, command.timeout.toUnsigned())
            } else {
                return ctx.getString(R.string.stream_tag_indefinitely)
            }
        } else if (command is WriteNdefTextRecordCommand) {
            val cmd = command
            if (cmd.timeout.toInt() != 0) {
                val form = ctx.getString(R.string.write_ndef_txt_seconds)
                return String.format(form, command.timeout.toUnsigned(), cmd.text)
            } else {
                val form = ctx.getString(R.string.write_ndef_txt_indefinite)
                return String.format(form, cmd.text)
            }
        } else if (command is WriteNdefUriRecordCommand) {
            val cmd = command
            val uri = NdefUriCodeUtils.decodeNdefUri(cmd.uriCode, cmd.uriBytes)
            if (cmd.timeout.toInt() != 0) {
                val form = ctx.getString(R.string.write_ndef_uri_seconds)
                return String.format(form, command.timeout.toUnsigned(), uri)
            } else {
                val form = ctx.getString(R.string.write_ndef_uri_indefinite)
                return String.format(form, uri)
            }
        } else if (command is StopCommand) {
            return ctx.getString(R.string.stop_command)
        } else if (command is LockTagCommand) {
            val cmd = command
            if (cmd.timeout.toInt() != 0) {
                val form = ctx.getString(R.string.lock_tags_seconds)
                return String.format(form, command.timeout.toUnsigned())
            } else {
                return ctx.getString(R.string.lock_tags_indefinite)
            }
        } else {
            return ctx.getString(R.string.unknown_command)
        }
    }

    fun getCommandDescriptionSystem(command: TCMPMessage,
                                              ctx: Context): String {
        if (command is GetBatteryLevelCommand) {
            return ctx.getString(R.string.get_battery_level)
        } else if (command is GetFirmwareVersionCommand) {
            return ctx.getString(R.string.get_firmware_version)
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

        } else if (command is GetType4LibraryVersionCommand) {
            return ctx.getString(R.string.get_type4_version)
        } else if (command is TransceiveApduCommand) {
            return String.format(ctx.getString(R.string.send_apdu), command.apdu.toHex())
        } else {
            return ctx.getString(R.string.unknown_command)
        }
    }

    fun getResponseDescription(response: TCMPMessage,
                               ctx: Context): String {
        if (response is StandardLibraryVersionResponse) {
            if (response is HardwareVersionResponse) {
                return parseStandardLibraryVersionResponse(ctx, R.string.resp_hardware, response)
            } else if (response is FirmwareVersionResponse) {
                return parseStandardLibraryVersionResponse(ctx, R.string.resp_firmware, response)
            } else if (response is BasicNfcLibraryVersionResponse) {
                return parseStandardLibraryVersionResponse(ctx, R.string.family_basicnfc, response)
            } else if (response is MifareClassicLibraryVersionResponse) {
                return parseStandardLibraryVersionResponse(ctx, R.string.family_classic, response)
            } else if (response is Type4LibraryVersionResponse) {
                return parseStandardLibraryVersionResponse(ctx, R.string.family_type4, response)
            } else {
                return parseStandardLibraryVersionResponse(ctx, R.string.family_unknown, response)
            }
        } else if (response is AbstractSystemMessage) {
            return getSystemResponseDescription(response, ctx)
        } else if (response is AbstractBasicNfcMessage) {
            return getBasicNfcResponseDescription(response, ctx)
        } else if (response is AbstractType4Message) {
            return getType4ResponseDescription(response, ctx)
        } else if (response is AbstractMifareClassicMessage) {
            return getClassicResponseDescription(response, ctx)
        } else if (response is StandardErrorResponse) {
            return parseStandardErrorResponse(ctx,
                    R.string.family_unknown,
                    response)
        } else {
            return ctx.getString(R.string.unknown_response)
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
        if (response is NdefFoundResponse) {
            return parseNdefFoundResponse(ctx, response)
        } else if (response is ScanTimeoutResponse) {
            return ctx.getString(R.string.scan_timeout_response)
        } else if (response is TagFoundResponse) {
            val resp = response
            return String.format(
                    ctx.getString(R.string.tag_found_response),
                    (resp.tagCode).toHex(),
                    parseTagType(ctx, resp.tagType))
        } else if (response is TagWrittenResponse) {
            return String.format(
                    ctx.getString(R.string.tag_written_response),
                    (response.tagCode).toHex())
        } else if (response is TagLockedResponse) {
            return String.format(
                    ctx.getString(R.string.tag_locked_response),
                    (response.tagCode).toHex())
        } else if (response is BasicNfcErrorResponse) {
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
        } else {
            return ctx.getString(R.string.unknown_response)
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
