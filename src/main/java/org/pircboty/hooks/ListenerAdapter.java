/**
 * Copyright (C) 2010-2013
 *
 * This file is part of PircBotY.
 *
 * PircBotY is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * PircBotY is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * PircBotY. If not, see <http://www.gnu.org/licenses/>.
 */
package org.pircboty.hooks;

import org.pircboty.PircBotY;
import org.pircboty.hooks.events.ActionEvent;
import org.pircboty.hooks.events.ChannelInfoEvent;
import org.pircboty.hooks.events.ConnectEvent;
import org.pircboty.hooks.events.DisconnectEvent;
import org.pircboty.hooks.events.FingerEvent;
import org.pircboty.hooks.events.HalfOpEvent;
import org.pircboty.hooks.events.IncomingChatRequestEvent;
import org.pircboty.hooks.events.IncomingFileTransferEvent;
import org.pircboty.hooks.events.InviteEvent;
import org.pircboty.hooks.events.JoinEvent;
import org.pircboty.hooks.events.KickEvent;
import org.pircboty.hooks.events.MessageEvent;
import org.pircboty.hooks.events.ModeEvent;
import org.pircboty.hooks.events.MotdEvent;
import org.pircboty.hooks.events.NickAlreadyInUseEvent;
import org.pircboty.hooks.events.NickChangeEvent;
import org.pircboty.hooks.events.NoticeEvent;
import org.pircboty.hooks.events.OpEvent;
import org.pircboty.hooks.events.OwnerEvent;
import org.pircboty.hooks.events.PartEvent;
import org.pircboty.hooks.events.PingEvent;
import org.pircboty.hooks.events.PrivateMessageEvent;
import org.pircboty.hooks.events.QuitEvent;
import org.pircboty.hooks.events.RemoveChannelBanEvent;
import org.pircboty.hooks.events.RemoveChannelKeyEvent;
import org.pircboty.hooks.events.RemoveChannelLimitEvent;
import org.pircboty.hooks.events.RemoveInviteOnlyEvent;
import org.pircboty.hooks.events.RemoveModeratedEvent;
import org.pircboty.hooks.events.RemoveNoExternalMessagesEvent;
import org.pircboty.hooks.events.RemovePrivateEvent;
import org.pircboty.hooks.events.RemoveSecretEvent;
import org.pircboty.hooks.events.RemoveTopicProtectionEvent;
import org.pircboty.hooks.events.ServerPingEvent;
import org.pircboty.hooks.events.ServerResponseEvent;
import org.pircboty.hooks.events.SetChannelBanEvent;
import org.pircboty.hooks.events.SetChannelKeyEvent;
import org.pircboty.hooks.events.SetChannelLimitEvent;
import org.pircboty.hooks.events.SetInviteOnlyEvent;
import org.pircboty.hooks.events.SetModeratedEvent;
import org.pircboty.hooks.events.SetNoExternalMessagesEvent;
import org.pircboty.hooks.events.SetPrivateEvent;
import org.pircboty.hooks.events.SetSecretEvent;
import org.pircboty.hooks.events.SetTopicProtectionEvent;
import org.pircboty.hooks.events.SocketConnectEvent;
import org.pircboty.hooks.events.SuperOpEvent;
import org.pircboty.hooks.events.TimeEvent;
import org.pircboty.hooks.events.TopicEvent;
import org.pircboty.hooks.events.UnknownEvent;
import org.pircboty.hooks.events.UserListEvent;
import org.pircboty.hooks.events.UserModeEvent;
import org.pircboty.hooks.events.VersionEvent;
import org.pircboty.hooks.events.VoiceEvent;
import org.pircboty.hooks.events.WhoisEvent;
import org.pircboty.hooks.types.GenericCTCPEvent;
import org.pircboty.hooks.types.GenericChannelEvent;
import org.pircboty.hooks.types.GenericChannelModeEvent;
import org.pircboty.hooks.types.GenericChannelUserEvent;
import org.pircboty.hooks.types.GenericDCCEvent;
import org.pircboty.hooks.types.GenericMessageEvent;
import org.pircboty.hooks.types.GenericUserEvent;
import org.pircboty.hooks.types.GenericUserModeEvent;

