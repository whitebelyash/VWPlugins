package net.xtrafrancyz.VimeNetwork.api.util;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.reflect.ReflectionFactory;

public class Reflect {
   private static final Map cache = new ConcurrentHashMap();

   private Reflect() {
   }

   public static Enum addEnum(Class enumType, String name, Object... args) {
      try {
         ClassData<T> data = getClass(enumType);
         Field field = null;

         try {
            field = data.findFinalField("$VALUES");
         } catch (UnableToFindFieldException var12) {
            try {
               field = data.findFinalField("ENUM$VALUES");
            } catch (UnableToFindFieldException var11) {
            }
         }

         if (field == null) {
            int flags = 4120;
            String valueType = "[L" + enumType.getName().replace('.', '/') + ";";

            for(Field f : enumType.getDeclaredFields()) {
               if ((f.getModifiers() & flags) == flags && f.getType().getName().replace('.', '/').equals(valueType)) {
                  field = f;
                  f.setAccessible(true);
                  Reflect.ClassData.FIELD_MODIFIERS.set(f, f.getModifiers() & -17);
                  break;
               }
            }
         }

         if (field == null) {
            throw new UnableToFindFieldException(enumType, "$VALUES");
         } else {
            T[] prev = (T[])((Enum[])((Enum[])field.get((Object)null)));
            List<T> values = new ArrayList(Arrays.asList(prev));
            Object[] params = new Object[args.length + 2];
            params[0] = name;
            params[1] = values.size();
            System.arraycopy(args, 0, params, 2, args.length);
            ReflectionFactory rFactory = ReflectionFactory.getReflectionFactory();
            T newValue = (T)((Enum)rFactory.newConstructorAccessor(data.findConstructor(params)).newInstance(params));
            values.add(newValue);
            field.set((Object)null, values.toArray((Enum[])Array.newInstance(enumType, 0)));
            setFinal(Class.class, enumType, "enumConstants", (Object)null);
            setFinal(Class.class, enumType, "enumConstantDirectory", (Object)null);
            return newValue;
         }
      } catch (Exception e) {
         error(e, "addEnum error");
         return null;
      }
   }

   public static Object construct(Class clazz, Object... args) {
      try {
         return getClass(clazz).construct(args);
      } catch (Exception e) {
         error(e, "Constructor error");
         return null;
      }
   }

   public static Object get(Class clazz, String field) {
      try {
         return getClass(clazz).get((Object)null, field);
      } catch (Exception e) {
         error(e, "Get static field error");
         return null;
      }
   }

   public static Object get(Object instance, String field) {
      try {
         return getClass(instance.getClass()).get(instance, field);
      } catch (Exception e) {
         error(e, "Get field error");
         return null;
      }
   }

   public static Object get(Class clazz, Object instance, String field) {
      try {
         return getClass(clazz).get(instance, field);
      } catch (Exception e) {
         error(e, "Get field error");
         return null;
      }
   }

   public static void set(Class clazz, String field, Object value) {
      try {
         getClass(clazz).set((Object)null, field, value);
      } catch (Exception e) {
         error(e, "Set static field error");
      }

   }

   public static void set(Object instance, String field, Object value) {
      try {
         getClass(instance.getClass()).set(instance, field, value);
      } catch (Exception e) {
         error(e, "Set field error");
      }

   }

   public static void set(Class clazz, Object instance, String field, Object value) {
      try {
         getClass(clazz).set(instance, field, value);
      } catch (Exception e) {
         error(e, "Set field error");
      }

   }

   public static void setFinal(Class clazz, String field, Object value) {
      try {
         getClass(clazz).setFinal((Object)null, field, value);
      } catch (Exception e) {
         error(e, "Set static final field error");
      }

   }

   public static void setFinal(Object instance, String field, Object value) {
      try {
         getClass(instance.getClass()).setFinal(instance, field, value);
      } catch (Exception e) {
         error(e, "Set final field error");
      }

   }

   public static void setFinal(Class clazz, Object instance, String field, Object value) {
      try {
         getClass(clazz).setFinal(instance, field, value);
      } catch (Exception e) {
         error(e, "Set final field error");
      }

   }

