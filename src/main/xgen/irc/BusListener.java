// BusListener.java

package xgen.irc;

import xgen.*;

import com.mongodb.*;

import java.util.logging.*;

public class BusListener implements MessageBus.Listener {
    
    final static Logger LOG = Logger.getLogger( "xgen.irc.BusListener" );

    public BusListener( Context context ) {
        _context = context;
    }

    public void gotMessage( DBObject message ) {

        System.out.println( "bus got message: " + message );

        String type = (String)message.get( "type" );
        
        if ( type == null ) {
            LOG.log( Level.WARNING , "bad message: " + message );
        }
        else if ( type.equals( "room" ) ) {
            _context.sendToRoom( message.get( "room" ).toString() , 
                                 message.get( "message" ).toString() , 
                                 null , false );
        }
        else {
            LOG.log( Level.WARNING , "unknown message type: " + message );
        }

    }

    final Context _context;

}
