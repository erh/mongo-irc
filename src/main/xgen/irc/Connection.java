// Connection.java

package xgen.irc;

import java.net.*;
import java.text.*;
import java.util.*;
import java.util.logging.*;

/**
 * one per socket
 */
public abstract class Connection extends Person {

    static Logger log = Logger.getLogger( "xgen.irc" );

    public Connection( Context context , SocketAddress addr ) {
        _context = context;
        _addr = addr;
    }
    
    // ----- atttribuets ---

    public String nickName() { return _nick; }
    public void setNickName( String nick ) { _nick = nick; }
    
    public String userName() { return _userName; }
    public void setUserName( String name ) { _userName = name; }

    public String host() { return _host; }
    public void setHost( String h ) { _host = h; }

    public void joinedRoom( String room ) {
        synchronized ( _rooms ) {
            _rooms.add( room );
        }
    }

    public boolean inRoom( String name ) {
        synchronized ( _rooms ) {
            return _rooms.contains( name );
        }
    }

    // -------  messaging ----
    
    /**
     * other things call this
     */
    public abstract void sendMessage( String msg );

    public void sendResponse( int code , String msg , Object ... extra ) {
        String x = null;
        synchronized( _responseCodeFormatter ) {
            x = _responseCodeFormatter.format( code );
        }

        String format = ":%s %03d %s " + msg;

        Object[] args = new Object[3+extra.length];
        args[0] = _context.getServerName();
        args[1] = code;
        args[2] = _nick;
        for ( int i=0; i<extra.length; i++ )
            args[3+i] = extra[i];
        sendMessage( String.format( format , args ) );
    }


    /**
     * networking layer calls this
     */
    public void receivedMessage( String msg ) {
        Command.Parsed cmd = Command.parse( msg );
        Command c = Command.getCommand( cmd.command );
        if ( c == null ) {
            log.log( Level.WARNING , "unknown command [" + cmd.command + "] [" + cmd.rest + "]" );
            sendResponse( 421 , cmd.command + " :unknown command" );
            return;
        }
        c.handle( _context , this , cmd.rest );
        //System.out.println( cmd.command + "\t" + c  );
        //_context.dummy( msg , this );
    }
    
    final Context _context;
    final SocketAddress _addr;

    String _nick;
    String _userName;
    String _host;

    Set<String> _rooms = new TreeSet<String>();

    // --- static ---

    final static NumberFormat _responseCodeFormatter;
    static {
        _responseCodeFormatter = NumberFormat.getIntegerInstance();
        _responseCodeFormatter.setMinimumIntegerDigits(3);
    }
}
