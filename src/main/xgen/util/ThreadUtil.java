// ThreadUtil.java

package xgen.util;

public class ThreadUtil {
    
    public static boolean sleepSafe( int millis ) {
        try {
            Thread.sleep( millis );
            return true;
        }
        catch ( InterruptedException ie ) {
            return false;
        }
    }
    
}
