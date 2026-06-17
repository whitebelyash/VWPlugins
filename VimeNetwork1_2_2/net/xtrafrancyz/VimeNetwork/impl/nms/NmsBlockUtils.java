/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.v1_6_R3.Block
 *  net.minecraft.server.v1_6_R3.ItemBlock
 *  net.minecraft.server.v1_6_R3.Material
 *  org.bukkit.Material
 */
package net.xtrafrancyz.VimeNetwork.impl.nms;

import java.util.Arrays;
import net.minecraft.server.v1_6_R3.Block;
import net.minecraft.server.v1_6_R3.ItemBlock;
import net.minecraft.server.v1_6_R3.Material;
import net.xtrafrancyz.VimeNetwork.api.util.Reflect;
import net.xtrafrancyz.VimeNetwork.impl.nms.block.VBlock;
import net.xtrafrancyz.VimeNetwork.impl.nms.item.ItemBlockWithData;

public class NmsBlockUtils {
    public static org.bukkit.Material addBlock(int id, String name, Material material, boolean hasMeta) {
        return NmsBlockUtils.addBlock(id, name, new VBlock(id, material), hasMeta);
    }

    public static org.bukkit.Material addBlock(int id, String name, Block block, boolean hasMeta) {
        if (hasMeta) {
            new ItemBlockWithData(id - 256);
        } else {
            new ItemBlock(id - 256);
        }
        return NmsBlockUtils.addIntoMaterials(id, name);
    }

    public static void addItem(int id, boolean hasMeta) {
    }

    private static org.bukkit.Material addIntoMaterials(int id, String name) {
        org.bukkit.Material[] byId = (org.bukkit.Material[])Reflect.get(org.bukkit.Material.class, "byId");
        org.bukkit.Material newMaterial = Reflect.addEnum(org.bukkit.Material.class, name, id);
        if (byId.length <= id) {
            byId = Arrays.copyOf(byId, id + 2);
            Reflect.setFinal(org.bukkit.Material.class, "byId", (Object)byId);
        }
        byId[id] = newMaterial;
        return newMaterial;
    }
}

