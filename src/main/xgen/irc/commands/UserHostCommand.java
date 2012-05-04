// UserCommand.java

package xgen.irc.commands;

import xgen.irc.*;

public class UserHostCommand extends Command {
    
    public UserHostCommand() {
        super( "USERHOST" );
    }

    public void handle( Context context , Connection conn , String line ) {
        String[] nicknames = line.trim().split( "\\s+" );
        // for each nickname, if found in room, return ident
        StringBuilder sb = new StringBuilder();
        for(String nickname : nicknames) {
        	if(conn.inRoom(nickname)) {
        		sb.append(nickname);
        		sb.append(" is in the room\n");
        	}
        }
        conn.sendMessage(sb.toString());
    }
}
