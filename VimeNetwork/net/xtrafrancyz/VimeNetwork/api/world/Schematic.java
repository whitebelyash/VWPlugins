package net.xtrafrancyz.VimeNetwork.api.world;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import net.minecraft.server.v1_6_R3.NBTCompressedStreamTools;
import net.minecraft.server.v1_6_R3.NBTTagCompound;
import net.minecraft.server.v1_6_R3.NBTTagList;
import net.minecraft.server.v1_6_R3.TileEntity;
import net.minecraft.server.v1_6_R3.WorldServer;
import net.xtrafrancyz.VimeNetwork.api.geom.Vec3i;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_6_R3.CraftWorld;

public class Schematic {
   protected int width;
   protected int height;
   protected int length;
   protected short[] blocks;
   protected byte[] data;
   protected NBTTagList tiles;
   protected NBTTagList entities;

   Schematic(NBTTagCompound tag) {
      this.width = tag.getShort("Width");
      this.height = tag.getShort("Height");
      this.length = tag.getShort("Length");
      this.data = tag.getByteArray("Data");
      this.tiles = tag.getList("TileEntities");
      this.readBlocks(tag);
   }

   private void readBlocks(NBTTagCompound tag) {
      byte[] ids = tag.getByteArray("Blocks");
      byte[] addId = new byte[0];
      this.blocks = new short[ids.length];
      if (tag.hasKey("AddBlocks")) {
         addId = tag.getByteArray("AddBlocks");
      }

      for(int i = 0; i < ids.length; ++i) {
         if (i >> 1 >= addId.length) {
            this.blocks[i] = (short)(ids[i] & 255);
         } else if ((i & 1) == 0) {
            this.blocks[i] = (short)(((addId[i >> 1] & 15) << 8) + (ids[i] & 255));
         } else {
            this.blocks[i] = (short)(((addId[i >> 1] & 240) << 4) + (ids[i] & 255));
         }
      }

   }

   public int index(int x, int y, int z) {
      return y * this.width * this.length + z * this.width + x;
   }

   public int getId(int index) {
      return this.blocks[index];
   }

   public int getId(int x, int y, int z) {
      return this.blocks[this.index(x, y, z)];
   }

   public short[] getRawBlocks() {
      return this.blocks;
   }

   public byte getData(int index) {
      return this.data[index];
   }

   public byte getData(int x, int y, int z) {
      return this.data[this.index(x, y, z)];
   }

   public byte[] getRawData() {
      return this.data;
   }

   public NBTTagList getTiles() {
      return this.tiles;
   }

   public NBTTagList getEntities() {
      return this.entities;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public int getLength() {
      return this.length;
   }

   public Vec3i getSize() {
      return new Vec3i(this.width, this.height, this.length);
   }

   public void paste(World world, Vec3i base) {
      this.paste(world, base, true, true);
   }

   public void paste(World world, Vec3i base, boolean fast) {
      this.paste(world, base, fast, true);
   }

   public void paste(World world, Vec3i base, boolean fast, boolean pasteTiles) {
      int x0 = base.x;
      int y0 = base.y;
      int z0 = base.z;
      int w = this.width;
      int h = this.height;
      int l = this.length;
      short[] b = this.blocks;
      byte[] d = this.data;

      for(int x = 0; x < w; ++x) {
         for(int y = 0; y < h; ++y) {
            for(int z = 0; z < l; ++z) {
               int index = y * w * l + z * w + x;
               world.getBlockAt(x0 + x, y0 + y, z0 + z).setTypeIdAndData(b[index], d[index], !fast);
            }
         }
      }

      if (pasteTiles) {
         WorldServer handle = ((CraftWorld)world).getHandle();
         NBTTagList tiles = (NBTTagList)this.tiles.clone();

         for(int i = 0; i < tiles.size(); ++i) {
            NBTTagCompound tag = (NBTTagCompound)tiles.get(i);
            int x = x0 + tag.getInt("x");
            int y = y0 + tag.getInt("y");
            int z = z0 + tag.getInt("z");
            tag.setInt("x", x);
            tag.setInt("y", y);
            tag.setInt("z", z);
            TileEntity tile = handle.getTileEntity(x, y, z);
            if (tile != null) {
               tile.a(tag);
            } else {
               System.out.println("Tile at " + new Vec3i(x, y, z) + " is null");
            }
         }
      }

   }

   public static Schematic load(File file) throws IOException {
      FileInputStream fileInputStream = new FileInputStream(file);
      Throwable var3 = null;

      Schematic schem;
      try {
         NBTTagCompound tag = NBTCompressedStreamTools.a(fileInputStream);
         if (tag.hasKey("WEOffsetX")) {
            schem = new WESchemetic(tag);
         } else {
            schem = new Schematic(tag);
         }
      } catch (Throwable var12) {
         var3 = var12;
         throw var12;
      } finally {
         if (fileInputStream != null) {
            if (var3 != null) {
               try {
                  fileInputStream.close();
               } catch (Throwable var11) {
                  var3.addSuppressed(var11);
               }
            } else {
               fileInputStream.close();
            }
         }

      }

      return schem;
   }
}
