package net.xtrafrancyz.Core.network.filter;

class Magic {
   static final byte[] BEGIN_BYTES = new byte[]{82, 7, -7, 20};
   static final int BEGIN_LENGTH;

   static {
      BEGIN_LENGTH = BEGIN_BYTES.length;
   }
}