/**
 * Adapter that provides methods to capture each event separately, removing the
 * need to check, cast, and call your custom method for each event you want to
 * capture.
 * <p>
 * To use, simply override the method that has the event you want to capture.
 * <p>
 * <b>WARNING:</b> If you are going to be implementing {@link Listener}'s
 * {@link Listener#onEvent(org.PircBotY.hooks.Event) } method, you must call
 * <code>super.onEvent(event)</code>, otherwise none of the Adapter hook methods
 * will be called!
 *
 * @author
 */
public abstract class ListenerAdapter<T extends PircBotY> implements Listener<T> {

    @Override
    public final void onEvent(Event<T> event) throws Exception {
        if (event instanceof ActionEvent) {
            onAction((ActionEvent<T>) event);
        } else if (event instanceof ChannelInfoEvent) {
            onChannelInfo((ChannelInfoEvent<T>) event);
        } else if (event instanceof ConnectEvent) {
            onConnect((ConnectEvent<T>) event);
        } else if (event instanceof DisconnectEvent) {
            onDisconnect((DisconnectEvent<T>) event);
        } else if (event instanceof FingerEvent) {
            onFinger((FingerEvent<T>) event);
        } else if (event instanceof HalfOpEvent) {
            onHalfOp((HalfOpEvent<T>) event);
        } else if (event instanceof IncomingChatRequestEvent) {
            onIncomingChatRequest((IncomingChatRequestEvent<T>) event);
        } else if (event instanceof IncomingFileTransferEvent) {
            onIncomingFileTransfer((IncomingFileTransferEvent<T>) event);
        } else if (event instanceof InviteEvent) {
            onInvite((InviteEvent<T>) event);
        } else if (event instanceof JoinEvent) {
            onJoin((JoinEvent<T>) event);
        } else if (event instanceof KickEvent) {
            onKick((KickEvent<T>) event);
        } else if (event instanceof MessageEvent) {
            onMessage((MessageEvent<T>) event);
        } else if (event instanceof ModeEvent) {
            onMode((ModeEvent<T>) event);
        } else if (event instanceof MotdEvent) {
            onMotd((MotdEvent<T>) event);
        } else if (event instanceof NickAlreadyInUseEvent) {
            onNickAlreadyInUse((NickAlreadyInUseEvent<T>) event);
        } else if (event instanceof NickChangeEvent) {
            onNickChange((NickChangeEvent<T>) event);
        } else if (event instanceof NoticeEvent) {
            onNotice((NoticeEvent<T>) event);
        } else if (event instanceof OpEvent) {
            onOp((OpEvent<T>) event);
        } else if (event instanceof OwnerEvent) {
            onOwner((OwnerEvent<T>) event);
        } else if (event instanceof PartEvent) {
            onPart((PartEvent<T>) event);
        } else if (event instanceof PingEvent) {
            onPing((PingEvent<T>) event);
        } else if (event instanceof PrivateMessageEvent) {
            onPrivateMessage((PrivateMessageEvent<T>) event);
        } else if (event instanceof QuitEvent) {
            onQuit((QuitEvent<T>) event);
        } else if (event instanceof RemoveChannelBanEvent) {
            onRemoveChannelBan((RemoveChannelBanEvent<T>) event);
        } else if (event instanceof RemoveChannelKeyEvent) {
            onRemoveChannelKey((RemoveChannelKeyEvent<T>) event);
        } else if (event instanceof RemoveChannelLimitEvent) {
            onRemoveChannelLimit((RemoveChannelLimitEvent<T>) event);
        } else if (event instanceof RemoveInviteOnlyEvent) {
            onRemoveInviteOnly((RemoveInviteOnlyEvent<T>) event);
        } else if (event instanceof RemoveModeratedEvent) {
            onRemoveModerated((RemoveModeratedEvent<T>) event);
        } else if (event instanceof RemoveNoExternalMessagesEvent) {
            onRemoveNoExternalMessages((RemoveNoExternalMessagesEvent<T>) event);
        } else if (event instanceof RemovePrivateEvent) {
            onRemovePrivate((RemovePrivateEvent<T>) event);
        } else if (event instanceof RemoveSecretEvent) {
            onRemoveSecret((RemoveSecretEvent<T>) event);
        } else if (event instanceof RemoveTopicProtectionEvent) {
            onRemoveTopicProtection((RemoveTopicProtectionEvent<T>) event);
        } else if (event instanceof ServerPingEvent) {
            onServerPing((ServerPingEvent<T>) event);
        } else if (event instanceof ServerResponseEvent) {
            onServerResponse((ServerResponseEvent<T>) event);
        } else if (event instanceof SetChannelBanEvent) {
            onSetChannelBan((SetChannelBanEvent<T>) event);
        } else if (event instanceof SetChannelKeyEvent) {
            onSetChannelKey((SetChannelKeyEvent<T>) event);
        } else if (event instanceof SetChannelLimitEvent) {
            onSetChannelLimit((SetChannelLimitEvent<T>) event);
        } else if (event instanceof SetInviteOnlyEvent) {
            onSetInviteOnly((SetInviteOnlyEvent<T>) event);
        } else if (event instanceof SetModeratedEvent) {
            onSetModerated((SetModeratedEvent<T>) event);
        } else if (event instanceof SetNoExternalMessagesEvent) {
            onSetNoExternalMessages((SetNoExternalMessagesEvent<T>) event);
        } else if (event instanceof SetPrivateEvent) {
            onSetPrivate((SetPrivateEvent<T>) event);
        } else if (event instanceof SetSecretEvent) {
            onSetSecret((SetSecretEvent<T>) event);
        } else if (event instanceof SetTopicProtectionEvent) {
            onSetTopicProtection((SetTopicProtectionEvent<T>) event);
        } else if (event instanceof SocketConnectEvent) {
            onSocketConnect((SocketConnectEvent<T>) event);
        } else if (event instanceof SuperOpEvent) {
            onSuperOp((SuperOpEvent<T>) event);
        } else if (event instanceof TimeEvent) {
            onTime((TimeEvent<T>) event);
        } else if (event instanceof TopicEvent) {
            onTopic((TopicEvent<T>) event);
        } else if (event instanceof UnknownEvent) {
            onUnknown((UnknownEvent<T>) event);
        } else if (event instanceof UserListEvent) {
            onUserList((UserListEvent<T>) event);
        } else if (event instanceof UserModeEvent) {
            onUserMode((UserModeEvent<T>) event);
        } else if (event instanceof VersionEvent) {
            onVersion((VersionEvent<T>) event);
        } else if (event instanceof VoiceEvent) {
            onVoice((VoiceEvent<T>) event);
        } else if (event instanceof WhoisEvent) {
            onWhois((WhoisEvent<T>) event);
        }
        //Generic methods
        if (event instanceof GenericCTCPEvent) {
            onGenericCTCP((GenericCTCPEvent<T>) event);
        }
        if (event instanceof GenericUserModeEvent) {
            onGenericUserMode((GenericUserModeEvent<T>) event);
        }
        if (event instanceof GenericChannelModeEvent) {
            onGenericChannelMode((GenericChannelModeEvent<T>) event);
        }
        if (event instanceof GenericDCCEvent) {
            onGenericDCC((GenericDCCEvent<T>) event);
        }
        if (event instanceof GenericMessageEvent) {
            onGenericMessage((GenericMessageEvent<T>) event);
        }
        if (event instanceof GenericUserEvent) {
            onGenericUser((GenericUserEvent<T>) event);
        }
        if (event instanceof GenericChannelEvent) {
            onGenericChannel((GenericChannelEvent<T>) event);
        }
        if (event instanceof GenericChannelUserEvent) {
            onGenericChannelUser((GenericChannelUserEvent<T>) event);
        }
    }

