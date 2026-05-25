package net.xtrafrancyz.VimeNetwork.api.mysql;

import java.sql.ResultSet;

public interface SelectCallback extends Callback {
   void done(ResultSet var1) throws Exception;
}
