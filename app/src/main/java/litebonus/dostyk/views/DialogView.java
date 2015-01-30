package litebonus.dostyk.views;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import litebonus.dostyk.R;
import litebonus.dostyk.core.modules.LayoutModule;
import litebonus.dostyk.core.modules.StyleModule;

/**
 * Created by Alexander on 29.10.2014.
 */
public class DialogView extends DialogFragment {

    private LinearLayout titleTextContainer;
    private LinearLayout imageContainer;
    private LinearLayout bottom;
    private LinearLayout content;
    private LinearLayout header;

    private JSONObject dialog_header;
    private JSONObject dialog_content;
    private JSONObject dialog_bottom;
    private JSONObject dialog_image;
    private JSONArray data;
    private JSONArray style;

    private long styles;
    private long action;

    public DialogView(){

    }

    public static DialogView getInsance(Bundle b){
        DialogView view=new DialogView();
        view.setArguments(b);
        return view;
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment, null);
        bottom = (LinearLayout) view.findViewById(R.id.bottom);
        titleTextContainer = (LinearLayout) view.findViewById(R.id.title);
        header=(LinearLayout) view.findViewById(R.id.title_container);
        content = (LinearLayout) view.findViewById(R.id.content);
        imageContainer = (LinearLayout) view.findViewById(R.id.image);

        if(getArguments()!=null){
            try {
                dialog_header= new JSONObject(getArguments().getString("dialog_header"));
                dialog_content= new JSONObject(getArguments().getString("dialog_content"));
                dialog_bottom=new JSONObject(getArguments().getString("dialog_bottom"));
                dialog_image=new JSONObject(getArguments().getString("dialog_image"));
                data=new JSONArray(getArguments().getString("data"));
                style=new JSONArray(getArguments().getString("style"));
                styles=getArguments().getLong("styleid");
                action=getArguments().getLong("action");


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
            titleTextContainer.addView(LayoutModule.createLayout(getActivity(), dialog_header, data, style, -1));

            content.addView(LayoutModule.createLayout(getActivity(), dialog_content, data, style, -1));

            bottom.addView(LayoutModule.createLayout(getActivity(), dialog_bottom, data, style, -1));

            imageContainer.addView(LayoutModule.createLayout(getActivity(), dialog_image, data, style, -1));


        StyleModule.setStyle(getActivity(),header, style, styles, -1);
        StyleModule.setStyle(getActivity(), content, style, styles, action);


        imageContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        return view;
    }




}
