package challenge.tomas.pt.imagegalleryapp;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.util.Stack;

/**
 * Base core application to create an interface that can be accessed by the core module.
 * <p>
 * Modified by Tomás Rodrigues on 06-03-2018.
 */
public class ImageGalleryApplication extends Application {

    private final Object locker = new Object();

    private final Stack<Class> activityStack = new Stack<>();

    private boolean isInBackground;

    private ActivityLifecycleCallbacks lifecycleCallbacks = new CoreActivityLifecycleCallbacks();

    @Override
    public void onCreate() {
        super.onCreate();

        registerActivityLifecycleCallbacks(lifecycleCallbacks);
    }

    @Override
    public void onTrimMemory(int level) {
//        LOGGER.debug("Trim memory: {}", level);
        synchronized (locker) {
            isInBackground = level >= TRIM_MEMORY_UI_HIDDEN;
        }
        if (isInBackground && getOpenActivitiesCount() == 1) {
//            GeoLocationManager.getInstance(ImageGalleryApplication.this).sleepTracking();
        }

        super.onTrimMemory(level);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    private void incrementActivityCount(Class activity) {
        synchronized (locker) {
            activityStack.push(activity);
        }
    }

    private void decrementActivityCount(Class activity) {
        synchronized (locker) {
            if (activityStack.size() > 0) {
                boolean removed;
                do {
                    removed = activityStack.remove(activity);
                } while (removed);
            }
        }
    }

    public int getOpenActivitiesCount() {
        synchronized (locker) {
            return activityStack.size();
        }
    }

    private class CoreActivityLifecycleCallbacks implements ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            incrementActivityCount(activity.getClass());
//            LOGGER.debug("Activity created: [{}] {}", getOpenActivitiesCount(), activity.getClass().getName());
        }

        @Override
        public void onActivityStarted(Activity activity) {
        }

        @Override
        public void onActivityResumed(Activity activity) {
//            LOGGER.debug("Activity resumed: {}", activity.getClass().getName());
            if (isInBackground) {
//                GeoLocationManager.getInstance(ImageGalleryApplication.this).wakeTracking();
            }
            synchronized (locker) {
                isInBackground = false;
            }
        }

        @Override
        public void onActivityPaused(Activity activity) {
//            LOGGER.debug("Activity paused: {}", activity.getClass().getName());
        }

        @Override
        public void onActivityStopped(Activity activity) {
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            decrementActivityCount(activity.getClass());
//            LOGGER.debug("Activity destroyed: [{}] {}", getOpenActivitiesCount(), activity.getClass().getName());
        }
    }
}
