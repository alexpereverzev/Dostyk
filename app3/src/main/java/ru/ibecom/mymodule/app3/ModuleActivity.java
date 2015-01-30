package ru.ibecom.mymodule.app3;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKSdkListener;
import com.vk.sdk.VKUIHelper;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.api.model.VKAttachments;
import com.vk.sdk.api.model.VKPhotoArray;
import com.vk.sdk.api.model.VKWallPostResult;
import com.vk.sdk.api.photo.VKImageParameters;
import com.vk.sdk.api.photo.VKUploadImage;
import com.vk.sdk.dialogs.VKCaptchaDialog;

import org.json.JSONArray;
import org.json.JSONException;

import ru.ibecom.mymodule.app3.fb.Permissi;
import ru.ibecom.mymodule.app3.fb.SimpleFacebook;
import ru.ibecom.mymodule.app3.fb.SimpleFacebookConfiguration;
import ru.ibecom.mymodule.app3.fb.entities.Feed;


public class ModuleActivity extends Activity {

    private SimpleFacebook mSimpleFacebook;
    private String url_icon;
    private String shared_text;
    private DialogFragment dialogFragment;
    private ImageView icon;
    private String url_sharing ="http://vk.com/share.php?url=";
    private Bitmap photo;

    DisplayImageOptions options = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true).build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    public void sharingVK(String url){

        url_sharing=url_sharing+url;
        dialogFragment = new DialogFragment() {

            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                // Use the Builder class for convenient dialog construction
                return super.onCreateDialog(savedInstanceState);
            }

            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                     Bundle savedInstanceState) {
                View view = inflater.inflate(R.layout.dialog_vk, null);
                getDialog().requestWindowFeature(STYLE_NO_TITLE);
                getDialog().setCancelable(false);
                WebView vk=(WebView)view.findViewById(R.id.web_vk);
                vk.setWebViewClient(new WebViewClient());
                vk.getSettings().setJavaScriptEnabled(true);
                vk.loadUrl(url_sharing);
                return view;
            }
        };

        FragmentManager fm = getFragmentManager();

            dialogFragment.setCancelable(true);
            dialogFragment.show(fm, "DIALOG_FRAGMENT");


    }

    public void initialization(){
        Permissi[] permissions = new Permissi[]{
                Permissi.USER_PHOTOS,
                Permissi.EMAIL,
                Permissi.USER_BIRTHDAY,
                Permissi.PUBLISH_ACTION,
                Permissi.BASIC_INFO,
                Permissi.USER_ABOUT_ME
        };
        SimpleFacebookConfiguration configuration = new SimpleFacebookConfiguration.Builder()
                .setAppId(getString(R.string.facebook_app))
                .setNamespace("evenetapp")
                .setPermissions(permissions)
                .build();

        SimpleFacebook.setConfiguration(configuration);

        mSimpleFacebook= SimpleFacebook.getInstance(this);

        VKUIHelper.onCreate(this);
        VKSdk.initialize(sdkListener, "4581690");

        ImageLoaderConfiguration mConfiguration = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .threadPoolSize(5)
                .threadPriority(Thread.NORM_PRIORITY)
                .memoryCache(new LruMemoryCache(5 * 1024 * 1024))
                .memoryCacheSize(5 * 1024 * 1024)
                        //.discCacheSize(50 * 1024 * 1024)*/
                .build();
        //restore();
        // ImageLoader.getInstance().init(mConfiguration);
        initImageLoader(this);
    }

    public void showAbout(){
        startActivity(new Intent(this,AboutActivity.class));
    }

    public void showDialogFacebok(String shared_data,String url, final String title){

        url_icon=url;
        shared_text=shared_data;
        FragmentManager fm = getFragmentManager();
        dialogFragment = new DialogFragment() {

            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                // Use the Builder class for convenient dialog construction
                return super.onCreateDialog(savedInstanceState);
            }

            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                     Bundle savedInstanceState) {
                View view = inflater.inflate(R.layout.dialog_facebook, container, true);
                getDialog().requestWindowFeature(STYLE_NO_TITLE);
                getDialog().setCancelable(false);
                final EditText comment=(EditText) view.findViewById(R.id.comment);
                TextView textView=(TextView) view.findViewById(R.id.text_shared);
                ImageView imageView=(ImageView) view.findViewById(R.id.icon_shared);
                ImageLoader.getInstance().displayImage(url_icon,imageView,options);
                TextView title_widget=(TextView) view.findViewById(R.id.title);
                title_widget.setText(title);
                String message=textView.getText().toString();
                message=message+" "+shared_text;
                textView.setText(message);

                Button send=(Button) view.findViewById(R.id.send);
                send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Feed feed = new Feed.Builder()
                                .setMessage(comment.getText().toString())
                                .setName(title)
                                .setDescription(shared_text)
                                .setPicture(url_icon)
                                .setLink(shared_text)
                                .build();

                        mSimpleFacebook.publish(feed,onPublishListener);
                    }
                });
                return view;
            }
        };
        if(mSimpleFacebook.isLogin()){
            dialogFragment.setCancelable(true);
            dialogFragment.show(fm, "DIALOG_FRAGMENT");}
        else {
            mSimpleFacebook.login(mOnLoginListener);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mSimpleFacebook.onActivityResult(this, requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    public SimpleFacebook.OnLoginListener mOnLoginListener = new SimpleFacebook.OnLoginListener() {

        @Override
        public void onFail(String reason) {
            System.out.print("");

        }

        @Override
        public void onException(Throwable throwable) {
             System.out.print("");

        }

        @Override
        public void onThinking() {
            System.out.print("");

        }

        @Override
        public void onLogin() {
            System.out.print("");
         //   dialogFragment.;

        }

        @Override
        public void onNotAcceptingPermissions() {
            System.out.print("");

        }
    };

    public SimpleFacebook.OnPublishListener onPublishListener = new SimpleFacebook.OnPublishListener() {

        @Override
        public void onFail(String reason) {

            // insure that you are logged in before publishing
            System.out.print(" \"System.out\"");
        }

        @Override
        public void onException(Throwable throwable) {


        }

        @Override
        public void onThinking() {
            // show progress bar or something to the user while publishing

        }

        @Override
        public void onComplete(String postId) {
            dialogFragment.dismiss();
        }
    };


    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(75 * 1024 * 1024) // 50 Mb
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs() // Remove for release app
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }

    private final VKSdkListener sdkListener = new VKSdkListener() {
        @Override
        public void onCaptchaError(VKError captchaError) {
            new VKCaptchaDialog(captchaError).show();
        }

        @Override
        public void onTokenExpired(VKAccessToken expiredToken) {
            VKSdk.authorize(SupportInfo.sMyScope);
        }

        @Override
        public void onAccessDenied(final VKError authorizationError) {
            new AlertDialog.Builder(VKUIHelper.getTopActivity())
                    .setMessage(authorizationError.toString())
                    .show();
        }

        @Override
        public void onReceiveNewToken(VKAccessToken newToken) {
            // startTestActivity();
            // dialogFragment.show(getSupportFragmentManager(), "Dialog");
            getInfo();


        }

        @Override
        public void onAcceptUserToken(VKAccessToken token) {
            //startTestActivity();
            // dialogFragment.show(getSupportFragmentManager(), "Dialog");
            getInfo();
        }
    };

    private void makePost(VKAttachments attachments, String message) {

        VKRequest post = VKApi.wall().post(VKParameters.from(VKApiConst.OWNER_ID, id_user, VKApiConst.ATTACHMENTS, attachments, VKApiConst.MESSAGE, message));
        post.setModelClass(VKWallPostResult.class);
        post.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                VKWallPostResult result = (VKWallPostResult) response.parsedModel;
                //   Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("https://vk.com/wall-60479154_%s", result.post_id)) );
                //  startActivity(i);
            }

            @Override
            public void onError(VKError error) {
                // showError(error.apiError != null ? error.apiError : error);
            }
        });
    }

    public void getInfo() {
        VKRequest request = VKApi.users().get(VKParameters.from(VKApiConst.FIELDS,
                "id"));

        request.secure = false;
        request.useSystemLanguage = false;
        request.executeWithListener(mRequestListener);


    }

    String id_user;
    VKRequest.VKRequestListener mRequestListener = new VKRequest.VKRequestListener() {
        @Override
        public void onComplete(VKResponse response) {
            try {
                JSONArray jsonArray = response.json.getJSONArray("response");
                for (int i = 0; i < jsonArray.length(); i++) {
                    id_user = jsonArray.getJSONObject(i).getString("id");

                }
                showDialogVK(shared_text, url_icon);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            //  setResponseText(response.json.toString());
        }

        @Override
        public void onError(VKError error) {
            System.out.print("");

        }

        @Override
        public void onProgress(VKRequest.VKProgressType progressType, long bytesLoaded,
                               long bytesTotal) {

        }

        @Override
        public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {

        }
    };

    public void showDialog(String shared_data, String url){
        VKSdk.authorize(SupportInfo.sMyScope, false, true);
        url_icon = url;
        shared_text = shared_data;
    }

    public void showDialogVK(String shared_data, String url) {




        url_icon = url;
        shared_text = shared_data;

        dialogFragment = new DialogFragment() {

            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                // Use the Builder class for convenient dialog construction
                return super.onCreateDialog(savedInstanceState);
            }

            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                     Bundle savedInstanceState) {
                View view = inflater.inflate(R.layout.dialog_vk, container, true);
                getDialog().requestWindowFeature(STYLE_NO_TITLE);
                getDialog().setCancelable(false);
                EditText comment = (EditText) view.findViewById(R.id.comment);
                TextView textView = (TextView) view.findViewById(R.id.text_shared);
                icon = (ImageView) view.findViewById(R.id.icon_shared);
                ImageLoader.getInstance().displayImage(url_icon, icon);
                textView.setText(shared_text);

                Button send = (Button) view.findViewById(R.id.send);
                send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (photo != null) {
                            photo = getBitmapPhoto();
                        }
                        VKRequest request = VKApi.uploadWallPhotoRequest(new VKUploadImage(photo, VKImageParameters.jpgImage(0.9f)), 0, 60479154);
                        request.executeWithListener(new VKRequest.VKRequestListener() {
                            @Override
                            public void onComplete(VKResponse response) {
                                photo.recycle();
                                VKApiPhoto photoModel = ((VKPhotoArray) response.parsedModel).get(0);
                                VKAttachments vkApiAttachments = new VKAttachments(photoModel);

                                makePost(new VKAttachments(photoModel), shared_text);
                            }

                            @Override
                            public void onError(VKError error) {
                                System.out.print("");
                            }
                        });
                    }
                });
                return view;
            }
        };

        FragmentManager fm = getFragmentManager();
        if (VKSdk.isLoggedIn()) {
            dialogFragment.setCancelable(true);
            dialogFragment.show(fm, "DIALOG_FRAGMENT");
        } else {
            VKSdk.authorize(SupportInfo.sMyScope, false, true);
        }

    }

    public Bitmap getBitmapPhoto() {
        icon.setDrawingCacheEnabled(true);
        icon.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        icon.layout(0, 0,
                icon.getMeasuredWidth(), icon.getMeasuredHeight());
        icon.buildDrawingCache(true);
        Bitmap photo = Bitmap.createBitmap(icon.getDrawingCache());
        icon.setDrawingCacheEnabled(false);
        return photo;
    }

    public void show_dialog(){
        final Dialog dia = new Dialog(this, android.R.style.Theme_Translucent);
        dia.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dia.setCancelable(true);
        dia.setContentView(R.layout.bluetooth_dialog);

        Button cancel=(Button) dia.findViewById(R.id.btncancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dia.dismiss();
            }
        });

        Button setting=(Button)dia.findViewById(R.id.btnsetting);

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dia.dismiss();
                startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));

            }
        });

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            dia.show();
        }


    }
}
