package com.sampleboard.utils;
/*
 *
 *
 *  * Copyright © 2016, Mobilyte Inc. and/or its affiliates. All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions are met:
 *  *
 *  * - Redistributions of source code must retain the above copyright
 *  *    notice, this list of conditions and the following disclaimer.
 *  *
 *  * - Redistributions in binary form must reproduce the above copyright
 *  * notice, this list of conditions and the following disclaimer in the
 *  * documentation and/or other materials provided with the distribution.
 *
 * /
 */
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


/**
 * Created by Mobilyte on 29/3/16.
 */

public class SharedPreferencesHandler {

	public static SharedPreferences getSharedPreferences(Context ctx) {
		return ctx.getSharedPreferences("dezi_app", Context.MODE_ENABLE_WRITE_AHEAD_LOGGING);
	}

	public static void clearAll(Context ctx){
		Editor editor = getSharedPreferences(ctx).edit();
		editor.clear().apply();
	}

	public static void setStringValues(Context ctx, String key,
			String DataToSave) {
//		Toast.makeText(ctx, key+"---"+DataToSave, Toast.LENGTH_SHORT).show();
		Editor editor = getSharedPreferences(ctx).edit();
		editor.putString(key, DataToSave);
//		editor.commit();
		editor.apply();
	}

	public static String getStringValues(Context ctx, String key) {
//		Toast.makeText(ctx, key, Toast.LENGTH_SHORT).show();
		return getSharedPreferences(ctx).getString(key, null);
	}

	public static void setIntValues(Context ctx, String key, int DataToSave) {
		Editor editor = getSharedPreferences(ctx).edit();
		editor.putInt(key, DataToSave);
//		editor.commit();
		editor.apply();
	}

	public static int getIntValues(Context ctx, String key) {
		return getSharedPreferences(ctx).getInt(key, 0);
	}
	
	public static void setBooleanValues(Context ctx, String key, Boolean DataToSave) {
		Editor editor = getSharedPreferences(ctx).edit();
		editor.putBoolean(key, DataToSave);
//		editor.commit();
		editor.apply();
	}

	public static boolean getBooleanValues(Context ctx, String key) {
		return getSharedPreferences(ctx).getBoolean(key, false);
	}
	
	public static long getLongValues(Context ctx, String key) {
		return getSharedPreferences(ctx).getLong(key, 0L);
	}
	
	public static void setLongValues(Context ctx, String key, Long DataToSave) {
		Editor editor = getSharedPreferences(ctx).edit();
		editor.putLong(key, DataToSave);
//		editor.commit();
		editor.apply();
	}
	
}