   public static Object invoke(Class clazz, String method, Object... args) {
      try {
         return getClass(clazz).invoke((Object)null, method, args);
      } catch (Throwable e) {
         error(e, "Invoke static error");
         return null;
      }
   }

   public static Object invoke(Object instance, String method, Object... args) {
      try {
         return getClass(instance.getClass()).invoke(instance, method, args);
      } catch (Throwable e) {
         error(e, "Invoke error");
         return null;
      }
   }

   public static Object invoke(Class clazz, Object instance, String method, Object... args) {
      try {
         return getClass(clazz).invoke(instance, method, args);
      } catch (Throwable e) {
         error(e, "Invoke error");
         return null;
      }
   }

   public static boolean isConstructorExist(Class clazz, Class... args) {
      return findConstructor(clazz, args) != null;
   }

   public static boolean isMethodExist(Class clazz, String method, Class... args) {
      return findMethod(clazz, method, args) != null;
   }

   public static boolean isFieldExist(Class clazz, String field) {
      return findField(clazz, field) != null;
   }

   public static Constructor findConstructor(Class clazz, Class... args) {
      try {
         return getClass(clazz).findConstructor0(args);
      } catch (Exception var3) {
         return null;
      }
   }

   public static Method findMethod(Class clazz, String method, Class... args) {
      try {
         return getClass(clazz).findMethod0(method, args);
      } catch (Exception var4) {
         return null;
      }
   }

   public static Field findField(Class clazz, String field) {
      try {
         return getClass(clazz).findField(field);
      } catch (Exception var3) {
         return null;
      }
   }

   public static Field findFinalField(Class clazz, String field) {
      try {
         return getClass(clazz).findFinalField(field);
      } catch (Exception var3) {
         return null;
      }
   }

   public static Class findClass(String name) {
      try {
         return Class.forName(name);
      } catch (ClassNotFoundException var2) {
         return null;
      }
   }

   public static void setAggressiveMethodsOverloading(Class clazz, boolean flag) {
      ClassData data = getClass(clazz);
      if (data.aggressiveOverloading != flag) {
         data.aggressiveOverloading = flag;
         data.methods.clear();
      }

   }

   public static MethodHandles.Lookup lookup() {
      return (MethodHandles.Lookup)get(MethodHandles.Lookup.class, "IMPL_LOOKUP");
   }

   private static ClassData getClass(Class clazz) {
      ClassData<T> data = (ClassData)cache.get(clazz.getName());
      if (data == null) {
         cache.put(clazz.getName(), data = new ClassData(clazz));
      }

      return data;
   }

   private static void error(Throwable e, String message) {
      Logger.getLogger("Reflect").log(Level.SEVERE, message, e);
   }

   private static String classesToString(Class[] classes) {
      int iMax = classes.length - 1;
      if (iMax == -1) {
         return "()";
      } else {
         StringBuilder b = new StringBuilder();
         b.append('(');
         int i = 0;

         while(true) {
            b.append(classes[i].getName());
            if (i == iMax) {
               return b.append(')').toString();
            }

            b.append(',');
            ++i;
         }
      }
   }

   static class ClassData {
      private static Field FIELD_MODIFIERS = null;
      private final Class clazz;
      private final Map fields = new HashMap();
      private final Map methods = new HashMap();
      private final Map constructors = new HashMap();
      boolean aggressiveOverloading = false;

      public ClassData(Class clazz) {
         this.clazz = clazz;
      }

      void set(Object instance, String field, Object value) throws Exception {
         this.findField(field).set(instance, value);
      }

      void setFinal(Object instance, String field, Object value) throws Exception {
         this.findFinalField(field).set(instance, value);
      }

      Object get(Object instance, String field) throws Exception {
         return this.findField(field).get(instance);
      }

      Object invoke(Object instance, String method, Object... args) throws Throwable {
         return this.findMethod(method, args).invoke(instance, args);
      }

      Object construct(Object... args) throws Exception {
         return this.findConstructor(args).newInstance(args);
      }

