package cn.org.rapid_framework.generator.util;


public class ClassHelper {

	public static Object newInstance(Class<?> c) {
		try {
			return c.newInstance();
		} catch (Exception e) {
			throw new IllegalArgumentException(
					"cannot new instance with class:" + c.getName(), e);
		}
	}

	public static Object newInstance(String className) {
		try {
			return newInstance(Class.forName(className));
		} catch (Exception e) {
			throw new IllegalArgumentException(
					"cannot new instance with className:" + className, e);
		}
	}
	
	/**
	 * Return the default ClassLoader to use: typically the thread context
	 * ClassLoader, if available; the ClassLoader that loaded the ClassUtils
	 * class will be used as fallback.
	 * <p>Call this method if you intend to use the thread context ClassLoader
	 * in a scenario where you absolutely need a non-null ClassLoader reference:
	 * for example, for class path resource loading (but not necessarily for
	 * <code>Class.forName</code>, which accepts a <code>null</code> ClassLoader
	 * reference as well).
	 * @return the default ClassLoader (never <code>null</code>)
	 * @see java.lang.Thread#getContextClassLoader()
	 */
	public static ClassLoader getDefaultClassLoader() {
		ClassLoader cl = null;
		try {
			cl = Thread.currentThread().getContextClassLoader();
		}
		catch (Throwable ex) {
			// Cannot access thread context ClassLoader - falling back to system class loader...
		}
		if (cl == null) {
			// No thread context class loader -> use class loader of this class.
			cl = ClassHelper.class.getClassLoader();
		}
		return cl;
	}
}
