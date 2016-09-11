package augier.fr.phoebius.core

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.KeyEvent
import augier.fr.phoebius.utils.ApplicationUtilities
/**
 * This class is responsible for managing the button
 * events comming from the headset
 */
public class MediaButtonReceiver extends BroadcastReceiver implements ApplicationUtilities
{
    /**
     * Logs the last time an event happened.
     * This variabla is static as a new {@link MediaButtonReceiver}
     * seems to be instanciated each time an event happens.
     */
    private static long lastEventTime = 0
    private static final long eventTimeOffset = 500

    @Override
    void onReceive(Context context, Intent intent)
    {
        def intentAction = intent.action
        if(!Intent.ACTION_MEDIA_BUTTON.equals(intentAction)) return

        def event = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT) as KeyEvent
        if(event == null) return

        if(isAnEcho(event.eventTime)) return

        def action = event.keyCode

        switch(action)
        {
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                musicService.playPause()
                abortBroadcast()
                break
            case KeyEvent.KEYCODE_MEDIA_PLAY:
                if(!musicService.playing)
                {
                    musicService.playPause()
                    abortBroadcast()
                }
                break
            case KeyEvent.KEYCODE_MEDIA_PAUSE:
                if(musicService.playing)
                {
                    musicService.playPause()
                    abortBroadcast()
                }
                break
            case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                musicService.playPrevious()
                abortBroadcast()
                break
            case KeyEvent.KEYCODE_MEDIA_NEXT:
                musicService.playNext()
                abortBroadcast()
                break
            case KeyEvent.KEYCODE_VOLUME_UP:
                Log.d(this.class.toString(), "Volume up")
                abortBroadcast()
                break
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                Log.d(this.class.toString(), "Volume down")
                abortBroadcast()
                break
        }
    }

    /**
     * Determines if the received event is an echo and should be ignored
     *
     * {@link BroadcastReceiver#onReceive} sometimes receives the same event multiple
     * times in a short time interval. These echos should be ignored.
     * @param evTime The time the event happened
     * @return Should be ignored or not
     */
    private static boolean isAnEcho(long evTime)
    {
        if(lastEventTime + eventTimeOffset > evTime) return true

        lastEventTime = evTime
        return false
    }
}