      Constructor findConstructor(Object... args) {
         return this.findConstructor0(this.toTypes(args));
      }

      Constructor findConstructor0(Class... types) {
         Object mapped = new ConstructorMapKey(types);
         Constructor<K> con = (Constructor)this.constructors.get(mapped);
         if (con == null) {
            label37:
            for(Constructor c : this.clazz.getDeclaredConstructors()) {
               Class<?>[] ptypes = c.getParameterTypes();
               if (ptypes.length == types.length) {
                  for(int i = 0; i < ptypes.length; ++i) {
                     if (types[i] != null && ptypes[i] != types[i] && !ptypes[i].isAssignableFrom(types[i])) {
                        continue label37;
                     }
                  }

                  con = c;
                  c.setAccessible(true);
                  this.constructors.put(mapped, c);
                  break;
               }
            }

            if (con == null) {
               throw new UnableToFindConstructorException(this.clazz, types);
            }
         }

         return con;
      }

      Method findMethod(String name, Object... args) {
         Class[] types = null;
         Object mapped;
         if (this.aggressiveOverloading) {
            types = this.toTypes(args);
            mapped = new AggressiveMethodMapKey(name, types);
         } else {
            mapped = new MethodMapKey(name, args.length);
         }

         Method method = (Method)this.methods.get(mapped);
         if (method == null) {
            if (types == null) {
               types = this.toTypes(args);
            }

            method = this.fastFindMethod(name, types);
            if (method == null) {
               throw new UnableToFindMethodException(this.clazz, name, types);
            }

            this.methods.put(mapped, method);
         }

         return method;
      }

      Method findMethod0(String name, Class... types) {
         Object mapped;
         if (this.aggressiveOverloading) {
            mapped = new AggressiveMethodMapKey(name, types);
         } else {
            mapped = new MethodMapKey(name, types.length);
         }

         Method method = (Method)this.methods.get(mapped);
         if (method == null) {
            method = this.fastFindMethod(name, types);
            if (method == null) {
               throw new UnableToFindMethodException(this.clazz, name, types);
            }

            this.methods.put(mapped, method);
         }

         return method;
      }

      private Method fastFindMethod(String name, Class... types) {
         Method method = null;
         name = name.intern();
         Class clazz0 = this.clazz;

         do {
            label41:
            for(Method m : clazz0.getDeclaredMethods()) {
               if (name == m.getName()) {
                  Class<?>[] ptypes = m.getParameterTypes();
                  if (ptypes.length == types.length) {
                     for(int i = 0; i < ptypes.length; ++i) {
                        if (types[i] != null && ptypes[i] != types[i] && !ptypes[i].isAssignableFrom(types[i])) {
                           continue label41;
                        }
                     }

                     method = m;
                     break;
                  }
               }
            }

            if (method != null) {
               method.setAccessible(true);
               break;
            }

            clazz0 = clazz0.getSuperclass();
         } while(clazz0 != null);

         return method;
      }

      Field findFinalField(String name) throws Exception {
         Field field = this.findField(name);
         FIELD_MODIFIERS.set(field, field.getModifiers() & -17);
         return field;
      }

      Field findField(String name) {
         Field field = (Field)this.fields.get(name);
         if (field == null) {
            for(Class clazz0 = this.clazz; clazz0 != null; clazz0 = clazz0.getSuperclass()) {
               try {
                  field = clazz0.getDeclaredField(name);
                  field.setAccessible(true);
                  this.fields.put(name, field);
                  break;
               }
            }

            if (field == null) {
               throw new UnableToFindFieldException(this.clazz, name);
            }
         }

         return field;
      }

