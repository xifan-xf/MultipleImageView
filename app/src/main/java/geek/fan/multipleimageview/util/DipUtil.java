package geek.fan.multipleimageview.util;

import android.content.Context;

/**
 * Created by fan on 16/3/16.
 */
public class DipUtil {
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) ((dipValue * scale) + 0.5f);
    }
}
