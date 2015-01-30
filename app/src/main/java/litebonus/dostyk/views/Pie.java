package litebonus.dostyk.views;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by prog on 27.10.14.
 */
public class Pie extends LinearLayout {
    private int position;
    private List<View> viewList;
    public Pie(Context context) {
        super(context);
        viewList = new ArrayList<>();
        position = 0;

    }
    public Pie(Context context, List<View> views){
        super(context);
        this.viewList = views;
        position = 0;
        this.removeAllViews();
        this.addView(viewList.get(position));
    }

    public void putView(View view){

        viewList.add(view);
        if(viewList.size() > 0){
            this.removeAllViews();
            this.addView(viewList.get(position));
        }
    }

    public void next(){
        if(position < (viewList.size() - 1)){
            position++;
        }else{
            position = 0;
        }
        this.removeAllViews();
        this.addView(viewList.get(position));
    }

    public void previos(){
        if(position > 0){
            position--;
        }else{
            position = viewList.size() - 1;
        }
        this.removeAllViews();
        this.addView(viewList.get(position));
    }

    public void setCurentView(String tag){
        /*if(position < viewList.size() - 1 && position >= 0){
            this.removeAllViews();
            this.addView(viewList.get(position));
        }*/
        for(View view : viewList){
            String viewTag = view.getTag().toString();
            if(viewTag.equals(tag)){
                this.removeAllViews();
                this.addView(view);
            }
        }
    }
}
