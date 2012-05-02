// CapCommand.java


package xgen.irc.commands;

import xgen.irc.*;
import xgen.util.*;


public class CapCommand extends Command {
    
    public CapCommand() {
        super( "CAP" );
    }

    public void handle( Context context , Connection conn , String line ) {
        StringUtil.PartitionResult res = StringUtil.partition( line , " " );
        
        if ( res.front.equals( "REQ" ) ) {
            conn.sendMessage( "CAP NAK " + res.back.trim() );
        }
        else {
            conn.sendResponse( 410 , ":Unknown CAP sub command" );
        }
    }
}
