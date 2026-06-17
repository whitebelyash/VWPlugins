/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.VimeNetwork.api.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import net.xtrafrancyz.VimeNetwork.api.util.FileUtil;

public class ZipUtil {
    public static void unzip(File zip, File dir) throws IOException {
        byte[] buffer = new byte[1024];
        if (!dir.exists()) {
            dir.mkdir();
        }
        ZipInputStream zis = new ZipInputStream(new FileInputStream(zip));
        ZipEntry ze = zis.getNextEntry();
        while (ze != null) {
            File newFile = new File(dir, ze.getName());
            if (!ze.isDirectory()) {
                int len;
                FileUtil.createFile(newFile);
                FileOutputStream fos = new FileOutputStream(newFile);
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
            }
            ze = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();
    }
}

