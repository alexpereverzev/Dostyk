package litebonus.dostyk.core.modules;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by prog on 09.10.2014.
 */
public class WebViewModule {


    public static WebView createWebView(Activity context, JSONObject body, JSONArray data, JSONArray styles, long actionId){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        WebView webView = new WebView(context);
        webView.setBackgroundColor(0);
        webView.setLayoutParams(params);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setAppCacheEnabled(true);
      //  webView.getSettings().setAppCachePath("/ibecom/dostyk/cache/");
        webView.getSettings().setAllowFileAccess(true);

        int styleId = body.optInt("styleid");
        String fieldName = body.optString("fieldname");
        String content_type=body.optString("content_type");
        StyleModule.setStyle(context, webView, styles, styleId, actionId);
        String content=data.optJSONObject(0).optString(fieldName);
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Activity.CONNECTIVITY_SERVICE);
       /* if(cm != null && cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()){
            webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        }
        else{
*/
                webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
  //      }


        if(!content_type.equals("url")){
           webView.loadData("<html><body>"+ content+"</body></html>", "text/html; charset=utf-8", "UTF-8");
        }
        else  {
            webView.loadUrl(content);
        }
        return webView;
    }

}
