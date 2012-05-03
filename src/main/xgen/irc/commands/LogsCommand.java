// LogsCommand.java

package xgen.irc.commands;

import xgen.util.*;
import xgen.irc.*;

import java.util.*;

public class LogsCommand extends Command {

    public LogsCommand() {
        super( "LOGS" );
    }
    
    public void handle( Context context , Connection conn , String extra ) {
        extra = extra.trim();
        
        StringUtil.PartitionResult res = StringUtil.partition( extra , " " );
        String room = res.front;

        List<RoomLogging.LogMessage> logs = context.getLogging().getMessages( room , 10 );
        
        Date now = new Date();

        for ( int i=logs.size()-1; i>=0; i-- ) {
            RoomLogging.LogMessage msg = logs.get(i);
            long minutesAgo = ( now.getTime() - msg.ts.getTime() ) / ( 1000 * 60 );
            conn.sendMessage( msg.msg + "\t" + minutesAgo + " minutes ago" );
        }
        
    }
    
}
