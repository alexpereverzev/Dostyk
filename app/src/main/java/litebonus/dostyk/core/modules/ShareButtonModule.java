package litebonus.dostyk.core.modules;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.widget.WebDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import litebonus.dostyk.views.vk.VkDialog;

/**
 * Created by prog on 06.11.14.
 */
public class ShareButtonModule {
   static String description;
    public static View createShareButton(final Activity context, JSONObject body, JSONArray data, JSONArray styles, long actionId){
        final String shareType = body.optString("share_destination");
      //  final String logo = data.optJSONObject(0).optString(ExpandableModule.findChild(body, "sharebutton_logo").optString("fieldname"));
        final String logo =  "http://evenet.me//attachments//theatre_default_small.jpeg";

        final String title = data.optJSONObject(0).optString(ExpandableModule.findChild(body, "sharebutton_title").optString("fieldname"));
         description = data.optJSONObject(0).optString(ExpandableModule.findChild(body, "sharebutton_description").optString("fieldname"));

        final String url = data.optJSONObject(0).optString(ExpandableModule.findChild(body, "sharebutton_url").optString("fieldname"));
        View button = ImageButtonModule.createButton(context, body, data, styles, actionId);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (shareType){
                    case "fb":{
                        Session.openActiveSession(context, true, new Session.StatusCallback() {
                            @Override
                            public void call(Session session, SessionState state, Exception exception) {
                                if (session.isOpened()) {
                                    Log.d("Andrey", "logined");
                                    shareFacebook(context, title, description, url, logo);
                                }
                                else {
                                    System.out.print("");
                                }
                            }



                        });


                        break;
                    }
                    case "vk":{

                        Uri.Builder builder = new Uri.Builder();
                        builder.scheme("https")
                                .authority("vk.com")
                                .appendPath("share.php")
                                .appendQueryParameter("url", url)
                                .appendQueryParameter("title", title)
                                .appendQueryParameter("description", description.substring(0,description.length()/4)+"...")
                                .appendQueryParameter("image", logo)
                                .appendQueryParameter("noparse", "true");
                        String uri = builder.build().toString();
                        final VkDialog dialog = new VkDialog(context, uri);
                        dialog.setListener(new VkDialog.OnSuccesListener() {
                            @Override
                            public void actSucces() {
                                dialog.dismiss();
                                context.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context, "Запись добавлена", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                        dialog.show();
                        break;
                    }
                }
            }
        });
        return button;
    }

    public static void shareFacebook(final Activity context, String title, String description, String url, String logo){
        Bundle params = new Bundle();
        params.putString("name", title);
        params.putString("caption", description);
        params.putString("link", url);
        params.putString("picture", logo);

        WebDialog feedDialog = (
                new WebDialog.FeedDialogBuilder(context,
                        Session.getActiveSession(),
                        params))
                .setOnCompleteListener(new WebDialog.OnCompleteListener() {

                    @Override
                    public void onComplete(Bundle values,
                                           FacebookException error) {
                        if (error == null) {
                            // When the story is posted, echo the success
                            // and the post Id.
                            final String postId = values.getString("post_id");
                            if (postId != null) {
                                Toast.makeText(context,
                                        "Запись добавлена",
                                        Toast.LENGTH_SHORT).show();
                            } else {

                                //Toast.makeText(context, "Publish cancelled", Toast.LENGTH_SHORT).show();
                            }
                        } else if (error instanceof FacebookOperationCanceledException) {
                            // User clicked the "x" button
                            //Toast.makeText(context, "Publish cancelled", Toast.LENGTH_SHORT).show();
                        } else {
                            // Generic, ex: network error
                            //Toast.makeText(context, "Error posting story", Toast.LENGTH_SHORT).show();
                        }
                    }

                })
                .build();
        feedDialog.show();
    }
}
