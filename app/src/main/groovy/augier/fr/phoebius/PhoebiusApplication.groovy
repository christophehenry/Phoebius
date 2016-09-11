package augier.fr.phoebius

import android.app.Application
import android.content.*
import android.os.IBinder
import augier.fr.phoebius.core.MusicService
import augier.fr.phoebius.providers.Albums
import augier.fr.phoebius.providers.Favorites
import augier.fr.phoebius.providers.Songs
import com.squareup.otto.Bus
import com.squareup.otto.ThreadEnforcer

public class PhoebiusApplication extends Application implements ServiceConnection
{
    private static final String CONF_KEY = "augier.fr.phoebius.config"
    private MusicService vMusicService
    private static PhoebiusApplication instance
    public static final Bus bus = new Bus(ThreadEnforcer.ANY)

    @Override
    void onCreate()
    {
        super.onCreate()
        instance = this
        initialize()
    }

    private void initialize()
    {
        bindService()
        Songs.backgroundQuery()
        Albums.backgroundQuery()
        Favorites.backgroundQuery()
        Runtime.runtime.addShutdownHook(new ShutdownHook())
    }

    @Override
    void onServiceConnected(ComponentName componentName, IBinder iBinder)
    {
        MusicService.MusicBinder binder = iBinder as MusicService.MusicBinder
        vMusicService = binder.service
        log("Got service")
    }

    @Override void onServiceDisconnected(ComponentName componentName){}

    private void dumpConfig()
    {
        Favorites.save()
        vMusicService.save()
    }

    private void bindService()
    {
        def intent = new Intent(applicationContext, MusicService.class)
        applicationContext.bindService(intent, this, BIND_AUTO_CREATE)
    }

    public static Context getAppContext(){ instance.applicationContext }
    public static MusicService getMusicService(){ instance.vMusicService }
    public static void saveAppProperties(){ instance.dumpConfig() }
    public static SharedPreferences getPreferences(){
        instance.getSharedPreferences(CONF_KEY, MODE_PRIVATE)
    }

    private class ShutdownHook extends Thread
    {
        @Override void run()
        {
            unbindService(PhoebiusApplication.this)
            dumpConfig()
        }
    }
}