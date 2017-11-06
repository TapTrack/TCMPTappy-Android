package com.taptrack.experiments.rancheria.ui.views.viewmessages

import com.taptrack.tcmptappy.tappy.constants.NdefUriCodes

internal object NdefUriCodeUtils {
    fun decodeNdefUri(ndefUriCode: Byte, message: ByteArray): String {
        val prefix: String

        when (ndefUriCode) {
            NdefUriCodes.URICODE_HTTPWWW -> prefix = "http://www."
            NdefUriCodes.URICODE_HTTPSWWW -> prefix = "https://www."
            NdefUriCodes.URICODE_HTTP -> prefix = "http://"
            NdefUriCodes.URICODE_HTTPS -> prefix = "https://"
            NdefUriCodes.URICODE_TEL -> prefix = "tel:"
            NdefUriCodes.URICODE_MAILTO -> prefix = "mailto:"
            NdefUriCodes.URICODE_FTP_ANON -> prefix = "ftp://anonymous:anonymous@"
            NdefUriCodes.URICODE_FTP_FTP -> prefix = "ftp://ftp."
            NdefUriCodes.URICODE_FTPS -> prefix = "ftps://"
            NdefUriCodes.URICODE_SFTP -> prefix = "sftp://"
            NdefUriCodes.URICODE_SMB -> prefix = "smb://"
            NdefUriCodes.URICODE_NFS -> prefix = "nfs://"
            NdefUriCodes.URICODE_FTP -> prefix = "ftp://"
            NdefUriCodes.URICODE_DAV -> prefix = "dav://"
            NdefUriCodes.URICODE_NEWS -> prefix = "news:"
            NdefUriCodes.URICODE_TELNET -> prefix = "telnet://"
            NdefUriCodes.URICODE_IMAP -> prefix = "imap:"
            NdefUriCodes.URICODE_RTSP -> prefix = "rtsp://"
            NdefUriCodes.URICODE_URN -> prefix = "urn:"
            NdefUriCodes.URICODE_POP -> prefix = "pop:"
            NdefUriCodes.URICODE_SIP -> prefix = "sip:"
            NdefUriCodes.URICODE_SIPS -> prefix = "sips:"
            NdefUriCodes.URICODE_TFTP -> prefix = "tftp:"
            NdefUriCodes.URICODE_BTSPP -> prefix = "btspp://"
            NdefUriCodes.URICODE_BTL2CAP -> prefix = "btl2cap://"
            NdefUriCodes.URICODE_BTGOEP -> prefix = "btgoep://"
            NdefUriCodes.URICODE_TCPOBEX -> prefix = "tcpobex://"
            NdefUriCodes.URICODE_IRDAOBEX -> prefix = "irdaobex://"
            NdefUriCodes.URICODE_FILE -> prefix = "file://"
            NdefUriCodes.URICODE_URN_EPC_ID -> prefix = "urn:epc:id:"
            NdefUriCodes.URICODE_URN_EPC_TAG -> prefix = "urn:epc:tag:"
            NdefUriCodes.URICODE_URN_EPC_PAT -> prefix = "urn:epc:pat:"
            NdefUriCodes.URICODE_URN_EPC_RAW -> prefix = "urn:epc:raw:"
            NdefUriCodes.URICODE_URN_EPC -> prefix = "urn:epc:"
            NdefUriCodes.URICODE_URN_NFC -> prefix = "urn:nfc:"
            else -> prefix = ""
        }

        return prefix + String(message)
    }
}
