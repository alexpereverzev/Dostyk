package litebonus.dostyk.push;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

import litebonus.dostyk.R;
import litebonus.dostyk.activity.MainActivity;
import litebonus.dostyk.core.API;
import litebonus.dostyk.core.modules.ActionModule;
import retrofit.Callback;

/**
 * Created by Alexander on 20.11.2014.
 */
public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    private Callback<JSONObject> dynamicTemplateCallback;
    NotificationCompat.Builder builder;
    private String subject;
    public GcmIntentService() {
        super("GcmIntentService");
    }
    private final IBinder mBinder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {

        return mBinder;
    }

    public void setDynamicTemplateCallback(Callback<JSONObject> dynamicTemplateCallback) {
        this.dynamicTemplateCallback = dynamicTemplateCallback;
    }

    public class LocalBinder extends Binder {
        public GcmIntentService getService() {
            // Return this instance of LocalService so clients can call public methods
            return GcmIntentService.this;
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
            //    sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
              //  sendNotification("Deleted messages on server: " +
                   //     extras.toString());
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // This loop represents the service doing some work.

                //    Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
                // Post notification of received message.
            String body=extras.getString("body");
            subject=extras.getString("subject");
                String action=extras.getString("action");

                sendNotification(body,action);
                // Log.i(TAG, "Received: " + extras.toString());
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg, String action) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent=new Intent(this, MainActivity.class);
        intent.putExtra("action_new",action);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                     //   .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(subject)
                        .setAutoCancel(true)
                        .setTicker(msg.substring(0, 10) + "...")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg);
        mBuilder.setContentIntent(contentIntent);
        Notification note = mBuilder.build();
        note.defaults |= Notification.DEFAULT_VIBRATE;
        note.defaults |= Notification.DEFAULT_SOUND;

        mNotificationManager.notify(NOTIFICATION_ID, note);

        if(ActionModule.isUpdate()){


            Map<String, String> options = new LinkedHashMap<>();
            options.put("1[pfield]", "device_id");
            options.put("1[matching]", "=");
            options.put("1[vfield]", "" + getSharedPreferences("device_id", MODE_PRIVATE).getInt("device_id", 0));
            API.getInstance().getMethods().getDynamicScreenDeviceId(36, options, dynamicTemplateCallback);
        }
    }

}