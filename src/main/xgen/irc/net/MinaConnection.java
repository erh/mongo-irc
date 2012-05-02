// MinaConnection.java

package xgen.irc.net;

import org.apache.mina.core.session.IoSession;

import xgen.irc.*;

public class MinaConnection extends Connection {
    
    MinaConnection( Context context , IoSession session ) {
        super( context , session.getRemoteAddress() );
        _session = session;
    }
    
    public void sendMessage( String msg ) {
        debugMessage( _session , false , msg );
        _session.write( msg );
    }
    
    final IoSession _session;


    static void debugMessage( IoSession s , boolean clientToServer , String msg ) {
        StringBuilder buf = new StringBuilder();

        if ( clientToServer )
            buf.append( "CLIENT " );
        else
            buf.append( "SERVER " );
        
        buf.append( msg );

        System.out.println( buf.toString() );
    }
}
