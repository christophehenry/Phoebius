package augier.fr.phoebius.UI


import android.os.Bundle
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import augier.fr.phoebius.PhoebiusApplication
import augier.fr.phoebius.R
import augier.fr.phoebius.model.Playlist
import augier.fr.phoebius.providers.Favorites
import augier.fr.phoebius.providers.Song
import augier.fr.phoebius.providers.Songs
import augier.fr.phoebius.utils.ApplicationUtilities
import com.arasthel.swissknife.annotations.InjectView
import com.arasthel.swissknife.annotations.OnItemClick
import com.arasthel.swissknife.annotations.OnItemLongClick
import groovy.transform.CompileStatic
/**
 * Fragment to display the song list
 *
 * This class uses <a href="https://github.com/Arasthel/SwissKnife">SwissKnife</a>.
 * The views are injected in the {@link SongListFragment#onCreateView onCreateView} method
 */
@CompileStatic
public class SongListFragment extends AbstractSongListFragment implements ApplicationUtilities
{
    @InjectView private ListView songView
    private Song songSelected

    SongListFragment()
    {
        super()
        PhoebiusApplication.bus.register(this)
    }

    @Override
    View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        def view = super.onCreateView(inflater, container, savedInstanceState)
        registerForContextMenu(view)
        return view
    }

    @Override Playlist getLocalSongs(){ return Songs.songs }

    @Override
    void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo)
        MenuInflater inflater = activity.menuInflater
        inflater.inflate(R.menu.playlists_contextual, menu)
        Favorites.favoritesNames.each{ menu.add(it) }
    }

    @Override
    boolean onContextItemSelected(MenuItem item)
    {
        int l = Favorites.size()
        String playlistName = Favorites.getFavoritesNames()[item.itemId as int]
        if(0 <= item.itemId && item.itemId < l && songSelected != null)
        {
            Favorites.addToPlaylist(playlistName, songSelected)
            songSelected = null
            return true
        }
        return false
    }

    /**
     * Callback when user clicks on a song
     *
     * This method uses <a href="https://github
     * .com/Arasthel/SwissKnife/wiki/@OnItemClick">Swissknife's @OnItemClick annotation</a>
     * @param position
     */
    @OnItemClick(R.id.songView)
    public void onItemClick(int position){ musicService.play(localSongs, position) }

    @OnItemLongClick(R.id.songView)
    public boolean onItemLongClick(int position)
    {
        songSelected = Songs.getSongs()[position]
        songView.showContextMenu()
        return true
    }
}