package litebonus.dostyk.api;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Field;

import ru.ibecom.api.util.ReflectionUtil;

/**
 * Intervals provider
 * Created by alexeyshcherbinin on 01.12.14.
 */
public class IntervalsProvider {
    private static final String TAG = "IntervalsProvider";
    private static final String halClassName = "ru.ibecom.api.hal.HalImpl";

    private static final String enterIntervalsFieldName = "REGION_ENTER_INTERVALS";
    private static final String exitIntervalsFieldName = "REGION_EXIT_INTERVALS";

    private static final String enterImmediateFieldName = "IMMEDIATE_REGION_ENTRANCE";
    private static final String exitImmediateFieldName = "IMMEDIATE_REGION_EXIT";

    private Context _context;

    public IntervalsProvider(Context context) { _context = context; }

    public Integer getEnterCount() {
        try {
            Class<?> halClass = getHalClass();
            return ReflectionUtil.getFieldValue(halClass, enterIntervalsFieldName);
        } catch (ClassNotFoundException e) {
            Toast.makeText(_context, "Error: beacon class not found!", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    public Integer getExitCount() {
        try {
            Class<?> halClass = getHalClass();
            return ReflectionUtil.getFieldValue(halClass, exitIntervalsFieldName);
        } catch (ClassNotFoundException e) {
            Toast.makeText(_context, "Error: beacon class not found!", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    public Boolean IsEnterImmediate() {
        try {
            Class<?> halClass = getHalClass();
            return ReflectionUtil.getFieldValue(halClass, enterImmediateFieldName);
        } catch (ClassNotFoundException e) {
            Toast.makeText(_context, "Error: beacon class not found!", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    public Integer IsExitImmediate() {
        try {
            Class<?> halClass = getHalClass();
            return ReflectionUtil.getFieldValue(halClass, exitImmediateFieldName);
        } catch (ClassNotFoundException e) {
            Toast.makeText(_context, "Error: beacon class not found!", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private Class<?> getHalClass() throws ClassNotFoundException {
        Class<?> cls = Class.forName(halClassName);
        if (cls == null) throw new ClassNotFoundException();
        return cls;
    }

    public void setEnterCounter(Integer counter) {
        try {
            Class<?> halClass = getHalClass();
            setFinalFieldValue(halClass, enterIntervalsFieldName, counter);
        } catch (ClassNotFoundException ignored) {}
    }

    public void setExitCounter(Integer counter) {
        try {
            Class<?> halClass = getHalClass();
            setFinalFieldValue(halClass, exitIntervalsFieldName, counter);
        } catch (ClassNotFoundException ignored) {}
    }

    public void setEnterImmediate(boolean flag) {
        try {
            Class<?> halClass = getHalClass();
            setFinalFieldValue(halClass, enterImmediateFieldName, flag);
        } catch (ClassNotFoundException ignored) {}
    }

    public void setExitImmediate(boolean flag) {
        try {
            Class<?> halClass = getHalClass();
            setFinalFieldValue(halClass, exitImmediateFieldName, flag);
        } catch (ClassNotFoundException ignored) {}
    }

    private void setFinalFieldValue(Class<?> cls, String fieldName, Object newValue) {
        try {
            Field field = cls.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(null, newValue);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Log.e(TAG, "Error setting missing counter:", e);
            Toast.makeText(_context, "Error setting missing counter: " + e, Toast.LENGTH_SHORT).show();
        }

    }
}
