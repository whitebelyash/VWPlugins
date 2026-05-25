package net.xtrafrancyz.bukkit.texteria.utils;

public class Animation3D {
   public Params start = null;
   public Params finish = null;

   public Animation3D setStart(Params params) {
      this.start = params;
      return this;
   }

   public Animation3D setFinish(Params params) {
      this.finish = params;
      return this;
   }

   public Animation3D setBoth(Params params) {
      this.finish = this.start = params;
      return this;
   }

   public static class Params {
      public float x = 0.0F;
      public float y = 0.0F;
      public float z = 0.0F;
      public float scaleX = 0.0F;
      public float scaleY = 0.0F;
      public float scaleZ = 0.0F;
      public float angleX = 0.0F;
      public float angleY = 0.0F;
      public float angleZ = 0.0F;

      public Params setOffset(float x, float y, float z) {
         this.x = x;
         this.y = y;
         this.z = z;
         return this;
      }

      public Params setScale(float scale) {
         this.setScale(scale, scale, scale);
         return this;
      }

      public Params setScale(float x, float y, float z) {
         this.scaleX = x;
         this.scaleY = y;
         this.scaleZ = z;
         return this;
      }

      public Params setRotation(float x, float y, float z) {
         this.angleX = x;
         this.angleY = y;
         this.angleZ = z;
         return this;
      }

      public ByteMap serialize() {
         ByteMap map = new ByteMap();
         if (this.x != 0.0F) {
            map.put("x", this.x);
         }

         if (this.y != 0.0F) {
            map.put("y", this.y);
         }

         if (this.z != 0.0F) {
            map.put("z", this.z);
         }

         if (this.angleX != 0.0F) {
            map.put("angle.x", this.angleX);
         }

         if (this.angleY != 0.0F) {
            map.put("angle.y", this.angleY);
         }

         if (this.angleZ != 0.0F) {
            map.put("angle.z", this.angleZ);
         }

         if (this.scaleX == this.scaleY && this.scaleX == this.scaleZ && this.scaleX != 0.0F) {
            map.put("scale", this.scaleX);
         } else {
            if (this.scaleX != 0.0F) {
               map.put("scale.x", this.scaleX);
            }

            if (this.scaleY != 0.0F) {
               map.put("scale.y", this.scaleY);
            }

            if (this.scaleZ != 0.0F) {
               map.put("scale.z", this.scaleZ);
            }
         }

         return map;
      }
   }
}
