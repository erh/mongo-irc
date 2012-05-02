// Context.java

package xgen.irc;

import xgen.*;

import com.mongodb.*;

import java.util.*;
import java.util.logging.*;
import java.net.*;

/**
 * one per server
 */
public class Context {

    static Logger log = Logger.getLogger( "xgen.irc" );

    public Context() 
        throws java.net.UnknownHostException {
        _storage = new MongoStorage();
        _serverName = InetAddress.getLocalHost().getHostName();

        _bus = _storage.getMessageBus();
        _bus.start( new BusListener( this ) );
    }

    public Storage getStorage() { return _storage; }
    public String getServerName() { return _serverName; }

    public String getServerIpPort() { 
        return "127.0.0.1/6667"; // XXX
    }

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

    
    List<Connection> _allConnections = new LinkedList<Connection>();
    Storage _storage;
    MessageBus _bus;
    String _serverName;
}
