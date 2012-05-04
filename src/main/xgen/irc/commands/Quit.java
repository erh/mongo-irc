// Quit.java

package xgen.irc.commands;

import xgen.irc.*;

public class Quit extends Command {
    
    public Quit() {
        super( "QUIT" );
    }

    public void handle( Context context , Connection conn , String rest ) {
        conn.closed();
    }
}
