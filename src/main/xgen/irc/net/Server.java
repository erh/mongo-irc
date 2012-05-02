// Server.java

package xgen.irc.net;

import java.net.*;
import java.util.logging.*;

import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.apache.mina.filter.codec.ProtocolCodecFilter;

import xgen.irc.*;

public class Server {

    static Logger log = Logger.getLogger( "xgen.irc.net" );

    public Server( Context context, int port ) {
        _context = context;
        _port = port;
    }

    public void start() 
        throws java.io.IOException {

        SocketAcceptor acceptor = new NioSocketAcceptor(1);
        acceptor.setReuseAddress( true );

        acceptor.getFilterChain().addLast( "protocolFilter", new ProtocolCodecFilter( new org.apache.mina.filter.codec.textline.TextLineCodecFactory() ) );

        acceptor.setHandler(new IRCProtocolHandler(_context));
        acceptor.bind(new InetSocketAddress(_port));

        log.info("Listening on port " + _port);
    }

    final Context _context;
    final int _port;
}
