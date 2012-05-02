// Away.java

package xgen.irc.commands;

import xgen.irc.*;

public class Away extends Command {
    
    public Away() {
        super( "AWAY" );
    }

    public void handle( Context context , Connection conn , String rest ) {

        System.out.println( "should do something with AWAY: " + rest );
    }
}
