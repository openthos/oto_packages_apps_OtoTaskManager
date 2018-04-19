package org.openthos.taskmanager.piebridge.prevent.ui.util;

import android.app.ActivityManager;
import android.content.Context;
import android.os.SystemClock;
import android.text.format.DateUtils;
import android.util.SparseIntArray;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openthos.taskmanager.R;

public class StatusUtils {

    private static SparseIntArray statusMap = new SparseIntArray();

    static {
        statusMap.put(400, R.string.importance_background);
        statusMap.put(500, R.string.importance_empty);
        statusMap.put(1000, R.string.importance_gone);
        statusMap.put(100, R.string.importance_foreground);
        statusMap.put(125, R.string.importance_foreground_service);
        statusMap.put(150, R.string.importance_top_sleeping);
        statusMap.put(130, R.string.importance_perceptible);
        statusMap.put(300, R.string.importance_service);
        statusMap.put(200, R.string.importance_visible);
        statusMap.put(-300, R.string.importance_service_not_started);
    }

    private StatusUtils() {

    }

    public static CharSequence formatRunning(Context context, Set<Long> running) {
        if (running == null) {
            return context.getString(R.string.not_running);
        } else {
            if (running.contains((long) 300) 
                && running.contains((long) -300)) {
                running.remove((long) -300);
            }
            return doFormatRunning(context, running);
        }
    }

    private static CharSequence doFormatRunning(Context context, Set<Long> running) {
        Set<String> sets = new LinkedHashSet<String>();
        for (Long i : running) {
            int v = statusMap.get(i.intValue());
            if (v == 0) {
                long elapsed = TimeUnit.MILLISECONDS.toSeconds(SystemClock.elapsedRealtime()) - Math.abs(i);
                sets.add(DateUtils.formatElapsedTime(elapsed));
            } else {
                sets.add(context.getString(v));
            }
        }
        return toString(sets);
    }

    private static CharSequence toString(Set<String> sets) {
        StringBuilder buffer = new StringBuilder();
        Iterator<?> it = sets.iterator();
        while (it.hasNext()) {
            buffer.append(it.next());
            if (it.hasNext()) {
                buffer.append(", ");
            } else {
                break;
            }
        }
        return buffer.toString();
    }


    private static boolean isPriority(Set<Long> running) {
        for (Long i : running) {
            int v = statusMap.get(i.intValue());
            if (v == 0) {
                return i < 0;
            }
        }
        return false;
    }

    public static int getDrawable(Set<Long> running, boolean prevent) {
        if (running == null) {
            return R.mipmap.ic_menu_block;
        }
        if (isPriority(running)) {
            return R.mipmap.ic_menu_star;
        } else if (prevent) {
            return R.mipmap.ic_menu_block;
        } else {
            return R.mipmap.ic_menu_stop;
        }
    }

}
