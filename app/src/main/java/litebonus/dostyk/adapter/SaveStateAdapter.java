package litebonus.dostyk.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * Created by Alexander on 10.10.2014.
 */
public class SaveStateAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> mFragments;

    public SaveStateAdapter(FragmentManager fm,List<Fragment> fragmentList) {
        super(fm);
        mFragments=fragmentList;
    }

    @Override
    public Fragment getItem(int i) {
        return mFragments.get(i);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }
}
