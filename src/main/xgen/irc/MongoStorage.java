// MongoStorage.java

package xgen.irc;

import xgen.*;
import xgen.irc.data.*;

import com.mongodb.*;
import org.bson.*;

public class MongoStorage implements Storage {

    public MongoStorage() 
        throws java.net.UnknownHostException {
        _mongo = new Mongo();
        _db = _mongo.getDB( "irc" );

        _rooms = _db.getCollection( "rooms" );

        _bus = new MessageBus( _db );
    }

    public MessageBus getMessageBus() { return _bus; }
    
    public Room loadRoom( String name ) {
        DBObject o =  _rooms.findOne( new BasicDBObject( "_id" , name ) );
        if ( o != null ) 
            return new Room( o );
        
        Room r = new Room( name );
        _rooms.insert( r.toObject() );
        
        return r;
    }


    final Mongo _mongo;
    final DB _db;

    final DBCollection _rooms;
    final MessageBus _bus;
}
