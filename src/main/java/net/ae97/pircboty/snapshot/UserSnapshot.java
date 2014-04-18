package net.ae97.pircboty.snapshot;

import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.User;
import net.ae97.pircboty.UserChannelDao;

public class UserSnapshot extends User {

    private final User generatedFrom;
    private UserChannelDao<? extends PircBotY, UserSnapshot, ChannelSnapshot> dao;

    public UserSnapshot(User user) {
        super(user.getBot(), null, user.getNick());
        generatedFrom = user;
        super.setAwayMessage(user.getAwayMessage());
        super.setHops(user.getHops());
        super.setHostmask(user.getHostmask());
        super.setIrcop(user.isIrcop());
        super.setLogin(user.getLogin());
        super.setRealName(user.getRealName());
        super.setServer(user.getServer());
    }

    @Override
    public UserChannelDao<? extends PircBotY, UserSnapshot, ChannelSnapshot> getDao() {
        return dao;
    }

    @Override
    public UserSnapshot createSnapshot() {
        throw new UnsupportedOperationException("Attempting to generate user snapshot from a snapshot");
    }

    @Override
    protected void setAwayMessage(String away) {
        throw new UnsupportedOperationException("Attempting to set field on user snapshot");
    }

    @Override
    protected void setHops(int hops) {
        throw new UnsupportedOperationException("Attempting to set field on user snapshot");
    }

    @Override
    protected void setHostmask(String hostmask) {
        throw new UnsupportedOperationException("Attempting to set field on user snapshot");
    }

    @Override
    protected void setIrcop(boolean ircop) {
        throw new UnsupportedOperationException("Attempting to set field on user snapshot");
    }

    @Override
    protected void setLogin(String login) {
        throw new UnsupportedOperationException("Attempting to set field on user snapshot");
    }

    @Override
    protected void setNick(String nick) {
        throw new UnsupportedOperationException("Attempting to set field on user snapshot");
    }

    @Override
    protected void setRealName(String realName) {
        throw new UnsupportedOperationException("Attempting to set field on user snapshot");
    }

    @Override
    protected void setServer(String server) {
        throw new UnsupportedOperationException("Attempting to set field on user snapshot");
    }

    public User getGeneratedFrom() {
        return generatedFrom;
    }

    public void setDao(UserChannelDao<? extends PircBotY, UserSnapshot, ChannelSnapshot> dao) {
        this.dao = dao;
    }
}
