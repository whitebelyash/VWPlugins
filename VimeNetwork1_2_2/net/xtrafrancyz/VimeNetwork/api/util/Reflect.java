/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.VimeNetwork.api.util;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.reflect.ReflectionFactory;

public class Reflect {
    private static final Map<String, ClassData> cache = new ConcurrentHashMap<String, ClassData>();

    private Reflect() {
    }

    public static <T extends Enum<?>> T addEnum(Class<T> enumType, String name, Object ... args) {
        try {
            ClassData<T> data = Reflect.getClass(enumType);
            Field field = null;
            try {
                field = data.findFinalField("$VALUES");
            }
            catch (UnableToFindFieldException ex) {
                try {
                    field = data.findFinalField("ENUM$VALUES");
                }
                catch (UnableToFindFieldException unableToFindFieldException) {
                    // empty catch block
                }
            }
            if (field == null) {
                int flags = 4120;
                String valueType = "[L" + enumType.getName().replace('.', '/') + ";";
                for (Field f : enumType.getDeclaredFields()) {
                    if ((f.getModifiers() & flags) != flags || !f.getType().getName().replace('.', '/').equals(valueType)) continue;
                    field = f;
                    field.setAccessible(true);
                    ClassData.FIELD_MODIFIERS.set(field, field.getModifiers() & 0xFFFFFFEF);
                    break;
                }
            }
            if (field == null) {
                throw new UnableToFindFieldException(enumType, "$VALUES");
            }
            Enum[] prev = (Enum[])field.get(null);
            ArrayList<Enum> values = new ArrayList<Enum>(Arrays.asList(prev));
            Object[] params = new Object[args.length + 2];
            params[0] = name;
            params[1] = values.size();
            System.arraycopy(args, 0, params, 2, args.length);
            ReflectionFactory rFactory = ReflectionFactory.getReflectionFactory();
            Enum newValue = (Enum)rFactory.newConstructorAccessor(data.findConstructor(params)).newInstance(params);
            values.add(newValue);
            field.set(null, values.toArray((Enum[])Array.newInstance(enumType, 0)));
            Reflect.setFinal(Class.class, enumType, "enumConstants", null);
            Reflect.setFinal(Class.class, enumType, "enumConstantDirectory", null);
            return (T)newValue;
        }
        catch (Exception e) {
            Reflect.error(e, "addEnum error");
            return null;
        }
    }

    public static <E> E construct(Class<E> clazz, Object ... args) {
        try {
            return Reflect.getClass(clazz).construct(args);
        }
        catch (Exception e) {
            Reflect.error(e, "Constructor error");
            return null;
        }
    }

    public static <E> E get(Class clazz, String field) {
        try {
            return (E)Reflect.getClass(clazz).get(null, field);
        }
        catch (Exception e) {
            Reflect.error(e, "Get static field error");
            return null;
        }
    }

    public static <R> R get(Object instance, String field) {
        try {
            return (R)Reflect.getClass(instance.getClass()).get(instance, field);
        }
        catch (Exception e) {
            Reflect.error(e, "Get field error");
            return null;
        }
    }

    public static <T, E> E get(Class<T> clazz, T instance, String field) {
        try {
            return (E)Reflect.getClass(clazz).get(instance, field);
        }
        catch (Exception e) {
            Reflect.error(e, "Get field error");
            return null;
        }
    }

    public static void set(Class clazz, String field, Object value) {
        try {
            Reflect.getClass(clazz).set(null, field, value);
        }
        catch (Exception e) {
            Reflect.error(e, "Set static field error");
        }
    }

    public static void set(Object instance, String field, Object value) {
        try {
            Reflect.getClass(instance.getClass()).set(instance, field, value);
        }
        catch (Exception e) {
            Reflect.error(e, "Set field error");
        }
    }

    public static <T> void set(Class<T> clazz, T instance, String field, Object value) {
        try {
            Reflect.getClass(clazz).set(instance, field, value);
        }
        catch (Exception e) {
            Reflect.error(e, "Set field error");
        }
    }

    public static void setFinal(Class clazz, String field, Object value) {
        try {
            Reflect.getClass(clazz).setFinal(null, field, value);
        }
        catch (Exception e) {
            Reflect.error(e, "Set static final field error");
        }
    }

    public static void setFinal(Object instance, String field, Object value) {
        try {
            Reflect.getClass(instance.getClass()).setFinal(instance, field, value);
        }
        catch (Exception e) {
            Reflect.error(e, "Set final field error");
        }
    }

    public static <T> void setFinal(Class<T> clazz, T instance, String field, Object value) {
        try {
            Reflect.getClass(clazz).setFinal(instance, field, value);
        }
        catch (Exception e) {
            Reflect.error(e, "Set final field error");
        }
    }

    public static <E> E invoke(Class clazz, String method, Object ... args) {
        try {
            return (E)Reflect.getClass(clazz).invoke(null, method, args);
        }
        catch (Throwable e) {
            Reflect.error(e, "Invoke static error");
            return null;
        }
    }

