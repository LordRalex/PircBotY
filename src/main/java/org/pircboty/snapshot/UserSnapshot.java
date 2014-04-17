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
package org.pircboty.snapshot;

import org.pircboty.Channel;
import org.pircboty.User;
import org.pircboty.UserChannelDao;

/**
 * A snapshot of a user in time. Useful to get information before a user leaves
 * a channel or server. Any attempts to modify data throw an exception
 *
 * @author
 */
//Only use super implementation which uses UIDs
public class UserSnapshot extends User {

    private final User generatedFrom;
    private UserChannelDaoSnapshot dao;

    public UserSnapshot(User user) {
        super(user.getBot(), null, user.getNick());
        generatedFrom = user;
        //Clone fields
        super.setAwayMessage(user.getAwayMessage());
        super.setHops(user.getHops());
        super.setHostmask(user.getHostmask());
        super.setIrcop(user.isIrcop());
        super.setLogin(user.getLogin());
        super.setRealName(user.getRealName());
        super.setServer(user.getServer());
    }

    @Override
    public UserChannelDao<User, Channel> getDao() {
        //Workaround for generics
        return (UserChannelDao<User, Channel>) (Object) dao;
    }

    @Override
    public UserSnapshot createSnapshot() {
        throw new UnsupportedOperationException("Attempting to generate user snapshot from a snapshot");
    }

    @Override
    public void setAwayMessage(String away) {
        throw new UnsupportedOperationException("Attempting to set field on user snapshot");
    }

    @Override
    public void setHops(int hops) {
        throw new UnsupportedOperationException("Attempting to set field on user snapshot");
    }

    @Override
    public void setHostmask(String hostmask) {
        throw new UnsupportedOperationException("Attempting to set field on user snapshot");
    }

    @Override
    public void setIrcop(boolean ircop) {
        throw new UnsupportedOperationException("Attempting to set field on user snapshot");
    }

    @Override
    public void setLogin(String login) {
        throw new UnsupportedOperationException("Attempting to set field on user snapshot");
    }

    @Override
    public void setNick(String nick) {
        throw new UnsupportedOperationException("Attempting to set field on user snapshot");
    }

    @Override
    public void setRealName(String realName) {
        throw new UnsupportedOperationException("Attempting to set field on user snapshot");
    }

    @Override
    public void setServer(String server) {
        throw new UnsupportedOperationException("Attempting to set field on user snapshot");
    }

    public User getGeneratedFrom() {
        return generatedFrom;
    }

    public void setDao(UserChannelDaoSnapshot dao) {
        this.dao = dao;
    }
}
