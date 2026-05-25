package net.xtrafrancyz.VimeNetwork.api.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import net.xtrafrancyz.VimeNetwork.api.player.Rank;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CmdSub {
   String[] value();

   String[] aliases() default {};

   Rank rank() default Rank.PLAYER;

   Rank[] ranks() default {};

   boolean hidden() default false;
}
