package augier.fr.phoebius.utils


import android.widget.Toast
import groovy.transform.CompileStatic

@CompileStatic
final class MessageHelper implements ApplicationUtilities
{
    private MessageHelper(){ /* Helper class */ }

    public static void post(String s){ Toast.makeText(appContext, s, Toast.LENGTH_LONG).show() }
    public static void post(int res){ /* TODO: I18n helper*/ }
}
