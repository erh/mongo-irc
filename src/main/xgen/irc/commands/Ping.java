// Ping.java

package xgen.irc.commands;

import xgen.irc.*;

public class Ping extends Command {
    
    public Ping() {
        super( "PING" );
    }

    public void handle( Context context , Connection conn , String rest ) {
        conn.sendMessage( "PONG " + rest );
    }
}
