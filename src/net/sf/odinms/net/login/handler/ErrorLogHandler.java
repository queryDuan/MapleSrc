package net.sf.odinms.net.login.handler;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.MaplePacketHandler;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

public class ErrorLogHandler implements MaplePacketHandler {

    @Override
    public boolean validateState(MapleClient c) {
        return !c.isLoggedIn();
    }

    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        String error = slea.readMapleAsciiString();
        try {
            RandomAccessFile file;
            file = new RandomAccessFile("c:\\error.txt", "rw");
            int num = (int) file.length();
            file.seek(num);
            file.writeBytes("\r\n");
            file.write(("错误信息：\r\n").getBytes());
            file.write((error + "\r\n").getBytes());
            file.close();
        } catch (IOException ex) {
            Logger.getLogger(ErrorLogHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}