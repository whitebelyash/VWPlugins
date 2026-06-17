package net.xtrafrancyz.VimeNetwork.api.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipUtil {
   public static void unzip(File zip, File dir) throws IOException {
      byte[] buffer = new byte[1024];
      if (!dir.exists()) {
         dir.mkdir();
      }

      ZipInputStream zis = new ZipInputStream(new FileInputStream(zip));

      for(ZipEntry ze = zis.getNextEntry(); ze != null; ze = zis.getNextEntry()) {
         File newFile = new File(dir, ze.getName());
         if (!ze.isDirectory()) {
            FileUtil.createFile(newFile);
            FileOutputStream fos = new FileOutputStream(newFile);

            int len;
            while((len = zis.read(buffer)) > 0) {
               fos.write(buffer, 0, len);
            }

            fos.close();
         }
      }

      zis.closeEntry();
      zis.close();
   }
}
