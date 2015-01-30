package litebonus.dostyk.core;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

import litebonus.dostyk.views.Picker;

/**
 * Created by prog on 29.10.14.
 */
public class Views {
    private static Views instance;
    private List<View> viewList;
    private List<View> afterLoadList;
    private Views(){
        viewList = new ArrayList<>();
        afterLoadList = new ArrayList<>();
    }
    public static Views getInstance() {
        if(instance == null){
            instance = new Views();
        }
        return instance;
    }

    public void resetInstance(){
        instance = new Views();
    }

    public List<View> getViewList() {
        return viewList;
    }

    public View find(String tag){
        for(View v : viewList){
            if(v.getTag() != null) {
                if (v.getTag().toString().equals(tag)) {
                    return v;
                }
            }
        }
        return null;
    }

    public List<View> getAfterLoadList() {
        return afterLoadList;
    }

    public void setAfterLoadList(List<View> afterLoadList) {
        this.afterLoadList = afterLoadList;
    }


 static boolean flag_postload=false;

    public static boolean isFlag_postload() {
        return flag_postload;
    }

    public void postLoad(){
        for(View v : afterLoadList){
            if(v instanceof Picker){
                ((Picker)v).activate();
            }
            //v.callOnClick();
        }

    }

    public void postLoad(boolean flag){
        flag_postload=flag;
        for(View v : afterLoadList){
            v.callOnClick();
        }
        flag_postload=false;

    }
}
