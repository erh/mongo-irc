// StringUtil.java

package xgen.util;

public class StringUtil {
    
    public static class PartitionResult {
        PartitionResult( String f , String b ) {
            front = f;
            back = b;
        }
        public final String front;
        public final String back;
    }

    public static PartitionResult partition( String s , String on ) {
        int idx = s.indexOf( on );
        if ( idx < 0 ) 
            return new PartitionResult( s , "" );
        
        return new PartitionResult( s.substring( 0 , idx ) , s.substring( idx + 1 ) );
    }
}
