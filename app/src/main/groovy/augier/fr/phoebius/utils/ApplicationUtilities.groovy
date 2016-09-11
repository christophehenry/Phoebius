package augier.fr.phoebius.utils

import android.content.Context
import android.content.SharedPreferences
import augier.fr.phoebius.PhoebiusApplication
import augier.fr.phoebius.core.MusicService
import groovy.transform.CompileStatic

@CompileStatic
public trait ApplicationUtilities
{
    public static Context getAppContext(){ PhoebiusApplication.appContext }
    public static MusicService getMusicService(){ PhoebiusApplication.musicService }
    public static void saveAppProperties(){ PhoebiusApplication.saveAppProperties() }
    public static SharedPreferences getPreferences(){ PhoebiusApplication.preferences }
}