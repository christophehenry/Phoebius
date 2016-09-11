package augier.fr.phoebius.UI

import android.app.Activity
import android.support.v4.app.NavUtils
import augier.fr.phoebius.PlaylistDetailActivity
import augier.fr.phoebius.R
import augier.fr.phoebius.model.Playlist
import augier.fr.phoebius.utils.ApplicationUtilities
import com.arasthel.swissknife.annotations.OnItemClick
import groovy.transform.CompileStatic

@CompileStatic
public class PlaylistDetailFragment extends AbstractSongListFragment implements ApplicationUtilities
{
    Playlist songs
    @Override void onAttach(Activity activity)
    {
        super.onAttach(activity)
        songs = (activity as PlaylistDetailActivity).songs
    }

    /**
     * Callback when user clicks on a song
     *
     * This method uses <a href="https://github
     * .com/Arasthel/SwissKnife/wiki/@OnItemClick">Swissknife's @OnItemClick annotation</a>
     * @param position
     */
    @OnItemClick(R.id.songView)
    public void onItemClick(int position)
    {
        musicService.play(localSongs, position)
        NavUtils.navigateUpFromSameTask(activity)
    }

    @Override Playlist getLocalSongs(){ return songs }
}
