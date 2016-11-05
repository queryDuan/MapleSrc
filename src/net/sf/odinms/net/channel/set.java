 package net.sf.odinms.net.channel;

 import java.io.FileReader;
 import java.io.InputStreamReader;
 import java.util.Properties;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;

 public class set
 {
   private static set instance = null;
   private static boolean CANLOG;
   private Properties itempb_cfg = new Properties();
   private String[] itempb_id;
   private static Logger log = LoggerFactory.getLogger(set.class);

   public set() {
     try {
       InputStreamReader is = new FileReader("其他设置.properties");
       this.itempb_cfg.load(is);
       is.close();
       this.itempb_id = this.itempb_cfg.getProperty("cashban").split(",");
     }
     catch (Exception e) {
       log.error("Could not configuration", e);
     }
   }

   public String[] getItempb_id()
   {
     return this.itempb_id;
   }

   public boolean isCANLOG()
   {
     return CANLOG;
   }

   public void setCANLOG(boolean CANLOG) {
     CANLOG = CANLOG;
   }

   public static set getInstance()
   {
     if (instance == null) {
       instance = new set();
     }
     return instance;
   }
 }

