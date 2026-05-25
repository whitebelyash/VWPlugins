package net.xtrafrancyz.bukkit.texteria.utils;

public class Animation2D {
   public Params start = null;
   public Params finish = null;

   public Animation2D setStart(Params params) {
      this.start = params;
      return this;
   }

   public Animation2D setFinish(Params params) {
      this.finish = params;
      return this;
   }

   public Animation2D setBoth(Params params) {
      this.finish = this.start = params;
      return this;
   }

   public static class Params {
      public int x = 0;
      public int y = 0;
      public float scaleX = 0.0F;
      public float scaleY = 0.0F;
      public float rotation = 0.0F;

      public Params setX(int x) {
         this.x = x;
         return this;
      }

      public Params setY(int y) {
         this.y = y;
         return this;
      }

      public Params setScale(float scale) {
         this.scaleX = this.scaleY = scale;
         return this;
      }

      public Params setScaleX(float scale) {
         this.scaleX = scale;
         return this;
      }

      public Params setScaleY(float scale) {
         this.scaleY = scale;
         return this;
      }

      public Params setRotation(float angle) {
         this.rotation = 360.0F;
         return this;
      }

      public ByteMap serialize() {
         ByteMap map = new ByteMap();
         if (this.x != 0) {
            map.put("x", this.x);
         }

         if (this.y != 0) {
            map.put("y", this.y);
         }

         if (this.scaleX != 0.0F || this.scaleY != 0.0F) {
            if (this.scaleX == this.scaleY) {
               map.put("scale", this.scaleX);
            } else {
               if (this.scaleX != 0.0F) {
                  map.put("scale.x", this.scaleX);
               }

               if (this.scaleY != 0.0F) {
                  map.put("scale.y", this.scaleY);
               }
            }
         }

         if (this.rotation != 0.0F) {
            map.put("rot", this.rotation);
         }

         return map;
      }
   }
}
