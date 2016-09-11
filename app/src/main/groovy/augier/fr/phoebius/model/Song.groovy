package augier.fr.phoebius.providers

import android.content.ContentUris
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import augier.fr.phoebius.model.Album
import augier.fr.phoebius.utils.ApplicationUtilities
import augier.fr.phoebius.utils.Queryable
import com.arasthel.swissknife.annotations.Parcelable
import groovy.transform.CompileStatic
/**
 * Representation of a song
 */
@CompileStatic
@Parcelable(exclude = { album })
class Song implements Comparable, Queryable, ApplicationUtilities
{
    private Long id
    private String title
    private String artist
    private String albumName
    private Album album
    private Integer trackNumber
    private Integer year

    /**
     * Builds a new song
     * @param songID ID of the song in the Android's DB
     * @param songTitle Songs title
     * @param songArtist Artist of the song
     * @param songAlbum Songs's albumName
     * @param songNb Songs's rank on the albumName
     * @param songYear Songs's year of release
     */
    public Song(Long songID, String songTitle, String songArtist,
                  String songAlbum, Integer songNb, Integer songYear)
    {
        id = songID
        title = songTitle
        artist = songArtist
        albumName = songAlbum
        trackNumber = songNb
        year = songYear
    }

    /**
     * Constructor for default song
     */
    Song(){ this(new Long(-1), "Song title", "Song artist", "Album name", 0, 0) }

    //region GET/SET
    /** @return Songs id */
    public Long getID(){ return id }

    /** @return Songs title */
    public String getTitle(){ return title }

    /** @return Songs artist */
    public String getArtist(){ return artist }

    /** @return Songs URI (path in FS) */
    public Uri getURI()
    {
        return ContentUris.withAppendedId(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
    }

    /** @return Songs albumName     */
    public Album getAlbum()
    {
        if(!album){ album = Albums.findByName(albumName) }
        return album
    }

    /** @return Songs rank in albumName     */
    public int getTrackNumber(){ return trackNumber }

    /** @return Songs year of release     */
    public int getYear(){ return year }

    public Bitmap getCover(){ return this.getAlbum().cover }
    //endregion

    /**
     * Compares for sorting
     *
     * Compare by 2 criterias: by albumName title first and then by rank on the albumName.
     *
     * @param o Other object to compare to
     * @return Result of comparison
     */
    @Override
    int compareTo(Object o)
    {
        if(!o instanceof Song) return 0
        Song other = o as Song
        return this.albumName <=> other.albumName ?: this.trackNumber <=> other.trackNumber
    }

    @Override
    public String toString()
    {
        "Song{id=" + id + ", title='" + title + '\'' + ", artist='" + artist + '\'' +
            ", albumName='" + albumName + '\'' + ", album=" + album +
            ", trackNumber=" + trackNumber + ", year=" + year + '}'
    }
}