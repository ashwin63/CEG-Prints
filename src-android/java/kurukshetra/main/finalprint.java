package kurukshetra.main;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.icu.text.SimpleDateFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


import static org.apache.commons.io.IOUtils.toByteArray;

public class finalprint extends AppCompatActivity {
    TextView response;
    String path = "";
    //"192.168.1.2"; //
    //Ip in Amenities Centre 10.5.19.7 Port no: 7777
    private FirebaseAuth mAuth;
    StorageReference storageReference;
    String ip = "10.5.19.7";
    String extension = "";
    int port = 7777;
    boolean finished = false;
    int kno = 0;
    int cno = 0;
    int ptr = 0;
    int fkp;
    int counter_here = 0;
    ArrayList<String> filenames;
    ArrayList<String> fname; //=new ArrayList<String>();
    ArrayList<String> fileextensions;
    int pcopies;
    String pcolor;
    int pcolour;
    private AdView mAdView;
    ProgressDialog progressDialog;
    AlertDialog dialoger;
    String page_count;
    int total_pages;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        setContentView(R.layout.activity_finalprint);
        fname = getIntent().getStringArrayListExtra("fname");
        filenames = getIntent().getStringArrayListExtra("filenames");
        page_count = getIntent().getExtras().getString("page_count");
        fileextensions = getIntent().getStringArrayListExtra("fileextensions");
        pcopies = getIntent().getExtras().getInt("copies");
        pcolor = getIntent().getExtras().getString("color");
        // Toast.makeText(this, "text " + pcolor, Toast.LENGTH_SHORT).show();
        pcolour = (pcolor.equals("Singleside")) ? 1 : 0;
        progressDialog = new ProgressDialog(finalprint.this);
        progressDialog.setMessage("Printing File..."); // Setting Message
        progressDialog.setTitle("ProgressDialog"); // Setting Title
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        //setRequestedOrientation (finalprint.SCREEN_ORIENTATION_PORTRAIT);
        // setRequestedOrientation(finalprint.SCREEN_ORIENTATION_PORTRAIT);
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        dialoger = new AlertDialog.Builder(finalprint.this).create();
        dialoger.setTitle("Success");
        String stk="";
        String kfilename="" ; //= path.substring(path.lastIndexOf("/")+1);
        String kfile="";
        for(int ii=0;ii<fname.size();ii++)
        {
            System.out.println(" \n \n  Fname "+fname.get(ii));
            stk+= String.valueOf(ii+1)+".  "+fname.get(ii);
              stk+="\n\n";
        }
        System.out.println(" \n \n  Fname "+fname.size());
        //for(int i=0;i<fname.size();i++)
       // {
            //kfilename = filenames.get(i).substring(filenames.get(i).lastIndexOf("/")+1);

   //System.out.println(" \n \n  Fname "+fname.get(i));
              //kfilename=kfilename.substring(10);
          //  stk+= String.valueOf(i+1)+".  "+fname.get(i);
          //  stk+="\n\n";
        //}
        dialoger.setMessage("Total no. of pages(approx)   -  "+page_count+"\nCopies   -  "+pcopies+"\nFiles    -  "+filenames.size()+"\nPrinted successfully. \n "+stk);
       // dialoger.setMessage("Total Files = "+filenames.size()+" has been printed successfully.");

