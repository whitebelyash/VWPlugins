/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.SkyWars.kit;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import net.xtrafrancyz.SkyWars.kit.ArmoryKit;
import net.xtrafrancyz.SkyWars.kit.AssassinKit;
import net.xtrafrancyz.SkyWars.kit.BlacksmithKit;
import net.xtrafrancyz.SkyWars.kit.CanoneerKit;
import net.xtrafrancyz.SkyWars.kit.EcologKit;
import net.xtrafrancyz.SkyWars.kit.FarmerKit;
import net.xtrafrancyz.SkyWars.kit.FisherKit;
import net.xtrafrancyz.SkyWars.kit.Kit;
import net.xtrafrancyz.SkyWars.kit.KnightKit;
import net.xtrafrancyz.SkyWars.kit.LuckyKit;
import net.xtrafrancyz.SkyWars.kit.MagicianKit;
import net.xtrafrancyz.SkyWars.kit.PyroKit;
import net.xtrafrancyz.SkyWars.kit.SnowmanKit;
import net.xtrafrancyz.SkyWars.kit.SpeleologKit;
import net.xtrafrancyz.SkyWars.kit.TrollKit;

public class Kits {
    public final Map<String, Kit> kits = new HashMap<String, Kit>();

    public Kits() {
        Consumer<Kit> adder = kit -> this.kits.put(kit.id, (Kit)kit);
        Kit.slotCounter = 10;
        adder.accept(new BlacksmithKit());
        adder.accept(new FarmerKit());
        adder.accept(new MagicianKit());
        adder.accept(new SpeleologKit());
        adder.accept(new CanoneerKit());
        adder.accept(new FisherKit());
        adder.accept(new ArmoryKit());
        Kit.slotCounter = 19;
        adder.accept(new KnightKit());
        adder.accept(new EcologKit());
        adder.accept(new PyroKit());
        adder.accept(new SnowmanKit());
        adder.accept(new TrollKit());
        adder.accept(new LuckyKit());
        adder.accept(new AssassinKit());
    }
}

