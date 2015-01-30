package litebonus.dostyk.views;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import litebonus.dostyk.core.modules.PickerModule;

/**
 * Created by prog on 29.10.14.
 */
public class Picker extends LinearLayout {
    private long id;
    private View active;
    private View passive;
    private boolean isActive;

    public Picker(Context context, long id) {
        super(context);
        this.id = id;
    }

    public void setActive(View active) {
        this.active = active;
    }

    public void activate(){
        isActive = true;
        Picker.this.removeAllViews();
        Picker.this.addView(active);
    }

    public void setPassive(View passive) {
        this.passive = passive;
        this.removeAllViews();
        this.addView(passive);
    }

    public void reset(){
        if(isActive){
            this.removeAllViews();
            this.addView(passive);
            isActive = false;
        }
    }

    public void click(){
        if(!isActive){
            PickerModule.Grouping.getInstance().resetGroup(Picker.this.id);

            activate();
        }else{
            if(id < 1) {
                reset();
            }
        }
    }



    public boolean isActive() {
        return isActive;
    }

    public long getGroupId() {
        return id;
    }
}
