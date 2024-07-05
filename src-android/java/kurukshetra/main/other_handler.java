package kurukshetra.main;

import android.content.Intent;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;

import static kurukshetra.main.MainActivity.getMimeType;

public class other_handler extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_handler);
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            System.out.println(type+" reached");
            if ("text/plain".equals(type)||"image/jpg".equals(type)||"image/jpeg".equals(type)||"image/png".equals(type)||"application/pdf".equals(type)
                    ||"application/vnd.openxmlformats-officedocument.wordprocessingml.document".equals(type)) {
                handleSend(intent); // Handle text being sent
            }else if("image/*".equals(type)){
                handleImage(intent);
            }
            else
            {
                Toast.makeText(this, "File not Supported", Toast.LENGTH_SHORT).show();
            }
        } else if(Intent.ACTION_SEND_MULTIPLE.equals(action)&&type!=null){
            System.out.println("type  "+type);
            handleall(intent);

        }else
        {
            Toast.makeText(this, "File not Supported", Toast.LENGTH_SHORT).show();
        }
    }



    void handleImage(Intent intent)
    {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        String extension = getMimeType(getApplicationContext(),imageUri);
        System.out.println("image handler reached"+extension);
        if(extension.equals("jpg")||extension.equals("png")||extension.equals("jpeg"))
        {
            Intent i = new Intent(other_handler.this,MainActivity.class);
            i.putExtra("shareduri",imageUri.toString());
            startActivity(i);
        }
        else
        {
            Toast.makeText(this, "File not Supported", Toast.LENGTH_SHORT).show();
        }
    }
    void handleSend(Intent intent)
    {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        System.out.println("uri reached is "+imageUri);
        if (imageUri != null) {
            // Update UI to reflect image being shared
            Intent i = new Intent(other_handler.this,MainActivity.class);
            i.putExtra("shareduri",imageUri.toString());
            startActivity(i);
        }
        else
        {
            Toast.makeText(this, "File not supported", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleall(Intent intent)
    {
        System.out.println("handle all invoked");
        ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if(imageUris!=null) {
            ArrayList<Uri> imageUris2 = new ArrayList<Uri>();
            boolean flag = false;
            for (int i = 0; i < imageUris.size(); i++) {
                Uri temp = imageUris.get(i);
                String extension = getMimeType(getApplicationContext(), temp);
                if (extension.equals("jpg") || extension.equals("png") || extension.equals("jpeg")|| extension.equals("docx")|| extension.equals("pdf")|| extension.equals("txt")) {
                        imageUris2.add(temp);
                    }
                else
                {
                    flag = true;
                }
                }
            Intent i = new Intent(other_handler.this, MainActivity.class);
            i.putParcelableArrayListExtra("urilist", imageUris2);
            startActivity(i);
        }
        else
        {
            Toast.makeText(this, "File not supported", Toast.LENGTH_SHORT).show();
        }
    }
}
