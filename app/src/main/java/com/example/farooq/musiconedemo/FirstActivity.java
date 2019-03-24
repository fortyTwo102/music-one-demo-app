package com.example.farooq.musiconedemo;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.util.regex.Pattern;

public class FirstActivity extends AppCompatActivity {

    TextView path;
    String filePath;
    boolean isPathSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        Button go = (Button) findViewById(R.id.go);
        Button open = (Button) findViewById(R.id.open);
        path = (TextView) findViewById(R.id.path);

        if (shouldAskPermissions()) {
            askPermissions();
        }

        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialFilePicker()
                        .withActivity(FirstActivity.this)
                        .withRequestCode(1)
                        .withFilter(Pattern.compile("((?:[^/]*/)*)(.*)")) // Filtering files and directories by file name using regexp
                        .withFilterDirectories(true) // Set directories filterable (false by default)
                        .withHiddenFiles(true) // Show hidden files and folders
                        .start();

                isPathSelected=true;
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            // Do anything with file

            path.setText(filePath);
        }
    }

    public void openMain(View view){
        if(isPathSelected) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("PATH", filePath);
            startActivity(intent);
        }
        else{
            Toast.makeText(getApplicationContext(),"Please select a file! :(",Toast.LENGTH_SHORT).show();
        }
    }

    protected boolean shouldAskPermissions() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(23)
    protected void askPermissions() {
        String[] permissions = {
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE"
        };
        int requestCode = 200;
        requestPermissions(permissions, requestCode);
    }
}
