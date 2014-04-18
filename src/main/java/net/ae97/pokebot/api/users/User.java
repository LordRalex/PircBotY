package net.ae97.pokebot.api.users;

import net.ae97.pokebot.api.recipients.Hostable;
import net.ae97.pokebot.api.recipients.MessageRecipient;
import net.ae97.pokebot.api.recipients.ModeRecipient;
import net.ae97.pokebot.api.recipients.Nameable;
import net.ae97.pokebot.api.recipients.Nickable;
import net.ae97.pokebot.api.recipients.NickservRecipient;
import net.ae97.pokebot.api.recipients.NoticeRecipient;
import net.ae97.pokebot.permissions.Permissible;

public abstract class User implements
        MessageRecipient,
        NoticeRecipient,
        ModeRecipient,
        Permissible,
        Nameable,
        Nickable,
        Hostable,
        NickservRecipient {

    public abstract String[] getChannels();
}
