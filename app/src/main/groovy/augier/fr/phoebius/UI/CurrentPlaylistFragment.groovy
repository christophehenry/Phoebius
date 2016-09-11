package augier.fr.phoebius.UI

import android.os.Bundle
import android.support.annotation.Nullable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import augier.fr.phoebius.model.Playlist
import augier.fr.phoebius.utils.ApplicationUtilities
import groovy.transform.CompileStatic

@CompileStatic
class CurrentPlaylistFragment extends AbstractSongListFragment implements ApplicationUtilities
{
    @Override Playlist getLocalSongs(){ musicService?.currentPlaylist ?: [] as Playlist }

    @Override
    View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        def view = super.onCreateView(inflater, container, savedInstanceState)
        musicService?.register(songView.adapter as BaseAdapter)
        return view
    }

    @Override
    void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState)
        activity.runOnUiThread(new Runnable(){
            @Override void run(){ getView().invalidate() }
        })
    }
}
