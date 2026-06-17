/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.VimeNetwork.api.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import net.xtrafrancyz.Commons.player.Rank;

@Target(value={ElementType.METHOD})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface CmdSub {
    public String[] value();

    public String[] aliases() default {};

    public Rank rank() default Rank.PLAYER;

    public Rank[] ranks() default {};

    public boolean hidden() default false;
}

