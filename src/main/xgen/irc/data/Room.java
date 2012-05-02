// Room.java

package xgen.irc.data;

import com.mongodb.*;
import org.bson.*;

public class Room {
    
    public Room( BSONObject data ) {
        _name = data.get( "_id" ).toString();
        _topic = data.get( "topic" ).toString();
        _created = (java.util.Date)data.get( "created" );
    }

    public Room( String name ) {
        _name = name;
        _topic = "not set";
        _created = new java.util.Date();
    }

    public String getName() { return _name; }
    public String getTopic() { return _topic; }

    public DBObject toObject() {
        BasicDBObject o = new BasicDBObject();
        o.put( "_id" , _name );
        o.put( "topic" , _topic );
        o.put( "created" , _created );
        return o;
    }

    final String _name;
    final String _topic;
    final java.util.Date _created;
    // ------
    
    public static void main( String[] args ) 
        throws Exception {
        
        Room a = new Room( "#x" );
        Room b = new Room( a.toObject() );
        
        System.out.println( a.getName() );
        System.out.println( b.getName() );
        
    }
}