    public void onAction(ActionEvent<T> event) throws Exception {
    }

    public void onChannelInfo(ChannelInfoEvent<T> event) throws Exception {
    }

    public void onConnect(ConnectEvent<T> event) throws Exception {
    }

    public void onDisconnect(DisconnectEvent<T> event) throws Exception {
    }

    public void onFinger(FingerEvent<T> event) throws Exception {
    }

    public void onHalfOp(HalfOpEvent<T> event) throws Exception {
    }

    public void onIncomingChatRequest(IncomingChatRequestEvent<T> event) throws Exception {
    }

    public void onIncomingFileTransfer(IncomingFileTransferEvent<T> event) throws Exception {
    }

    public void onInvite(InviteEvent<T> event) throws Exception {
    }

    public void onJoin(JoinEvent<T> event) throws Exception {
    }

    public void onKick(KickEvent<T> event) throws Exception {
    }

    public void onMessage(MessageEvent<T> event) throws Exception {
    }

    public void onMode(ModeEvent<T> event) throws Exception {
    }

    public void onMotd(MotdEvent<T> event) throws Exception {
    }

    public void onNickAlreadyInUse(NickAlreadyInUseEvent<T> event) throws Exception {
    }

    public void onNickChange(NickChangeEvent<T> event) throws Exception {
    }

