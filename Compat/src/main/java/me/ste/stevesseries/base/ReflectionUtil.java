package me.ste.stevesseries.base;

/**
 * @deprecated For backwards compatibility only.
 */
@Deprecated
public class ReflectionUtil {
    public static final String NMS_VERSION = me.ste.stevesseries.base.util.ReflectionUtil.INSTANCE.getNMS_VERSION();
    public static final String NMS_PACKAGE = me.ste.stevesseries.base.util.ReflectionUtil.INSTANCE.getNMS_PACKAGE();
    public static final String OBC_PACKAGE = me.ste.stevesseries.base.util.ReflectionUtil.INSTANCE.getOBC_PACKAGE();

    private ReflectionUtil() {}

    public static <T> Class<T> resolveClass(String classPackage, String clazz) throws ClassNotFoundException {
        return (Class<T>) me.ste.stevesseries.base.util.ReflectionUtil.INSTANCE.resolveClass(classPackage, clazz);
    }
}
