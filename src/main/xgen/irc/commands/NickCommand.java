// NickCommand.java

package xgen.irc.commands;

import xgen.irc.*;

public class NickCommand extends Command {
    
    public NickCommand() {
        super( "NICK" );
    }

    public void handle( Context context , Connection conn , String rest ) {
        rest = rest.trim();
        conn.setNickName( rest );

        conn.sendResponse( 1 , ":Welcome to xgen irc" );
        conn.sendResponse( 2 , ":Your host is %s[%s], running version xgen-1" , context.getServerName() , context.getServerIpPort() );
        /*
          :holmes.freenode.net 003 erh :This server was created Mon Jan 23 2012 at 22:17:59 GMT
          :holmes.freenode.net 004 erh holmes.freenode.net ircd-seven-1.1.3 DOQRSZaghilopswz CFILMPQbcefgijklmnopqrstvz bkloveqjfI
          :holmes.freenode.net 005 erh CHANTYPES=# EXCEPTS INVEX CHANMODES=eIbq,k,flj,CFLMPQcgimnprstz CHANLIMIT=#:120 PREFIX=(ov)@+ MAXLIST=bqeI:100 MODES=4 NETWORK=freenode KNOCK STATUSMSG=@+ CALLERID=g :are supported by this server
          :holmes.freenode.net 005 erh CASEMAPPING=rfc1459 CHARSET=ascii NICKLEN=16 CHANNELLEN=50 TOPICLEN=390 ETRACE CPRIVMSG CNOTICE DEAF=D MONITOR=100 FNC TARGMAX=NAMES:1,LIST:1,KICK:1,WHOIS:1,PRIVMSG:4,NOTICE:4,ACCEPT:,MONITOR: :are supported by this server
          :holmes.freenode.net 005 erh EXTBAN=$,arx WHOX CLIENTVER=3.0 SAFELIST ELIST=CTU :are supported by this server
          :holmes.freenode.net 251 erh :There are 268 users and 75340 invisible on 28 servers
          :holmes.freenode.net 252 erh 40 :IRC Operators online
          :holmes.freenode.net 253 erh 4 :unknown connection(s)
          :holmes.freenode.net 254 erh 43451 :channels formed
          :holmes.freenode.net 255 erh :I have 3318 clients and 1 servers
          :holmes.freenode.net 265 erh 3318 4373 :Current local users 3318, max 43
          :holmes.freenode.net 266 erh 75608 81443 :Current global users 75608, max 81443
          :holmes.freenode.net 250 erh :Highest connection count: 4374 (4373 clients) (977509 connections received)
        */

        conn.sendResponse( 375 , ":- %s Message of the day" , context.getServerName() );
        conn.sendResponse( 372 , ":- Welcome to the 10gen irc server" );
        conn.sendResponse( 372 , ":- This is very experimental" );

        conn.sendResponse( 376 , ":End of MOTD command." );
    }


}
