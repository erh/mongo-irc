// Command.java

package xgen.irc;

import java.util.*;

import xgen.irc.commands.*;

// https://www.alien.net.au/irc/irc2numerics.html
// http://en.wikipedia.org/wiki/List_of_Internet_Relay_Chat_commands

public abstract class Command {

    public static class Parsed {
        Parsed( String command , String rest ) {
            this.command = command;
            this.rest = rest;
        }
        
        public String toString() {
            return command + " " + rest;
        }
        
        public final String command;
        public final String rest;
    }
    
    public static Parsed parse( String line ) {
        line = line.trim();
        String command = line;
        String rest = "";
        { 
            int idx = command.indexOf( " " );
            if ( idx > 0 ) {
                rest = command.substring( idx + 1 ).trim();
                command = command.substring( 0 , idx ).trim();
            }
        }
        
        return new Parsed( command , rest );
                
    }

    
    // ---

    public Command( String name ) {
        _name = name.toUpperCase();
    }

    public String getName() { return _name; }
    
    public abstract void handle( Context context , Connection user , String line );

    final String _name;

    // -------

    public static Command getCommand( String name ) {
        return _commands.get( name.toUpperCase() );
    }
    
    static void _add( Map<String,Command> m , Command c ) {
        m.put( c.getName() , c );
    }

    static void _add( Map<String,Command> m , List<Command> cs ) {
        for ( Command c : cs ) 
            _add( m , c );
    }
    
    private static Map<String,Command> _commands;
    static {
        Map<String,Command> m = new HashMap<String,Command>();
        _add( m , new NickCommand() );
        _add( m , new UserCommand() );
        _add( m , new PrivMsg() );
        _add( m , new CapCommand() );
        _add( m , new Away() );
        _add( m , new Ping() );
        _add( m , new LogsCommand() );
        _add( m , new Quit() );
        _add( m , RoomCommands.getAll() );
        _commands = Collections.unmodifiableMap( m );
    }
    
}
