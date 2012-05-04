// Room.java

package xgen.irc.data;

import java.util.Date;

import com.mongodb.*;
import org.bson.*;

public class Room {
    
    public Room( BSONObject data ) {
        _name = data.get( "_id" ).toString();
        _topic = data.get( "topic" ).toString();
        _created = (Date)data.get( "created" );
        _users = (BSONObject)data.get( "users" );
    }

    public Room( String name ) {
        _name = name;
        _topic = "not set";
        _created = new java.util.Date();
        _users = new BasicDBObject();
    }

    public String getName() { return _name; }
    public String getTopic() { return _topic; }

    public int numUsers() {
        if ( _users == null )
            return 0;
        return _users.keySet().size();
    }

    public DBObject toObject() {
        BasicDBObject o = new BasicDBObject();
        o.put( "_id" , _name );
        o.put( "topic" , _topic );
        o.put( "created" , _created );
        return o;
    }

    final String _name;
    final String _topic;
    final Date _created;
    final BSONObject _users;

    // ------
    
    public static void main( String[] args ) 
        throws Exception {
        
        Room a = new Room( "#x" );
        Room b = new Room( a.toObject() );
        
        System.out.println( a.getName() );
        System.out.println( b.getName() );
        
    }
}
