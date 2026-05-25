package net.xtrafrancyz.Core;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.xtrafrancyz.Core.network.Buf;
import org.apache.mina.core.buffer.IoBuffer;

public class CoreByteMap extends HashMap {
   private static final long serialVersionUID = 4722366923612558988L;
   private static final byte TYPE_INT = 1;
   private static final byte TYPE_BYTE = 2;
   private static final byte TYPE_LONG = 3;
   private static final byte TYPE_STRING = 4;
   private static final byte TYPE_SHORT = 5;
   private static final byte TYPE_FLOAT = 6;
   private static final byte TYPE_DOUBLE = 7;
   private static final byte TYPE_BOOLEAN = 8;
   private static final byte TYPE_MAP = 9;
   private static final byte TYPE_BYTE_ARRAY = 10;
   private static final byte TYPE_STRING_ARRAY = 11;
   private static final byte TYPE_MAP_ARRAY = 12;

   public CoreByteMap() {
   }

   public CoreByteMap(HashMap map) {
      super(map);
   }

   public CoreByteMap(byte[] bytes) {
      try {
         Buf in = new Buf(IoBuffer.wrap(bytes));
         int size = in.readVarInt();

         for(int i = 0; i < size; ++i) {
            String key = in.readString();
            switch (in.read()) {
               case 1:
                  this.put(key, in.readInt());
                  break;
               case 2:
                  this.put(key, in.read());
                  break;
               case 3:
                  this.put(key, in.readLong());
                  break;
               case 4:
                  this.put(key, in.readString());
                  break;
               case 5:
                  this.put(key, in.readShort());
                  break;
               case 6:
                  this.put(key, in.readFloat());
                  break;
               case 7:
                  this.put(key, in.readDouble());
                  break;
               case 8:
                  this.put(key, in.read() == 1);
                  break;
               case 9:
                  this.put(key, new CoreByteMap(in.readByteArray()));
                  break;
               case 10:
                  this.put(key, in.readByteArray());
                  break;
               case 11:
                  String[] arr = new String[in.readVarInt()];

                  for(int j = 0; j < arr.length; ++j) {
                     arr[j] = in.readString();
                  }

                  this.put(key, arr);
                  break;
               case 12:
                  CoreByteMap[] arr = new CoreByteMap[in.readVarInt()];

                  for(int j = 0; j < arr.length; ++j) {
                     arr[j] = new CoreByteMap(in.readByteArray());
                  }

                  this.put(key, arr);
            }
         }
      } catch (Exception e) {
         e.printStackTrace();
      }

   }

   public byte[] toByteArray() {
      try {
         IoBuffer buffer = IoBuffer.allocate(128);
         buffer.setAutoExpand(true);
         Buf out = new Buf(buffer);
         out.writeVarInt(this.size());

         for(Map.Entry entry : this.entrySet()) {
            Object val = entry.getValue();
            if (val != null && entry.getKey() != null) {
               out.writeString((String)entry.getKey());
               Class clazz = val.getClass();
               if (clazz == Integer.class) {
                  out.write((byte)1);
                  out.writeInt((Integer)val);
               } else if (clazz == Float.class) {
                  out.write((byte)6);
                  out.writeFloat((Float)val);
               } else if (clazz == Byte.class) {
                  out.write((byte)2);
                  out.write((Byte)val);
               } else if (clazz == Short.class) {
                  out.write((byte)5);
                  out.writeShort((Short)val);
               } else if (clazz == Long.class) {
                  out.write((byte)3);
                  out.writeLong((Long)val);
               } else if (clazz == String.class) {
                  out.write((byte)4);
                  out.writeString((String)val);
               } else if (clazz == Double.class) {
                  out.write((byte)7);
                  out.writeDouble((Double)val);
               } else if (clazz == Boolean.class) {
                  out.write((byte)8);
                  out.write((byte)((Boolean)val ? 1 : 0));
               } else if (clazz == CoreByteMap.class) {
                  out.write((byte)9);
                  out.writeByteArray(((CoreByteMap)val).toByteArray());
               } else if (clazz == byte[].class) {
                  out.write((byte)10);
                  out.writeByteArray((byte[])val);
               } else if (clazz == String[].class) {
                  out.write((byte)11);
                  String[] arr = (String[])val;
                  out.writeVarInt(arr.length);

                  for(String str : arr) {
                     out.writeString(str);
                  }
               } else {
                  if (clazz != CoreByteMap[].class) {
                     throw new IllegalArgumentException("Unsupported type of data: " + clazz.getName());
                  }

                  out.write((byte)12);
                  CoreByteMap[] arr = (CoreByteMap[])val;
                  out.writeVarInt(arr.length);

                  for(CoreByteMap map : arr) {
                     out.writeByteArray(map.toByteArray());
                  }
               }
            }
         }

         byte[] bytes = new byte[buffer.position()];
         buffer.flip();
         buffer.get(bytes);
         buffer.free();
         return bytes;
      } catch (Exception ex) {
         ex.printStackTrace();
         return new byte[0];
      }
   }

