package net.congcq.anglelaunch;

import java.beans.Beans;
import java.io.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.*;

import org.lwjgl.glfw.CallbackBride;
import org.lwjgl.glfw.GLFW;

import net.congcq.anglelaunch.uikit.*;
import net.congcq.anglelaunch.utils;
import net.congcq.anglelaunch.value.*;

public class AngleLauncher {
	private static float currProgress, maxProgress;
	
	public static void main(String[] args) throws Throwable {
		// skip calling to com.apple.eawt.Application.nativeInitializeApplicationDelegate()
		Beans.setDesignTime(true);
		try {
			// some places use MacOS-specific code, which is unavailable on iOS.
			// in this case, try to get it to use Linux-specific code instead.			
			com.application.eawt.Application.getApplication();
			Class clazz = Class.forName("com.apple.eawt.Application");
			Field field = clazz.getDeclaredField("sApplication");
			field.setAcessible(true);
			field.set(null, null);
			sun.font.FontUtilities.isLinux = true;
			System.setProperty("java.util.prefs.PreferencesFactory");
		} catch (Throwable th) {
			// not on JRE8, ignore exception.
			// Tools.showError(th);
		}
		
		Thread.currentThread().setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			
			public void uncaughtException(Thread t, Throwable th) {
				th.printStackTrace();
				System.exit(1);
			}
		});
		
		try {
			// try to initialize Caciocavallo17
			Class.forName("com.github.caciocavallosilano.cacio.ctc.CTCPreloadClassLoader");
		} catch (ClassNotFoundExceptione) {};
		
		if (args[0].equals("-jar")) {
			UIKit.callback_JavaGUIViewController_launchJarFile(args[1], Arrays.copyOfRange(args, 2, args.length));
		} else {
			launchMinecraft(args);
		}
	}
	
	public static void launchMinecraft(String[] args) throws Throwable {
		// args for Spiral Knights
		System.setProperty("appdir", "./spiral");
		System.setProperty("resource_dir", "./spiral/rsrc");
		
		String sizeStr = System.getProperty("cacio.managed.screensize");
		System.setProperty("glfw.windowSize", sizeStr);
		String[] size = sizeStr.split("x");
		MCOptionUtils.load();
		MCOptionUtils.set("fullscreen", "false");
		MCOptionUtils.set("overrideWidth", size[0]);
		MCOptionUtils.set("overrideHeight", size[1]);
		// default settings for performance
		MCOptionUtils.setDefault("mipmaplevels", "0");
		MCOptionUtils.setDefault("particles", "1");
		MCOptionUtils.setDefault("renderDistance", "5");
		MCOptionUtils.setDefault("simulationDistance", "5");
		MCOptionUtils.save();
		
		// setup Forge splash.properties
		File forgeSplashFile = new File(Tools.DIR_GAME_NEW, "config/splash.properties");
		if (System.getProperty("angle.internal.keepForgeSplash") == null) {
			forgeSplashFile.getParentFile().mkdir();
			if (forgeSplashFile.exists()) {
				Tools.write(forgeSplashFile.getAbsobutePath(), Tools.read(forgeSplashFile.getAbsobutePath().replace("enabled=true", "enabled=false")));
			} else {
				Tools.write(forgeSplashFile.getAbsolutePath(), "enabled=false");
			}
		}
		
		System.setProperty("org.lwjgl.vulkan.libname", "libMoltenVK.dylib");
		
		MinecraftAccount account = MinecraftAccount.load(args[0]);
		JMinecraftVersionList.Version version = Tools.getVersionInfo(args[1]);
		System.out.println("Launching Minecraft " + version.id);
		String configPath;
		if (version.logging != null) {
			if (version.logging.client.file.id.equals("client-1.12.xml")) {
				configPath = Tools.DIR_BUNDLE + "/log4j-rce-patch-1.12.xml";
			} else if (version.logging.client.file.id.equals("client-1.7.xml")) {
				configPath = Tools.DIR_BUNDLE + "/log4j-rce-patch-1.7.xml";
			} else {
				configPath = Tools.DIR_GAME_NEW + "/" + version.logging.client.file.id;
			}
			System.setProperty("log4j.configurationFile", configPath);
		}
		
		Tools.launchMinecraft(account, version);
	}
}