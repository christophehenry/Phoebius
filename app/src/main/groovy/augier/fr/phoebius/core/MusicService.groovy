package augier.fr.phoebius.core

import android.app.Service
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.media.MediaPlayer.OnErrorListener
import android.os.Binder
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import android.widget.BaseAdapter
import augier.fr.phoebius.PhoebiusApplication
import augier.fr.phoebius.model.Playlist
import augier.fr.phoebius.providers.Song
import augier.fr.phoebius.providers.Songs
import augier.fr.phoebius.utils.ApplicationUtilities
import augier.fr.phoebius.utils.MessageHelper
import com.arasthel.swissknife.SwissKnife
import com.arasthel.swissknife.annotations.OnUIThread
import com.squareup.otto.Produce
import groovy.transform.CompileStatic

@CompileStatic
class MusicService extends Service
    implements OnErrorListener, OnCompletionListener, ApplicationUtilities
{
    private MediaPlayer mediaPlayer
    private MediaPlayer nextMediaPlayer
    private final IBinder musicBinder = new MusicBinder()
    private NotificationPlayer notificationPlayer = NotificationPlayer.INSTANCE
    private PlaylistManager playlistManager = new PlaylistManager()

    //region Player logic
    public void play(Playlist playlist, int position=0)
    {
        def initialized = playlistManager.initialize(playlist, position)
        def song = playlistManager.currentSong
        if(initialized && song.present) startMediaPlayer(song.get())
        //TODO: I18n
        else MessageHelper.post("Playlist is empty!")
    }

    public void enqueue(Playlist songs)
    {
        if(!playlistManager.lenght) // Playlist is empty: enqueue and play
        {
            playlistManager.addSongs(songs)
            def song = playlistManager.currentSong
            if(song.present) startMediaPlayer(song.get())
        }
        else playlistManager.addSongs(songs) // Playlist isn't empty: just enqueue
        notifyChanged()
    }

    private void startMediaPlayer(Song song)
    {
        releasePlayers()
        mediaPlayer = createMediaPlayer(song)
        mediaPlayer.start()
        postNotification()
        tryPrepareNextPlayer()
    }

    public void stop()
    {
        releasePlayers()
        def song = playlistManager.moveToFirst()
        if(song.present) postNotification()
        stopForeground(true)
        // TODO: I18n
        MessageHelper.post("Playlist ended")
        PhoebiusApplication.bus.post(PlayerActions.ACTION_STOP)
    }

    public void playPause()
    {
        // Empty playlist, fill with all songs
        if(!playlistManager.lenght) playlistManager.initialize(Songs.songs, 0)
        if(!mediaPlayer) // Media player isn't initialized
        {
            def song = playlistManager.currentSong
            if(song.present) startMediaPlayer(song.get())
            // TODO: I18n
            else MessageHelper.post("Nothing to play")
        }
        else
        {
            if(playing) mediaPlayer.pause()
            else mediaPlayer.start()
        }
        postNotification()
    }

    public void seek(int position)
    {
        if(position > duration) mediaPlayer?.seekTo(position - 2000)
        else if(position < 0) mediaPlayer?.seekTo(0)
        else mediaPlayer?.seekTo(position)
    }

    public void forward(){ seek(position + 10000) }

    public void backward(){ seek(position - 10000) }

    /**
     * Plays the previous song
     * If the media player is already playing the first song, the playback will stop
     * else, it will play the last song of the playlist
     */
    public void playPrevious()
    {
        if(mediaPlayer) // Normal case
        {
            def optSong = playlistManager.moveToPrevious()
            if(optSong.present) startMediaPlayer(optSong.get())
            else stop()
        }
        else if(playlistManager.lenght) // Player hasn't started yet or has stopped
        {
            //Try play the previous, if no previous, play last song of playlist
            def optSong = playlistManager.moveToPreviousOrLast()
            if(optSong.present) startMediaPlayer(optSong.get())
            else
            {
                // This case should not happen as we checked that playlist isn't empty
                Log.e(MusicService.toString(), "Unable to retrieve previous or last playlist song")
                MessageHelper.post("Playlist is empty!")
            }
        }
        else MessageHelper.post("Playlist is empty!")
    }

    /**
     * Plays the next song
     * If the media player is already playing the first song, the playback will stop
     * else, it will play the last song of the playlist
     */
    public void playNext()
    {
        if(mediaPlayer) // Normal case
        {
            def optSong = playlistManager.moveToNext()
            if(optSong.present) startMediaPlayer(optSong.get())
            else stop()
        }
        else if(playlistManager.lenght) // Player hasn't started yet or has stopped
        {
            //Try play the next, if no next, play first song of playlist
            def optSong = playlistManager.moveToNextOrFirst()
            if(optSong.present) startMediaPlayer(optSong.get())
            else
            {
                // This case should not happen as we checked that playlist isn't empty
                Log.e(MusicService.toString(), "Unable to retrieve next or first playlist song")
                MessageHelper.post("Playlist is empty!")
            }
        }
        else MessageHelper.post("Playlist is empty!")
    }
    //endregion

    //region Overriden methods
    @Override
    public IBinder onBind(Intent intent)
    {
        log("Service bound")
        return musicBinder
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        if(!intent?.action) return START_NOT_STICKY
        PlayerActions action = PlayerActions.valueOf(intent.action)
        log("Intent received: $action")

        switch(action)
        {
            case PlayerActions.ACTION_PLAY_PAUSE:
                playPause()
                break
            case PlayerActions.ACTION_PREVIOUS:
                playPrevious()
                break
            case PlayerActions.ACTION_NEXT:
                playNext()
                break
            case PlayerActions.ACTION_FORWARD:
                forward()
                break
            case PlayerActions.ACTION_BACKWARD:
                backward()
                break
        }
        return START_NOT_STICKY
    }

    @Override
    public void onCompletion(MediaPlayer me)
    {
        mediaPlayer.release()
        mediaPlayer = nextMediaPlayer
        playlistManager.moveToNext()
        postNotification()

        tryPrepareNextPlayer()
        log("Previous song ended. Playing ${currentSong}")
    }

    @Override boolean onError(MediaPlayer mediaPlayer, int i, int i2)
    {
        switch(i2)
        {
            case MediaPlayer.MEDIA_ERROR_IO:
                Log.e(MediaPlayer.toString(), "Error met while playing: MEDIA_ERROR_IO")
                break
            case MediaPlayer.MEDIA_ERROR_MALFORMED:
                Log.e(MediaPlayer.toString(), "Error met while playing: MEDIA_ERROR_MALFORMED")
                break
            case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                Log.e(MediaPlayer.toString(), "Error met while playing: MEDIA_ERROR_UNSUPPORTED")
                break
            default:
                Log.e(MediaPlayer.toString(), "Error met while playing: MEDIA_ERROR_SYSTEM")
                break
        }
        MessageHelper.post("Hmm... Something bad happened while playing")
        releasePlayers()
        return true
    }

    @Override void onDestroy()
    {
        stopForeground(true)
        playlistManager.backup()
        releasePlayers()
        log("Service destroyed")
        super.onDestroy()
    }

    @Override void onCreate()
    {
        super.onCreate()
        PhoebiusApplication.bus.register(this)
    }
    //endregion

    public void save(){ playlistManager.backup() }

    private MediaPlayer createMediaPlayer(Song song)
    {
        def mediaPlayer = MediaPlayer.create(applicationContext, song.URI)
        mediaPlayer.audioStreamType = AudioManager.STREAM_MUSIC
        mediaPlayer.setWakeMode(applicationContext, PowerManager.PARTIAL_WAKE_LOCK)
        mediaPlayer.onCompletionListener = this
        mediaPlayer.onErrorListener = this
        return mediaPlayer
    }

    private void tryPrepareNextPlayer()
    {
        def song = playlistManager.nextSong
        if(song.present)
        {
            nextMediaPlayer = createMediaPlayer(song.get())
            mediaPlayer?.nextMediaPlayer = nextMediaPlayer
        }
        else
        {
            mediaPlayer.onCompletionListener = new OnCompletionListener(){
                @Override void onCompletion(MediaPlayer mp)
                {
                    MessageHelper.post("Playlist ended")
                    log("End of playlist, stopping")
                    stop()
                }
            }
        }
    }

    private void releasePlayers()
    {
        mediaPlayer?.release()
        mediaPlayer = null
        nextMediaPlayer?.release()
        nextMediaPlayer = null
    }

    private void postNotification()
    {
        Song song
        if(playlistManager.currentSong.present) song = playlistManager.currentSong.get()
        else song = Songs.defaultSong

        PhoebiusApplication.bus.post(song)
        PhoebiusApplication.bus.post(PlayerActions.ACTION_PLAY_PAUSE)
        notificationPlayer.updateNotification(song)
        if(playing) startForeground(1, notificationPlayer.notification)
        else stopForeground(false)
    }


    //region GET/SET
    public int getDuration(){ mediaPlayer?.duration ?: 0 }

    public boolean isPlaying(){ mediaPlayer?.playing ?: false }

    public int getPosition(){ mediaPlayer?.currentPosition ?: 0 }

    public Playlist getCurrentPlaylist(){ playlistManager.currentPlaylist }

    @Produce
    public Song getCurrentSong()
    {
        if(playlistManager.currentSong.present) return playlistManager.currentSong.get()
        else return Songs.defaultSong
    }
    //endregion

    //region Observable
    private List<BaseAdapter> observers = []

    @OnUIThread private void _notifyChanged(){ observers*.notifyDataSetChanged() }
    public void notifyChanged(){ SwissKnife.runOnUIThread(this, "_notifyChanged") }
    public void register(BaseAdapter adapter){
        if(!observers.contains(adapter)) observers << adapter
    }
    public boolean unregister(BaseAdapter adapter){ observers.remove(adapter) }
    //endregion

    public class MusicBinder extends Binder{ MusicService getService(){ MusicService.this } }
}
