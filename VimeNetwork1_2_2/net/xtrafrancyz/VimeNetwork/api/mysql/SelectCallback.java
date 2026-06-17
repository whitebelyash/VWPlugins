/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.VimeNetwork.api.mysql;

import java.sql.ResultSet;
import net.xtrafrancyz.VimeNetwork.api.mysql.Callback;

public interface SelectCallback
extends Callback {
    public void done(ResultSet var1) throws Exception;
}

