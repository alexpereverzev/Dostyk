package litebonus.dostyk.core;

import android.app.Dialog;

/**
 * Created by Alexander on 29.10.2014.
 */
public class DialogApi {

    private static Dialog dialog;

    public static void setDialog(Dialog dialog) {
        DialogApi.dialog = dialog;
    }

    private DialogApi(){

    }

    public static Dialog getDialog(){
        return dialog;
    }

}
