package augier.fr.phoebius.UI


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import augier.fr.phoebius.R
import com.arasthel.swissknife.SwissKnife
import com.arasthel.swissknife.annotations.OnClick

public class NavbarFragment extends Fragment
{
    @Override
    View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_navbar, container, false)
        SwissKnife.inject(this, view)

        return view
    }

    @OnClick(R.id.action_end) public void onClick(){ System.exit(0) }
}
