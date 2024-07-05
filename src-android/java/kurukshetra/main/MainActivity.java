package kurukshetra.main;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.text.TextUtils;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

//import com.aspose.words.Document;
import com.github.andreilisun.swipedismissdialog.SwipeDismissDialog;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.snackbar.Snackbar;
import com.tom_roush.pdfbox.pdmodel.PDDocument;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import im.delight.apprater.AppRater;
import me.himanshusoni.quantityview.QuantityView;


public class MainActivity extends AppCompatActivity {

    Dialog epicdialog = null;
    private static final int FILE_REQUEST_CODE = 1;
    private TextView mTextMessage;
    private static final int PICKFILE_REQUEST_CODE = 1;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA =100 ;
    String ipvalue="";
    ArrayList<String> filelist=new ArrayList<String>();
    ArrayList<String> fileextensions=new ArrayList<String>();
    ArrayList<String> fname=new ArrayList<String>();
    List<Product> productList;
    Dialog uploadDialog;
    int flagindex=0;
    int total_pages = 0;
    boolean first_time = true;
    protected Context context;
    RecyclerView recyclerView;
    TextView pager;
    ProductAdapter adapter;
    private int pageIndex;
    private PdfRenderer pdfRenderer;
    private PdfRenderer.Page currentPage;
    private ParcelFileDescriptor parcelFileDescriptor;
    private AdView mAdView;
    CoordinatorLayout coordinatorLayout;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch(item.getItemId())
        {
            case R.id.favorites:
                View dialog = LayoutInflater.from(this).inflate(R.layout.custom_help, null);
                final SwipeDismissDialog swipeDialog= new SwipeDismissDialog.Builder(this)
                        .setView(dialog)
                        .build()
                        .show();
                Button mbutton = (Button) dialog.findViewById(R.id.printButton);

                mbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        swipeDialog.dismiss();
                    }
                });
                return true;
            case R.id.favorites2:
                Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=" +this.getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                // To count with Play market backstack, After pressing back button,
                // to taken back to our application, we need to add following flags to intent.
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" +this.getPackageName())));
                }
                return true;
            default: return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
       // android.support.v7.widget.Toolbar toolbar= (android.support.v7.widget.Toolbar) findViewById(R.id.app_bar);
        //setSupportActionBar(toolbar);
        pager = (TextView) findViewById(R.id.pager);
        SpannableString s = new SpannableString("CEG Prints");
        s.setSpan(new ForegroundColorSpan(Color.BLACK), 0, "CEG Prints".length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
       getSupportActionBar().setTitle(s);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);

        }
        ConstraintLayout constraintLayout1=(ConstraintLayout)findViewById(R.id.mainLayout);
        constraintLayout1.setVisibility(View.VISIBLE);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //initializing the productlist
        productList = new ArrayList<>();
        enableSwipeToDeleteAndUndo();
        Intent intenter = getIntent();
        if(intenter.hasExtra("shareduri"))
        {
            String shared = intenter.getExtras().getString("shareduri");
            System.out.println("reacged here with uri "+shared);
            filelist.add(shared);
            String ext =getMimeType(getApplicationContext(),Uri.parse(shared));
            fname.add(getNameFromUri(Uri.parse(shared)));
            //total_pages+=find_pages(shared,ext);
            fileextensions.add(ext);
            //pager.setText("No.of pages = "+total_pages);
            try {
                showuser();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if(intenter.hasExtra("urilist"))
        {
            ArrayList<Uri> imageUris = intenter.getParcelableArrayListExtra("urilist");
            for(int urino=0;urino<imageUris.size();urino++)
            {
                String a_uri =imageUris.get(urino).toString();
                String a_ext = getMimeType(getApplicationContext(),imageUris.get(urino));
                filelist.add(a_uri);
                fileextensions.add(a_ext);
                //total_pages+=find_pages(a_uri,a_ext);
                fname.add(getNameFromUri(Uri.parse(imageUris.get(urino).toString())));
                //pager.setText("No.of pages = "+total_pages);
            }
            try {
                showuser();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //google ads
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("6B588198B274AD5368A3950FC0761C89").addTestDevice("FEEF50E7B149467C8FBEB5239DD606A9").build();
        mAdView.loadAd(adRequest);
        feedback_creater();
        }

    private void feedback_creater() {
        AppRater appRater = new AppRater(this);
        appRater.setDaysBeforePrompt(0);
        appRater.setLaunchesBeforePrompt(10);
        appRater.setPhrases(R.string.rate_title, R.string.rate_explanation, R.string.rate_now, R.string.rate_later, R.string.rate_never);
        appRater.setTargetUri("https://play.google.com/store/apps/details?id="+this.getPackageName());
        appRater.show();
// or
    }

    public void reset(View view)
    {
        flagindex=0;
        productList.clear();
        filelist.clear();
        fname.clear();
        total_pages = 0;
        fileextensions.clear();
        adapter = new ProductAdapter(this, productList);
        recyclerView.setAdapter(adapter);
        first_time = true;

        pager.setText("No.of pages = "+total_pages+"(approx)");

    }

    public int find_pages(String uri,String ext)
    {
        int tt = 0;

        if(ext.equals("jpg")||ext.equals("png")||ext.equals("jpeg"))
        {
            tt= 1;
        }
        else if(ext.equals("pdf"))
        {
            try {
                PDDocument doc = PDDocument.load(getContentResolver().openInputStream(Uri.parse(uri)));
                tt = doc.getNumberOfPages();
                // Toast.makeText(MainActivity.this, "Number of pages: " + doc.getNumberOfPages(), Toast.LENGTH_LONG).show();
                doc.close();
            }
            catch (Exception e )
            {
                Log.e("pdf pages exception ",e.toString());
                //   pages = "No. of pages not found";
            }
        }
        else if(ext.equals("docx"))
        {
            try{
                //Document doc = new Document(getContentResolver().openInputStream(Uri.parse(ext)));
                //tt = doc.getPageCount();
                tt = 0;
            }
            catch (Exception e)
            {
                Log.e("Docx_exception",e.toString());
                //pages = "No. of pages not found";
            }

        }
        else
        {
            tt = 0;
        }
        return tt;
    }
        public void fileopener(View view)
        {


            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);



            intent.setType("*/*");


            intent.addCategory(Intent.CATEGORY_OPENABLE);
            String[] mimetypes = {"image/*", "application/pdf","application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                    ,"text/*"};
            // intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            //intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
             intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(intent, PICKFILE_REQUEST_CODE);



        }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICKFILE_REQUEST_CODE) {
            if(null != data) { // checking empty selection

                    flagindex=filelist.size();
                    if(null != data.getClipData()) { // checking multiple selection or not
                        for(int i = 0; i < data.getClipData().getItemCount(); i++) {
                            Uri uri = data.getClipData().getItemAt(i).getUri();


                                    String extension=getMimeType(getApplicationContext(),uri);
                                    if(extension==null)
                                    {
                                        Toast.makeText(this, "File Format not supported", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                         if(extension.equals("jpeg")||extension.equals("jpg")||extension.equals("png")||extension.equals("pdf")||extension.equals("txt")||extension.equals("docx")) {
                                        filelist.add(uri.toString());
                                        fileextensions.add(extension);
                                        fname.add(getNameFromUri(uri));
                                      //  Toast.makeText(this, "extension " + extension, Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        Toast.makeText(this,"File Format "+extension+" not supported:  ", Toast.LENGTH_SHORT).show();
                                    }
                                    }
                            //    }
                            //    else
                            //    {
                            //        Toast.makeText(this, "Please Select Files below 10mb.", Toast.LENGTH_SHORT).show();
                            /*    }
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }*/


                        }
                    } else {
                        Uri uri = data.getData();

                        String extension=getMimeType(getApplicationContext(),uri);
                        if(extension==null) {
                            Toast.makeText(this, "File Format not supported:  ", Toast.LENGTH_SHORT).show();

                        }else {
                            if (extension.equals("jpeg")||extension.equals("jpg") || extension.equals("png") || extension.equals("pdf") || extension.equals("txt") || extension.equals("docx")) {
                                filelist.add(uri.toString());
                                fileextensions.add(extension);
                                fname.add(getNameFromUri(uri));
                              //  Toast.makeText(this, "extension " + extension, Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(this, "File  Format "+extension+" not supported:  ", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                try {
                    showuser();
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }

    }


    private String getPath(final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        if(isKitKat) {
            // MediaStore (and general)
            return getForApi19(uri);
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }
    private static String getDataColumn(Context context, Uri uri, String selection,
                                        String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    @TargetApi(19)
    private String getForApi19(Uri uri) {
        String tag="";
        Log.e(tag, "+++ API 19 URI :: " + uri);
        if (DocumentsContract.isDocumentUri(this, uri)) {
            Log.e(tag, "+++ Document URI");
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                Log.e(tag, "+++ External Document URI");
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    Log.e(tag, "+++ Primary External Document URI");
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
               /* Log.e(tag, "+++ Downloads External Document URI");
                final String id = DocumentsContract.getDocumentId(uri);
                System.out.println("ID ---------- "+id);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://com.android.providers.downloads.documents/document/"), Long.valueOf(id));

                return getDataColumn(contentUri, null, null);

                */
                System.out.println("Hello------------");
                Cursor cursor = null;
                try {
                    System.out.println("Helkkkkkkkklo------------");
                    System.out.println("URI-----------"+uri);
                    cursor = context.getContentResolver().query(uri, new String[]{MediaStore.MediaColumns.DISPLAY_NAME}, null, null, null);
                    //cursor=getContentResolver().query(uri, null, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        String fileName = cursor.getString(0);
                        System.out.println("Helllllllllllllo------------");
                        String path = Environment.getExternalStorageDirectory().toString() + "/Download/" + fileName;
                        System.out.println("Path ----  "+path);
                        if (!TextUtils.isEmpty(path)) {
                            return path;
                        }
                    }
                }
                catch(Exception e)
                {
                    System.out.println(e);
                }
                finally {
                    if (cursor != null)
                        cursor.close();
                }
                final String id = DocumentsContract.getDocumentId(uri);
                try {
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/"), Long.valueOf(id));

                    return getDataColumn(context, contentUri, null, null);
                } catch (NumberFormatException e) {
                    //In Android 8 and Android P the id is not a number
                    return uri.getPath().replaceFirst("^/document/raw:", "").replaceFirst("^raw:", "");
                }
            }
            /*String id = DocumentsContract.getDocumentId(uri);
            if (id.startsWith("raw:")) {
                return id.replaceFirst("raw:", "");
            }
            Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads"), Long.valueOf(id));

            return getDataColumn(context, contentUri, null, null);
               */

        }
        // MediaProvider
        else if (isMediaDocument(uri)) {
            Log.e(tag, "+++ Media Document URI");
            final String docId = DocumentsContract.getDocumentId(uri);
            final String[] split = docId.split(":");
            final String type = split[0];

            Uri contentUri = null;
            if ("image".equals(type)) {
                Log.e(tag, "+++ Image Media Document URI");
                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            } else if ("video".equals(type)) {
                Log.e(tag, "+++ Video Media Document URI");
                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            } else if ("audio".equals(type)) {
                Log.e(tag, "+++ Audio Media Document URI");
                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            }

            final String selection = "_id=?";
            final String[] selectionArgs = new String[] {
                    split[1]
            };

            return getDataColumn(contentUri, selection, selectionArgs);
        }
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            Log.e(tag, "+++ No DOCUMENT URI :: CONTENT ");

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            Log.e(tag, "+++ No DOCUMENT URI :: FILE ");
            return uri.getPath();
        }
        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public String getDataColumn(Uri uri, String selection,
                                String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        }
        catch (Exception e)
        {

        }
        finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
//............swipe to delete
private void enableSwipeToDeleteAndUndo() {
    SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {
        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {


            final int position = viewHolder.getAdapterPosition();
            adapter.removeItem(position);

            int tt = 0;
            System.out.println(position+"    "+filelist.size());
            try {
                if (fileextensions.get(position).equals("jpg") || fileextensions.get(position).equals("png") || fileextensions.get(position).equals("jpeg")) {
                    tt = 1;
                } else if (fileextensions.get(position).equals("pdf")) {
                    try {
                        PDDocument doc = PDDocument.load(getContentResolver().openInputStream(Uri.parse(filelist.get(position))));
                        tt = doc.getNumberOfPages();
                        // Toast.makeText(MainActivity.this, "Number of pages: " + doc.getNumberOfPages(), Toast.LENGTH_LONG).show();
                        doc.close();
                    } catch (Exception e) {
                        Log.e("pdf pages exception ", e.toString());
                        //   pages = "No. of pages not found";
                    }
                } else if (fileextensions.get(position).equals("docx")) {
                    try {
                        //Document doc = new Document(getContentResolver().openInputStream(Uri.parse(filelist.get(position))));
                        //tt = doc.getPageCount();
                        tt = 0;
                    } catch (Exception e) {
                        Log.e("Docx_exception", e.toString());
                        //pages = "No. of pages not found";
                    }

                } else {

                }
            }
            catch (Exception e)
            {
                System.out.println("page_count exception in swipe "+e);
            }
            total_pages-=tt;
            filelist.remove(position);
            fname.remove(position);
            fileextensions.remove(position);
            pager.setText("No.of pages = "+total_pages+"(approx)");
        }
    };

    ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
    itemTouchhelper.attachToRecyclerView(recyclerView);
}
  
//..........................................................................
    private void showuser() throws IOException {

        for(int i=flagindex;i<filelist.size();i++)
        {
                String temp=filelist.get(i);
                System.out.println("URI ----------     "+temp);
           // PdfRenderer renderer = new PdfRenderer(getSeekableFileDescriptor(temp));
            //final int pcc= renderer.getPageCount();
          // System.out.println("Number of pages "+pcc);

                    Uri tempuri=Uri.parse(temp);
            String fkname = getNameFromUri(tempuri);

            String pages = 0+" pages";
            if(fileextensions.get(i).equals("jpg")||fileextensions.get(i).equals("png")||fileextensions.get(i).equals("jpeg"))
            {
                pages = 1+" page";
                total_pages+=1;
            }
            else if(fileextensions.get(i).equals("pdf"))
            {
                try {
                    PDDocument doc = PDDocument.load(getContentResolver().openInputStream(Uri.parse(filelist.get(i))));
                    int tt = doc.getNumberOfPages();
                    pages = tt+" pages";
                   // Toast.makeText(MainActivity.this, "Number of pages: " + doc.getNumberOfPages(), Toast.LENGTH_LONG).show();
                    doc.close();
                    total_pages+=tt;
                }
                catch (Exception e )
                {
                    Log.e("pdf pages exception ",e.toString());
                    pages = "No. of pages not found";
                }
            }
            else if(fileextensions.get(i).equals("docx"))
            {
                    pages = "No. of pages not found";
                    try{
                       // Document doc = new Document(getContentResolver().openInputStream(Uri.parse(filelist.get(i))));
                        //int tt = doc.getPageCount();
                        int tt=0;
                        pages = "No. of pages not found";
                        total_pages+=tt;
                    }
                    catch (Exception e)
                    {
                        Log.e("Docx_exception",e.toString());
                        pages = "No. of pages not found";
                    }

            }
            else
            {
                pages = "No of pages not found";
            }

            pager.setText("No.of pages = "+total_pages+"(approx)");
            productList.add(
                    new Product(
                            fkname+"      ",pages,R.drawable.file1));

        }
        if(first_time) {
            adapter = new ProductAdapter(this, productList);
            recyclerView.setAdapter(adapter);
            adapter.setOnItemClickListener(new ProductAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    System.out.println("position clicked "+position+" "+Uri.parse(filelist.get(position)));
                    //Toast.makeText(context, "Item clicked at position "+position, Toast.LENGTH_SHORT).show();
                    open_file(position);
                }
            });
            first_time= false;
        }else
        {
            adapter.notifyItemRangeInserted(flagindex,filelist.size()-flagindex);
        }

    }

    private String getNameFromUri(Uri tempuri) {
        Cursor returnCursor =
                getContentResolver().query(tempuri, null, null, null, null);

        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();
        return returnCursor.getString(nameIndex);
    }

    public void open_file(int position) {
        {
            try {
        /*
        if(fileextensions.get(position)!="docx") {
            try {
                Uri uri = Uri.parse(filelist.get(position));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
               // intent.setDataAndType(uri,"image/"+fileextensions.get(position));
                PackageManager packageManager = getPackageManager();
                List<ResolveInfo> activities = packageManager.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
                boolean isIntentSafe = activities.size() > 0;
                // intent.setDataAndType(_uri,"image/jpeg");
                if (isIntentSafe)
                    startActivity(intent);
                else
                    Toast.makeText(context, "File cannot be opened", Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                System.out.println("Exception in open file " + e);
                Toast.makeText(context, "File can't be opened", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(context, "File cannot be opened", Toast.LENGTH_SHORT).show();
        }
        */
                File file = null;
                String uri = filelist.get(position);
                // String fn=f
                String kt = fileextensions.get(position);
                System.out.println("\n \n \n   ---------------file Extensions     --            " + kt);
//"/storage/emulated/0/WhatsApp/Media/WhatsApp Documents/__Human_computer_interaction.pdf");
                boolean flag = false;
                Uri tempuri = Uri.parse(uri);
                // System.out.println("\n \n \n \n \n REAL APTH URI--------   "+gps(this,tempuri));
                Cursor returnCursor =
                        getContentResolver().query(tempuri, null, null, null, null);

                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                returnCursor.moveToFirst();
                String fkname = returnCursor.getString(nameIndex);
                // String flength=Long.toString(returnCursor.getLong(sizeIndex));
                System.out.println("\n \n \n   ---------------file name     --            " + fkname);
                System.out.println("\n \n \n file memory      " + Environment.getExternalStorageDirectory().toString());
                int flen = fkname.length();
                System.out.println("\n \n \n file length    " + flen);

                try {
                    if (uri.contains("whatsapp"))
                    //  file = new File("/storage/emulated/0/WhatsApp/Media/WhatsApp Documents/"+fkname);
                    {
                        // if (uri.contains(".pdf"))

                        file = new File(Environment.getExternalStorageDirectory().toString() + "/WhatsApp/Media/WhatsApp Documents/" + fkname + "." + kt);
                        // else if(uri.contains(".docx"))
                        //      file = new File(Environment.getExternalStorageDirectory().toString() + "/WhatsApp/Media/WhatsApp Documents/" + fkname + ".docx");
                        //  else //if (uri.contains(".txt"))
                        //    file = new File(Environment.getExternalStorageDirectory().toString() + "/WhatsApp/Media/WhatsApp Documents/" + fkname + ".txt");

                    } else if (uri.contains("com.android.providers.downloads.documents")) {
                        System.out.println("\n \n \n file size       " + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString());
                        file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + fkname);//+ "." + kt);
                    } else
                        file = new File(getPath(Uri.parse(uri)));//Environment.getExternalStorageDirectory() .getPath() +"/"+ fkname); // Here you declare your pdf path

                    flag = true;

                    System.out.println("-----------  uri " + uri);
                    System.out.println("FK ----------      " + Uri.fromFile(file) + " uri");
                } catch (Exception e) {
                    Toast.makeText(this, "File Cannot be Opened",
                            Toast.LENGTH_LONG).show();
                    System.out.println("e ---------- " + e);
                    return;
                    //Environment.getExternalStorageDirectory().toString() + "/Download/" + fileName;
                    //file = new File(Environment.getExternalStorageDirectory().toString() + "/Download/" + uri);
                }
                try {
                    Intent pdfViewIntent = new Intent(Intent.ACTION_VIEW);
                    if (fkname.indexOf(flen - 1) == 'g' || kt.equalsIgnoreCase("png") || kt.equalsIgnoreCase("jpg") || kt.equalsIgnoreCase("jpeg"))
                        pdfViewIntent.setDataAndType(Uri.fromFile(file), "image/*");
                    else if (fkname.indexOf(flen - 1) == 'x' || kt.equalsIgnoreCase("docx"))
                        pdfViewIntent.setDataAndType(Uri.fromFile(file), "application/msword");
                    else
                        pdfViewIntent.setDataAndType(Uri.fromFile(file), "application/pdf");

                    pdfViewIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    //pdfViewIntent.setFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                    Intent intent = Intent.createChooser(pdfViewIntent, "Open File");

                    startActivity(intent);
                } catch (Exception e) {

                    // Instruct the user to install a PDF reader here, or something
                    Toast.makeText(MainActivity.this, "No Application available to view pdf", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "File Cannot be Opened",
                        Toast.LENGTH_LONG).show();
                //System.out.println()
            }

        }
    }



        public  void upload(View view)
{
    if(filelist.size()==0)
    {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.custom_negative_popup, null);

        Button mbutton = (Button) mView.findViewById(R.id.printButton);
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();
        mbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            dialog.dismiss();
            }
        });
    }
    else {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        final View mView = getLayoutInflater().inflate(R.layout.custom_popup, null);
        final QuantityView qview = (QuantityView) mView.findViewById(R.id.quantityView_default);

        final RadioGroup rgroup = (RadioGroup) mView.findViewById(R.id.radioGroup);
        final RadioButton[] rbutton = new RadioButton[1];
        ImageView closeview = (ImageView) mView.findViewById(R.id.closePopUp);

        Button mbutton = (Button) mView.findViewById(R.id.printButton);
        mbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = rgroup.getCheckedRadioButtonId();
                rbutton[0] = (RadioButton) mView.findViewById(selectedId);

                int copi = qview.getQuantity();
               // Toast.makeText(MainActivity.this, "id "+selectedId, Toast.LENGTH_SHORT).show();
//5  Toast.makeText(MainActivity.this, "called invoked" + qview.getQuantity() + "   " + rbutton.getText(), Toast.LENGTH_SHORT).show();
                Intent i;
                i = new Intent(MainActivity.this, qrcode.class);
                i.putStringArrayListExtra("filenames", filelist);
                System.out.println("first page " + filelist);
                i.putStringArrayListExtra("fileextensions", fileextensions);
                i.putExtra("copies", copi);
                i.putExtra("page_count",total_pages+"");
                i.putExtra("color", rbutton[0].getText());
                i.putStringArrayListExtra("fname",fname);

                startActivity(i);

            }
        });
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();
        closeview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

}

    public static String getMimeType(Context context, Uri uri) {
        String extension;

        //Check uri format to avoid null
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //If scheme is a content
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());

        }

        return extension;
    }
    @Override
    public void onBackPressed()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            this.finishAffinity();
        }
        else
        {
            System.exit(0);
        }
    }

}
