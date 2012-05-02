// PrivMsg.java

package xgen.irc.commands;

import xgen.irc.*;

public class PrivMsg extends Command {
    
    public PrivMsg() {
        super( "PRIVMSG" );
    }

    public void handle( Context context , Connection conn , String line ) {
        int idx = line.indexOf( " :" );
        if ( idx < 0 )
            throw new IllegalArgumentException( "bad msg [" + line + "]" );
        String to = line.substring( 0 , idx ).trim();
        String msg = line.substring( idx + 2 ).trim();

        System.out.println( "to: " + to );
        System.out.println( "msg: " + msg );
        
        if ( to.charAt(0) == '#' ) {
            // room
            String response = ":" + conn.ident() + " PRIVMSG " + to + " :" + msg;
            context.sendToRoom( to , response , conn , true );
        }
        else {
            System.err.println( "can't handle direct messages" );
        }
    }


}
