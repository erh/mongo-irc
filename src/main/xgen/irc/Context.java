// Context.java

package xgen.irc;

import xgen.*;
import xgen.util.*;

import com.mongodb.*;

import java.util.*;
import java.util.logging.*;
import java.net.*;

/**
 * one per server
 */
public class Context {

    static Logger log = Logger.getLogger( "xgen.irc" );

    public Context( int port ) 
        throws java.net.UnknownHostException {
        _storage = new Storage( this );
        _serverName = InetAddress.getLocalHost().getHostName();
        _serverIp = InetAddress.getLocalHost().getHostAddress();
        _port = port;

        _bus = _storage.getMessageBus();
        _bus.start( new BusListener( this ) );
        
        _logging = _storage.getLogging();

        _pinger = new Pinger();
        _pinger.start();

    }

    public Storage getStorage() { return _storage; }
    public String getServerName() { return _serverName; }
    public int getServerPort() { return _port; } // XXXX
    public String getServerIpPort() { return _serverIp + "/" + _port; }
    public String getServerIdent() { return _serverName + ":" + _port; }

    public RoomLogging getLogging() { return _logging; }

    public void connectionOpened( Connection c ) {
        synchronized ( _allConnections ) {
            _allConnections.add( c );
            System.out.println( "# connections: " + _allConnections.size() );
        }
    }

    public void connectionClosed( Connection c ){
        synchronized( _allConnections ) {
            _allConnections.remove( c );
            System.out.println( "# connections: " + _allConnections.size() );
        }
    }

    public int getNumberClients() {
        synchronized ( _allConnections ) {
            return _allConnections.size();
        }
    }

    public void sendToRoom( String room , String msg , Connection skip , boolean putOnBus ) {
        for ( Connection c : _getLocalInRoom( room ) ) { 
            if ( c == skip )
                continue;
            c.sendMessage( msg );
        }
        
        if ( putOnBus ) {
            BasicDBObject o = new BasicDBObject();
            o.put( "type" , "room" );
            o.put( "room" , room );
            o.put( "message" , msg );
            _bus.sendMessage( o );
        }

        _logging.log( room , msg );
    }

    public List<Person> getInRoom( String room ) {
        List<Person> in = new LinkedList<Person>();
        synchronized ( _allConnections ) {
            for ( Connection c : _allConnections ) 
                if ( c.inRoom( room ) )
                    in.add( c );
        }
        return in;
    }

    private List<Connection> _getLocalInRoom( String room ) {
        List<Connection> in = new LinkedList<Connection>();
        synchronized ( _allConnections ) {
            for ( Connection c : _allConnections ) 
                if ( c.inRoom( room ) )
                    in.add( c );
        }
        return in;
    }

    
    class Pinger extends Thread {
        Pinger() {
            super( "xgen.irc.Context-PINGER" );
            setDaemon( true );
        }
        
        public void run() {
            final int defaultSleepMillis = 1000;
            int sleepMillis = defaultSleepMillis;
            
            while ( true ) {
                
                if ( _storage.serverAlivePing( getServerIdent() ) ) {
                    sleepMillis = defaultSleepMillis;
                }
                else {
                    sleepMillis = Math.min( 1000 * 30 , sleepMillis * 2 );
                }
                
                ThreadUtil.sleepSafe( sleepMillis );
            }
        }
            
    }

    List<Connection> _allConnections = new LinkedList<Connection>();
    
    final Storage _storage;
    final MessageBus _bus;
    final RoomLogging _logging;

    final Pinger _pinger;

    final String _serverName;
    final String _serverIp;
    final int _port;
}
