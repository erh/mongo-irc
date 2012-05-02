// IRCProtocolHandler.java

package xgen.irc.net;

import java.net.*;
import java.util.logging.*;
  
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.filter.codec.ProtocolCodecFilter;

import xgen.irc.*;

public class IRCProtocolHandler extends IoHandlerAdapter {

    static Logger log = Logger.getLogger( "xgen.irc.net" );

    IRCProtocolHandler( Context context ){
        _context = context;
    }
    
    public void sessionCreated(IoSession session) {
        // this should only do network setup
        session.getConfig().setIdleTime(IdleStatus.BOTH_IDLE, 3600 /* seconds */);
    }
    
    public void sessionOpened(IoSession session) throws Exception {
        // actually do system setup here

        log.info("connected from: " + session.getRemoteAddress() );
        Connection c = new MinaConnection( _context , session );
        session.setAttribute( "irc" , c );
        _context.connectionOpened( c );
    }

    public void sessionClosed(IoSession session) throws Exception {
        log.info("socket closed from: " + session.getRemoteAddress() );
        Connection c = (Connection)session.getAttribute( "irc" );
        if ( c != null )
            _context.connectionClosed( c );
    }
    
    public void sessionIdle(IoSession session, IdleStatus status) {
        log.log( Level.FINE , "*** IDLE #" + session.getIdleCount(IdleStatus.BOTH_IDLE) + " " + session.getRemoteAddress() );
    }
    
    public void exceptionCaught(IoSession session, Throwable cause) {
        Connection c = (Connection)session.getAttribute( "irc" );
        
        log.log( Level.SEVERE , "error when dealing with: " + c , cause );

        if ( c != null )
            _context.connectionClosed( c );
        session.close(true);
    }
    
    public void messageReceived(IoSession session, Object message)
        throws Exception {
        
        MinaConnection.debugMessage( session , true , message.toString() );

        Connection c = (Connection)session.getAttribute( "irc" );
        if ( c == null ) {
            log.log( Level.SEVERE , "no connection associated with: " + session.getRemoteAddress() );
            session.close();
            return;
        }
        
        c.receivedMessage( message.toString() );
    }


    final Context _context;
}
