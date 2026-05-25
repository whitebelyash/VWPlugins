package net.xtrafrancyz.bukkit.texteria.utils;

import net.xtrafrancyz.bukkit.texteria.elements.Element;

public class OffsetUtils {
   public static final int SLOT_SIZE = 16;

   public static Element doubleChest(Element elem, int slot) {
      if (slot >= 0 && slot <= 53) {
         elem.setPosition(Position.CENTER).setOffset(slot % 9 * 18 - 72, slot / 9 * 18 - 85);
         return elem;
      } else {
         throw new IllegalArgumentException("Slot must be between 0 and 53");
      }
   }

   public static Element chest(Element elem, int slot) {
      if (slot >= 0 && slot < 26) {
         elem.setPosition(Position.CENTER).setOffset(slot % 9 * 18 - 72, slot / 9 * 18 - 58);
         return elem;
      } else {
         throw new IllegalArgumentException("Slot must be between 0 and 26");
      }
   }

   public static Element inv9(Element elem, int slot) {
      if (slot >= 0 && slot <= 8) {
         elem.setPosition(Position.CENTER).setOffset(slot * 18 - 72, -40);
         return elem;
      } else {
         throw new IllegalArgumentException("Slot must be between 0 and 8");
      }
   }

   public static Element inv45(Element elem, int slot) {
      if (slot >= 0 && slot <= 44) {
         elem.setPosition(Position.CENTER).setOffset(slot % 9 * 18 - 72, slot / 9 * 18 - 76);
         return elem;
      } else {
         throw new IllegalArgumentException("Slot must be between 0 and 44");
      }
   }

   public static Element hotbar(Element elem, int slot) {
      if (slot >= 0 && slot <= 8) {
         elem.setPosition(Position.BOTTOM).setOffset(slot * 20 - 80, 3);
         return elem;
      } else {
         throw new IllegalArgumentException("Slot must be between 0 and 8");
      }
   }
}