    public void onNotice(NoticeEvent<T> event) throws Exception {
    }

    public void onOp(OpEvent<T> event) throws Exception {
    }

    public void onOwner(OwnerEvent<T> event) throws Exception {
    }

    public void onPart(PartEvent<T> event) throws Exception {
    }

    public void onPing(PingEvent<T> event) throws Exception {
    }

    public void onPrivateMessage(PrivateMessageEvent<T> event) throws Exception {
    }

    public void onQuit(QuitEvent<T> event) throws Exception {
    }

    public void onRemoveChannelBan(RemoveChannelBanEvent<T> event) throws Exception {
    }

    public void onRemoveChannelKey(RemoveChannelKeyEvent<T> event) throws Exception {
    }

    public void onRemoveChannelLimit(RemoveChannelLimitEvent<T> event) throws Exception {
    }

    public void onRemoveInviteOnly(RemoveInviteOnlyEvent<T> event) throws Exception {
    }

    public void onRemoveModerated(RemoveModeratedEvent<T> event) throws Exception {
    }

    public void onRemoveNoExternalMessages(RemoveNoExternalMessagesEvent<T> event) throws Exception {
    }

    public void onRemovePrivate(RemovePrivateEvent<T> event) throws Exception {
    }

    public void onRemoveSecret(RemoveSecretEvent<T> event) throws Exception {
    }

    public void onRemoveTopicProtection(RemoveTopicProtectionEvent<T> event) throws Exception {
    }

    public void onServerPing(ServerPingEvent<T> event) throws Exception {
    }

    public void onServerResponse(ServerResponseEvent<T> event) throws Exception {
    }

    public void onSetChannelBan(SetChannelBanEvent<T> event) throws Exception {
    }

    public void onSetChannelKey(SetChannelKeyEvent<T> event) throws Exception {
    }

    public void onSetChannelLimit(SetChannelLimitEvent<T> event) throws Exception {
    }

    public void onSetInviteOnly(SetInviteOnlyEvent<T> event) throws Exception {
    }

    public void onSetModerated(SetModeratedEvent<T> event) throws Exception {
    }

    public void onSetNoExternalMessages(SetNoExternalMessagesEvent<T> event) throws Exception {
    }

    public void onSetPrivate(SetPrivateEvent<T> event) throws Exception {
    }

    public void onSetSecret(SetSecretEvent<T> event) throws Exception {
    }

    public void onSetTopicProtection(SetTopicProtectionEvent<T> event) throws Exception {
    }

    public void onSocketConnect(SocketConnectEvent<T> event) throws Exception {
    }

    public void onSuperOp(SuperOpEvent<T> event) throws Exception {
    }

    public void onTime(TimeEvent<T> event) throws Exception {
    }

    public void onTopic(TopicEvent<T> event) throws Exception {
    }

    public void onUnknown(UnknownEvent<T> event) throws Exception {
    }

    public void onUserList(UserListEvent<T> event) throws Exception {
    }

    public void onUserMode(UserModeEvent<T> event) throws Exception {
    }

    public void onVersion(VersionEvent<T> event) throws Exception {
    }

    public void onVoice(VoiceEvent<T> event) throws Exception {
    }

    public void onWhois(WhoisEvent<T> event) throws Exception {
    }

    public void onGenericCTCP(GenericCTCPEvent<T> event) throws Exception {
    }

    public void onGenericUserMode(GenericUserModeEvent<T> event) throws Exception {
    }

    public void onGenericChannelMode(GenericChannelModeEvent<T> event) throws Exception {
    }

    public void onGenericDCC(GenericDCCEvent<T> event) throws Exception {
    }

    public void onGenericMessage(GenericMessageEvent<T> event) throws Exception {
    }

    public void onGenericChannel(GenericChannelEvent<T> event) throws Exception {
    }

    public void onGenericUser(GenericUserEvent<T> event) throws Exception {
    }

    public void onGenericChannelUser(GenericChannelUserEvent<T> event) throws Exception {
    }
}
