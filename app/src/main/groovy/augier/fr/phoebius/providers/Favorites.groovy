package augier.fr.phoebius.providers


import android.util.Log
import augier.fr.phoebius.model.Playlist
import augier.fr.phoebius.utils.ApplicationUtilities
import com.arasthel.swissknife.SwissKnife
import com.arasthel.swissknife.annotations.OnBackground
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import groovy.transform.CompileStatic


/**
 * Provider for favorite playlists.
 * Performs query, save and load of playlists.
 */
@CompileStatic
public final class Favorites implements ApplicationUtilities, ObservableProvider
{
    private static final String PREFERENCE_KEY = "phoebius.favorites"
    private static final Object vLock = new Object()
    private static HashMap<String, List<Long>> vFavorites = [Test:[]]

    public static boolean createFavorite(String name, Playlist playlist=[])
    {
        synchronized(vLock)
        {
            if(vFavorites.containsKey(name)) return false
            else
            {
                vFavorites[name] = playlist.collect{ it.ID }
                return true
            }
        }
    }

    public static Set<String> getFavoritesNames(){ synchronized(vLock){ vFavorites.keySet() } }

    public static boolean deleteFavorites(String name)
    {
        synchronized(vLock){
             return vFavorites.remove(name) == null
        }
    }

    public static void backgroundQuery(){ SwissKnife.runOnBackground(this, "query") }

    @OnBackground
    public static void query(){
        synchronized(vLock)
        {
            String rawJSON = getPreferences().getString(PREFERENCE_KEY, "{}")
            try
            {
                def conf = (Map<String, List<String>>)new JsonSlurper().parseText(rawJSON)
                conf?.each{
                    List<Long> ids = []
                    it.value?.each{ ids << Long.parseLong(it) }
                    vFavorites[it.key] = ids
                }
            }
            catch(Exception e){ Log.e("${this.class.name}", "Couldn't parse favorite songs", e) }
            this.notifyChanged()
        }
    }

    public static void save()
    {
        synchronized(vLock)
        {
            def editor = getPreferences().edit()
            editor.putString(PREFERENCE_KEY, new JsonBuilder(vFavorites).toPrettyString())
            editor.commit()
        }
    }

    public static int size(){ synchronized(vLock){ return vFavorites.size() } }
    public static Playlist get(String name)
    {
        synchronized(vLock){
            return Songs.findByIds(vFavorites[name])
        }
    }

    public static void addToPlaylist(String name, Song song)
    {
        synchronized(vLock)
        {
            if(vFavorites.containsKey(name)) vFavorites[name] << song.ID
            else vFavorites[name] = [song.ID]
        }
    }
}