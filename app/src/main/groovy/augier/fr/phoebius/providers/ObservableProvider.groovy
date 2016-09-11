package augier.fr.phoebius.providers

import android.widget.BaseAdapter
import com.arasthel.swissknife.SwissKnife
import com.arasthel.swissknife.annotations.OnUIThread
import groovy.transform.CompileStatic

@CompileStatic
trait ObservableProvider
{
    private static List<BaseAdapter> observers = []

    @OnUIThread private static void _notifyChanged(){ observers*.notifyDataSetChanged() }
    public static void notifyChanged(){ SwissKnife.runOnUIThread(this, "_notifyChanged") }
    public static void register(BaseAdapter adapter){
        if(!observers.contains(adapter)) observers << adapter
    }
    public static boolean unregister(BaseAdapter adapter){ observers.remove(adapter) }
}