      private Class[] toTypes(Object[] objects) {
         if (objects.length == 0) {
            return new Class[0];
         } else {
            Class[] types = new Class[objects.length];

            for(int i = 0; i < objects.length; ++i) {
               if (objects[i] == null) {
                  types[i] = null;
               } else {
                  Class type = objects[i].getClass();
                  if (type == Integer.class) {
                     type = Integer.TYPE;
                  } else if (type == Double.class) {
                     type = Double.TYPE;
                  } else if (type == Boolean.class) {
                     type = Boolean.TYPE;
                  } else if (type == Float.class) {
                     type = Float.TYPE;
                  } else if (type == Long.class) {
                     type = Long.TYPE;
                  } else if (type == Character.class) {
                     type = Character.TYPE;
                  } else if (type == Byte.class) {
                     type = Byte.TYPE;
                  } else if (type == Short.class) {
                     type = Short.TYPE;
                  }

                  types[i] = type;
               }
            }

            return types;
         }
      }

      static {
         try {
            FIELD_MODIFIERS = Field.class.getDeclaredField("modifiers");
            FIELD_MODIFIERS.setAccessible(true);
         } catch (Exception ex) {
            Reflect.error(ex, "Field modifiers field not found");
         }

      }
   }

   static class ConstructorMapKey {
      Class[] types;

      public ConstructorMapKey(Class[] types) {
         this.types = types;
      }

      public int hashCode() {
         return Arrays.hashCode(this.types);
      }

      public boolean equals(Object obj) {
         if (!(obj instanceof AggressiveMethodMapKey)) {
            return false;
         } else {
            AggressiveMethodMapKey other = (AggressiveMethodMapKey)obj;
            if (this.types.length != other.types.length) {
               return false;
            } else {
               for(int i = 0; i < this.types.length; ++i) {
                  if (this.types[i] != other.types[i]) {
                     return false;
                  }
               }

               return true;
            }
         }
      }
   }

   static class MethodMapKey {
      String name;
      int args;

      public MethodMapKey(String name, int args) {
         this.name = name;
         this.args = args;
      }

      public int hashCode() {
         return this.name.hashCode() + this.args;
      }

      public boolean equals(Object obj) {
         if (!(obj instanceof MethodMapKey)) {
            return false;
         } else {
            MethodMapKey other = (MethodMapKey)obj;
            return other.args == this.args && other.name.equals(this.name);
         }
      }
   }

   static class AggressiveMethodMapKey {
      Class[] types;
      String name;

      public AggressiveMethodMapKey(String name, Class[] types) {
         this.name = name;
         this.types = types;
      }

      public int hashCode() {
         int hash = this.name.hashCode();
         hash = 31 * hash + Arrays.hashCode(this.types);
         return hash;
      }

      public boolean equals(Object obj) {
         if (!(obj instanceof AggressiveMethodMapKey)) {
            return false;
         } else {
            AggressiveMethodMapKey other = (AggressiveMethodMapKey)obj;
            if (this.types.length == other.types.length && other.name.equals(this.name)) {
               for(int i = 0; i < this.types.length; ++i) {
                  if (this.types[i] != other.types[i]) {
                     return false;
                  }
               }

               return true;
            } else {
               return false;
            }
         }
      }
   }

   private static class UnableToFindFieldException extends RuntimeException {
      private String fieldName;
      private String className;

      public UnableToFindFieldException(Class clazz, String fieldName) {
         this.fieldName = fieldName;
         this.className = clazz.getName();
      }

      public String getMessage() {
         return this.toString();
      }

      public String toString() {
         return "Unable to find field '" + this.fieldName + "' in class '" + this.className + "'";
      }
   }

   private static class UnableToFindMethodException extends RuntimeException {
      protected String methodName;
      protected String className;
      protected Class[] types;

      public UnableToFindMethodException(Class clazz, String methodName, Class[] types) {
         this.methodName = methodName;
         this.className = clazz.getName();
         this.types = types;
      }

      public String getMessage() {
         return this.toString();
      }

      public String toString() {
         return "Unable to find method '" + this.className + "." + this.methodName + Reflect.classesToString(this.types) + "'";
      }
   }

   private static class UnableToFindConstructorException extends UnableToFindMethodException {
      public UnableToFindConstructorException(Class clazz, Class[] types) {
         super(clazz, (String)null, types);
      }

      public String toString() {
         return "Unable to find constructor '" + this.className + ".<init>" + Reflect.classesToString(this.types) + "'";
      }
   }
}
