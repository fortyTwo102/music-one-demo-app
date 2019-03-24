package com.example.farooq.musiconedemo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.AsyncTask;
import android.os.Build;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.ID3v24Tag;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;


import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.TagOptionSingleton;
import org.jaudiotagger.tag.datatype.Artwork;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.io.ByteArrayOutputStream;
import java.io.File;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    ID3v2 id3v2tag;
    Bitmap albumCover;
    byte[] bytes;

    boolean isAlbumChanged;
    boolean isTitleChanged;
    boolean isArtistChanged;

    EditText fillTitle;
    EditText fillAlbum;
    EditText fillArtist;

    String albumName;
    String artisName;
    String titleName;

    TextView title;
    TextView album;
    TextView artist;

    TextView artistLabel;
    TextView albumLabel;
    TextView titleLabel;
    ImageView albumArt;
    String filename;
    String ImagefilePath;
    Button justSave;

    org.jaudiotagger.tag.id3.AbstractID3v2Tag tag;
    MP3File musicFile;

    boolean secondActivityVisited;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        title = (TextView) findViewById(R.id.title);
        album = (TextView) findViewById(R.id.album);
        artist = (TextView) findViewById(R.id.artist);

        fillTitle = (EditText) findViewById(R.id.fillTitle);
        fillAlbum = (EditText) findViewById(R.id.fillAlbum);
        fillArtist = (EditText) findViewById(R.id.fillArtist);



        titleLabel = (TextView) findViewById(R.id.titleLabel);
        albumLabel = (TextView) findViewById(R.id.albumLabel);
        artistLabel = (TextView) findViewById(R.id.artistLabel);

        fillTitle.setVisibility(View.INVISIBLE);
        fillAlbum.setVisibility(View.INVISIBLE);
        fillArtist.setVisibility(View.INVISIBLE);

        artistLabel.setVisibility(View.INVISIBLE);
        albumLabel.setVisibility(View.INVISIBLE);
        titleLabel.setVisibility(View.INVISIBLE);


        albumArt = (ImageView) findViewById(R.id.albumArt);

        if (secondActivityVisited) {

            isAlbumChanged = (boolean) getIntent().getSerializableExtra("AL");
            isArtistChanged = (boolean) getIntent().getSerializableExtra("AR");
            isTitleChanged = (boolean) getIntent().getSerializableExtra("TI");

            albumName = (String) getIntent().getSerializableExtra("ALBUM");
            artisName = (String) getIntent().getSerializableExtra("ARTIST");
            titleName = (String) getIntent().getSerializableExtra("TITLE");

            Log.i("album", albumName);
            Toast.makeText(getApplicationContext(), "album: " + albumName, Toast.LENGTH_SHORT).show();
        }

        if (shouldAskPermissions()) {
            askPermissions();
        }

        title.setText("hey");

        try {

            filename = (String) getIntent().getSerializableExtra("PATH");
            Mp3File song = new Mp3File(filename);
            if (song.hasId3v2Tag()) {
                id3v2tag = song.getId3v2Tag();

            } else {
                // mp3 does not have an ID3v2 tag, let's create one..
                id3v2tag = new ID3v24Tag();
                song.setId3v2Tag(id3v2tag);
            }

            byte[] imageData = id3v2tag.getAlbumImage();
            //converting the bytes to an image
            albumCover = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
            albumArt.setImageBitmap(albumCover);

            title.setText("Title: " + id3v2tag.getTitle());
            album.setText("Album: " + id3v2tag.getAlbum());
            artist.setText("Artist: " + id3v2tag.getArtist());

            fillTitle.setText(id3v2tag.getTitle());
            fillAlbum.setText(id3v2tag.getAlbum());
            fillArtist.setText(id3v2tag.getArtist());

            File sourceFile = new File(filename);
            musicFile = (MP3File) AudioFileIO.read(sourceFile);
            tag = musicFile.getID3v2Tag();
            //bytes = getBytesFromBitmap(Environment.getExternalStorageDirectory().getPath() + "/album.png");
            //setMp3AlbumArt(sourceFile, bytes);

            //Toast.makeText(getApplicationContext(),"Page fetched: "+pageFetched,Toast.LENGTH_SHORT).show();
            // Log.i("PageContent: ",pageFetched);


        } catch (Exception e) {
            e.printStackTrace();
            title.setText(e.toString());
        }

      //  Button findCover = (Button) findViewById(R.id.findCover);
      //  findCover.setOnClickListener(new View.OnClickListener() {
      //      @Override
      //      public void onClick(View v) {
       //         new MaterialFilePicker()
       //                 .withActivity(MainActivity.this)
        //                .withRequestCode(1)
       //                 .withFilter(Pattern.compile("((?:[^/]*/)*)(.*)")) // Filtering files and directories by file name using regexp
       //                 .withFilterDirectories(true) // Set directories filterable (false by default)
      //                  .withHiddenFiles(true) // Show hidden files and folders
       //                 .start();
      //      }
      //  });

        final Button findCover = (Button) findViewById(R.id.findCover);
        findCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popupMenu = new PopupMenu(MainActivity.this, findCover);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu,popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getTitle().equals("Find Image from Phone")){
                            new MaterialFilePicker()
                                             .withActivity(MainActivity.this)
                                            .withRequestCode(1)
                                            .withFilter(Pattern.compile("((?:[^/]*/)*)(.*)")) // Filtering files and directories by file name using regexp
                                             .withFilterDirectories(true) // Set directories filterable (false by default)
                                              .withHiddenFiles(true) // Show hidden files and folders
                                             .start();
                        }else{

                            DownloadFromLastFM(id3v2tag.getArtist(),id3v2tag.getTitle());
                        }
                    return true;
                    }
                });

                popupMenu.show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            ImagefilePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            // Do anything with file

            File sourceFile = new File(filename);
            byte[] newCoverBytes = getBytesFromBitmap(ImagefilePath);

            try {
                setMp3AlbumArt(sourceFile, newCoverBytes);
                Mp3File song = new Mp3File(filename);
                if (song.hasId3v2Tag()) {
                    id3v2tag = song.getId3v2Tag();

                } else {
                    // mp3 does not have an ID3v2 tag, let's create one..
                    id3v2tag = new ID3v24Tag();
                    song.setId3v2Tag(id3v2tag);
                }

                byte[] imageData = id3v2tag.getAlbumImage();
                //converting the bytes to an image
                albumCover = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                albumArt.setImageBitmap(albumCover);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected boolean shouldAskPermissions() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    public String setMp3AlbumArt(File SourceFile, byte[] bytes)
            throws Exception {
        String error = null;
        try {
            musicFile = (MP3File) AudioFileIO.read(SourceFile);
            tag = musicFile.getID3v2Tag();
            if (tag != null) {
                Artwork artwork = null;
                try {
                    artwork = ArtworkFactory.createArtworkFromFile(SourceFile);
                } catch (IOException e) {
                    e.printStackTrace();
                    error = e.getMessage();
                }
                if (artwork != null) {
                    artwork.setBinaryData(bytes);
                    tag.deleteArtworkField();
                    tag.setField(artwork);
                    musicFile.setTag(tag);
                    musicFile.commit();
                } else {
                    artwork.setBinaryData(bytes);
                    tag.addField(artwork);
                    tag.setField(artwork);
                    musicFile.setTag(tag);
                    musicFile.commit();
                }
            }

        } catch (CannotReadException | IOException | TagException
                | ReadOnlyFileException | InvalidAudioFrameException e) {
            error = e.getMessage();
        }
        return error;
    }

    public byte[] getBytesFromBitmap(String path) {
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        ByteArrayOutputStream blob = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, blob);
        byte[] bitmapdata = blob.toByteArray();
        return bitmapdata;
    }

    public byte[] getBytesFromBitmap(Bitmap bitmap) {
       // Bitmap bitmap = BitmapFactory.decodeFile(path);
        ByteArrayOutputStream blob = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, blob);
        byte[] bitmapdata = blob.toByteArray();
        return bitmapdata;
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

    public void changeFields(View view) {


       /* secondActivityVisited = true;
        Intent intent = new Intent(this, SecondActivity.class);

        intent.putExtra("ARTIST",id3v2tag.getArtist());
        intent.putExtra("TITLE",id3v2tag.getTitle());
        intent.putExtra("ALBUM",id3v2tag.getAlbum());


        startActivity(intent); */

        justSave = (Button) findViewById(R.id.justSave);
        justSave.setVisibility(View.VISIBLE);

        title.setVisibility(View.INVISIBLE);
        album.setVisibility(View.INVISIBLE);
        artist.setVisibility(View.INVISIBLE);

        titleLabel.setVisibility(View.VISIBLE);
        albumLabel.setVisibility(View.VISIBLE);
        artistLabel.setVisibility(View.VISIBLE);

        fillTitle.setVisibility(View.VISIBLE);
        fillAlbum.setVisibility(View.VISIBLE);
        fillArtist.setVisibility(View.VISIBLE);

        fillTitle.setHint(titleName);
        fillAlbum.setHint(albumName);
        fillArtist.setHint(artisName);


    }

    public void saveFields(View view) throws FieldDataInvalidException, CannotWriteException, InvalidDataException, IOException, UnsupportedTagException {

        String titleName = fillTitle.getText().toString();
        tag.setField(FieldKey.TITLE, titleName);
        // Toast.makeText(getApplicationContext(), "title changed: " + titleName, Toast.LENGTH_SHORT).show();


        String albumName = fillAlbum.getText().toString();
        tag.setField(FieldKey.ALBUM, albumName);
        //  Toast.makeText(getApplicationContext(), "album changed: " + albumName, Toast.LENGTH_SHORT).show();


        String artistName = fillArtist.getText().toString();
        tag.setField(FieldKey.ARTIST, artistName);
        // Toast.makeText(getApplicationContext(), "artist changed: " + artistName, Toast.LENGTH_SHORT).show();

        musicFile.commit();
        TagOptionSingleton.getInstance().setAndroid(true);

        Mp3File song = new Mp3File(filename);
        id3v2tag = song.getId3v2Tag();


        title.setText("Title: " + id3v2tag.getTitle());
        album.setText("Album: " + id3v2tag.getAlbum());
        artist.setText("Artist: " + id3v2tag.getArtist());

        title.setVisibility(View.VISIBLE);
        album.setVisibility(View.VISIBLE);
        artist.setVisibility(View.VISIBLE);

        titleLabel.setVisibility(View.INVISIBLE);
        albumLabel.setVisibility(View.INVISIBLE);
        artistLabel.setVisibility(View.INVISIBLE);

        fillTitle.setVisibility(View.INVISIBLE);
        fillAlbum.setVisibility(View.INVISIBLE);
        fillArtist.setVisibility(View.INVISIBLE);

        Toast.makeText(getApplicationContext(), "Your changes have been saved! :)", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, FirstActivity.class);
        startActivity(intent);


    }

    public class DownloadTask extends AsyncTask<String, Void, Bitmap> {
        protected Bitmap doInBackground(String... urls) {
            String url = urls[0];
            String result = "";
            Bitmap newCover=null;

            try {
                Document doc = Jsoup.connect(url).get();
                Elements coverart = doc.getElementsByClass("header-expanded-image");
                for (Element el : coverart) {
                    result = el.absUrl("src");
                }

                URL urlCover = new URL(result);
                HttpURLConnection urlConnection = (HttpURLConnection) urlCover.openConnection();
                urlConnection.connect();
                InputStream in = urlConnection.getInputStream();
                newCover = BitmapFactory.decodeStream(in);

            } catch (IOException e) {
                e.printStackTrace();

            }

            return newCover;
        }

    }

    public void DownloadFromLastFM(String nameOfArtist, String nameOfTrack){
      //  Toast.makeText(getApplicationContext(),"Downloading from Last.fm ... ",Toast.LENGTH_SHORT).show();

        nameOfArtist = nameOfArtist.toLowerCase().replace(" ","+");
        nameOfTrack = nameOfTrack.toLowerCase().replace(" ","+");

        String baseURL = "https://www.last.fm/music/" + nameOfArtist + "/" + nameOfTrack;

        DownloadTask task = new DownloadTask();
        Bitmap newCoverBitmap;
        byte[] newCoverByte;
        try {
          //  Toast.makeText(getApplicationContext(),"Downloading...",Toast.LENGTH_SHORT).show();;
            newCoverBitmap = task.execute(baseURL).get();
            newCoverByte = getBytesFromBitmap(newCoverBitmap);
            File newSourceFile = new File(filename);
            setMp3AlbumArt(newSourceFile,newCoverByte);
            albumArt.setImageBitmap(newCoverBitmap);

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void fromLastFM(View view){
        DownloadFromLastFM(id3v2tag.getArtist(),id3v2tag.getTitle());
    }

    public void OnlySave(View view){
        String titleName = fillTitle.getText().toString();
        try {
            tag.setField(FieldKey.TITLE, titleName);
            // Toast.makeText(getApplicationContext(), "title changed: " + titleName, Toast.LENGTH_SHORT).show();


            String albumName = fillAlbum.getText().toString();
            tag.setField(FieldKey.ALBUM, albumName);
            //  Toast.makeText(getApplicationContext(), "album changed: " + albumName, Toast.LENGTH_SHORT).show();


            String artistName = fillArtist.getText().toString();
            tag.setField(FieldKey.ARTIST, artistName);
            // Toast.makeText(getApplicationContext(), "artist changed: " + artistName, Toast.LENGTH_SHORT).show();

            musicFile.commit();
            TagOptionSingleton.getInstance().setAndroid(true);

            Mp3File song = new Mp3File(filename);
            id3v2tag = song.getId3v2Tag();
        }catch (Exception e){
            e.printStackTrace();
        }

        title.setText("Title: " + id3v2tag.getTitle());
        album.setText("Album: " + id3v2tag.getAlbum());
        artist.setText("Artist: " + id3v2tag.getArtist());

        title.setVisibility(View.VISIBLE);
        album.setVisibility(View.VISIBLE);
        artist.setVisibility(View.VISIBLE);

        titleLabel.setVisibility(View.INVISIBLE);
        albumLabel.setVisibility(View.INVISIBLE);
        artistLabel.setVisibility(View.INVISIBLE);

        fillTitle.setVisibility(View.INVISIBLE);
        fillAlbum.setVisibility(View.INVISIBLE);
        fillArtist.setVisibility(View.INVISIBLE);
        justSave.setVisibility(View.INVISIBLE);
    }
}
