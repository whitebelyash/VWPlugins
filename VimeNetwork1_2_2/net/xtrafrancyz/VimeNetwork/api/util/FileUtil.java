/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.VimeNetwork.api.util;

import java.io.File;
import java.io.IOException;

public class FileUtil {
    public static void createFile(File file) throws IOException {
        if (file.exists()) {
            return;
        }
        File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        file.createNewFile();
    }

    public static void delete(File file) {
        if (!file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    FileUtil.delete(f);
                }
            }
            file.delete();
        } else {
            file.delete();
        }
    }
}

