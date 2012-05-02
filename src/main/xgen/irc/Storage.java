// Storage.java

package xgen.irc;

import xgen.*;
import xgen.irc.data.*;

public interface Storage {
    public Room loadRoom( String name );
    public MessageBus getMessageBus();
}
