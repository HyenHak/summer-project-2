package com.example.q.tabse;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

public class Second extends Fragment {

    GridView galleryGridView;
    public static ArrayList<Image> imageList = new ArrayList<>();
    public static ArrayList<Id_element> id_list = new ArrayList<>();
    String album_name = "";
    LoadAlbumImages loadAlbumTask;
    ProgressBar progressbar;
    FloatingActionButton button, button2, button3;
    static final int REQUEST_PERMISSION_KEY = 1000;
    private Uri photoUri;
    private String currentPhotoPath;//실제 사진 파일 경로
    private String mImageCaptureName;//이미지 이름
    private final int CAMERA_CODE = 1234;
    public static String user_id;
    StringBuffer response = new StringBuffer();
    public Gson gson = new Gson();

    class LoadAlbumImages extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            imageList.clear();
            album_name = user_id;
        }

        protected String doInBackground(String... args) {
            String xml = "";

            String path = null;
            Uri uriExternal = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            Uri uriInternal = android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI;

            String[] projection = { MediaStore.MediaColumns.DATA,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.MediaColumns.DATE_MODIFIED };

            Cursor cursorExternal = getActivity().getContentResolver().query(uriExternal, projection, "bucket_display_name = \""+album_name+"\"", null, null);
            Cursor cursorInternal = getActivity().getContentResolver().query(uriInternal, projection, "bucket_display_name = \""+album_name+"\"", null, null);
            Cursor cursor = new MergeCursor(new Cursor[]{cursorExternal,cursorInternal});

            while (cursor.moveToNext()) {
                path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
                imageList.add(new Image(path));
            }
            cursor.close();
            //Collections.sort(imageList); // Arranging photo album by timestamp decending
            return xml;
        }

        @Override
        protected void onPostExecute(String xml) {
            SingleAlbumAdapter adapter = new SingleAlbumAdapter(getActivity(), imageList);
            galleryGridView.setAdapter(adapter);
            galleryGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view,
                                        final int position, long id) {
                    Intent intent = new Intent(getActivity(), GalleryPreview.class);
                    intent.putExtra("pos", position);
                    startActivity(intent);
                }
            });
            galleryGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(galleryGridView.getContext());
                    builder.setTitle("삭제")
                            .setMessage("사진을 삭제하시겠습니까?")
                            .setCancelable(false)
                            .setPositiveButton("확인", new DialogInterface.OnClickListener(){
                                public void onClick(DialogInterface dialog, int whichButton){
                                    File file = new File(imageList.get(i).file_name);
                                    if(file.exists()){
                                        file.delete();
                                        imageList.remove(i);

                                        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                                        String selection = MediaStore.Images.Media.DATA + " = ?";
                                        String[] selectionArgs = {file.getAbsolutePath()};
                                        getActivity().getContentResolver().delete(uri, selection, selectionArgs);

                                        galleryGridView.setAdapter(galleryGridView.getAdapter());
                                    }
                                }
                            })
                            .setNegativeButton("취소", new DialogInterface.OnClickListener(){
                                public void onClick(DialogInterface dialog, int whichButton){
                                    dialog.cancel();
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return true;
                }
            });
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_second, null);

        galleryGridView = (GridView) view.findViewById(R.id.galleryGridView);
        int iDisplayWidth = getResources().getDisplayMetrics().widthPixels ;
        Resources resources = getActivity().getApplicationContext().getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = iDisplayWidth / (metrics.densityDpi / 160f);

        if(dp < 360)
        {
            dp = (dp - 17) / 2;
            float px = Function.convertDpToPixel(dp, getActivity().getApplicationContext());
            galleryGridView.setColumnWidth(Math.round(px));
        }

        button = view.findViewById(R.id.floatingActionButton6);
        button2 = view.findViewById(R.id.floatingActionButton5);
        button3 = view.findViewById(R.id.floatingActionButton7);
        progressbar = view.findViewById(R.id.progressBar2);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectPhoto();
            }
        });

        button3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                button3.setClickable(false);
                button2.setClickable(false);
                button.setClickable(false);
                galleryGridView.setClickable(false);
                int i;
                for(i=0;i<imageList.size();i++){
                    if(imageList.get(i).bitmap == "") {
                        Bitmap bitmap = DecodeUtils.decode(getActivity(), getUriFromPath(imageList.get(i).file_name), 1024, 1024);
                        imageList.get(i).bitmap = getBase64String(bitmap);
                    }
                }
                final String finalQuery = "https://logapps.azurewebsites.net/tables/Gallery";
                progressbar.setProgress(10);

                new Thread(new Runnable() {
                    public void run() {
                        try {
                            int i;
                            for (i = 0; i < imageList.size(); i++) {
                                URL obj = new URL(finalQuery + "?$filter=owner eq '" + user_id + "'" + "&$select=id");
                                HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                                // optional default is GET
                                con.setRequestMethod("GET");
                                con.setRequestProperty("Accept", "application/json");
                                con.setRequestProperty("zumo-api-version", "2.0.0");

                                int responseCode = 0;

                                responseCode = con.getResponseCode();

                                System.out.println("\nSending 'GET' request to URL : " + finalQuery);
                                System.out.println("Response Code : " + responseCode);

                                BufferedReader in = null;

                                if (responseCode != 409) {
                                    in = new BufferedReader(
                                            new InputStreamReader(con.getInputStream()));

                                    String inputLine;
                                    response = new StringBuffer();

                                    while ((inputLine = in.readLine()) != null) {
                                        response.append(inputLine);
                                    }
                                    in.close();
                                }
                                con.disconnect();
                                id_list = gson.fromJson(response.toString(), new TypeToken<ArrayList<Id_element>>(){}.getType());
                                progressbar.setProgress(progressbar.getProgress()+30/imageList.size());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            progressbar.setProgress(40);
                            int i;
                            for(i=0;i<id_list.size();i++) {
                                URL obj = new URL(finalQuery+"/"+id_list.get(i).id);
                                HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                                // optional default is GET
                                con.setRequestMethod("DELETE");
                                con.setRequestProperty("Accept", "application/json");
                                con.setRequestProperty("zumo-api-version", "2.0.0");

                                int responseCode = 0;

                                responseCode = con.getResponseCode();

                                System.out.println("\nSending 'DELETE' request to URL : " + finalQuery);
                                System.out.println("Response Code : " + responseCode);

                                BufferedReader in = null;

                                if(responseCode!=409) {
                                    in = new BufferedReader(
                                            new InputStreamReader(con.getInputStream()));

                                    String inputLine;
                                    response = new StringBuffer();

                                    while ((inputLine = in.readLine()) != null) {
                                        response.append(inputLine);
                                    }
                                    in.close();
                                }
                                con.disconnect();
                                progressbar.setProgress(progressbar.getProgress()+30/id_list.size());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            progressbar.setProgress(70);
                            int i;
                            for (i = 0; i < imageList.size(); i++) {
                                URL obj = new URL(finalQuery);
                                HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                                // optional default is GET
                                con.setRequestMethod("POST");
                                con.setRequestProperty("Accept", "application/json");
                                con.setRequestProperty("Content-Type", "application/json");
                                con.setRequestProperty("zumo-api-version", "2.0.0");
                                con.setDoOutput(true);

                                OutputStream out = con.getOutputStream();
                                out.write(gson.toJson(imageList.get(i), Image.class).getBytes());
                                out.close();

                                int responseCode = 0;

                                responseCode = con.getResponseCode();

                                System.out.println("\nSending 'GET' request to URL : " + finalQuery);
                                System.out.println("Response Code : " + responseCode);

                                BufferedReader in = null;

                                if (responseCode != 409) {
                                    in = new BufferedReader(
                                            new InputStreamReader(con.getInputStream()));

                                    String inputLine;
                                    response = new StringBuffer();

                                    while ((inputLine = in.readLine()) != null) {
                                        response.append(inputLine);
                                    }
                                    in.close();
                                    con.disconnect();
                                }
                                progressbar.setProgress(progressbar.getProgress()+30/imageList.size());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        progressbar.setProgress(0);
                        button3.setClickable(true);
                        button2.setClickable(true);
                        button.setClickable(true);
                        galleryGridView.setClickable(true);
                    }
                }).start();
            }
        });


        button2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                button3.setClickable(false);
                button2.setClickable(false);
                button.setClickable(false);
                galleryGridView.setClickable(false);
                final String finalQuery = "https://logapps.azurewebsites.net/tables/Gallery";
                progressbar.setProgress(10);
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            URL obj = new URL(finalQuery + "?$filter=owner eq '" + user_id + "'");
                            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                            progressbar.setProgress(40);

                            // optional default is GET
                            con.setRequestMethod("GET");
                            con.setRequestProperty("Accept", "application/json");
                            con.setRequestProperty("zumo-api-version", "2.0.0");

                            int responseCode = 0;

                            responseCode = con.getResponseCode();
                            progressbar.setProgress(70);

                            System.out.println("\nSending 'GET' request to URL : " + finalQuery);
                            System.out.println("Response Code : " + responseCode);

                            BufferedReader in = null;

                            if (responseCode != 409) {
                                in = new BufferedReader(new InputStreamReader(con.getInputStream()));

                                String inputLine;
                                response = new StringBuffer();

                                while ((inputLine = in.readLine()) != null) {
                                    response.append(inputLine);
                                }
                                in.close();
                            }
                            con.disconnect();
                            progressbar.setProgress(100);
                            imageList = gson.fromJson(response.toString(), new TypeToken<ArrayList<Image>>() {}.getType());
                            Message msg = handler.obtainMessage();
                            handler.sendMessage(msg);
                            progressbar.setProgress(0);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                        button3.setClickable(true);
                        button2.setClickable(true);
                        button.setClickable(true);
                        galleryGridView.setClickable(true);
                    }
                }).start();
            }
        });

        loadAlbumTask = new LoadAlbumImages();
        loadAlbumTask.execute();

        return view;
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            int i;

            for(i=0;i<imageList.size();i++){
                byte[] decodedByteArray = Base64.decode(imageList.get(i).bitmap, Base64.NO_WRAP);
                Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
                saveBitmaptoJpeg(decodedBitmap, Second.user_id, imageList.get(i).file_name, getActivity());
            }

            try{
                Thread.sleep(300);
            }
            catch (Exception e){
                e.printStackTrace();
            }

            loadAlbumTask = new LoadAlbumImages();
            loadAlbumTask.execute();
        }
    };

    public Uri getUriFromPath(String path) {
        Uri uri = Uri.fromFile(new File(path));
        return uri;
    }

    public String getBase64String(Bitmap bitmap)  {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        byte[] imageBytes = byteArrayOutputStream.toByteArray();

        return Base64.encodeToString(imageBytes, Base64.NO_WRAP);
    }

    private void selectPhoto() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {

                }
                if (photoFile != null) {
                    photoUri = FileProvider.getUriForFile(getContext(), getActivity().getPackageName(), photoFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    startActivityForResult(intent, CAMERA_CODE);
                }
            }

        }
    }

    private File createImageFile() throws IOException {
        File dir = new File(Environment.getExternalStorageDirectory() + "/path/" + user_id + "/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        mImageCaptureName = timeStamp + ".png";

        File storageDir = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/path/" + user_id + "/"
                + mImageCaptureName);
        currentPhotoPath = storageDir.getAbsolutePath();

        return storageDir;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CAMERA_CODE:
                    Intent media = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    File f = new File(currentPhotoPath);
                    Uri contentUri = Uri.fromFile(f);
                    media.setData(contentUri);
                    getActivity().sendBroadcast(media);

                    try{
                        Thread.sleep(1000);
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }

                    loadAlbumTask = new LoadAlbumImages();
                    loadAlbumTask.execute();
                    break;
                default:
                    break;
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case REQUEST_PERMISSION_KEY: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    loadAlbumTask = new LoadAlbumImages();
                    loadAlbumTask.execute();
                } else
                {
                    Toast.makeText(getActivity(), "You must accept permissions.", Toast.LENGTH_LONG).show();
                }
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if(!Function.hasPermissions(getContext(), PERMISSIONS)){
            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, REQUEST_PERMISSION_KEY);
        }else{
        }
    }

    public static void saveBitmaptoJpeg(Bitmap bitmap, String folder, String name, Context context){
        String ex_storage =Environment.getExternalStorageDirectory().getAbsolutePath(); // Get Absolute Path in External Sdcard
        String foler_name = "/path/" + folder + "/";
        String file_name = name;
        String string_path = ex_storage+foler_name;
        File file_path;
        try{
            file_path = new File(string_path);
            if(!file_path.isDirectory()){
                file_path.mkdirs();
            }
            File file = new File(file_name);
            if(file.isDirectory()) return;
            FileOutputStream out = new FileOutputStream(file_name);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();

            Intent media = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            File f = new File(file_name);
            Uri contentUri = Uri.fromFile(f);
            media.setData(contentUri);
            context.sendBroadcast(media);
        }catch(FileNotFoundException exception){
            Log.e("FileNotFoundException", exception.getMessage());
        }catch(IOException exception){
            Log.e("IOException", exception.getMessage());
        }
    }
}
class SingleAlbumAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList<Image> data;
    public SingleAlbumAdapter(Activity a, ArrayList<Image> d) {
        activity = a;
        data = d;
    }
    public int getCount() {
        return data.size();
    }
    public Object getItem(int position) {
        return position;
    }
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        SingleAlbumViewHolder holder = null;
        if (convertView == null) {
            holder = new SingleAlbumViewHolder();
            convertView = LayoutInflater.from(activity).inflate(
                    R.layout.single_album_row, parent, false);

            holder.galleryImage = (ImageView) convertView.findViewById(R.id.galleryImage);

            convertView.setTag(holder);
        } else {
            holder = (SingleAlbumViewHolder) convertView.getTag();
        }
        holder.galleryImage.setId(position);

        String song = data.get(position).file_name;
        try {
            Glide.with(activity)
                    .load(new File(song)) // Uri of the picture
                    .into(holder.galleryImage);
        } catch (Exception e) {}
        return convertView;
    }
}
class SingleAlbumViewHolder {
    ImageView galleryImage;
}
class Image{
    public String id;
    public String bitmap;
    public String file_name;
    public String owner;

    public Image(){
        id = "";
        bitmap = "";
        file_name = "";
        owner = Second.user_id;
    }
    public Image(String path){
        id = "";
        bitmap = "";
        file_name = path;
        owner = Second.user_id;
    }
}
class Id_element{
    public String id;
    public Id_element(){
        id = "";
    }
}

