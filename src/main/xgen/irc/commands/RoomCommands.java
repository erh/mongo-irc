// RoomCommands.java

package xgen.irc.commands;

import java.util.*;

import xgen.irc.*;
import xgen.irc.data.*;

public class RoomCommands {

    public static String getRoom( String thing ) {
        thing = thing.trim();
        if ( thing.charAt(0) != '#' )
            throw new RuntimeException( "not a room: " + thing );
        if ( thing.indexOf( ":" ) > 0 )
            thing = thing.substring( 0 , thing.indexOf( ":" ) );
        return thing.trim().toLowerCase();
    }

    public static class Join extends Command { 
        public Join() {
            super( "JOIN" );
        }
        
        public void handle( Context context , Connection conn , String room ) {
            room = getRoom(room);
            conn.joinedRoom( room );

            Room r = context.getStorage().loadRoom( room );

            // send TOPIC
            conn.sendMessage( ":eliot TOPIC " + room + " :" + r.getTopic() );
            
            // send JOIN
            String msg = ":" + conn.ident() + " JOIN " + room;
            context.sendToRoom( room , msg , null , true );

            // send NAMES
            StringBuilder buf = new StringBuilder();
            buf.append( " @ " ).append( room ).append( " :" );
            for ( Person p : context.getInRoom( room ) ) {
                buf.append( p.nickName() ).append( " " );
            }
            conn.sendResponse( 353 , buf.toString() );
            conn.sendResponse( 366 , room + " :End of /NAMES list." );
        }

    }

    public static class Mode extends Command {
        public Mode() {
            super( "MODE" );
        }

        public void handle( Context context , Connection conn , String room ) {
            room = getRoom( room );
            conn.sendResponse( 324 , room + " +cnst" );
        }        
        
    }
    
    public static class Who extends Command {
        public Who() {
            super( "WHO" );
        }

        public void handle( Context context , Connection conn , String room ) {
            room = getRoom( room );
            for ( Person p : context.getInRoom( room ) ) { 
                //conn.sendResponse( 352 , "room + " " + c.nickName() + " " + c.host() + " thisserver " + c.userName() + " H :0 " );
                conn.sendResponse( 352 , "%s %s %s %s %s H :0" , room , p.nickName() , p.host() , context.getServerName() , p.userName() );
            }
            conn.sendResponse( 315 , room + " End of /WHO list" );
        }
    }
    
    public static List<Command> getAll() {
        List<Command> l = new LinkedList<Command>();
        l.add( new Join() );
        l.add( new Mode() );
        l.add( new Who() );
        return l;
    }



}