   public String getString(String key) {
      return (String)this.get(key);
   }

   public byte getByte(String key) {
      return (Byte)this.get(key);
   }

   public short getShort(String key) {
      return (Short)this.get(key);
   }

   public float getFloat(String key) {
      return (Float)this.get(key);
   }

   public double getDouble(String key) {
      return (Double)this.get(key);
   }

   public int getInt(String key) {
      return (Integer)this.get(key);
   }

   public long getLong(String key) {
      return (Long)this.get(key);
   }

   public boolean getBoolean(String key) {
      return (Boolean)this.get(key);
   }

   public CoreByteMap getMap(String key) {
      return (CoreByteMap)this.get(key);
   }

   public byte[] getByteArray(String key) {
      return (byte[])this.get(key);
   }

   public String[] getStringArray(String key) {
      return (String[])this.get(key);
   }

   public CoreByteMap[] getMapArray(String key) {
      return (CoreByteMap[])this.get(key);
   }

   public String getString(String key, String def) {
      Object o = this.get(key);
      return o == null ? def : (String)o;
   }

   public byte getByte(String key, byte def) {
      Object o = this.get(key);
      return o == null ? def : (Byte)o;
   }

   public short getShort(String key, short def) {
      Object o = this.get(key);
      return o == null ? def : (Short)o;
   }

   public float getFloat(String key, float def) {
      Object o = this.get(key);
      return o == null ? def : (Float)o;
   }

   public double getDouble(String key, double def) {
      Object o = this.get(key);
      return o == null ? def : (Double)o;
   }

   public int getInt(String key, int def) {
      Object o = this.get(key);
      return o == null ? def : (Integer)o;
   }

   public long getLong(String key, long def) {
      Object o = this.get(key);
      return o == null ? def : (Long)o;
   }

   public boolean getBoolean(String key, boolean def) {
      Object o = this.get(key);
      return o == null ? def : (Boolean)o;
   }

   public CoreByteMap getMap(String key, CoreByteMap def) {
      Object o = this.get(key);
      return o == null ? def : (CoreByteMap)o;
   }

   public String toString() {
      Iterator<Map.Entry<String, Object>> i = this.entrySet().iterator();
      if (!i.hasNext()) {
         return "{}";
      } else {
         StringBuilder sb = new StringBuilder();
         sb.append('{');

         while(true) {
            Map.Entry<String, Object> e = (Map.Entry)i.next();
            String key = (String)e.getKey();
            Object value = e.getValue();
            sb.append(key);
            sb.append('=');
            String val;
            if (value == this) {
               val = "(this Map)";
            } else if (value instanceof byte[]) {
               val = Arrays.toString((byte[])value);
            } else if (value instanceof Object[]) {
               val = Arrays.toString(value);
            } else {
               val = String.valueOf(value);
            }

            sb.append(val);
            if (!i.hasNext()) {
               return sb.append('}').toString();
            }

            sb.append(',').append(' ');
         }
      }
   }
}
