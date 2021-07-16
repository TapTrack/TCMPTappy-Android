/*
 * Copyright (c) 2016. Papyrus Electronics, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * you may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.taptrack.tcmptappy2.commandfamilies.basicnfc;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Size;

import com.taptrack.tcmptappy2.CommandFamilyMessageResolver;
import com.taptrack.tcmptappy2.MalformedPayloadException;
import com.taptrack.tcmptappy2.TCMPMessage;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands.AutoPollCommand;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands.AutoPollNdefCommand;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands.DispatchTagCommand;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands.DispatchTagsCommand;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands.EmulateCustomNdefRecordCommand;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands.EmulateTextRecordCommand;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands.EmulateUriRecordCommand;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands.GetBasicNfcLibraryVersionCommand;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands.InitiateTappyTagHandshakeCommand;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands.LockTagCommand;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands.ScanNdefCommand;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands.ScanTagCommand;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands.StopCommand;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands.StreamNdefCommand;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands.StreamTagsCommand;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands.WriteNdefCustomMessageCommand;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands.WriteNdefTextRecordCommand;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands.WriteNdefUriRecordCommand;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.responses.AutoPollTagEnteredResponse;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.responses.AutoPollTagExitedResponse;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.responses.BasicNfcErrorResponse;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.responses.BasicNfcLibraryVersionResponse;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.responses.EmulationStoppedResponse;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.responses.EmulationSuccessResponse;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.responses.NdefFoundResponse;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.responses.ResponseDataTransmitted;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.responses.ScanTimeoutResponse;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.responses.SignedTagFoundResponse;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.responses.TagFoundResponse;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.responses.TagLockedResponse;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.responses.TagWrittenResponse;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.responses.TappyTagDataReceivedResponse;

import java.util.Arrays;

public class BasicNfcCommandResolver implements CommandFamilyMessageResolver {
    public static final byte[] FAMILY_ID = new byte[]{0x00, 0x01};

    private static void assertFamilyMatches(@NonNull TCMPMessage message) {
        if (!Arrays.equals(message.getCommandFamily(),FAMILY_ID)) {
            throw new IllegalArgumentException("Specified message is for a different command family");
        }
    }

    @Override
    @Nullable
    public TCMPMessage resolveCommand(@NonNull TCMPMessage message) throws MalformedPayloadException {
        assertFamilyMatches(message);

        TCMPMessage parsedMessage;
        switch (message.getCommandCode()) {
            case GetBasicNfcLibraryVersionCommand.COMMAND_CODE:
                parsedMessage = new GetBasicNfcLibraryVersionCommand();
                break;

            case ScanNdefCommand.COMMAND_CODE:
                parsedMessage = new ScanNdefCommand();
                break;

            case ScanTagCommand.COMMAND_CODE:
                parsedMessage = new ScanTagCommand();
                break;

            case StopCommand.COMMAND_CODE:
                parsedMessage = new StopCommand();
                break;

            case StreamNdefCommand.COMMAND_CODE:
                parsedMessage = new StreamNdefCommand();
                break;

            case StreamTagsCommand.COMMAND_CODE:
                parsedMessage = new StreamTagsCommand();
                break;

            case WriteNdefCustomMessageCommand.COMMAND_CODE:
                parsedMessage = new WriteNdefCustomMessageCommand();
                break;

            case WriteNdefTextRecordCommand.COMMAND_CODE:
                parsedMessage = new WriteNdefTextRecordCommand();
                break;

            case WriteNdefUriRecordCommand.COMMAND_CODE:
                parsedMessage = new WriteNdefUriRecordCommand();
                break;

            case LockTagCommand.COMMAND_CODE:
                parsedMessage = new LockTagCommand();
                break;

            case DispatchTagCommand.COMMAND_CODE:
                parsedMessage = new DispatchTagCommand();
                break;

            case DispatchTagsCommand.COMMAND_CODE:
                parsedMessage = new DispatchTagsCommand();
                break;

            case AutoPollCommand.COMMAND_CODE:
                parsedMessage = new AutoPollCommand();
                break;

            case AutoPollNdefCommand.COMMAND_CODE:
                parsedMessage = new AutoPollNdefCommand();
                break;

            case EmulateTextRecordCommand.COMMAND_CODE:
                parsedMessage = new EmulateTextRecordCommand();
                break;

            case EmulateUriRecordCommand.COMMAND_CODE:
                parsedMessage = new EmulateUriRecordCommand();
                break;

            case EmulateCustomNdefRecordCommand.COMMAND_CODE:
                parsedMessage = new EmulateCustomNdefRecordCommand();
                break;

            case InitiateTappyTagHandshakeCommand.COMMAND_CODE:
                parsedMessage = new InitiateTappyTagHandshakeCommand();
                break;

            default:
                return null;
        }
        parsedMessage.parsePayload(message.getPayload());
        return parsedMessage;
    }

    @Override
    @Nullable
    public TCMPMessage resolveResponse(@NonNull TCMPMessage message) throws MalformedPayloadException {
        assertFamilyMatches(message);

        TCMPMessage parsedMessage;
        switch (message.getCommandCode()) {
            case BasicNfcErrorResponse.COMMAND_CODE:
                parsedMessage = new BasicNfcErrorResponse();
                break;

            case BasicNfcLibraryVersionResponse.COMMAND_CODE:
                parsedMessage = new BasicNfcLibraryVersionResponse();
                break;

            case NdefFoundResponse.COMMAND_CODE:
                parsedMessage = new NdefFoundResponse();
                break;

            case ScanTimeoutResponse.COMMAND_CODE:
                parsedMessage = new ScanTimeoutResponse();
                break;

            case TagFoundResponse.COMMAND_CODE:
                parsedMessage = new TagFoundResponse();
                break;

            case SignedTagFoundResponse.COMMAND_CODE:
                parsedMessage = new SignedTagFoundResponse();
                break;

            case TagWrittenResponse.COMMAND_CODE:
                parsedMessage = new TagWrittenResponse();
                break;

            case TagLockedResponse.COMMAND_CODE:
                parsedMessage = new TagLockedResponse();
                break;

            case AutoPollTagEnteredResponse.COMMAND_CODE:
                parsedMessage = new AutoPollTagEnteredResponse();
                break;

            case AutoPollTagExitedResponse.COMMAND_CODE:
                parsedMessage = new AutoPollTagExitedResponse();
                break;

            case EmulationSuccessResponse.COMMAND_CODE:
                parsedMessage = new EmulationSuccessResponse();
                break;

            case EmulationStoppedResponse.COMMAND_CODE:
                parsedMessage = new EmulationStoppedResponse();
                break;

            case TappyTagDataReceivedResponse.COMMAND_CODE:
                parsedMessage = new TappyTagDataReceivedResponse();
                break;

            case ResponseDataTransmitted.COMMAND_CODE:
                parsedMessage = new ResponseDataTransmitted();
                break;

            default:
                return null;
        }
        parsedMessage.parsePayload(message.getPayload());
        return parsedMessage;
    }

    @Override
    @NonNull
    @Size(2)
    public byte[] getCommandFamilyId() {
        return FAMILY_ID;
    }
}
