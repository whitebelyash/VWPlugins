package net.xtrafrancyz.bukkit.texteria.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ByteMap extends HashMap {
   private static final long serialVersionUID = 4722366953612558988L;
   private static final byte TYPE_INT = 1;
   private static final byte TYPE_BYTE = 2;
   private static final byte TYPE_LONG = 3;
   private static final byte TYPE_UTF = 4;
   private static final byte TYPE_SHORT = 5;
   private static final byte TYPE_FLOAT = 6;
   private static final byte TYPE_DOUBLE = 7;
   private static final byte TYPE_BOOLEAN = 8;
   private static final byte TYPE_MAP = 9;
   private static final byte TYPE_BYTE_ARRAY = 10;
   private static final byte TYPE_STRING_ARRAY = 11;
   private static final byte TYPE_MAP_ARRAY = 12;

   public ByteMap() {
   }

   public ByteMap(HashMap map) {
      super(map);
   }

   public ByteMap(byte[] bytes) {
      try {
         DataInputStream dis = new DataInputStream(new ByteArrayInputStream(bytes));

         while(dis.available() > 0) {
            String key = dis.readUTF();
            switch (dis.readByte()) {
               case 1:
                  this.put(key, dis.readInt());
                  break;
               case 2:
                  this.put(key, dis.readByte());
                  break;
               case 3:
                  this.put(key, dis.readLong());
                  break;
               case 4:
                  this.put(key, dis.readUTF());
                  break;
               case 5:
                  this.put(key, dis.readShort());
                  break;
               case 6:
                  this.put(key, dis.readFloat());
                  break;
               case 7:
                  this.put(key, dis.readDouble());
                  break;
               case 8:
                  this.put(key, dis.readBoolean());
                  break;
               case 9:
                  byte[] mapBytes = new byte[dis.readInt()];
                  dis.read(mapBytes, 0, mapBytes.length);
                  this.put(key, new ByteMap(mapBytes));
                  break;
               case 10:
                  byte[] arr = new byte[dis.readInt()];
                  dis.read(arr, 0, arr.length);
                  this.put(key, arr);
                  break;
               case 11:
                  String[] arr = new String[dis.readShort()];

                  for(int i = 0; i < arr.length; ++i) {
                     arr[i] = dis.readUTF();
                  }

                  this.put(key, arr);
                  break;
               case 12:
                  ByteMap[] arr = new ByteMap[dis.readShort()];

                  for(int i = 0; i < arr.length; ++i) {
                     byte[] mapBytes = new byte[dis.readInt()];
                     dis.read(mapBytes, 0, mapBytes.length);
                     arr[i] = new ByteMap(mapBytes);
                  }

                  this.put(key, arr);
            }
         }
      } catch (Exception e) {
         e.printStackTrace();
      }

   }

   public byte[] toByteArray() {
      ByteArrayOutputStream baos = new ByteArrayOutputStream(128);

      try {
         DataOutputStream dis = new DataOutputStream(baos);

         for(Map.Entry entry : this.entrySet()) {
            dis.writeUTF((String)entry.getKey());
            Object val = entry.getValue();
            Class clazz = val.getClass();
            if (clazz == Integer.class) {
               dis.writeByte(1);
               dis.writeInt((Integer)val);
            } else if (clazz == Float.class) {
               dis.writeByte(6);
               dis.writeFloat((Float)val);
            } else if (clazz == Byte.class) {
               dis.writeByte(2);
               dis.writeByte((Byte)val);
            } else if (clazz == Short.class) {
               dis.writeByte(5);
               dis.writeShort((Short)val);
            } else if (clazz == Long.class) {
               dis.writeByte(3);
               dis.writeLong((Long)val);
            } else if (clazz == String.class) {
               dis.writeByte(4);
               dis.writeUTF((String)val);
            } else if (clazz == Double.class) {
               dis.writeByte(7);
               dis.writeDouble((Double)val);
            } else if (clazz == Boolean.class) {
               dis.writeByte(8);
               dis.writeBoolean((Boolean)val);
            } else if (clazz == ByteMap.class) {
               dis.writeByte(9);
               byte[] bytes = ((ByteMap)val).toByteArray();
               dis.writeInt(bytes.length);
               dis.write(bytes);
            } else if (clazz == byte[].class) {
               dis.writeByte(10);
               byte[] bytes = (byte[])val;
               dis.writeInt(bytes.length);
               dis.write(bytes);
            } else if (clazz == String[].class) {
               dis.writeByte(11);
               String[] arr = (String[])val;
               dis.writeShort(arr.length);

               for(String str : arr) {
                  dis.writeUTF(str);
               }
            } else if (clazz == ByteMap[].class) {
               dis.writeByte(12);
               ByteMap[] arr = (ByteMap[])val;
               dis.writeShort(arr.length);

               for(ByteMap map : arr) {
                  byte[] serialized = map.toByteArray();
                  dis.writeInt(serialized.length);
                  dis.write(serialized);
               }
            }
         }

         dis.flush();
      } catch (Exception e) {
         e.printStackTrace();
      }

      return baos.toByteArray();
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

   public ByteMap getMap(String key) {
      return (ByteMap)this.get(key);
   }

   public byte[] getByteArray(String key) {
      return (byte[])this.get(key);
   }

   public String[] getStringArray(String key) {
      return (String[])this.get(key);
   }

   public ByteMap[] getMapArray(String key) {
      return (ByteMap[])this.get(key);
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

   public ByteMap getMap(String key, ByteMap def) {
      Object o = this.get(key);
      return o == null ? def : (ByteMap)o;
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
