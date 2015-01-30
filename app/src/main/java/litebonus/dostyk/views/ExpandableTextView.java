package litebonus.dostyk.views;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import litebonus.dostyk.R;

/**
 * Created by prog on 08.10.2014.
 */
public class ExpandableTextView extends LinearLayout {
    private LinearLayout titleTextContainer;
    private LinearLayout imageContainer;
    private LinearLayout titleContainer;
    private LinearLayout expandContent;
    private LinearLayout delimitorContainer;
    private View passive;
    private View active;
    private boolean isVisible;

    public ExpandableTextView(final Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.expan_title_text, this);
        titleContainer = (LinearLayout) findViewById(R.id.title_container);
        titleTextContainer = (LinearLayout) findViewById(R.id.title);
        expandContent = (LinearLayout) findViewById(R.id.expand_content);
        expandContent.setVisibility(GONE);
        isVisible = false;
        imageContainer = (LinearLayout) findViewById(R.id.image);
        delimitorContainer = (LinearLayout) findViewById(R.id.delimitior);
        titleContainer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                isVisible = !isVisible;
                if(isVisible){
                    expandContent.setVisibility(VISIBLE);
                    active.setVisibility(VISIBLE);
                    passive.setVisibility(GONE);
                }else{
                    expandContent.setVisibility(GONE);
                    active.setVisibility(GONE);
                    passive.setVisibility(VISIBLE);
                }
            }
        });
    }

    public LinearLayout getTitleContainer() {
        return titleContainer;
    }

    public void setTitleTextContainer(LinearLayout titleTextContainer) {
        this.titleTextContainer.removeAllViews();
        this.titleTextContainer.addView(titleTextContainer);
    }

    public LinearLayout getExpandContent() {
        return expandContent;
    }

    public void setExpandContent(LinearLayout expandContent) {
        this.expandContent.removeAllViews();
        this.expandContent.addView(expandContent);
    }

    public void setDelimitorContainer(LinearLayout delimitorContainer) {
        this.delimitorContainer.removeAllViews();
        this.delimitorContainer.addView(delimitorContainer);
    }

    public void setPassive(View passive) {
        this.passive = passive;
        imageContainer.addView(passive);
    }

    public void setActive(View active) {
        this.active = active;
        this.active.setVisibility(GONE);
        imageContainer.addView(active);
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

}
