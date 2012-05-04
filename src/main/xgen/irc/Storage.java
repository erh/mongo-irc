// Storage.java


package xgen.irc;

import xgen.*;
import xgen.irc.data.*;

import com.mongodb.*;
import org.bson.*;

import java.util.*;
import java.util.logging.*;

public class Storage {

    static final Logger LOG = Logger.getLogger( "xgen.irc.Storage" );

    public Storage( Context context ) 
        throws java.net.UnknownHostException {

        _context = context;
        
        _mongo = new Mongo();
        _db = _mongo.getDB( "irc" );

        _rooms = _db.getCollection( "rooms" );
        _servers = _db.getCollection( "servers" );

        _bus = new MessageBus( _db );
        _logging = new RoomLogging( _db );
    }

    public MessageBus getMessageBus() { return _bus; }
    public RoomLogging getLogging() { return _logging; }
    
    public Room loadRoom( String name ) {
        try {
            DBObject o =  _rooms.findOne( new BasicDBObject( "_id" , name ) );
            if ( o != null ) 
                return new Room( o );
            
            Room r = new Room( name );
            _rooms.insert( r.toObject() );
            
            return r;
        }
        catch ( MongoException me ) {
            LOG.log( Level.SEVERE , "couldn't load room" , me );
            return new Room( name );
        }
    }

    public boolean serverAlivePing( String ident ) {
        try {
            _servers.update( new BasicDBObject( "_id" , ident ) , 
                             new BasicDBObject( "$set" , new BasicDBObject( "last_ping" , new java.util.Date() ) ) ,
                             true , false );
            return true;
        }
        catch ( MongoException me ) {
            LOG.log( Level.WARNING , "couldn't ping server" , me );
            return false;
        }
    }

    public void roomAdd( String room , String nick ) {
        loadRoom( room );
        _rooms.update( new BasicDBObject( "_id" , room ) , 
                       new BasicDBObject( "$set" , 
                                          new BasicDBObject( "users." + nick , _context.getServerIdent() ) ) );
    }
    
    public void roomRemove( String room , String nick ) {
        _rooms.update( new BasicDBObject( "_id" , room ) , 
                       new BasicDBObject( "$unset" , 
                                          new BasicDBObject( "users." + nick , 1 ) ) );

    }

    public List<Person> getOthersInRoom( String room ) {
        List<Person> lst = new LinkedList<Person>();
        
        try {
            DBObject o = _rooms.findOne( new BasicDBObject( "_id" , room ) );
            if ( o == null )
                return lst;
            o = (DBObject)o.get( "users" );
            if ( o == null )
                return lst;
            
            for ( String key : o.keySet() ) {
                if ( o.get( key ).equals( _context.getServerIdent() ) )
                    continue;
                lst.add( new MyPerson( key , key , o.get( key ).toString() ) );
            }
        }
        catch ( MongoException me ) {
            LOG.log( Level.WARNING , "couldn't load people in room" , me );
        }
        return lst;
            
    }

    static class MyPerson extends Person {
        public MyPerson( String n , String u , String h ) {
            nick = n;
            user = u;
            host = h;
        }
        
        public String nickName() { return nick; }
        public String userName() { return user; }
        public String host() { return host; }


        final String nick;
        final String user;
        final String host;
    }

    final Context _context;

    final Mongo _mongo;
    final DB _db;

    final DBCollection _rooms;
    final DBCollection _servers;

    final MessageBus _bus;
    final RoomLogging _logging;
}
