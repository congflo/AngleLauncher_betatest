package net.congcq.anglelaunch.utils;

import java.util.*;
import java.io.*;

import net.congcq.anglelaunch.*;

public class MCOptionUtils
{
    private static List<String> mLineList;
    
    public static void load() {
        if (mLineList == null) {
            mLineList = new ArrayList<String>();
        } else {
            mLineList.clear();
        }
        
        try {
            BufferedReader reader = new BufferedReader(new FileReader(Tools.DIR_GAME_PROFILE + "/options.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                mLineList.add(line);
            }
            reader.close();
        } catch (IOException e) {
            System.err.println("Could not load options.txt");
            e.printStackTrace();
        }
    }
    
    public static void set(String key, String value) {
        for (int i = 0; i < mLineList.size(); i++) {
            String line = mLineList.get(i);
            if (line.startsWith(key + ":")) {
                mLineList.set(i, key + ":" + value);
                return;
            }
        }
        
        mLineList.add(key + ":" + value);
    }

    public static void setDefault(String key, String value) {
        if (get(key) == null) {
            mLineList.add(key + ":" + value);
        }
    }

    public static String get(String key){
        if (mLineList == null){
            load();
        }
        for (int i = 0; i < mLineList.size(); i++) {
            String line = mLineList.get(i);
            if (line.startsWith(key + ":")) {
                String value = mLineList.get(i);
                return value.substring(value.indexOf(":")+1);
            }
        }
        return null;
    }

    public static void save() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < mLineList.size(); i++) {
            result.append(mLineList.get(i));
            if (i + 1 < mLineList.size()) {
                result.append("\n");
            }
        }
        
        try {
            Tools.write(Tools.DIR_GAME_PROFILE + "/options.txt", result.toString());
        } catch (IOException e) {
            System.err.println("Could not save options.txt");
            e.printStackTrace();
        }
        mLineList = null;
    }
}
