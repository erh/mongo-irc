// MessageBus.java

package xgen;

import com.mongodb.*;

import java.util.logging.*;

public class MessageBus {

    static final Logger LOG = Logger.getLogger( "xgen.MessageBus" );
    static final String COLLECTION_NAME = "messagebus";
    static final String INCARNATION = "" + new java.util.Date().toString() + "-" + Math.random();
    
    public static interface Listener {
        public void gotMessage( DBObject message );
    }

    public MessageBus( DB db ) {
        _db = db;

        if ( ! _db.getCollectionNames().contains( COLLECTION_NAME ) ) {
            BasicDBObject config = new BasicDBObject();
            config.put( "capped" , true );
            config.put( "size" , 100 * 1024 * 1024 );
            _db.createCollection( COLLECTION_NAME , config );
        }

        _bus = _db.getCollection( COLLECTION_NAME );
    }


    /**
     * starts a thread and puts messages onto Listener
     * this will return immediately
     */
    public void start( final Listener theListener ) {
        Thread t = new Thread() {
                public void run() {
                    loop( theListener );
                }
            };
        t.start();
    }

    public void loop( final Listener theListener ) {
        while ( true ) {
            try {
                DBCursor cursor = _bus.find( new BasicDBObject( "_id" , new BasicDBObject( "$gte" , new java.util.Date() ) ) );
                cursor.addOption( Bytes.QUERYOPTION_TAILABLE );
                cursor.addOption( Bytes.QUERYOPTION_AWAITDATA );
                while ( cursor.hasNext() ){
                    DBObject x = cursor.next();
                    if ( INCARNATION.equals( x.get( "incarnation" ) ) )
                        continue;
                    theListener.gotMessage( x );
                }
            }
            catch ( MongoException me ) {
                LOG.log( Level.WARNING , "can't read messagebus" , me );
            }

        }
    }

    public void sendMessage( DBObject o ) {
        o.put( "_id" , new java.util.Date() );
        o.put( "incarnation" , INCARNATION );
        _bus.insert( o );
    }

    final DB _db;
    final DBCollection _bus;

    // ------

    public static void main( String[] args )
        throws Exception {
        
        Mongo m = new Mongo();
        DB db = m.getDB( "mbtest" );
        
        MessageBus bus = new MessageBus( db );
        
        bus.loop( new Listener() { 
                public void gotMessage( DBObject message ) {
                    System.out.println( message );
                }
            } );
    }
    
}
