package augier.fr.phoebius.UI

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.TextView
import augier.fr.phoebius.AlbumDetailActivity
import augier.fr.phoebius.PhoebiusApplication
import augier.fr.phoebius.R
import augier.fr.phoebius.model.Album
import augier.fr.phoebius.providers.Albums
import augier.fr.phoebius.utils.SquareImageView
import com.arasthel.swissknife.SwissKnife
import com.arasthel.swissknife.annotations.InjectView
import com.arasthel.swissknife.annotations.OnItemClick
import groovy.transform.CompileStatic
/**
 * Fragment to display the albumName list
 *
 * This class uses <a href="https://github.com/Arasthel/SwissKnife">SwissKnife</a>.
 * The views are injected in the {@link AlbumListFragment#onCreateView onCreateView} method
 */
@CompileStatic
public class AlbumListFragment extends Fragment
{
    /**
     * Albums view (RLY!?)
     * The view s automatically injected by SwissKnife on start.
     *
     * See https://github.com/Arasthel/SwissKnife/wiki#how-to-use-the-annotations
     */
    @InjectView private GridView albumView

    AlbumListFragment()
    {
        super()
        PhoebiusApplication.bus.register(this)
    }

    @Override
    View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_album_list, container, false)
        SwissKnife.inject(this, view)
        AlbumAdapter albumAdapter = new AlbumAdapter()
        albumView.adapter = albumAdapter

        return view
    }

    @OnItemClick(R.id.albumView)
    public void onItemClick(int position)
    {
        Album currAlbum = allAlbums[position]
        def intent = new Intent(activity, AlbumDetailActivity.class)
        intent.putExtra("album", currAlbum as Parcelable)
        startActivity(intent)
    }

    private List<Album> getAllAlbums(){ return Albums.allAlbums }

    /**
     * Adaptater to createOne a grid of albums
     */
    class AlbumAdapter extends BaseAdapter
    {
        AlbumAdapter()
        {
            super()
            Albums.register(this)
        }

        @Override public int getCount(){ return getAllAlbums().size() }
        @Override Object getItem(int position){ return null }
        @Override long getItemId(int position){ return 0 }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            def id = R.layout.album_item
            def holder
            if(!convertView)
            {
                convertView = LayoutInflater.from(getActivity()).inflate(id, parent, false)
                holder = new AlbumHolder()
                holder.albumTitle = convertView.findViewById(R.id.albumTitle) as TextView
                holder.albumArtist = convertView.findViewById(R.id.albumArtist) as TextView
                holder.albumNbSongs = convertView.findViewById(R.id.albumNbSongs) as TextView
                holder.albumCover = convertView.findViewById(R.id.albumCover) as SquareImageView
                convertView.setTag(holder)
            }
            else holder = convertView.getTag() as AlbumHolder
            Album currAlbum = getAllAlbums()[position]
            holder.albumTitle.text = currAlbum.albumTitle
            holder.albumArtist.text = currAlbum.albumArtist
            holder.albumNbSongs.text = "${currAlbum.nbSongs} pistes"
            holder.albumCover.imageBitmap = currAlbum.cover

            return convertView
        }

        private class AlbumHolder
        {
            TextView albumTitle
            TextView albumArtist
            TextView albumNbSongs
            SquareImageView albumCover
        }
    }
}
