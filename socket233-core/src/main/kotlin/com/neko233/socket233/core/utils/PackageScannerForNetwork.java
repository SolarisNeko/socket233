package com.neko233.socket233.core.utils;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public final class PackageScannerForNetwork {
    private PackageScannerForNetwork() {
    }

    public static Set<Class<?>> scanClass(String packageName, boolean recursive, Class<?> parentClass) {
        if (parentClass == null) {
            return Collections.emptySet();
        } else {
            parentClass.getClass();
            return scanClass(packageName, recursive, parentClass::isAssignableFrom);
        }
    }

    public static Set<Class<?>> scanClassRecursive(String packageName, Class<?> parentClass) {
        return scanClass(packageName, true, parentClass);
    }

    public static Set<Class<?>> scanClass(String packageName, boolean recursive) {
        return scanClass(packageName, recursive, (clazz) -> {
            return true;
        });
    }

    public static Set<Class<?>> scanClass(Class<?> appClass) {
        return scanClass(appClass.getPackage().getName(), true, (clazz) -> {
            return true;
        });
    }

    public static Set<Class<?>> scanClass(Class<?> appClass, boolean recursive, ClassFilterApi filter) {
        return scanClass(appClass.getPackage().getName(), recursive, filter);
    }

    public static Set<Class<?>> scanClass(String packageName, boolean recursive, ClassFilterApi filter) {
        if (packageName != null && !packageName.isEmpty()) {
            String packagePath = packageName.replace('.', '/');
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            Set<Class<?>> resultSet = new HashSet();

            try {
                Enumeration<URL> urlEnum = cl.getResources(packagePath);

                label40:
                while (true) {
                    String protocol;
                    Set classFileSet;
                    do {
                        if (!urlEnum.hasMoreElements()) {
                            break label40;
                        }

                        URL currUrl = (URL) urlEnum.nextElement();
                        protocol = currUrl.getProtocol();
                        classFileSet = null;
                        File file = new File(currUrl.getFile());
                        if ("FILE".equalsIgnoreCase(protocol)) {
                            classFileSet = scanClassFromDir(file, packageName, recursive, filter);
                            break;
                        }
                    } while ("jar".equalsIgnoreCase(protocol));

                    if (classFileSet != null) {
                        resultSet.addAll(classFileSet);
                    }
                }
            } catch (Exception var12) {
                System.err.println("[PackageScanner] 包扫描 package 在 class 文件中报错. packageName = " + packageName);
                var12.printStackTrace();
            }

            try {
                Set<Class<?>> jarClassSet = scanClassWithJar(packageName, recursive, filter);
                if (jarClassSet != null) {
                    resultSet.addAll(jarClassSet);
                }
            } catch (Exception var11) {
                System.err.println("[PackageScanner] 包扫描 package 在 jar 文件中报错. packageName = " + packageName);
                var11.printStackTrace();
            }

            return resultSet;
        } else {
            return null;
        }
    }

    public static Set<Class<?>> scanClassWithJar(String packageName, boolean recursive, ClassFilterApi filter) {
        if (packageName != null && !packageName.isEmpty()) {
            String packagePath = packageName.replace('.', '/');
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            Set<Class<?>> resultSet = new HashSet();

            try {
                Enumeration<URL> urlEnum = cl.getResources(packagePath);

                while (urlEnum.hasMoreElements()) {
                    URL currUrl = (URL) urlEnum.nextElement();
                    String protocol = currUrl.getProtocol();
                    Set<Class<?>> tmpSet = null;
                    if ("FILE".equalsIgnoreCase(protocol)) {
                        tmpSet = scanClassFromDir(new File(currUrl.getFile()), packageName, recursive, filter);
                    } else if ("JAR".equalsIgnoreCase(protocol)) {
                        String fileStr = currUrl.getFile();
                        if (fileStr.startsWith("file:")) {
                            fileStr = fileStr.substring(5);
                        }

                        if (fileStr.lastIndexOf(33) > 0) {
                            fileStr = fileStr.substring(0, fileStr.lastIndexOf(33));
                        }

                        tmpSet = scanClassFromJar(new File(fileStr), packageName, recursive, filter);
                    }

                    if (tmpSet != null) {
                        resultSet.addAll(tmpSet);
                    }
                }

                return resultSet;
            } catch (Exception var11) {
                throw new RuntimeException(var11);
            }
        } else {
            return null;
        }
    }

    public static Set<Class<?>> scanClassFromDir(File dirFile, String packageName, boolean recursive, ClassFilterApi filter) {
        if (dirFile.exists() && dirFile.isDirectory()) {
            File[] subFileArr = dirFile.listFiles();
            if (subFileArr != null && subFileArr.length > 0) {
                Queue<File> fileQ = new LinkedList(Arrays.asList(subFileArr));
                Set<Class<?>> resultSet = new HashSet();

                while (true) {
                    while (!fileQ.isEmpty()) {
                        File currFile = (File) fileQ.poll();
                        if (currFile.isDirectory() && recursive) {
                            subFileArr = currFile.listFiles();
                            if (subFileArr != null && subFileArr.length > 0) {
                                fileQ.addAll(Arrays.asList(subFileArr));
                            }
                        } else if (currFile.isFile() && currFile.getName().endsWith(".class")) {
                            String clazzName = currFile.getAbsolutePath();
                            clazzName = clazzName.substring(dirFile.getAbsolutePath().length(), clazzName.lastIndexOf(46));
                            clazzName = clazzName.replace('\\', '/');
                            clazzName = trimLeft(clazzName, "/");
                            clazzName = join(clazzName.split("/"), ".");
                            clazzName = packageName + "." + clazzName;

                            try {
                                Class<?> clazzObj = Class.forName(clazzName);
                                if (null == filter || !filter.isNotNeed(clazzObj)) {
                                    resultSet.add(clazzObj);
                                }
                            } catch (Exception var10) {
                                throw new RuntimeException(var10);
                            }
                        }
                    }

                    return resultSet;
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    private static Set<Class<?>> scanClassFromJar(File jarFilePath, String packageName, boolean recursive, ClassFilterApi filter) {
        if (jarFilePath != null && !jarFilePath.isDirectory()) {
            Set<Class<?>> resultSet = new HashSet();

            try {
                JarInputStream jarIn = new JarInputStream(Files.newInputStream(jarFilePath.toPath()));

                while (true) {
                    Class clazzObj;
                    do {
                        String entryName;
                        String tmpStr;
                        do {
                            do {
                                JarEntry entry;
                                do {
                                    if ((entry = jarIn.getNextJarEntry()) == null) {
                                        jarIn.close();
                                        return resultSet;
                                    }
                                } while (entry.isDirectory());

                                entryName = entry.getName();
                            } while (!entryName.endsWith(".class"));

                            if (recursive) {
                                break;
                            }

                            tmpStr = entryName.substring(0, entryName.lastIndexOf(47));
                            tmpStr = join(tmpStr.split("/"), ".");
                        } while (!packageName.equals(tmpStr));

                        tmpStr = entryName.substring(0, entryName.lastIndexOf(46));
                        tmpStr = join(tmpStr.split("/"), ".");
                        clazzObj = Class.forName(tmpStr);
                    } while (null != filter && !filter.isNeed(clazzObj));

                    resultSet.add(clazzObj);
                }
            } catch (Exception var10) {
                throw new RuntimeException(var10);
            }
        } else {
            return null;
        }
    }

    private static String join(String[] strArr, String joiner) {
        if (null != strArr && strArr.length > 0) {
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < strArr.length; ++i) {
                if (i > 0) {
                    sb.append(joiner);
                }

                sb.append(strArr[i]);
            }

            return sb.toString();
        } else {
            return "";
        }
    }

    private static String trimLeft(String src, String trimStr) {
        if (null != src && !src.isEmpty()) {
            if (null != trimStr && !trimStr.isEmpty()) {
                if (src.equals(trimStr)) {
                    return "";
                } else {
                    while (src.startsWith(trimStr)) {
                        src = src.substring(trimStr.length());
                    }

                    return src;
                }
            } else {
                return src;
            }
        } else {
            return "";
        }
    }

    @FunctionalInterface
    public interface ClassFilterApi {
        boolean isNeed(Class<?> var1);

        default boolean isNotNeed(Class<?> clazz) {
            return !this.isNeed(clazz);
        }
    }
}
