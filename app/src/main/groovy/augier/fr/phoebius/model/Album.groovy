package augier.fr.phoebius.model


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import augier.fr.phoebius.R
import augier.fr.phoebius.providers.Albums
import augier.fr.phoebius.providers.Songs
import augier.fr.phoebius.utils.ApplicationUtilities
import com.arasthel.swissknife.annotations.Parcelable

/**
 * Represents an Album
 *
 * This class is used by {@link augier.fr.phoebius.UI.AlbumListFragment}
 * in adaptater to display albums
 *
 * @see {@link augier.fr.phoebius.core.PlaylistManager}
 */
@Parcelable(exclude = { cover; songs })
class Album implements Comparable, ApplicationUtilities
{
    private String albumArtist
    private String albumTitle
    private String date
    private String nbSongs
    private String coverPath
    private Bitmap cover
    private Playlist songs
    /**
     * Constructor
     *
     * @param albumTitle Title of albumName
     * @param albumArtist Name of albumName's artist
     * @param date Date of release
     * @param nbSongs Number of songs on the albumName
     * @param coverPath Artwork path (will be resoved to a {@link Bitmap}
     */
    public Album(String albumTitle, String albumArtist,
                 String date, String nbSongs, String coverPath)
    {
        this.albumArtist = albumArtist
        this.albumTitle = albumTitle
        this.date = date ?: ""
        this.nbSongs = nbSongs
        this.coverPath = coverPath
    }

    /**
     * Empty constructor for default album
     */
    public Album()
    {
        this("", "", "", "", "")
        this.cover = BitmapFactory.decodeResource(
            appContext.resources, R.drawable.default_cover)
    }

    //region GET/SET
    /** @return Albums artist */
    String getAlbumArtist(){ return albumArtist }

    /** @return Albums title */
    String getAlbumTitle(){ return albumTitle }

    /** @return Albums release date */
    String getDate(){ return date }

    /** @return Albums artist */
    String getNbSongs(){ return nbSongs }

    /** @return Albums artist */
    Bitmap getCover()
    {
        if(!cover)
        {
            if(coverPath != null && new File(coverPath).exists())
                this.cover = BitmapFactory.decodeFile(coverPath)
            else this.cover = Albums.getDefaultCover()
        }
        return cover
    }

    Playlist getSongs()
    {
        if(!songs) songs = Songs.findByAlbum(albumTitle)
        return songs
    }

    @Override
    int compareTo(Object o)
    {
        if(!o instanceof Album) return 0
        Album other = o as Album
        int byArtist = this.albumArtist <=> other.albumArtist
        int byDate = this.date <=> other.date
        int byTitle = this.albumTitle <=> other.albumTitle

        if(byArtist) return byArtist
        if(byDate) return byDate
        return byTitle
    }
}