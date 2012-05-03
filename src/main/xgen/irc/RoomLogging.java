// RoomLogging.java

package xgen.irc;

import com.mongodb.*;

import java.util.*;
import java.util.regex.*;

public class RoomLogging {

    public static class LogMessage {

        LogMessage( Date t , String r , String m ) {
            ts = t;
            room = r;
            msg = m;
        }
        
        public String toString() {
            return "" + ts + "\t" + room + "\t" + msg;
        }
        
        public final Date ts;
        public final String room;
        public final String msg;
        
    }

    public RoomLogging( DB db ) {
        _db = db;

        _logs = _db.getCollection( "logs" );
        _stats = _db.getCollection( "logstats" );
    }


    public void log( String room , String msg ) {
        _writeToDB( room , msg );
    }

    void _writeToDB( String room , String msg ) {
        BasicDBObject logDoc = new BasicDBObject();
        logDoc.put( "ts" , new Date() );
        logDoc.put( "room" , room );
        logDoc.put( "msg" , msg );

        String key = room + "@@@" + _getBucketNumber( room );

        _logs.update( new BasicDBObject( "_id" , key ) , 
                      new BasicDBObject( "$push" , new BasicDBObject( "logs" , logDoc ) ) , 
                      true , false );
    }

    int _getBucketNumber( String room ) {
        DBObject old = _stats.findAndModify( new BasicDBObject( "_id" , room ) ,
                                             null , null , false , 
                                             new BasicDBObject( "$inc" , new BasicDBObject( "num_messages" , 1 ) ) , 
                                             true , true );
        
        return ((Integer)old.get( "num_messages")) / 10;
    }

    public List<LogMessage> getMessages( String room , int lastNum ) {
        List<LogMessage> lst = new ArrayList<LogMessage>( lastNum );
        
        int left = lastNum;

        try {
            DBCursor cursor = _logs
                .find( new BasicDBObject( "_id" , Pattern.compile( "^" + room + "@@@" ) ) )
                .sort( new BasicDBObject( "_id" , -1 ) )
                .batchSize( 2 + ( lastNum / 100 ) );
            
            while ( left > 0 && cursor.hasNext() ) {
                DBObject o = cursor.next();
                List l = (List)o.get( "logs" );
                
                for ( int i=l.size()-1; i>=0 && left > 0; i-- ) {
                    DBObject foo = (DBObject)l.get(i);
                    lst.add( new LogMessage( (Date)foo.get( "ts" ) , room , foo.get( "msg" ).toString() ) );
                    left--;
                }
            }
        }
        catch ( MongoException me ) {
            me.printStackTrace();
            lst.add( new LogMessage( new java.util.Date() , room , "error getting logs: " + me.toString() ) );
        }
                                          

        return lst;
    }

    final DB _db;

    final DBCollection _logs;
    final DBCollection _stats;

    public static void main( String args[] ) 
        throws Exception {

        Mongo m = new Mongo();
        DB db = m.getDB( "irc" );
        
        RoomLogging log = new RoomLogging( db );
        for ( LogMessage msg : log.getMessages( args[0] , 10 ) ) {
            System.out.println( msg );
        }
        
    }

}
