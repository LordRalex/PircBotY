package net.ae97.pokebot.api.channels;

import net.ae97.pokebot.api.recipients.ActionRecipient;
import net.ae97.pokebot.api.recipients.MessageRecipient;
import net.ae97.pokebot.api.recipients.ModeRecipient;
import net.ae97.pokebot.api.recipients.Nameable;
import net.ae97.pokebot.api.recipients.NoticeRecipient;
import net.ae97.pokebot.permissions.Permissible;

public abstract class Channel implements
        MessageRecipient,
        NoticeRecipient,
        ModeRecipient,
        Permissible,
        Nameable,
        ActionRecipient {

    public abstract boolean isSecret();

    public abstract String[] getOps();

    public abstract String[] getVoiced();

    public abstract boolean hasOp(String name);

    public abstract boolean hasVoice(String name);

    public abstract String[] getUserList();

    public void kickUser(String name) {
        kickUser(name, null);
    }

    public abstract void kickUser(String name, String reason);

    public abstract void ban(String mask);

    public abstract void unban(String mask);

    public abstract void quiet(String mask);

    public abstract void unquiet(String mask);

    public abstract void opUser(String user);

    public abstract void deopUser(String user);

    public abstract void voiceUser(String user);

    public abstract void devoiceUser(String user);
}
