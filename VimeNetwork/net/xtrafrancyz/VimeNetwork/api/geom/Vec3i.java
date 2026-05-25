package net.xtrafrancyz.VimeNetwork.api.geom;

import org.bukkit.Location;
import org.bukkit.block.Block;

public class Vec3i {
   public final int x;
   public final int y;
   public final int z;

   public Vec3i() {
      this(0, 0, 0);
   }

   public Vec3i(Vec3d vec) {
      this.x = (int)vec.x;
      this.y = (int)vec.y;
      this.z = (int)vec.z;
   }

   public Vec3i(Vec3f vec) {
      this.x = (int)vec.x;
      this.y = (int)vec.y;
      this.z = (int)vec.z;
   }

   public Vec3i(Location loc) {
      this.x = loc.getBlockX();
      this.y = loc.getBlockY();
      this.z = loc.getBlockZ();
   }

   public Vec3i(Block bloc) {
      this.x = bloc.getX();
      this.y = bloc.getY();
      this.z = bloc.getZ();
   }

   public Vec3i(int x, int y, int z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public Vec3i setX(int x) {
      return new Vec3i(x, this.y, this.z);
   }

   public Vec3i setY(int y) {
      return new Vec3i(this.x, y, this.z);
   }

   public Vec3i setZ(int z) {
      return new Vec3i(this.x, this.y, z);
   }

   public Vec3i add(int val) {
      return new Vec3i(this.x + val, this.y + val, this.z + val);
   }

   public Vec3i add(Vec3i vec) {
      return new Vec3i(this.x + vec.x, this.y + vec.y, this.z + vec.z);
   }

   public Vec3i add(int x, int y, int z) {
      return new Vec3i(this.x + x, this.y + y, this.z + z);
   }

   public Vec3i subtract(int val) {
      return new Vec3i(this.x - val, this.y - val, this.z - val);
   }

   public Vec3i subtract(Vec3i vec) {
      return new Vec3i(this.x - vec.x, this.y - vec.y, this.z - vec.z);
   }

   public Vec3i subtract(int x, int y, int z) {
      return new Vec3i(this.x - x, this.y - y, this.z - z);
   }

   public Vec3i multiply(int val) {
      return new Vec3i(this.x * val, this.y * val, this.z * val);
   }

   public Vec3i multiply(Vec3i vec) {
      return new Vec3i(this.x * vec.x, this.y * vec.y, this.z * vec.z);
   }

   public Vec3i multiply(int x, int y, int z) {
      return new Vec3i(this.x * x, this.y * y, this.z * z);
   }

   public Vec3f divide(int val) {
      return new Vec3f((float)(this.x / val), (float)(this.y / val), (float)(this.z / val));
   }

   public Vec3f divide(float val) {
      return new Vec3f((float)this.x / val, (float)this.y / val, (float)this.z / val);
   }

   public Vec3f divide(Vec3i vec) {
      return new Vec3f((float)(this.x / vec.x), (float)(this.y / vec.y), (float)(this.z / vec.z));
   }

   public Vec3f divide(Vec3f vec) {
      return new Vec3f((float)this.x / vec.x, (float)this.y / vec.y, (float)this.z / vec.z);
   }

   public Vec3f divide(int x, int y, int z) {
      return new Vec3f((float)(this.x / x), (float)(this.y / y), (float)(this.z / z));
   }

   public Vec3i invert() {
      return new Vec3i(-this.x, -this.y, -this.z);
   }

   public Vec3f normalize() {
      return this.divide(this.length());
   }

   public Vec3i abs() {
      return new Vec3i(Math.abs(this.x), Math.abs(this.y), Math.abs(this.z));
   }

   public Vec3i clone() {
      return new Vec3i(this.x, this.y, this.z);
   }

   public float length() {
      return (float)Math.sqrt((double)(this.x * this.x + this.y * this.y + this.z * this.z));
   }

   public float lengthSq() {
      return (float)(this.x * this.x + this.y * this.y + this.z * this.z);
   }

   public String toString() {
      return "Vec3i[" + this.x + ", " + this.y + ", " + this.z + "]";
   }

   public int hashCode() {
      int result = this.x;
      result = 31 * result + this.y;
      result = 31 * result + this.z;
      return result;
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof Vec3i)) {
         return false;
      } else {
         Vec3i o = (Vec3i)obj;
         return o.x == this.x && o.y == this.y && o.z == this.z;
      }
   }
}
