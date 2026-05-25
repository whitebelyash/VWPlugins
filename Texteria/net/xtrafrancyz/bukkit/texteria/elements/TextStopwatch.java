package net.xtrafrancyz.bukkit.texteria.elements;

public class TextStopwatch extends Text {
   public TextStopwatch(String id, String... lines) {
      super(id, lines);
   }

   protected String getType() {
      return "TextStopwatch";
   }
}
