package augier.fr.phoebius.core

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.widget.RemoteViews
import augier.fr.phoebius.MainActivity
import augier.fr.phoebius.R
import augier.fr.phoebius.providers.Song
import augier.fr.phoebius.utils.ApplicationUtilities
import groovy.transform.CompileStatic
/**
 * Helper class for showing and canceling player notifications
 *
 * This class makes heavy use of the {@link NotificationCompat.Builder} helper
 * class to create notifications in a backward-compatible way.
 */
@CompileStatic
enum NotificationPlayer implements ApplicationUtilities
{
    INSTANCE()

    private final Map<Integer, PlayerActions> FOR_BUTTON = [
        (R.id.notifBtnPlayPause): PlayerActions.ACTION_PLAY_PAUSE,
        (R.id.notifBtnBackward) : PlayerActions.ACTION_BACKWARD,
        (R.id.notifBtnForward)  : PlayerActions.ACTION_FORWARD,
        (R.id.notifBtnNext)     : PlayerActions.ACTION_NEXT,
        (R.id.notifBtnPrevious) : PlayerActions.ACTION_PREVIOUS
    ]
    private Notification vNotification
    private RemoteViews remoteViews

    private NotificationPlayer()
    {
        def notificationIntent = new Intent(appContext, MainActivity);
        def contentIntent = PendingIntent.getActivity(appContext, 0, notificationIntent, 0);

        vNotification = new NotificationCompat.Builder(appContext)
            .setDefaults(Notification.DEFAULT_VIBRATE)
            .setSmallIcon(R.drawable.notification_player_icon)
            .setTicker("${R.string.app_name}")
            .setOngoing(true)
            .setContentIntent(contentIntent).build()

        remoteViews = new RemoteViews(appContext.packageName, R.layout.player_notification)

        FOR_BUTTON.keySet().each{
            remoteViews.setOnClickPendingIntent(it, generateAction(it))
        }
    }

    private PendingIntent generateAction(int btn)
    {
        String action = FOR_BUTTON[btn]
        Intent intent = new Intent(appContext, MusicService.class)
        intent.setAction(action)
        return PendingIntent.getService(appContext, 1, intent, 0);
    }

    public void updateNotification(Song song)
    {
        remoteViews.setImageViewBitmap(R.id.notifAlbumCover, song.cover)
        remoteViews.setTextViewText(R.id.notifSongTitleLabel, song.title)
        remoteViews.setTextViewText(R.id.notifSongArtistLabel, song.artist)
        if(musicService.playing)
            remoteViews.setImageViewResource(R.id.notifBtnPlayPause, R.drawable.btn_pause)
        else
            remoteViews.setImageViewResource(R.id.notifBtnPlayPause, R.drawable.btn_play)

        vNotification.contentView = remoteViews
        vNotification.bigContentView = remoteViews
    }

    public Notification getNotification(){ return this.vNotification }
}