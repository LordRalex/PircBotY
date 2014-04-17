package net.ae97.pircboty.hooks;

import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.hooks.events.ActionEvent;
import net.ae97.pircboty.hooks.events.ChannelInfoEvent;
import net.ae97.pircboty.hooks.events.ConnectEvent;
import net.ae97.pircboty.hooks.events.DisconnectEvent;
import net.ae97.pircboty.hooks.events.FingerEvent;
import net.ae97.pircboty.hooks.events.HalfOpEvent;
import net.ae97.pircboty.hooks.events.IncomingChatRequestEvent;
import net.ae97.pircboty.hooks.events.IncomingFileTransferEvent;
import net.ae97.pircboty.hooks.events.InviteEvent;
import net.ae97.pircboty.hooks.events.JoinEvent;
import net.ae97.pircboty.hooks.events.KickEvent;
import net.ae97.pircboty.hooks.events.MessageEvent;
import net.ae97.pircboty.hooks.events.ModeEvent;
import net.ae97.pircboty.hooks.events.MotdEvent;
import net.ae97.pircboty.hooks.events.NickAlreadyInUseEvent;
import net.ae97.pircboty.hooks.events.NickChangeEvent;
import net.ae97.pircboty.hooks.events.NoticeEvent;
import net.ae97.pircboty.hooks.events.OpEvent;
import net.ae97.pircboty.hooks.events.OwnerEvent;
import net.ae97.pircboty.hooks.events.PartEvent;
import net.ae97.pircboty.hooks.events.PingEvent;
import net.ae97.pircboty.hooks.events.PrivateMessageEvent;
import net.ae97.pircboty.hooks.events.QuitEvent;
import net.ae97.pircboty.hooks.events.RemoveChannelBanEvent;
import net.ae97.pircboty.hooks.events.RemoveChannelKeyEvent;
import net.ae97.pircboty.hooks.events.RemoveChannelLimitEvent;
import net.ae97.pircboty.hooks.events.RemoveInviteOnlyEvent;
import net.ae97.pircboty.hooks.events.RemoveModeratedEvent;
import net.ae97.pircboty.hooks.events.RemoveNoExternalMessagesEvent;
import net.ae97.pircboty.hooks.events.RemovePrivateEvent;
import net.ae97.pircboty.hooks.events.RemoveSecretEvent;
import net.ae97.pircboty.hooks.events.RemoveTopicProtectionEvent;
import net.ae97.pircboty.hooks.events.ServerPingEvent;
import net.ae97.pircboty.hooks.events.ServerResponseEvent;
import net.ae97.pircboty.hooks.events.SetChannelBanEvent;
import net.ae97.pircboty.hooks.events.SetChannelKeyEvent;
import net.ae97.pircboty.hooks.events.SetChannelLimitEvent;
import net.ae97.pircboty.hooks.events.SetInviteOnlyEvent;
import net.ae97.pircboty.hooks.events.SetModeratedEvent;
import net.ae97.pircboty.hooks.events.SetNoExternalMessagesEvent;
import net.ae97.pircboty.hooks.events.SetPrivateEvent;
import net.ae97.pircboty.hooks.events.SetSecretEvent;
import net.ae97.pircboty.hooks.events.SetTopicProtectionEvent;
import net.ae97.pircboty.hooks.events.SocketConnectEvent;
import net.ae97.pircboty.hooks.events.SuperOpEvent;
import net.ae97.pircboty.hooks.events.TimeEvent;
import net.ae97.pircboty.hooks.events.TopicEvent;
import net.ae97.pircboty.hooks.events.UnknownEvent;
import net.ae97.pircboty.hooks.events.UserListEvent;
import net.ae97.pircboty.hooks.events.UserModeEvent;
import net.ae97.pircboty.hooks.events.VersionEvent;
import net.ae97.pircboty.hooks.events.VoiceEvent;
import net.ae97.pircboty.hooks.events.WhoisEvent;
import net.ae97.pircboty.hooks.types.GenericCTCPEvent;
import net.ae97.pircboty.hooks.types.GenericChannelEvent;
import net.ae97.pircboty.hooks.types.GenericChannelModeEvent;
import net.ae97.pircboty.hooks.types.GenericChannelUserEvent;
import net.ae97.pircboty.hooks.types.GenericDCCEvent;
import net.ae97.pircboty.hooks.types.GenericMessageEvent;
import net.ae97.pircboty.hooks.types.GenericUserEvent;
import net.ae97.pircboty.hooks.types.GenericUserModeEvent;

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
