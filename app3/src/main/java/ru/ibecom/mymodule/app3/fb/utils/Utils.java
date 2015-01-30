package ru.ibecom.mymodule.app3.fb.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;

import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

public class Utils
{
	public static final String EMPTY = "";

	public String getFacebookSDKVersion()
	{
		String sdkVersion = null;
		ClassLoader classLoader = getClass().getClassLoader();
		try
		{
			Class<?> cls = classLoader.loadClass("com.facebook.FacebookSdkVersion");
			Field field = cls.getField("BUILD");
			sdkVersion = String.valueOf(field.get(null));
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (NoSuchFieldException e)
		{
			e.printStackTrace();
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
		return sdkVersion;
	}

	public static String getHashKey(Context context)
	{
		// Add code to print out the key hash
		try
		{
			PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
			for (Signature signature: info.signatures)
			{
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				return Base64.encodeToString(md.digest(), Base64.DEFAULT);
			}
		}
		catch (NameNotFoundException e)
		{

		}
		catch (NoSuchAlgorithmException e)
		{

		}
		return null;
	}

	/**
	 * <p>
	 * Joins the elements of the provided {@code Iterator} into a single String containing the provided
	 * elements.
	 * </p>
	 * 
	 * <p>
	 * No delimiter is added before or after the list. Null objects or empty strings within the iteration are
	 * represented by empty strings.
	 * </p>
	 * 
	 * <p>
	 * See the examples here: {@link #join(Object[],char)}.
	 * </p>
	 * 
	 * @param iterator the {@code Iterator} of values to join together, may be null
	 * @param separator the separator character to use
	 * @return the joined String, {@code null} if null iterator input
	 * @since 2.0
	 */
	public static String join(Iterator<?> iterator, char separator)
	{

		// handle null, zero and one elements before building a buffer
		if (iterator == null)
		{
			return null;
		}
		if (!iterator.hasNext())
		{
			return EMPTY;
		}
		Object first = iterator.next();
		if (!iterator.hasNext())
		{
			return first == null? "": first.toString();
		}

		// two or more elements
		StringBuilder buf = new StringBuilder(256); // Java default is 16, probably too small
		if (first != null)
		{
			buf.append(first);
		}

		while (iterator.hasNext())
		{
			buf.append(separator);
			Object obj = iterator.next();
			if (obj != null)
			{
				buf.append(obj);
			}
		}

		return buf.toString();
	}

	public static String join(Map<?, ?> map, char separator, char valueStartChar, char valueEndChar)
	{

		// handle null, zero and one elements before building a buffer
		if (map == null)
		{
			return null;
		}
		if (map.size() == 0)
		{
			return EMPTY;
		}

		// two or more elements
		StringBuilder buf = new StringBuilder(256); // Java default is 16, probably too small

		boolean isFirst = true;
		for (Entry<?, ?> entry: map.entrySet())
		{
			if (isFirst)
			{
				buf.append(entry.getKey());
				buf.append(valueStartChar);
				buf.append(entry.getValue());
				buf.append(valueEndChar);
				isFirst = false;
			}
			else
			{
				buf.append(separator);
				buf.append(entry.getKey());
				buf.append(valueStartChar);
				buf.append(entry.getValue());
				buf.append(valueEndChar);
			}
		}

		return buf.toString();
	}

    public static void printHashKey(Context context)
    {
        try
        {
            String TAG = "com.sromku.simple.fb.example";
            PackageInfo info = context.getPackageManager().getPackageInfo(TAG,
                    PackageManager.GET_SIGNATURES);
            for (Signature signature: info.signatures)
            {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String keyHash = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                Log.d(TAG, "keyHash: " + keyHash);
            }
        }
        catch (NameNotFoundException e)
        {

        }
        catch (NoSuchAlgorithmException e)
        {

        }
    }

    /**
     * Update language
     *
     * @param code The language code. Like: en, cz, iw, ...
     */
    public static void updateLanguage(Context context, String code)
    {
        Locale locale = new Locale(code);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }

    public static AlertDialog buildProfileResultDialog(Activity activity, Pair<String, String>... pairs)
    {
        StringBuilder stringBuilder = new StringBuilder();

        for (Pair<String, String> pair: pairs)
        {
            stringBuilder.append(String.format("<h3>%s</h3>", pair.first));
            stringBuilder.append(String.valueOf(pair.second));
            stringBuilder.append("<br><br>");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(Html.fromHtml(stringBuilder.toString()))
                .setPositiveButton("OK", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                    }
                });
        AlertDialog alertDialog = builder.create();
        return alertDialog;
    }
}
