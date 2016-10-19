package com.hank.corelib.util;

import android.text.TextUtils;

import com.hank.corelib.exception.EmptyException;

import java.util.Collection;
import java.util.Map;

/**
 * 辅助判断
 * 
 * @author Hank
 * @date 2013-6-10下午5:50:57
 */
public class Check {

	public static boolean isEmpty(CharSequence str) {
		return isNull(str) || str.length() == 0;
	}

	public static boolean isEmpty(Object[] os) {
		return isNull(os) || os.length == 0;
	}

	public static boolean isEmpty(Collection<?> l) {
		return isNull(l) || l.isEmpty();
	}

	public static boolean isEmpty(Map<?, ?> m) {
		return isNull(m) || m.isEmpty();
	}

	public static boolean isNull(Object o) {
		return o == null;
	}

	public static void checkEmpty(String t, String msg){
		if(TextUtils.isEmpty(t)){
			throw new EmptyException(msg);
		}
	}
	public static <T> void checkNull(T t, String msg){
		if(t==null)
		{
			throw new EmptyException(msg);
		}
	}
}
