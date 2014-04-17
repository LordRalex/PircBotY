package net.ae97.pircboty.dcc;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;
import net.ae97.pircboty.User;

public class SendChat extends Chat {

    public SendChat(User user, Socket socket, Charset encoding) throws IOException {
        super(user, socket, encoding);
    }
}
