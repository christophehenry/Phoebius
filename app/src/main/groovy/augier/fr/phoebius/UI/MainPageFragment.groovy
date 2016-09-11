package augier.fr.phoebius.UI

import android.app.Activity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import augier.fr.phoebius.MainActivity
import augier.fr.phoebius.PhoebiusApplication
import augier.fr.phoebius.R
import com.arasthel.swissknife.SwissKnife
import com.arasthel.swissknife.annotations.InjectView
import groovy.transform.CompileStatic
/**
 * Fragment for the main view of the application. Manages the {@link ViewPager} and the tabs.
 *
 * This class uses <a href="https://github.com/Arasthel/SwissKnife">SwissKnife</a>.
 * The views are injected in the {@link MainPageFragment#onCreateView onCreateView} method
 */
@CompileStatic
public class MainPageFragment extends Fragment
{
    @InjectView private ViewPager mainPager
    @InjectView private TabLayout slidingTabs
    private PagerAdaptater pagerAdaptater

    /**
     * Constructor
     * @param fm Fragment manager from main activity (just use {@link
     * MainActivity # getSupportFragmentManager}
     * @param ms Music service from the main activity (just use {@link
     * PhoebiusApplication # getMusicService}
     */
    public MainPageFragment(){ super() }

    @Override
    void onAttach(Activity activity)
    {
        super.onAttach(activity)
        def a = activity as AppCompatActivity
        pagerAdaptater = new PagerAdaptater(a.supportFragmentManager)
    }

    @Override
    View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_main_page, container, false)
        SwissKnife.inject(this, view)

        mainPager.adapter = pagerAdaptater
        slidingTabs.setupWithViewPager(mainPager)
        return view
    }

    /**
     * Adaptater to manage displaying of the tabs
     */
    class PagerAdaptater extends FragmentPagerAdapter
    {
        /**
         * Title of each tab
         *
         * The title have to be ordered the same order than
         * the corresponding fragments
         *
         * @see {@link #FRAGMENTS}
         */
        private final int[] HEADER_ITEMS = [
            R.string.curr_playing as int,
            R.string.titles as int,
            R.string.album as int,
            R.string.playlist as int
        ]

        /**
         * {@link Fragment} to be displayed for each tab
         *
         * The fragments have to be ordered the same order than
         * the correspondoig tab titles.
         *
         * @see {@link #HEADER_ITEMS}
         */
        private final Fragment[] FRAGMENTS = [
            new CurrentPlaylistFragment(),
            new SongListFragment(),
            new AlbumListFragment(),
            new PlaylistsFragment()
        ]

        /** Constructor. Nothing special */
        public PagerAdaptater(FragmentManager fm){ super(fm) }

        /**
         * Returns the {@link Fragment} for the queried tab
         * @param arg0 Index of the queried {@link Fragment}
         * @return The {@link Fragment} corresponding to the index in the {@link #FRAGMENTS} array
         */
        @Override public Fragment getItem(int arg0){ return FRAGMENTS[arg0] }

        /**
         * Get the number of pages
         * @return number of pages
         */
        @Override public int getCount(){ return HEADER_ITEMS.length }

        /**
         * Returns the title for the queried tab
         * @param position Index of the queried title
         * @return The title corresponding to the index in the {@link #HEADER_ITEMS} array
         */
        @Override CharSequence getPageTitle(int position){ return getText(HEADER_ITEMS[position]) }
    }
}
