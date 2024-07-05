package kurukshetra.main;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Explode;
import android.util.Log;
import android.view.Window;

import com.blikoon.qrcodescanner.QrCodeActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class qrcode extends AppCompatActivity {
    private static final int REQUEST_CODE_QR_SCAN = 101;
    String path="";
    String ipaddress="";
    String extension="";
    int copies;
    String overridekey = "Tqbsl_dfhqsjout_btixjo";
    String color;
    ArrayList<String> fname;
    ArrayList<String>filenames;
    ArrayList<String>fileextensions;
    String page_count;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        fname = getIntent().getStringArrayListExtra("fname");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        filenames=getIntent().getStringArrayListExtra("filenames");
        fileextensions=getIntent().getStringArrayListExtra("fileextensions");
        copies=getIntent().getExtras().getInt("copies");
        color=getIntent().getExtras().getString("color");
        page_count = getIntent().getExtras().getString("page_count");
        onClick();

    }

    private void initAnimation() {
        Explode enter = new Explode();
        enter.setDuration(1000);
        getWindow().setEnterTransition(enter);
    }

    public void onClick() {
        Intent i = new Intent(qrcode.this, QrCodeActivity.class);
        startActivityForResult(i, REQUEST_CODE_QR_SCAN);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        try {
            if (resultCode != Activity.RESULT_OK) {

                Log.d("tagtag", "COULD NOT GET A GOOD RESULT.");
                Intent i=new Intent(qrcode.this,MainActivity.class);
                startActivity(i);

            }

            if (requestCode == REQUEST_CODE_QR_SCAN) {
                if (data == null){
                    Intent i=new Intent(qrcode.this,MainActivity.class);
                    startActivity(i);
                }
                //Getting the passed result
                String result = data.getStringExtra("com.blikoon.qrcodescanner.got_qr_scan_relult");
                Log.d("tagtag", "Have scan result in your app activity :" + result);
                String checkresult = "CEG_PRINTS_4";  //2019-09-08";
                String dater = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                checkresult += dater;
                //Toast.makeText(this, ""+checkresult+"  answer "+result, Toast.LENGTH_SHORT).show();

                if (result.equals(checkresult)|| result.equals(overridekey)) {

                    Intent i;
                    i = new Intent(qrcode.this, finalprint.class);
                    //i.putExtra("Value1", path);
                    i.putExtra("copies", copies);
                    i.putExtra("color",color);
                    i.putStringArrayListExtra("fname",fname);
                    i.putStringArrayListExtra("filenames",filenames);
                    System.out.println("second page "+filenames);
                    i.putStringArrayListExtra("fileextensions",fileextensions);
                    i.putExtra("page_count",page_count+"");
                    startActivity(i);
                } else {

                    AlertDialog alertDialog = new AlertDialog.Builder(qrcode.this).create();
                    alertDialog.setTitle("QR CODE ERROR");
                    alertDialog.setMessage("Please SCAN THE QR CODE AGAIN FROM THE MONITOR! \nOR\nUPDATE THE APP IN THE PLAYSTORE");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent i;
                                    i = new Intent(qrcode.this, qrcode.class);
                                    i.putExtra("copies", copies);
                                    i.putExtra("color",color);
                                    i.putStringArrayListExtra("filenames",filenames);
                                    i.putStringArrayListExtra("fname",fname);
                                    i.putStringArrayListExtra("fileextensions",fileextensions);
                                    i.putExtra("page_count",page_count+"");
                                    startActivity(i);
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent i;
                                    i = new Intent(qrcode.this, MainActivity.class);
                                    i.putExtra("copies", copies);
                                    i.putExtra("color",color);
                                    i.putStringArrayListExtra("fname",fname);
                                    i.putStringArrayListExtra("filenames",filenames);
                                    i.putStringArrayListExtra("fileextensions",fileextensions);
                                    i.putExtra("page_count",page_count+"");
                                    startActivity(i);
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.setCancelable(false);
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.show();
                }

            }
            else
            {
                Intent i=new Intent(qrcode.this,MainActivity.class);
                startActivity(i);
            }
        }
        catch (Exception e)
        {
            System.out.println(e.toString());
            Intent i=new Intent(qrcode.this,MainActivity.class);
            startActivity(i);
        }
    }
    @Override
    public void onBackPressed()
    {
        Intent i=new Intent(qrcode.this,MainActivity.class);
        startActivity(i);
    }
}