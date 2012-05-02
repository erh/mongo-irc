// LogFormat.java

package xgen;

import java.io.*;
import java.util.Date;
import java.util.logging.*;
import java.text.*;

public class LogFormat extends Formatter {

    private Date date = new java.util.Date();
    private SimpleDateFormat format = new SimpleDateFormat( "MMM-dd HH:mm:ss.S" );

    public String format(LogRecord record) {
        StringBuffer buf = new StringBuffer();
        
        synchronized ( date ) {
            date.setTime( record.getMillis() );
            buf.append( format.format( date ) ).append( " " );
        }

        buf.append( record.getLoggerName() ).append( " " );
        buf.append( record.getLevel().getLocalizedName() ).append( " " );
        
        buf.append( "[" ).append( Thread.currentThread().getName() ).append( "] " );

        buf.append( formatMessage( record ) );

        buf.append( "\n" );


        if (record.getThrown() != null) {
            try {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                record.getThrown().printStackTrace(pw);
                pw.close();
                buf.append(sw.toString());
            } 
            catch (Exception ex) {
            }
        }

        return buf.toString();
    }

    
}
