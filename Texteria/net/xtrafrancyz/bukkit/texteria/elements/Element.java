package net.xtrafrancyz.bukkit.texteria.elements;

import net.xtrafrancyz.bukkit.texteria.utils.Animation2D;
import net.xtrafrancyz.bukkit.texteria.utils.Attachment;
import net.xtrafrancyz.bukkit.texteria.utils.ByteMap;
import net.xtrafrancyz.bukkit.texteria.utils.OnClick;
import net.xtrafrancyz.bukkit.texteria.utils.Position;
import net.xtrafrancyz.bukkit.texteria.utils.Visibility;

public abstract class Element {
   public String id;
   public int color = -1;
   public long duration = -1L;
   public Position pos;
   public Attachment attach;
   public Visibility visibility;
   public Animation2D anim;
   public float scaleX;
   public float scaleY;
   public int x;
   public int y;
   public float rotation;
   public int delay;
   public int fadeStart;
   public int fadeFinish;
   public OnClick click;
   public boolean hoverable;

   protected Element(String id) {
      this.pos = Position.CENTER;
      this.attach = null;
      this.visibility = null;
      this.anim = null;
      this.scaleX = 1.0F;
      this.scaleY = 1.0F;
      this.x = 0;
      this.y = 0;
      this.rotation = 0.0F;
      this.delay = 0;
      this.fadeStart = 255;
      this.fadeFinish = 255;
      this.hoverable = false;
      this.id = id;
   }

   public Element setScale(float scale) {
      return this.setScale(scale, scale);
   }

   public Element setScale(float scaleX, float scaleY) {
      this.scaleX = scaleX;
      this.scaleY = scaleY;
      return this;
   }

   public Element setColor(int color) {
      this.color = color;
      return this;
   }

   public Element setPosition(Position pos) {
      this.pos = pos;
      return this;
   }

   public Element setOffset(int x, int y) {
      this.x = x;
      this.y = y;
      return this;
   }

   public Element setDuration(long duration) {
      this.duration = duration;
      return this;
   }

   public Element setDelay(int delay) {
      this.delay = delay;
      return this;
   }

   public Element setFadeStart(int fade) {
      this.fadeStart = fade;
      return this;
   }

   public Element setFadeFinish(int fade) {
      this.fadeFinish = fade;
      return this;
   }

   public Element setFade(int fade) {
      this.fadeFinish = this.fadeStart = fade;
      return this;
   }

   public Element setRotation(float angle) {
      this.rotation = angle;
      return this;
   }

   public Element setHoverable(boolean hoverable) {
      this.hoverable = hoverable;
      return this;
   }

   public Element setOnClick(OnClick click) {
      this.hoverable = true;
      this.click = click;
      return this;
   }

   public Element setVisibility(Visibility visibility) {
      this.visibility = visibility;
      return this;
   }

   public Element setAttachment(Attachment attach) {
      this.attach = attach;
      return this;
   }

   public Element setAnimation(Animation2D anim) {
      this.anim = anim;
      return this;
   }

   public void write(ByteMap map) {
      map.put("type", this.getType());
      if (this.id != null) {
         map.put("id", this.id);
      }

      if (this.x != 0) {
         map.put("x", this.x);
      }

      if (this.y != 0) {
         map.put("y", this.y);
      }

      if (this.duration > 0L) {
         map.put("dur", this.duration);
      }

      if (this.delay != 0) {
         map.put("delay", this.delay);
      }

      if (this.color != -1) {
         map.put("color", this.color);
      }

      if (this.rotation != 0.0F) {
         map.put("rot", this.rotation);
      }

      if (this.hoverable && this.click == null) {
         map.put("hv", true);
      }

      if (this.scaleX != 1.0F || this.scaleY != 1.0F) {
         if (this.scaleX == this.scaleY) {
            map.put("scale", this.scaleX);
         } else {
            if (this.scaleX != 1.0F) {
               map.put("scale.x", this.scaleX);
            }

            if (this.scaleY != 1.0F) {
               map.put("scale.y", this.scaleY);
            }
         }
      }

      if (this.fadeStart != 255 || this.fadeFinish != 255) {
         if (this.fadeStart == this.fadeFinish) {
            map.put("fade", this.fadeStart);
         } else {
            if (this.fadeStart != 255) {
               map.put("fade.s", this.fadeStart);
            }

            if (this.fadeFinish != 255) {
               map.put("fade.f", this.fadeFinish);
            }
         }
      }

      if (this.click != null) {
         ByteMap c = new ByteMap();
         c.put("act", this.click.action.name());
         c.put("data", this.click.data);
         map.put("click", c);
      }

      if (this.visibility != null) {
         ByteMap vis = new ByteMap();
         this.visibility.write(vis);
         map.put("vis", vis);
      }

      if (this.attach != null) {
         map.put("attach.to", this.attach.attachTo);
         map.put("attach.loc", this.attach.attachLocation.name());
         if (this.attach.attachLocation != this.attach.orientation) {
            map.put("attach.orient", this.attach.orientation.name());
         }

         if (!this.attach.removeWhenParentRemove) {
            map.put("attach.rwpr", false);
         }
      } else if (this.pos != Position.CENTER) {
         map.put("pos", this.pos.name());
      }

      if (this.anim != null) {
         if (this.anim.start != null) {
            map.put("anim.s", this.anim.start.serialize());
         }

         if (this.anim.finish != null) {
            map.put("anim.f", this.anim.finish.serialize());
         }
      }

   }

   protected abstract String getType();
}