    public static <E> E invoke(Object instance, String method, Object ... args) {
        try {
            return (E)Reflect.getClass(instance.getClass()).invoke(instance, method, args);
        }
        catch (Throwable e) {
            Reflect.error(e, "Invoke error");
            return null;
        }
    }

    public static <T, E> E invoke(Class<T> clazz, T instance, String method, Object ... args) {
        try {
            return (E)Reflect.getClass(clazz).invoke(instance, method, args);
        }
        catch (Throwable e) {
            Reflect.error(e, "Invoke error");
            return null;
        }
    }

    public static <T> boolean isConstructorExist(Class<T> clazz, Class ... args) {
        return Reflect.findConstructor(clazz, args) != null;
    }

    public static <T> boolean isMethodExist(Class<T> clazz, String method, Class ... args) {
        return Reflect.findMethod(clazz, method, args) != null;
    }

    public static <T> boolean isFieldExist(Class<T> clazz, String field) {
        return Reflect.findField(clazz, field) != null;
    }

    public static <T> Constructor<T> findConstructor(Class<T> clazz, Class ... args) {
        try {
            return Reflect.getClass(clazz).findConstructor0(args);
        }
        catch (Exception ignored) {
            return null;
        }
    }

    public static <T> Method findMethod(Class<T> clazz, String method, Class ... args) {
        try {
            return Reflect.getClass(clazz).findMethod0(method, args);
        }
        catch (Exception ignored) {
            return null;
        }
    }

    public static <T> Field findField(Class<T> clazz, String field) {
        try {
            return Reflect.getClass(clazz).findField(field);
        }
        catch (Exception ignored) {
            return null;
        }
    }

    public static <T> Field findFinalField(Class<T> clazz, String field) {
        try {
            return Reflect.getClass(clazz).findFinalField(field);
        }
        catch (Exception ignored) {
            return null;
        }
    }

    public static Class<?> findClass(String name) {
        try {
            return Class.forName(name);
        }
        catch (ClassNotFoundException ignored) {
            return null;
        }
    }

    public static void setAggressiveMethodsOverloading(Class clazz, boolean flag) {
        ClassData data = Reflect.getClass(clazz);
        if (data.aggressiveOverloading != flag) {
            data.aggressiveOverloading = flag;
            data.methods.clear();
        }
    }

    public static MethodHandles.Lookup lookup() {
        return (MethodHandles.Lookup)Reflect.get(MethodHandles.Lookup.class, "IMPL_LOOKUP");
    }

    private static <T> ClassData<T> getClass(Class<T> clazz) {
        ClassData<T> data = cache.get(clazz.getName());
        if (data == null) {
            data = new ClassData<T>(clazz);
            cache.put(clazz.getName(), data);
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
        }
        StringBuilder b = new StringBuilder();
        b.append('(');
        int i = 0;
        while (true) {
            b.append(classes[i].getName());
            if (i == iMax) {
                return b.append(')').toString();
            }
            b.append(',');
            ++i;
        }
    }

    private static class UnableToFindConstructorException
    extends UnableToFindMethodException {
        public UnableToFindConstructorException(Class clazz, Class[] types) {
            super(clazz, null, types);
        }

        @Override
        public String toString() {
            return "Unable to find constructor '" + this.className + ".<init>" + Reflect.classesToString(this.types) + "'";
        }
    }

    private static class UnableToFindMethodException
    extends RuntimeException {
        protected String methodName;
        protected String className;
        protected Class[] types;

        public UnableToFindMethodException(Class clazz, String methodName, Class[] types) {
            this.methodName = methodName;
            this.className = clazz.getName();
            this.types = types;
        }

        @Override
        public String getMessage() {
            return this.toString();
        }

        @Override
        public String toString() {
            return "Unable to find method '" + this.className + "." + this.methodName + Reflect.classesToString(this.types) + "'";
        }
    }

