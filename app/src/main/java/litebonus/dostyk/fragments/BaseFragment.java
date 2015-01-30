package litebonus.dostyk.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import litebonus.dostyk.core.Screen;
import litebonus.dostyk.core.Views;
import litebonus.dostyk.widget.BounceScrollView;

/**
 * Created by prog on 03.10.2014.
 */
public class BaseFragment extends Fragment {

    private BounceScrollView v;
    public static LinearLayout layout;

    public LinearLayout getLayout() {
        return layout;
    }

    public LinearLayout setLayout(LinearLayout l) {
        layout = l;
        return layout;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = new BounceScrollView(getActivity());

        View paren = (View) Screen.getInstance().createView(getActivity());

        if (getArguments() != null) {
            if (getArguments().getString("gravity") != null) {
                ScrollView.LayoutParams params = new ScrollView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.gravity = Gravity.CENTER;
                v.setLayoutParams(params);
            } else {
                Bundle bundle=getArguments();
                System.out.print(bundle);
                LinearLayout layout1 = (LinearLayout) paren.findViewWithTag(getArguments().getInt("tag"));
                if (getArguments().getInt("tag") != -100) {
                    if(layout1!=null)
                    layout1.setVisibility(View.GONE);
                } else {
                    ViewGroup viewGroup = (ViewGroup) paren;
                    LinearLayout linearLayout = (LinearLayout) viewGroup.getChildAt(0);
                    EditText editText = (EditText) linearLayout.getChildAt(0);
                    editText.setText(getArguments().getString("search"));
                    layout1 = (LinearLayout) paren.findViewWithTag(getArguments().getInt("tag_next"));
                    if(layout1!=null) {
                        ViewGroup parent = (ViewGroup) layout1.getParent();
                        parent.removeView(layout1);
                        parent.addView(layout);
                    }
                    //    layout1.removeAllViews();
                    //    layout1.addView(layout);
                    // layout1.setVisibility(View.VISIBLE);
                }
            }
        }
        Views.getInstance().postLoad();
        v.addView(paren);

        return v;
    }
}
