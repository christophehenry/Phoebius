package augier.fr.phoebius

import android.content.Context
import android.os.Bundle
import android.support.v4.app.NavUtils
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import android.widget.ListView
import augier.fr.phoebius.model.Album
import augier.fr.phoebius.utils.ApplicationUtilities
import augier.fr.phoebius.utils.MessageHelper
import augier.fr.phoebius.utils.SquareImageView
import com.arasthel.swissknife.SwissKnife
import com.arasthel.swissknife.annotations.InjectView
import com.arasthel.swissknife.annotations.OnClick
import com.arasthel.swissknife.annotations.OnItemClick
import groovy.transform.CompileStatic

@CompileStatic
public class AlbumDetailActivity extends AppCompatActivity implements ApplicationUtilities
{
    @InjectView private SquareImageView albumDetailCover
    @InjectView private ListView albumDetailSongList
    private Album album

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState)
        album = intent.getParcelableExtra("album") as Album
        this.contentView = R.layout.activity_album_detail
        SwissKnife.inject(this)
        albumDetailCover.imageBitmap = album.cover
        def songs = album.getSongs().collect{ it.title }
        albumDetailSongList.adapter = new ArrayAdapter<String>(
            this as Context, android.R.layout.simple_list_item_1, songs)
    }

    @OnClick(R.id.albumDetailPlayAllButton)
    public void onClickPlayAll()
    {
        musicService.play(album.songs)
        onBackPressed()
    }

    @OnClick(R.id.albumDetailEnqueueAllButton)
    public void onClickEnqueueAll()
    {
        musicService.enqueue(album.songs)
        //TODO: I18n
        MessageHelper.post("Songs enqueud")
    }

    @OnItemClick(R.id.albumDetailSongList)
    public void onItemClick(int position)
    {
        musicService.play(album.songs, position)
        onBackPressed()
    }

    @Override void onBackPressed(){ NavUtils.navigateUpFromSameTask(this) }
}
