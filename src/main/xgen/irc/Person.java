// Person.java

package xgen.irc;

public abstract class Person {

    public abstract String nickName();
    public abstract String userName();
    public abstract String host();

    public String ident() { 
        return nickName() + "!~" + userName() + "@" + host();
    }
}
