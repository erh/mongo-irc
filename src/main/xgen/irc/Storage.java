// Storage.java


package xgen.irc;

import xgen.*;
import xgen.irc.data.*;

import com.mongodb.*;
import org.bson.*;

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

    final Context _context;

    final Mongo _mongo;
    final DB _db;

    final DBCollection _rooms;
    final DBCollection _servers;

    final MessageBus _bus;
    final RoomLogging _logging;
}
