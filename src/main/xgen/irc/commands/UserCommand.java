// UserCommand.java

package xgen.irc.commands;

import xgen.irc.*;

public class UserCommand extends Command {
    
    public UserCommand() {
        super( "USER" );
    }

    public void handle( Context context , Connection conn , String line ) {
        String[] pcs = line.trim().split( "\\s+" );
        conn.setUserName( pcs[0] );
        conn.setHost( pcs[1] );
        // TODO: use other fields
    }


}
