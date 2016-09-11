package augier.fr.phoebius.core

import android.util.Log
import augier.fr.phoebius.model.Playlist
import augier.fr.phoebius.providers.Song
import augier.fr.phoebius.providers.Songs
import augier.fr.phoebius.utils.ApplicationUtilities
import augier.fr.phoebius.utils.Optional
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import groovy.transform.CompileStatic
/**
 * This class manages the creation, listing and iteration through the musics
 *
 * This class is a singleton to easier it's use everywhere in the code.
 * It is an abstraction of Andrdoid FS
 */
@CompileStatic
public class PlaylistManager
{
    private int vCursor = 0
    private LoopMode vLoopMode = LoopMode.NONE
    private Playlist vCurrentPlaylist = []
    private Saver saver = new Saver()

    public PlaylistManager(){ saver.restore() }

    public boolean initialize(Playlist playlist, int position)
    {
        if(!playlist || position < 0 || position > playlist.size()) return false

        vCurrentPlaylist = playlist
        vCursor = position
        return true
    }

    public void addSongs(Playlist songs){ vCurrentPlaylist.addAll(songs) }

    public Optional<Song> moveToFirst()
    {
        vCursor = 0
        return currentSong
    }
    public Optional<Song> moveToLast()
    {
        if(lenght > 0) vCursor = lenght - 1
        return currentSong
    }

    public Optional<Song> moveToPreviousOrLast()
    {
        def loopModeBckp = loopMode
        loopMode = LoopMode.ALL
        def optSong = moveToPrevious()
        loopMode = loopModeBckp
        return optSong
    }

    public Optional<Song> moveToNextOrFirst()
    {
        def loopModeBckp = loopMode
        loopMode = LoopMode.ALL
        def optSong = moveToNext()
        loopMode = loopModeBckp
        return optSong
    }

    public Optional<Song> moveToNext()
    {
        def next = nextCursor
        vCursor = next.present ? next.get() : 0
        if(next.present) return Optional.of(vCurrentPlaylist[next.get()])
        else return Optional.<Song> absent()
    }

    public Optional<Song> moveToPrevious()
    {
        def previous = previousCursor
        vCursor = previous.present ? previous.get() : 0
        if(previous.present) return Optional.of(vCurrentPlaylist[previous.get()])
        else return Optional.<Song>absent()
    }

    private Optional<Integer> getNextCursor()
    {
        if(!lenght) return Optional.<Integer>absent()
        Integer next
        switch(vLoopMode)
        {
            case LoopMode.NONE:
                next = vCursor + 1
                break
            case LoopMode.ONE:
                next = vCursor
                break
            case LoopMode.ALL:
                next = (vCursor + 1) % lenght
        }
        if(next >= 0 && next < lenght) return Optional.of(next)
        else return Optional.<Integer>absent()
    }

    private Optional<Integer> getPreviousCursor()
    {
        if(!lenght) return Optional.<Integer>absent()
        Integer next
        switch(vLoopMode)
        {
            case LoopMode.NONE:
                next = vCursor - 1
                break
            case LoopMode.ONE:
                next = vCursor
                break
            case LoopMode.ALL:
                next = (vCursor + lenght - 1) % lenght
        }
        if(next >= 0 && next < lenght) return Optional.of(next)
        else return Optional.<Integer> absent()
    }

    public Optional<Song> getNextSong()
    {
        def next = nextCursor
        if(next.present) return Optional.of(vCurrentPlaylist[next.get()])
        else return Optional.<Song> absent()
    }

    public Optional<Song> getPreviousSong()
    {
        def previous = previousCursor
        if(previous.present) return Optional.of(vCurrentPlaylist[previous.get()])
        else return Optional.<Song> absent()
    }

    public int getLenght(){ currentPlaylist.size() }

    public Playlist getCurrentPlaylist(){ vCurrentPlaylist }

    public Optional<Song> getCurrentSong()
    {
        def song = vCurrentPlaylist[vCursor]
        if(song) return Optional.of(song)
        else return Optional.<Song> absent()
    }

    public void backup(){ saver.backup() }

    public LoopMode getLoopMode(){ vLoopMode }

    public void setLoopMode(LoopMode vLoopMode){ this.vLoopMode = vLoopMode }

    public enum LoopMode
    {
        NONE,
        ONE,
        ALL
    }

    private class Saver implements ApplicationUtilities
    {
        private static final String PREFERENCE_KEY = "phoebius.musicService"
        private static final String PLAYLIST_KEY = "${PREFERENCE_KEY}.playlist"
        private static final String POSITION_KEY = "${PREFERENCE_KEY}.position"
        private static final String LOOP_MODE_KEY = "${PREFERENCE_KEY}.loopMode"

        public void backup()
        {
            def editor = getPreferences().edit()
            List<Long> ids = vCurrentPlaylist.collect{ it.ID }
            editor.putString(PLAYLIST_KEY, new JsonBuilder(ids).toString())
            editor.putInt(POSITION_KEY, vCursor)
            editor.putString(LOOP_MODE_KEY, vLoopMode.toString())
            editor.commit()
        }

        public void restore()
        {
            String rawJSON = getPreferences().getString(PLAYLIST_KEY, "[]")
            int playlistPosition = getPreferences().getInt(POSITION_KEY, 0)
            def savedLoopMode = getPreferences().getString(LOOP_MODE_KEY, LoopMode.NONE.toString())
            vLoopMode = LoopMode.valueOf(savedLoopMode)

            try
            {
                def conf = (List<String>)new JsonSlurper().parseText(rawJSON)
                List<Long> ids = []
                conf?.each{ ids << Long.parseLong(it) }
                PlaylistManager.this.initialize(Songs.findByIds(ids), playlistPosition)
            }
            catch(Exception e){ Log.e("${this.class.name}", "Couldn't parse favorite songs", e) }
        }
    }
}
