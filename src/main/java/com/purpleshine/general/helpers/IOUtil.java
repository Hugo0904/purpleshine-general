package com.purpleshine.general.helpers;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class IOUtil {
    
    static public final String ANSI_RESET = "\u001B[0m";
    static public final String ANSI_BLACK = "\u001B[30m";
    static public final String ANSI_RED = "\u001B[31m";
    static public final String ANSI_GREEN = "\u001B[32m";
    static public final String ANSI_YELLOW = "\u001B[33m";
    static public final String ANSI_BLUE = "\u001B[34m";
    static public final String ANSI_PURPLE = "\u001B[35m";
    static public final String ANSI_CYAN = "\u001B[36m";
    static public final String ANSI_WHITE = "\u001B[37m";
	
    /**
     * 動態建立class
     * 
     * @param className
     * @param params
     * @return
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    @SuppressWarnings("unchecked")
    static public <T> T createInstance(final String className, Object ...params) throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException { 
        final Class<T> clazz = (Class<T>) Class.forName(className);
        final Class<?>[] types = Arrays.asList(params).stream()
                .map(i -> i.getClass())
                .collect(Collectors.toList())
                .toArray(new Class<?>[params.length]);
        
        final Constructor<T> ctor = clazz.getConstructor(types);
        return ctor.newInstance(params);
    }
    
    static public String getExceptionDetail(Exception e) {
        final StringBuffer stringBuffer = new StringBuffer(e.toString() + "\n");
        StackTraceElement[] messages = e.getStackTrace();
        int length = messages.length;
        for (int i = 0; i < length; i++) {
            stringBuffer.append("\t" + messages[i].toString() + "\n");
        }
        return stringBuffer.toString();
    }
    
	/**
	 * 將Exception輸出String
	 * @param e - Exception
	 * @return String
	 */
	static public String traceToString(Throwable e) {
		return String.format("%s\nException: %s\nStackTrace: %s",
				e.getMessage(),
				e.getClass().getName(),
				Stream.of(e.getStackTrace())
					.map(Object::toString)
					.collect(Collectors.joining("\n")));
	}
	
	/**
	 * 清除command line or console
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	static public void clearConsole() throws IOException, InterruptedException {
		String os = System.getProperty("os.name");
        if (os.contains("Windows"))
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        else
            Runtime.getRuntime().exec("clear");
	}
	
	/**
	 * 映射方法
	 * @param instance - 方法的class名稱
	 * @param properties - 方法名稱
	 * @param type - 該方法是什麼型別
	 * @param value - 值
	 * @throws Exception
	 */
	static public boolean inSetValue(Object instance, String properties, String type, String value) {
		Objects.requireNonNull(instance);
		Objects.requireNonNull(properties);
		Objects.requireNonNull(type);
		Objects.requireNonNull(value);
		
		Class<?> typeClazz = null;
		Object typeValue = null;
		switch (type.toUpperCase()) {
		case "INT":
			typeClazz = int.class;
			typeValue = Integer.parseInt(value);
			break;
		case "FLOAT":
			typeClazz = float.class;
			typeValue = Float.parseFloat(value);
			break;
		case "DOUBLE":
			typeClazz = double.class;
			typeValue = Double.parseDouble(value);
			break;
		case "STRING":
		case "SIMPLESTRINGPROPERTY":
			typeClazz = String.class;
			typeValue = value;
			break;
		case "LONG":
		case "SIMPLELONGPROPERTY":
			typeClazz = long.class;
			typeValue = Long.parseLong(value);
			break;
		case "BOOLEAN":
			typeClazz = boolean.class;
			typeValue = Boolean.parseBoolean(value);
			break;
		}
		
		return inSetValue(instance, properties, typeClazz, typeValue);
	}
	
	static public boolean inSetValue(Object instance, String properties, Class<?> typeClazz, Object typeValue) {
		Objects.requireNonNull(instance);
		Objects.requireNonNull(properties);
		Objects.requireNonNull(typeClazz);
		
		Class<?> curc = instance.getClass();
		do {
			final String methodName = asserSetMethodName(properties);
			try {
				final Method method = Objects.requireNonNull(curc).getDeclaredMethod(methodName, typeClazz);
				method.invoke(instance, typeValue);
				return true;
			} catch (Exception e) {
				if (curc != null)
					curc = curc.getSuperclass();
				else {
					System.out.println(instance.getClass().getName() + " = " + methodName + " = " + typeClazz);
					return false;
				}
			}
		} while (true);
	}
	
	/**
	 * 取得field的set method
	 * @param attributeName - field名稱
	 * @return - set method name
	 */
	static private String asserSetMethodName(String attributeName) {
		char[] ch = attributeName.toCharArray();
		return "set" + String.valueOf(Character.toUpperCase(ch[0]) + attributeName.substring(1));
	}
}
