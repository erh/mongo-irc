// RoomLogging.java

package xgen.irc;

import xgen.util.*;

import com.mongodb.*;

import java.util.*;
import java.util.regex.*;
import java.util.logging.*;
import java.util.concurrent.*;

public class RoomLogging {

    public static Logger LOG = Logger.getLogger( "xgen.irc.RoomLogging" );

    public static class LogMessage {

        LogMessage( Date t , String r , String m ) {
            ts = t;
            room = r;
            msg = m;
        }
        
        LogMessage( DBObject o ) {
            ts = (Date)o.get( "ts" );
            room = o.get( "room" ).toString();
            msg = o.get( "msg" ).toString();
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

        _queue = new LinkedBlockingQueue<LogMessage>( 100 * 1000 );
        _inserter = new LogInserter();
        _inserter.start();
        
    }


    public void log( String room , String msg ) {
        if ( _queue.offer( new LogMessage( new Date() , room , msg ) ) )
            return;

        LOG.log( Level.WARNING , "log queue full" );
    }

    void _writeToDB( LogMessage msg ) {
        BasicDBObject logDoc = new BasicDBObject();
        logDoc.put( "ts" , msg.ts );
        logDoc.put( "room" , msg.room );
        logDoc.put( "msg" , msg.msg );

        String key = msg.room + "@@@" + _getBucketNumber( msg.room );

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
                    lst.add( new LogMessage( foo ) );
                    left--;
                }
            }
        }
        catch ( MongoException me ) {
            LOG.log( Level.WARNING , "error getting log" , me );
            lst.add( new LogMessage( new java.util.Date() , room , "error getting logs: " + me.toString() ) );
        }
                                          

        return lst;
    }

    public List<LogMessage> search( String query ) {
        String[] pcs = query.split( " +" );

        List<LogMessage> lst = new ArrayList<LogMessage>();
        
        try {

            DBObject cmd = new BasicDBObject();
            cmd.put( "fts" , _logs.getName() );
            cmd.put( "search" , query );
            
            DBObject res = _db.command( cmd );
            List results = (List)res.get( "results" );
            if ( results == null ) {
                LOG.log( Level.WARNING , "can't do fts query: " + res );
                return lst;
            }
            for ( Object raw : results ) {
                DBObject scored = (DBObject)raw;
                DBObject bucket = (DBObject)scored.get( "obj" );
                
                List messages = (List)bucket.get( "logs" );
                
                for ( Object x : messages ) {
                    DBObject msg = (DBObject)x;
                    String txt = msg.get( "msg" ).toString();

                    int idx = txt.indexOf( "PRIVMSG" );
                    if ( idx < 0 )
                        continue;

                    txt = txt.substring( idx + 7 );
                    txt = txt.substring( txt.indexOf( ":" ) + 1 ).trim();
                    
                    boolean good = false;
                    
                    for ( int i=0; i<pcs.length; i++ ) {
                        if ( txt.indexOf( pcs[i] ) >= 0 ) {
                            good = true;
                            break;
                        }
                    }

                    if ( good ) 
                        lst.add( new LogMessage( msg ) ); 
                }

            }
            
        }
        catch ( MongoException me ) {
            LOG.log( Level.WARNING , "error doing search" , me );
        }
        
        return lst;
    }

    class LogInserter extends Thread {

        public void run() {
            while ( true ) {
                
                LogMessage msg = null;
                
                try {
                    msg = _queue.take();
                }
                catch ( InterruptedException ie ) {
                    ThreadUtil.sleepSafe( 10 );
                }

                if ( msg == null )
                    continue;

                try {
                    _writeToDB( msg );
                }
                catch ( MongoException me ) {
                    LOG.log( Level.WARNING , "can't insert log message to db" , me );
                }
            }
        }
        
    }

    final DB _db;

    final DBCollection _logs;
    final DBCollection _stats;
    
    final BlockingQueue<LogMessage> _queue;
    final LogInserter _inserter;

    // ------------

    public static void main( String args[] ) 
        throws Exception {

        Mongo m = new Mongo();
        DB db = m.getDB( "irc" );

        RoomLogging log = new RoomLogging( db );        

        if ( args.length > 0 ) {
            for ( int i=0; i<Integer.parseInt(args[0]); i++ ) {
                String room = "#dummy" + (int)(100*Math.random());
                System.out.println( room );
                log.log( room , "this is a dummy message: " + Math.random() );
                if ( i % 10 == 0 ) 
                    log._db.getLastError( 2 , 0 , false );
            }
        }
        else {
            System.out.println( "----" );
            
            for ( LogMessage msg : log.getMessages( args[0] , 10 ) ) {
                System.out.println( msg );
            }
            
            System.out.println( "----" );
            
            for ( LogMessage msg : log.search( args[1] ) ) {
                System.out.println( msg );
            }
        }

        
    }

}
