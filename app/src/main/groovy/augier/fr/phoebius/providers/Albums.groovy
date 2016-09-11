package augier.fr.phoebius.providers

import android.database.Cursor
import android.graphics.Bitmap
import augier.fr.phoebius.model.Album
import augier.fr.phoebius.utils.ApplicationUtilities
import augier.fr.phoebius.utils.Queryable
import com.arasthel.swissknife.SwissKnife
import com.arasthel.swissknife.annotations.OnBackground
import groovy.transform.CompileStatic


/**
 * Provider for albums. Queries album in Android's database.
 * Provides methods to query albums in memory.
 */
@CompileStatic
public final class Albums implements Queryable, ApplicationUtilities, ObservableProvider
{
    private static final Album vDefaultAlbum = new Album()
    private static final Object vLock = new Object()
    private static List<Album> vAlbums = []

    public static List<Album> getAllAlbums(){ synchronized(vLock){ return vAlbums } }

    public static Album findByName(String name)
    {
        synchronized(vLock)
        {
            return this.vAlbums.find{
                it.albumTitle == name
            } ?: vDefaultAlbum
        }
    }

    public static Bitmap getDefaultCover(){ vDefaultAlbum.cover }

    public static void backgroundQuery(){ SwissKnife.runOnBackground(this, "query") }

    @OnBackground
    public static void query()
    {
        synchronized(vLock)
        {
            Cursor albumCursor = getAppContext()
                .contentResolver.query(ALBUM_URI, ALBUM_COLS, null, null, null)

            if(albumCursor?.moveToFirst())
            {
                def getString = { String columnName ->
                    return albumCursor.getString(
                        albumCursor.getColumnIndex(columnName))
                }

                while(albumCursor.moveToNext())
                {
                    this.vAlbums << new Album(
                        getString(ALBUM_TITLE),
                        getString(ALBUM_ARTIST),
                        getString(ALBUM_DATE),
                        getString(ALBUM_NB_SONG),
                        getString(ALBUM_COVER)
                    )
                }
            }
            albumCursor.close()
            this.vAlbums.sort{ it.albumTitle }
            this.notifyChanged()
        }
    }
}