package com.taptrack.tcmptappy2;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A MessageResolverMux multiplexes several message resolvers.
 *
 * If it is created with CommandFamilyMessageResolvers, they will be
 * separated out and used first in order to attempt to resolve
 * the message. Other message resolvers will then be called in the
 * order they are specified until one of them returns a non-null
 * result or an exception is thrown.
 *
 * Note that this muxer does not support multiple resolvers for each
 * command family. In case of duplicates, the last one you specify
 * will be the one used.
 */
public class MessageResolverMux implements MessageResolver {
    private static class CommandFamilyIdKey {
        private final byte[] familyId;

        private CommandFamilyIdKey(byte[] familyId) {
            this.familyId = familyId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            CommandFamilyIdKey that = (CommandFamilyIdKey) o;

            return Arrays.equals(familyId, that.familyId);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(familyId);
        }
    }

    private final Map<CommandFamilyIdKey, MessageResolver> familyResolverMap;
    private final List<MessageResolver> otherResolverList;

    public MessageResolverMux(MessageResolver... resolvers) {
        Map<CommandFamilyIdKey,MessageResolver> resolverMap = null;
        List<MessageResolver> otherResolverList = null;

        for (MessageResolver resolver :
                resolvers) {
            if (resolver instanceof CommandFamilyMessageResolver) {
                if (resolverMap == null) {
                    resolverMap = new HashMap<>(1);
                }
                resolverMap.put(
                        new CommandFamilyIdKey(((CommandFamilyMessageResolver) resolver).getCommandFamilyId()),
                        resolver
                );
            } else {
                if (otherResolverList == null) {
                    otherResolverList = new ArrayList<>(1);
                }
                otherResolverList.add(resolver);
            }
        }

        if (resolverMap != null) {
            familyResolverMap = resolverMap;
        } else {
            familyResolverMap = Collections.emptyMap();
        }

        if (otherResolverList != null) {
            this.otherResolverList = otherResolverList;
        } else {
            this.otherResolverList = Collections.emptyList();
        }
    }


    @Nullable
    @Override
    public TCMPMessage resolveCommand(@NonNull TCMPMessage message) throws MalformedPayloadException {
        CommandFamilyIdKey key = new CommandFamilyIdKey(message.getCommandFamily());
        MessageResolver familyResolver = familyResolverMap.get(key);
        if (familyResolver != null) {
            TCMPMessage result = familyResolver.resolveCommand(message);
            if (result != null) {
                return result;
            }
        }

        for(MessageResolver resolver: otherResolverList) {
            TCMPMessage result = resolver.resolveCommand(message);
            if (result != null) {
                return result;
            }
        }

        return null;
    }

    @Nullable
    @Override
    public TCMPMessage resolveResponse(@NonNull TCMPMessage message) throws MalformedPayloadException {
        CommandFamilyIdKey key = new CommandFamilyIdKey(message.getCommandFamily());
        MessageResolver familyResolver = familyResolverMap.get(key);
        if (familyResolver != null) {
            TCMPMessage result = familyResolver.resolveResponse(message);
            if (result != null) {
                return result;
            }
        }

        for(MessageResolver resolver: otherResolverList) {
            TCMPMessage result = resolver.resolveResponse(message);
            if (result != null) {
                return result;
            }
        }

        return null;
    }
}
