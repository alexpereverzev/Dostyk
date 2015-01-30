package litebonus.dostyk.adapter;

import android.app.Activity;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import litebonus.dostyk.fragments.PagerPage;

/**
 * Created by prog on 24.10.14.
 */
public class DynamicPagerAdapter extends PagerAdapter {
    Activity context;
    int pages;
    public DynamicPagerAdapter(int pages, Activity context){
        this.pages = pages;
        this.context = context;
    }

    @Override
    public Object instantiateItem(View collection, int position){
        View v = new PagerPage(context, position + 1);
        ((ViewPager) collection).addView(v, 0);
        return v;
    }

    @Override
    public void destroyItem(View collection, int position, Object view){
        ((ViewPager) collection).removeView((View) view);
    }

    @Override
    public int getCount(){
        return pages;
    }

    @Override
    public boolean isViewFromObject(View view, Object object){
        return view.equals(object);
    }

    @Override
    public void finishUpdate(View arg0){
    }

    @Override
    public void restoreState(Parcelable arg0, ClassLoader arg1){
    }

    @Override
    public Parcelable saveState(){
        return null;
    }

    @Override
    public void startUpdate(View arg0){
    }
}
