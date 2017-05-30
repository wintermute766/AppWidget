package ru.sberbank.learning.appwidget;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.BatteryManager;
import android.os.IBinder;
import android.widget.RemoteViews;

public class WidgetService extends Service {
    public WidgetService() {
    }

    private BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int capacity = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            String tech = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);

            float fPercent = ((float) level / (float) capacity) * 100f;
            int percent = Math.round(fPercent);

            updateBatteryWidget(percent);
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(batteryReceiver);
    }

    private void updateBatteryWidget(int percent) {

        AppWidgetManager manager = AppWidgetManager.getInstance(this);

        int[] ids = manager.getAppWidgetIds(new ComponentName(this, WidgetReceiver.class));
        for (int id: ids) {
            RemoteViews views = new RemoteViews(getPackageName(), R.layout.battery_widget);
            views.setTextViewText(R.id.widget_text, getString(R.string.percent_format, percent));

            Bitmap bitmap = Bitmap.createBitmap(
                    getResources().getDimensionPixelSize(R.dimen.battery_width),
                    getResources().getDimensionPixelSize(R.dimen.battery_height),
                            Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            Drawable battery = getResources().getDrawable(R.drawable.battery);
            battery.setLevel(percent * 100);
            battery.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            battery.draw(canvas);

            views.setImageViewBitmap(R.id.widget_image, bitmap);

            manager.updateAppWidget(id, views);
        }

    }
}
