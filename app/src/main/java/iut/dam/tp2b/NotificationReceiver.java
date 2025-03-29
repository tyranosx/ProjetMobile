package iut.dam.tp2b;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.*;
import android.os.Build;
import androidx.core.app.NotificationCompat;

// 🎯 Classe appelée automatiquement par le système quand l'AlarmManager déclenche une notif
public class NotificationReceiver extends BroadcastReceiver {

    // ID unique pour le canal de notifications
    public static final String CHANNEL_ID = "CRITICAL_SLOT_CHANNEL";

    // 🔔 Méthode exécutée quand la notification est déclenchée
    @Override
    public void onReceive(Context context, Intent intent) {
        // 📆 Données passées par l'intent (créneau engagé)
        String slotDate = intent.getStringExtra("slot_date");
        String slotHour = intent.getStringExtra("slot_hour");

        // ✅ Crée le canal de notification si nécessaire (Android 8+)
        createChannel(context);

        // 📲 Intent pour ouvrir l'application quand l'utilisateur clique sur la notif
        Intent openApp = new Intent(context, MainActivity.class);
        openApp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                openApp,
                PendingIntent.FLAG_IMMUTABLE // Requis depuis Android 12
        );

        // 🛎️ Construction de la notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications) // Icône affichée
                .setContentTitle("⚠️ Créneau critique bientôt")
                .setContentText("Engagement à " + slotHour + " le " + slotDate)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true) // Disparait au clic
                .setContentIntent(pendingIntent); // Lancement de l’app au clic

        // 📤 Envoi de la notification via le système
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify((int) System.currentTimeMillis(), builder.build());
    }

    // 🔧 Création du canal de notification pour Android 8+
    private void createChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Créneaux critiques";
            String desc = "Notifications pour engagements à venir";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(desc);

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
}