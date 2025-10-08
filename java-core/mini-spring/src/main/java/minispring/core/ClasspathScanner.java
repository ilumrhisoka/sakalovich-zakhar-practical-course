package minispring.core;

import java.io.File;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class ClasspathScanner {

    public static Set<Class<?>> findClasses(String packageName) {
        Set<Class<?>> classes = new HashSet<>();
        String packagePath = packageName.replace('.', '/');
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        if (classLoader == null) {
            return classes;
        }

        try {
            URL resource = classLoader.getResource(packagePath);

            if (resource == null) {
                System.err.println("Package not found on classpath: " + packageName);
                return classes;
            }

            File directory = new File(resource.getFile());

            if (directory.exists()) {
                scanDirectory(directory, packageName, classes);
            }

        } catch (Exception e) {
            System.err.println("Error during classpath scanning: " + e.getMessage());
            e.printStackTrace();
        }
        return classes;
    }

    private static void scanDirectory(File directory, String packageName, Set<Class<?>> classes) {
        File[] files = directory.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                scanDirectory(file, packageName + "." + file.getName(), classes);
            } else if (file.getName().endsWith(".class")) {
                String className = file.getName().substring(0, file.getName().length() - 6);
                try {
                    String fullyQualifiedName = packageName + "." + className;
                    Class<?> clazz = Class.forName(fullyQualifiedName);
                    classes.add(clazz);
                } catch (ClassNotFoundException e) {
                }
            }
        }
    }
}