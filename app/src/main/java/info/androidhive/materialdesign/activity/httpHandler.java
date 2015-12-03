package info.androidhive.materialdesign.activity;

import android.app.Activity;
import android.content.Context;
import android.widget.Spinner;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;

import info.androidhive.materialdesign.R;

/**
 * Created by Hartas on 20/10/2015.
 */
public class httpHandler extends Activity {

    private boolean DHCP;
    private TextView g0, g1, g2, g3;
    private TextView s0, s1, s2, s3;
    private TextView ip0, ip1, ip2, ip3;
    private TextView ssid, pass;
    private Spinner SSid_infra;

    public httpHandler(boolean dhcp,Enviar_to_infra cont) {

        DHCP = dhcp;

        g0 = (TextView) cont.findViewById(R.id.g0);
        g1 = (TextView) cont.findViewById(R.id.g1);
        g2 = (TextView) cont.findViewById(R.id.g2);
        g3 = (TextView) cont.findViewById(R.id.g3);

        s0 = (TextView) cont.findViewById(R.id.s0);
        s1 = (TextView) cont.findViewById(R.id.s1);
        s2 = (TextView) cont.findViewById(R.id.s2);
        s3 = (TextView) cont.findViewById(R.id.s3);

        ip0 = (TextView) cont.findViewById(R.id.ip0);
        ip1 = (TextView) cont.findViewById(R.id.ip1);
        ip2 = (TextView) cont.findViewById(R.id.ip2);
        ip3 = (TextView) cont.findViewById(R.id.ip3);

        pass = (TextView) cont.findViewById(R.id.pass);
        SSid_infra = (Spinner) cont.findViewById(R.id.SSid_infra);
    }

    public String post(String posturl) {


        try {

            HttpClient httpclient = new DefaultHttpClient();

            HttpPost httppost = new HttpPost(posturl);

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            if (DHCP) {
                params.add(new BasicNameValuePair("dhcp", "dhcp"));
            }

            params.add(new BasicNameValuePair("g0", g0.getText() + ""));
            params.add(new BasicNameValuePair("g1", g1.getText() + ""));
            params.add(new BasicNameValuePair("g2", g2.getText() + ""));
            params.add(new BasicNameValuePair("g3", g3.getText() + ""));

            params.add(new BasicNameValuePair("ip0", ip0.getText() + ""));
            params.add(new BasicNameValuePair("ip1", ip1.getText() + ""));
            params.add(new BasicNameValuePair("ip2", ip2.getText() + ""));
            params.add(new BasicNameValuePair("ip3", ip3.getText() + ""));

            params.add(new BasicNameValuePair("pass", pass.getText() + ""));

            params.add(new BasicNameValuePair("s0", s0.getText() + ""));
            params.add(new BasicNameValuePair("s1", s1.getText() + ""));
            params.add(new BasicNameValuePair("s2", s2.getText() + ""));
            params.add(new BasicNameValuePair("s3", s3.getText() + ""));

            params.add(new BasicNameValuePair("ssid",SSid_infra.getSelectedItem() + ""));

            httppost.setEntity(new UrlEncodedFormEntity(params));

            HttpResponse resp = httpclient.execute(httppost);
            HttpEntity ent = resp.getEntity();
            String text = EntityUtils.toString(ent);
            return text;


        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }

    }

}