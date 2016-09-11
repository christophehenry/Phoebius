package augier.fr.phoebius.utils

import android.os.Handler
import com.arasthel.swissknife.SwissKnife
import com.arasthel.swissknife.annotations.OnBackground
/**
 * This clas will handle a refreshing job executed regularly on a separate thread
 * Thread-safe
 */
public class Refresher
{
    private boolean isStarted = false
    private int refreshTime
    private Closure onRefresh
    private Handler handler = new Handler()
    private Runnable refresh = new Runnable(){
        @Override void run()
        {
            if(!isStarted){ return }
            onRefresh()
            handler.postDelayed(this, refreshTime)
        }
    }
    public Refresher(Closure callback, int refreshTime = 1000)
    {
        this.refreshTime = refreshTime
        onRefresh = callback
    }

    @OnBackground private void threadedOnRefresh(){ handler.post(refresh) }

    public void setOnRefresh(Closure callback){ onRefresh = callback }

    public void start()
    {
        if(isStarted){ return }
        isStarted = true
        SwissKnife.runOnBackground(this, "threadedOnRefresh")
    }

    public void stop()
    {
        isStarted = false
        handler.removeCallbacks(refresh)
    }
}