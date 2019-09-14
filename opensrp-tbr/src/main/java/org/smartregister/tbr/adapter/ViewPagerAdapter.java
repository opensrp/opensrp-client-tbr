package org.smartregister.tbr.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import android.widget.MediaController;
import android.widget.VideoView;

import org.smartregister.tbr.R;
import org.smartregister.tbr.util.Constants;

import static org.smartregister.tbr.R.*;

public class ViewPagerAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private Integer [] images;

    public ViewPagerAdapter(Context context, int week) {

        this.context = context;
        if(Constants.getMonthsImageList().containsKey(week))
            images = Constants.getMonthsImageList().get(week);
        else
            images = new Integer[]{drawable.infographic_no};

    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(layout.custom_layout, null);

        Resources resources = context.getResources();
        String res = resources.getResourceTypeName(images[position]);

        ImageView imageView = (ImageView) view.findViewById(id.imageView);
        final VideoView videoView = (VideoView) view.findViewById(id.videoPlaceHolder);

        if (res.equals("drawable")){

            imageView.setImageResource(images[position]);
            videoView.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);


        } else if (res.equals("raw")){

            imageView.setVisibility(View.GONE);

            final MediaController mediaController= new MediaController(context);

            //specify the location of media file
            Uri uri=Uri.parse(("android.resource://" + context.getPackageName() + "/" + images[position]));

            //Setting MediaController and URI, then starting the videoView
            videoView.setMediaController(mediaController);
            videoView.setVideoURI(uri);
            videoView.requestFocus();
            videoView.setZOrderOnTop(true);

            mediaController.setAnchorView(videoView);

            videoView.setVisibility(View.VISIBLE);


        }

        ViewPager vp = (ViewPager) container;
        vp.addView(view, 0);
        return view;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);

    }

}