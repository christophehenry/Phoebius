package augier.fr.phoebius.UI

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import augier.fr.phoebius.PlaylistDetailActivity
import augier.fr.phoebius.R
import augier.fr.phoebius.providers.Favorites
import augier.fr.phoebius.utils.ApplicationUtilities
import com.arasthel.swissknife.SwissKnife
import com.arasthel.swissknife.annotations.InjectView
import com.arasthel.swissknife.annotations.OnItemClick
import groovy.transform.CompileStatic

@CompileStatic
public class PlaylistsFragment extends Fragment implements ApplicationUtilities
{
    @InjectView private ListView songView

    @Override
    View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_song_list, container, false)
        SwissKnife.inject(this, view)
        songView.adapter = new PlaylistAdaptater()
        return view
    }

    @OnItemClick(R.id.songView)
    public void onItemClick(int position)
    {
        def playlistName = Favorites.getFavoritesNames()[position]
        def intent = new Intent(activity, PlaylistDetailActivity.class)
        intent.putExtra("songs", Favorites.get(playlistName) as Parcelable)
        startActivity(intent)
    }

    private class PlaylistAdaptater extends BaseAdapter
    {
        @Override int getCount(){ return Favorites.size() }
        @Override Object getItem(int position){ return null }
        @Override long getItemId(int position){ return 0 }

        public View getView(int position, View convertView, ViewGroup parent)
        {
            def id = R.layout.playlist_item
            PlaylistHolder holder
            if(!convertView)
            {
                convertView = LayoutInflater.from(getActivity()).inflate(id, parent, false)
                holder = new PlaylistHolder()
                holder.playlistName = convertView.findViewById(R.id.playlistName) as TextView
                convertView.setTag(holder)
            }
            else holder = convertView.getTag() as PlaylistHolder
            holder.playlistName.text = Favorites.getFavoritesNames()[position]
            return convertView
        }

        private class PlaylistHolder{ TextView playlistName }
    }
}