    private static class UnableToFindFieldException
    extends RuntimeException {
        private String fieldName;
        private String className;

        public UnableToFindFieldException(Class clazz, String fieldName) {
            this.fieldName = fieldName;
            this.className = clazz.getName();
        }

        @Override
        public String getMessage() {
            return this.toString();
        }

        @Override
        public String toString() {
            return "Unable to find field '" + this.fieldName + "' in class '" + this.className + "'";
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
            }
            AggressiveMethodMapKey other = (AggressiveMethodMapKey)obj;
            if (this.types.length != other.types.length || !other.name.equals(this.name)) {
                return false;
            }
            for (int i = 0; i < this.types.length; ++i) {
                if (this.types[i] == other.types[i]) continue;
                return false;
            }
            return true;
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
            }
            MethodMapKey other = (MethodMapKey)obj;
            return other.args == this.args && other.name.equals(this.name);
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
            }
            AggressiveMethodMapKey other = (AggressiveMethodMapKey)obj;
            if (this.types.length != other.types.length) {
                return false;
            }
            for (int i = 0; i < this.types.length; ++i) {
                if (this.types[i] == other.types[i]) continue;
                return false;
            }
            return true;
        }
    }

    static class ClassData<K> {
        private static Field FIELD_MODIFIERS = null;
        private final Class<K> clazz;
        private final Map<String, Field> fields = new HashMap<String, Field>();
        private final Map<Object, Method> methods = new HashMap<Object, Method>();
        private final Map<Object, Constructor<K>> constructors = new HashMap<Object, Constructor<K>>();
        boolean aggressiveOverloading = false;

        public ClassData(Class<K> clazz) {
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

        Object invoke(Object instance, String method, Object ... args) throws Throwable {
            return this.findMethod(method, args).invoke(instance, args);
        }

        K construct(Object ... args) throws Exception {
            return this.findConstructor(args).newInstance(args);
        }

        Constructor<K> findConstructor(Object ... args) {
            return this.findConstructor0(this.toTypes(args));
        }

        Constructor<K> findConstructor0(Class ... types) {
            ConstructorMapKey mapped = new ConstructorMapKey(types);
            Constructor<Object> con = this.constructors.get(mapped);
            if (con == null) {
                block0: for (Constructor<?> c : this.clazz.getDeclaredConstructors()) {
                    Class<?>[] ptypes = c.getParameterTypes();
                    if (ptypes.length != types.length) continue;
                    for (int i = 0; i < ptypes.length; ++i) {
                        if (types[i] != null && ptypes[i] != types[i] && !ptypes[i].isAssignableFrom(types[i])) continue block0;
                    }
                    con = c;
                    con.setAccessible(true);
                    this.constructors.put(mapped, con);
                    break;
                }
                if (con == null) {
                    throw new UnableToFindConstructorException(this.clazz, types);
                }
            }
            return con;
        }

        Method findMethod(String name, Object ... args) {
            Object mapped;
            Class[] types = null;
            if (this.aggressiveOverloading) {
                types = this.toTypes(args);
                mapped = new AggressiveMethodMapKey(name, types);
            } else {
                mapped = new MethodMapKey(name, args.length);
            }
            Method method = this.methods.get(mapped);
            if (method == null) {
                if (types == null) {
                    types = this.toTypes(args);
                }
                if ((method = this.fastFindMethod(name, types)) == null) {
                    throw new UnableToFindMethodException(this.clazz, name, types);
                }
                this.methods.put(mapped, method);
            }
            return method;
        }

        Method findMethod0(String name, Class ... types) {
            Object mapped = this.aggressiveOverloading ? new AggressiveMethodMapKey(name, types) : new MethodMapKey(name, types.length);
            Method method = this.methods.get(mapped);
            if (method == null) {
                method = this.fastFindMethod(name, types);
                if (method == null) {
                    throw new UnableToFindMethodException(this.clazz, name, types);
                }
                this.methods.put(mapped, method);
            }
            return method;
        }

        private Method fastFindMethod(String name, Class ... types) {
            Method method = null;
            name = name.intern();
            Class<K> clazz0 = this.clazz;
            do {
                block1: for (Method m : clazz0.getDeclaredMethods()) {
                    Class<?>[] ptypes;
                    if (name != m.getName() || (ptypes = m.getParameterTypes()).length != types.length) continue;
                    for (int i = 0; i < ptypes.length; ++i) {
                        if (types[i] != null && ptypes[i] != types[i] && !ptypes[i].isAssignableFrom(types[i])) continue block1;
                    }
                    method = m;
                    break;
                }
                if (method == null) continue;
                method.setAccessible(true);
                break;
            } while ((clazz0 = clazz0.getSuperclass()) != null);
            return method;
        }

        Field findFinalField(String name) throws Exception {
            Field field = this.findField(name);
            FIELD_MODIFIERS.set(field, field.getModifiers() & 0xFFFFFFEF);
            return field;
        }

        Field findField(String name) {
            Field field = this.fields.get(name);
            if (field == null) {
                for (Class<K> clazz0 = this.clazz; clazz0 != null; clazz0 = clazz0.getSuperclass()) {
                    try {
                        field = clazz0.getDeclaredField(name);
                        field.setAccessible(true);
                        this.fields.put(name, field);
                        break;
                    }
                    catch (Exception e) {
                        continue;
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
            }
            Class[] types = new Class[objects.length];
            for (int i = 0; i < objects.length; ++i) {
                if (objects[i] == null) {
                    types[i] = null;
                    continue;
                }
                Class<Object> type = objects[i].getClass();
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
            return types;
        }

        static {
            try {
                FIELD_MODIFIERS = Field.class.getDeclaredField("modifiers");
                FIELD_MODIFIERS.setAccessible(true);
            }
            catch (Exception ex) {
                Reflect.error(ex, "Field modifiers field not found");
            }
        }
    }
}

