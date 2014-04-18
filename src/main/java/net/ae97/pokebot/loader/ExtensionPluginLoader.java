package net.ae97.pokebot.loader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import net.ae97.pokebot.extension.Extension;

public class ExtensionPluginLoader {

    private final Map<String, Class<?>> classes = new HashMap<>();
    private final Map<String, ExtensionLoader> loaders = new LinkedHashMap<>();

    public Set<Extension> findExtensions(File listener) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        if (!listener.exists()) {
            throw new FileNotFoundException("File " + listener.getPath() + " not found");
        }
        URL[] urls = new URL[]{listener.toURI().toURL()};
        ExtensionLoader loader = new ExtensionLoader(this, urls, getClass().getClassLoader());
        if (listener.getName().endsWith(".jar")) {
            JarFile jar = new JarFile(listener);
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.isDirectory()) {
                    continue;
                }
                if (entry.getName().startsWith("META-INF")) {
                    continue;
                }
                try {
                    classes.put(entry.getName().replace('/', '.').replace(".class", ""), loader.findClass(entry.getName().replace('/', '.').replace(".class", ""), true));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace(System.err);
                }
            }
        }
        Set<Class<? extends Extension>> mainClasses = loader.findMainClasses();
        Set<Extension> extensionList = new HashSet<>();
        for (Class<? extends Extension> mainClass : mainClasses) {
            loaders.put(mainClass.getName(), loader);
            Extension result = mainClass.newInstance();
            extensionList.add(result);
        }
        return extensionList;
    }

    protected Class<?> getClassByName(String name) {
        if (name == null) {
            return null;
        }
        Class<?> cachedClass = classes.get(name);
        if (cachedClass == null) {
            for (String current : loaders.keySet()) {
                ExtensionLoader loader = loaders.get(current);
                try {
                    cachedClass = loader.findClass(name, false);
                    if (cachedClass != null) {
                        return cachedClass;
                    }
                } catch (ClassNotFoundException e) {
                }
            }
        }
        return null;
    }

    protected void setClass(String name, Class<?> cl) {
        classes.put(name, cl);
    }
}
