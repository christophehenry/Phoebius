package augier.fr.phoebius.providers


import augier.fr.phoebius.model.Playlist
import augier.fr.phoebius.utils.ApplicationUtilities
import augier.fr.phoebius.utils.Queryable
import com.arasthel.swissknife.SwissKnife
import com.arasthel.swissknife.annotations.OnBackground
import groovy.transform.CompileStatic


/**
 * Provider for querying in listed songs
 */
@CompileStatic
public final class Songs implements Queryable, ApplicationUtilities, ObservableProvider
{
    private static final Song vDefaultSong = new Song()
    private static final Object vlock = new Object()
    private static Playlist vSongs = []

    public static Song getDefaultSong(){ vDefaultSong }

    public static Playlist getSongs(){ synchronized(vlock){ return vSongs } }

    public static Playlist findByAlbum(String name)
    {
        synchronized(vlock)
        {
            return new Playlist(this.vSongs.findAll{
                it.album.albumTitle == name
            })
        }
    }

    public static Song findById(Long id){ synchronized(vlock){ this.vSongs.find{ it.ID == id } } }

    public static Playlist findByIds(List<Long> ids)
    {
        Playlist result = []
        ids.each{
            Song found = findById(it)
            if(found) result << found
        }
        return result
    }

    public static void backgroundQuery(){ SwissKnife.runOnBackground(this, "query") }

    @OnBackground
    public static void query()
    {
        synchronized(vlock)
        {
            def musicCursor = getAppContext()
                .contentResolver.query(MUSIC_URI, null, null, null, null)

            def getString = { String columnName ->
                return musicCursor.getString(
                    musicCursor.getColumnIndex(columnName))
            }

            def getInt = { String columnName ->
                return musicCursor.getInt(
                    musicCursor.getColumnIndex(columnName))
            }

            def getLong = { String columnName ->
                return musicCursor.getLong(
                    musicCursor.getColumnIndex(columnName))
            }

            if(musicCursor?.moveToFirst())
            {
                while(musicCursor.moveToNext())
                {
                    this.vSongs << new Song(
                        getLong(SONG_ID),
                        getString(SONG_TITLE),
                        getString(SONG_ARTIST),
                        getString(SONG_ALBUM),
                        getInt(SONG_NUMBER),
                        getInt(SONG_YEAR)
                    )
                }
            }
            musicCursor.close()
            this.vSongs.sort()
            this.notifyChanged()
        }
    }
}