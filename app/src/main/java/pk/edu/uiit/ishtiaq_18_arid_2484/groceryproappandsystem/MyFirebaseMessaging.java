package pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

import pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.activities.OrderDetailsSellerActivity;
import pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.activities.OrderDetailsUsersActivity;

public class MyFirebaseMessaging extends FirebaseMessagingService {

    private static final String NOTIFICATION_CHANNEL_ID = "MY_NOTIFICATION_CHANNEL_ID"; // Required For Android 0 or above

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        // All notifications will be received here

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        // Get Data From Notification
        String notificationType = message.getData().get("notificationType");
        if (notificationType.equals("NewOrder")){
            String buyerUid = message.getData().get("buyerUid");
            String sellerUid = message.getData().get("sellerUid");
            String orderId = message.getData().get("orderId");
            String notificationTitle = message.getData().get("notificationTitle");
            String notificationDescription = message.getData().get("notificationDescription");

            if (firebaseUser !=null && firebaseAuth.getUid().equals(sellerUid)){
                // User Is Signed In And Is Same User To Which Notification Is Sent
                showNotification(orderId, sellerUid, buyerUid, notificationTitle, notificationDescription, notificationType);
            }
        }
        if (notificationType.equals("OrderStatusChanged")){
            String buyerUid = message.getData().get("buyerUid");
            String sellerUid = message.getData().get("sellerUid");
            String orderId = message.getData().get("orderId");
            String notificationTitle = message.getData().get("notificationTitle");
            String notificationDescription = message.getData().get("notificationMessage");

            if (firebaseUser !=null && firebaseAuth.getUid().equals(buyerUid)){
                // User Is Signed In And Is Same User To Which Notification is sent
                showNotification(orderId, sellerUid, buyerUid, notificationTitle, notificationDescription, notificationType);
            }
        }
    }

    private void showNotification(String orderId, String sellerUid, String buyerUid, String notificationTitle, String notificationDescription, String notificationType) {
        // Notifications
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Id for Notification, Random
        int notificationID = new Random().nextInt(3000);

        // Check If Android Version Is Oreo/O or Above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            setupNotificationChannel(notificationManager);
        }

        // Handle Notification click, Start Order Activity
        Intent intent = null;
        if (notificationType.equals("NewOrder")){
            // Open OrderDetailsSellerActivity
            intent = new Intent(this, OrderDetailsSellerActivity.class);
            intent.putExtra("orderId", orderId);
            intent.putExtra("orderBy", buyerUid);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }

        else if (notificationType.equals("OrderStatusChanged")){
            // Open OrderDetailsUsersActivity
            intent = new Intent(this, OrderDetailsUsersActivity.class);
            intent.putExtra("orderId", orderId);
            intent.putExtra("orderTo", sellerUid);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        // Large Icon
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_notifications_red);

        // Sound Of Notification
        Uri notificationSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setSmallIcon(R.drawable.ic_notifications_red)
                .setLargeIcon(largeIcon)
                .setContentTitle(notificationTitle)
                .setContentText(notificationDescription)
                .setSound(notificationSoundUri)
                .setAutoCancel(true) // Cancel/Dismiss when Clicked
                .setContentIntent(pendingIntent); // Add Intent
        // Show Notification
        notificationManager.notify(notificationID, notificationBuilder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupNotificationChannel(NotificationManager notificationManager) {
        CharSequence channelName = "Some Sample Text";
        String channelDescription = "Channel Description Here";

        NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.setDescription(channelDescription);
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.RED);
        notificationChannel.enableVibration(true);
        if (notificationManager !=null){
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
