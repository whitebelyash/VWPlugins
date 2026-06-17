/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.VimeNetwork.core;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.regex.Pattern;
import net.xtrafrancyz.Core.network.packet.Packet304ConsoleLog;
import net.xtrafrancyz.VimeNetwork.core.CoreBukkitImpl;

public class ConsoleLogHandler
extends Handler {
    private final CoreBukkitImpl core;
    private final LogFormatter formatter;
    boolean active = false;

    public ConsoleLogHandler(CoreBukkitImpl core) {
        this.core = core;
        this.formatter = new LogFormatter();
    }

    @Override
    public void publish(LogRecord record) {
        if (this.active && this.core.isConnected()) {
            this.core.sendPacket(new Packet304ConsoleLog(this.formatter.format(record)));
        }
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() throws SecurityException {
    }

    private static class LogFormatter
    extends Formatter {
        Pattern colorPattern = Pattern.compile("\\x1B\\[([0-9]{1,2}(;[0-9]{1,2})*)?[m|K]");
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

        private LogFormatter() {
        }

        @Override
        public String format(LogRecord lr) {
            String message = this.dateFormat.format(new Date(lr.getMillis())) + " [" + lr.getLevel().getName() + "]";
            Throwable thr = lr.getThrown();
            if (lr.getMessage() != null || thr == null) {
                message = message + " " + this.colorPattern.matcher(this.formatMessage(lr)).replaceAll("");
            }
            if (thr != null) {
                StringWriter sw = new StringWriter();
                sw.write("\n\r");
                PrintWriter pw = new PrintWriter(sw);
                thr.printStackTrace(pw);
                while (thr.getCause() != null) {
                    thr = thr.getCause();
                    thr.printStackTrace(pw);
                }
                message = message + sw.toString();
            }
            return message;
        }
    }
}

