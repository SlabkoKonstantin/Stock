package ru.slayter.stock.strategies;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


public class StrategyLoader 
{
	@SuppressWarnings("rawtypes")
	private Class moduleClass;

	@SuppressWarnings({ "resource" })
	public Strategable load(String type) throws StrategyLoadException {
		moduleClass = null;
		try {
			File file = new File(type);
			if (file.exists() && file.isFile()) {
				Properties props = getPluginProps(file);
				if (props == null)
					throw new IllegalArgumentException("No props file found");

				String pluginClassName = props.getProperty("main.class");
				if (pluginClassName == null || pluginClassName.length() == 0) {
					throw new StrategyLoadException("Missing property main.class");
				}

				URL jarURL = file.toURI().toURL();
				URLClassLoader classLoader = new URLClassLoader(new URL[] { jarURL });
				moduleClass = classLoader.loadClass(pluginClassName);
			} else {
				moduleClass = null;
				throw new StrategyLoadException("File " + type + " is not exists or not a file.");
			}
		} catch (Exception e) {
			throw new StrategyLoadException(e);
		}

		if (moduleClass != null) {
			try {
				return (Strategable) moduleClass.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				throw new StrategyLoadException(e);
			}
		} else {
			throw new StrategyLoadException("Load class "+ type + " return null.");
		}
	}

	private Properties getPluginProps(File file) throws IOException {
		Properties result = null;
		JarFile jar = new JarFile(file);
		try {
			Enumeration<JarEntry> entries = jar.entries();

			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				if (entry.getName().equals("module.properties")) {
					InputStream is = null;
					try {
						is = jar.getInputStream(entry);
						result = new Properties();
						result.load(is);
					} finally {
						if (is != null)
							is.close();
					}
				}
			}
		} finally {
			jar.close();
		}
		return result;
	}
}