        dialoger.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(finalprint.this,MainActivity.class);
                        startActivity(i);
                    }
                });
        if (!mWifi.isConnected()) {
            AlertDialog alertDialog = new AlertDialog.Builder(finalprint.this).create();
            alertDialog.setTitle("Server Busy");
            alertDialog.setMessage("Please Connect to the Open WIFI CEG PRINTS to print Files..");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        } else {
            try {
                // Toast.makeText(this, "Sending data to "+ip, Toast.LENGTH_SHORT).show();

               // progressDialog.show();
                // downloadclient myClient = new downloadclient(ip, filenames, fileextensions, port, pcopies, pcolour, response);

                // String aaa = myClient.execute().get();
                // myClient.wait();
                cloud_sender();
                //Toast.makeText(this, "value received "+aaa, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                // progressbar.setVisibility(ProgressBar.INVISIBLE);
                System.out.println("exceptio  "+e.toString());
                progressDialog.dismiss();
                AlertDialog alertDialog = new AlertDialog.Builder(finalprint.this).create();
                alertDialog.setTitle("Server Busy");
                alertDialog.setMessage("Server is currently in use\nPlease Try Again after few minutes:" + e.toString());
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        }
    }

    private void finished() {
        dialoger.setCancelable(false);
        dialoger.setCanceledOnTouchOutside(false);
        dialoger.show();
    }

    public void retry(View view) {

        Intent i;
        i = new Intent(finalprint.this, finalprint.class);
        //i.putExtra("Value1", path);
        i.putExtra("copies", pcopies);
        i.putExtra("color",pcolor);
        i.putStringArrayListExtra("filenames",filenames);
        i.putStringArrayListExtra("fname",fname);
        i.putExtra("page_count",page_count+"");
        System.out.println("second page "+filenames);
        i.putStringArrayListExtra("fileextensions",fileextensions);
        startActivity(i);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void cloud_sender()
    {
        final int nooffiles = filenames.size();

        //int cno;
        //= filePath;
        String filepath = "";
        kno = 0;
        cno = 0;
        ptr = 0;

        while (ptr < nooffiles) {
            System.out.println(ptr+"   "+nooffiles);

            //   Toast.makeText(finalprint.this, "nooffiles "+nooffiles, Toast.LENGTH_SHORT).show();
            String temp = filenames.get(ptr);
            //int ct=efficientPDFPageCount(filenames.get(ptr));
            String[] words = temp.split("/");
            int nk;
            nk = words.length;
            for (String w : words) {
                System.out.println("Split   " + w);
            }
            String lw = "";
            lw += words[nk - 1];
            String res = "";
            for (int i = 0; i < lw.length(); i++) {

                if (lw.charAt(i) == '.')
                    break;
                res += lw.charAt(i);
            }
            int numPages;
            System.out.println("------------------           File Path "+filepath);
            System.out.println("------------------           File Name "+filenames.get(ptr));

            filepath = filenames.get(ptr);
            System.out.println(" \n \n  P copies  and side  ----    " + pcopies + "  side   " + pcolour + " \n \n  ");
            System.out.println("\n ]n  RES NAME ----    " + res);
            System.out.println("\n \n \n file extensions----------- " + fileextensions.get(ptr));

            long time = System.currentTimeMillis();
            System.out.println(" MILLI -- " + time);
            String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
            System.out.println("\n \n  TIME -- " + currentTime);
            String fres = "";
            for (int i = 0; i < currentTime.length(); i++) {
                if (currentTime.charAt(i) == ':')
                    fres += "-";
                else
                    fres += currentTime.charAt(i);
            }
            res += "_" + fres + "_";
            res += "CEG-PRINTS_";
            if (pcopies == 10)
                res += "" + pcopies;
            else
                res += "0" + pcopies;
            if (pcolour == 0)
                res += "0";
            else
                res += "1";

            StorageReference riversRef = storageReference.child("cegprints/" + res + "." + fileextensions.get(ptr));
            riversRef.putFile(Uri.parse(filepath))
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //if the upload is successfull
                            //hiding the progress dialog
                            //progressDialog.show();
                           //progressDialog.setMessage("File "+(cno+1)+" has been printed successfully.");
                            // response+="success";
                             //progressDialog.dismiss();
                            //
                            //and displaying a success toast
                             Toast.makeText(finalprint.this,"File "+(cno+1)+" has been printed successfully." , Toast.LENGTH_SHORT).show();
                            System.out.println(" \n \n \n ------------ PTR " + (++cno));
                            if(cno==nooffiles)
                            {
                                progressDialog.dismiss();
                                finished();

                            }

                            //Toast.makeText(getApplicationContext(), " File Uploaded "+(++cno), Toast.LENGTH_LONG).show();
                            // counter_here++;
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            //if the upload is not successfull
                            //hiding the progress dialog
                            //and displaying error message
                            // response+="failure";
                            // Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                            progressDialog.setMessage(" Failure ");
                            progressDialog.dismiss();
                            counter_here++;
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //calculating progress percentage
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                            //displaying percentage in progress dialog
                            progressDialog.show();
                            progressDialog.setMessage("Uploading file  " + (cno + 1) + "    " + ((int) progress) + "%...");

                            //progressDialog.dismiss();

                            //progressDialog.show();
                            //    Toast.makeText(finalprint.this, "progress", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        }
                    });


            ptr++;
            //   Toast.makeText(finalprint.this, "ptr value "+ptr, Toast.LENGTH_SHORT).show();
            System.out.println("level crossed   "+ptr);
            if(ptr+1==nooffiles) {
             System.out.println("\n \n \n File Upload Finised");
                //finished();
            }
        }
       AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                //TODO your
                while(counter_here<nooffiles);

                System.out.println("reaee  hurray    "+ptr);
                progressDialog.dismiss();
                //dialoger.show();
                //functioncall();
                // finished();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        finished();
                    }
                });
                //Toast.makeText(finalprint.this, "completed", Toast.LENGTH_SHORT).show();
            }
        });



        // Toast.makeText(this, "files printed ", Toast.LENGTH_SHORT).show();


    }



    private void functioncall() {
        // Toast.makeText(this, "holala ", Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}