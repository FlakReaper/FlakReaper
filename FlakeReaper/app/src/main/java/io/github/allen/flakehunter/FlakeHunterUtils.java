package io.github.allen.flakehunter;


import android.os.Environment;
import android.os.Message;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.robv.android.xposed.XposedBridge;

public class FlakeHunterUtils {

    private static final int SEED = 131; // 31 131 1313 13131 131313 etc..

    /**
     * 所有的set、get方法信息
     */
    public static Map<String, List<UIOperation>> uiOperationListMap;

    /**
     * 日志-线程池
     */
    private static ExecutorService logExecutor = Executors.newSingleThreadExecutor();
    private static BufferedWriter bw;

    private static BufferedReader br;

    // 创建日期格式
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");

    /**
     * 基础类型的class
     */
    private static Map<String, Class<?>> baseTypes;

    private static Set<String> staticIdSet = new HashSet<>();

    private static Set<String> packageNameSet = new HashSet<>();

    static {
        try {
            File logFile = new File(Environment.getExternalStorageDirectory(), getTimeNow() + ".txt");
            bw = new BufferedWriter(new FileWriter(logFile, true));

            File staticIdFile = new File(Environment.getExternalStorageDirectory(),"static_id_list.txt");
            br  = new BufferedReader(new FileReader(staticIdFile));

            String line;
            while ((line = br.readLine()) != null) {
                staticIdSet.add(line);
            }

            packageNameSet.add("io.github.marktony.espresso");
            packageNameSet.add("fr.neamar.kiss.debug");
            packageNameSet.add("cs.ualberta.ca.medlog");

        } catch (IOException e) {
        }

        baseTypes = new HashMap<>();
        baseTypes.put("boolean", boolean.class);
        baseTypes.put("byte", byte.class);
        baseTypes.put("short", short.class);
        baseTypes.put("int", int.class);
        baseTypes.put("long", long.class);
        baseTypes.put("float", float.class);
        baseTypes.put("double", double.class);
        baseTypes.put("char", char.class);
        baseTypes.put("void", void.class);

        uiOperationListMap = new HashMap<>();
        uiOperationListMap.put("get", analyseFile("get.txt"));
        uiOperationListMap.put("set", analyseFile("set.txt"));
    }

    /**
     * 字符串hash
     *
     * @param str
     * @return
     */
    public static int hash(String str) {
        int hash = 0;
        for (int i = 0; i < str.length(); i++) {
            hash = (hash * SEED) + str.charAt(i);
        }
        return (hash & 0x7FFFFFFF);
    }

    public static int hash(Message message, long when) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append(message.what)
                .append(hash(message.obj))
                .append(hash(message.getCallback()))
                .append(message.arg1)
                .append(message.arg2)
                .append(message.replyTo)
                .append(message.getData().hashCode())
                .append(message.hashCode())
                .append(when)
                .append(hash(message.getTarget()));
        return hash(stringBuilder.toString());
    }

    public static int hash(Message message) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append(message.what)
                .append(hash(message.obj))
                .append(hash(message.getCallback()))
                .append(message.arg1)
                .append(message.arg2)
                .append(message.replyTo)
                .append(message.getData().hashCode())
                .append(message.hashCode())
                .append(message.getWhen())
                .append(hash(message.getTarget()));
        return hash(stringBuilder.toString());
    }

    private static int hash(Object object) {
        if (Objects.isNull(object)) {
            return 0;
        }
        return object.hashCode();
    }


    /**
     * 异步写日志
     *
     * @param info
     */
    public static void log(String info) {
        logExecutor.submit(() -> {
            try {
                bw.write(info);
                bw.newLine(); // 这将添加一个新行
            } catch (IOException e) {
                XposedBridge.log(e.toString());
            }
        });
    }

    /**
     * 解析set/get文件
     *
     * @param filePath
     * @return
     */
    private static List<UIOperation> analyseFile(String filePath) {
        List<UIOperation> content = new ArrayList<>();
        File file = new File(Environment.getExternalStorageDirectory(), filePath);
        try {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isEmpty()) continue;
                String[] infos = line.trim().split(" ");
                UIOperation uiOperation = new UIOperation();
                uiOperation.setOperationClass(Class.forName(infos[0]));
                uiOperation.setMethodName(infos[1]);
                uiOperation.setReturnType(getClass(infos[2]));
                if (uiOperation.getReturnType() == null)
                    continue;
                // 下标3之后都是paramType
                List<Class<?>> paramTypes = new ArrayList<>();
                for (int i = 3; i < infos.length; i++) {
                    if ("".equals(infos[i])) {
                        continue;
                    }
                    paramTypes.add(getClass(infos[i]));
                }
                uiOperation.setParamTypes(paramTypes);
                content.add(uiOperation);
//                XposedBridge.log(uiOperation.toString());
            }
            br.close();
            isr.close();
            fis.close();
        } catch (IOException e) {
            XposedBridge.log(e.toString());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return content;
    }

    public static Set<String> getStaticIdSet() {
        return staticIdSet;
    }

    private static Class<?> getClass(String name) throws ClassNotFoundException {
        Class<?> clazz;
        if (name.contains("String[]")) {
            clazz = Class.forName("[Ljava.lang.String;");
            ;
        } else if (name.contains("int[]")) {
            clazz = Class.forName("[I");
            ;
        } else if (name.contains("boolean[]")) {
            clazz = Class.forName("[Z");
            ;
        } else if (name.contains("byte[]")) {
            clazz = Class.forName("[B");
            ;
        } else if (name.contains("char[]")) {
            clazz = Class.forName("[C");
            ;
        } else if (name.contains("float[]")) {
            clazz = Class.forName("[F");
            ;
        } else if (name.contains("double[]")) {
            clazz = Class.forName("[D");
            ;
        } else if (name.contains("long[]")) {
            clazz = Class.forName("[J");
            ;
        } else if (name.contains("short[]")) {
            clazz = Class.forName("[S");
            ;
        } else if (baseTypes.containsKey(name)) {
            clazz = baseTypes.get(name);
        } else if (name.contains("[]")) {
            String classNameWithoutArrayTag = name.replace("[]", "");
            Object o = Array.newInstance(Class.forName(classNameWithoutArrayTag), 1);
            clazz = o.getClass();
        } else {
            clazz = Class.forName(name);
        }
        return clazz;
    }

    public static String getTimeNow() {
        return String.valueOf(System.nanoTime());
    }

    /**
     * 判断obj对象是否有某属性propertyName
     *
     * @param obj
     * @param propertyName
     */
    public static Object getProperty(Object obj, String propertyName) {
        try {
            Class<?> clazz = obj.getClass();
            Field field = clazz.getDeclaredField(propertyName);
            field.setAccessible(true); // 设置属性可访问
            return field.get(obj);
        } catch (NoSuchFieldException | IllegalAccessException | NullPointerException e) {
            return null;

        }
    }
}
