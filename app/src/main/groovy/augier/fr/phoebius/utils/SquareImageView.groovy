package augier.fr.phoebius.utils
import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import groovy.transform.CompileStatic
/**
 * Overriding class used to display square images
 */
@CompileStatic
public class SquareImageView extends ImageView
{
    SquareImageView(Context context, AttributeSet attrs){ super(context, attrs) }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        def dim = Math.max(widthMeasureSpec, heightMeasureSpec)
        super.onMeasure(dim, dim)
        setMeasuredDimension(dim, dim)
    }
}
