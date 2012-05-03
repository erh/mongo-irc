// IRC.java

package xgen.irc;

import java.io.*;
import java.net.*;
import java.util.logging.*;

import xgen.*;
import xgen.irc.net.*;

public class IRC {

    static Logger log = Logger.getLogger( "xgen.irc" );

    public static void main( String[] args ) 
        throws Exception {
        
        int port = 6667;

        for ( int i=0; i<args.length; i++ ) {

            if ( args[i].equals( "--port" ) ) {
                port = Integer.parseInt( args[++i] );
                continue;
            }

            System.err.println( "unknown option: " + args[i] );
            return;
        }

        Logger root = log.getParent();
        while ( root.getHandlers().length > 0 )
            root.removeHandler( root.getHandlers()[0] );
        
        ConsoleHandler ch = new ConsoleHandler();
        ch.setFormatter( new xgen.LogFormat() );
        root.addHandler( ch );
        ch.setLevel( Level.ALL );
        
        root.setLevel( Level.ALL );
        log.setLevel( Level.ALL );

        Context context = new Context( port );
        
        Server server = new Server( context , port );
        server.start();

        while ( true ) {
            log.info( "number of clients: " + context.getNumberClients() );
            Thread.sleep( 10 * 1000 );
        }

    }

}
