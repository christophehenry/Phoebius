package augier.fr.phoebius.UI


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import augier.fr.phoebius.R
import augier.fr.phoebius.model.Playlist
import augier.fr.phoebius.providers.Song
import augier.fr.phoebius.utils.SquareImageView
import com.arasthel.swissknife.SwissKnife
import com.arasthel.swissknife.annotations.InjectView
import groovy.transform.CompileStatic

@CompileStatic
public abstract class AbstractSongListFragment extends Fragment
{
    @InjectView protected ListView songView

    AbstractSongListFragment(){ super() }

    abstract Playlist getLocalSongs()

    @Override
    View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_song_list, container, false)
        SwissKnife.inject(this, view)
        songView.adapter = new SongAdapter()

        return view
    }

    protected class SongAdapter extends BaseAdapter
    {
        @Override public int getCount(){ return getLocalSongs().size() }
        @Override Object getItem(int position){ return null }
        @Override long getItemId(int position){ return 0 }

        public View getView(int position, View convertView, ViewGroup parent)
        {
            def id = R.layout.song_item
            SongHolder holder
            if(!convertView)
            {
                convertView = LayoutInflater.from(getActivity()).inflate(id, parent, false)
                holder = new SongHolder()
                holder.songArtist = convertView.findViewById(R.id.songArtist) as TextView
                holder.songTitle = convertView.findViewById(R.id.songTitle) as TextView
                holder.songCover = convertView.findViewById(R.id.songCover) as SquareImageView
                convertView.setTag(holder)
            }
            else holder = convertView.getTag() as SongHolder

            Song currSong = getLocalSongs()[position]

            holder.songCover.imageBitmap = currSong.cover
            holder.songTitle.text = currSong.title
            holder.songArtist.text = currSong.artist

            return convertView
        }

        private class SongHolder
        {
            SquareImageView songCover
            TextView songTitle
            TextView songArtist
        }
    }
}