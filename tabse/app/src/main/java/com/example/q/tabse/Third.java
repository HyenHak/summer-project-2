package com.example.q.tabse;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;
import android.graphics.Bitmap;

public class Third extends Fragment {

    public ListView listview1, listview2;
    public ListViewAdapter adapter1, adapter2;
    public String FileName = new String();
    public String FileName2 = new String();
    public String FilePath = new String();
    public Gson gson = new Gson();
    public Person person;
    public ArrayList<myString> data1 = new ArrayList<>();
    public ArrayList<myString> data2 = new ArrayList<>();
    public ImageView imageview;
    private final int GALLERY_CODE=1112;
    private final int CAMERA_CODE = 1111;

    public final static int REQUEST_READEXTERN = 1113;

    public Third() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FileName2 = "image.txt";

        try {
            File file = new File(getActivity().getFilesDir(), FileName2);
            FileReader fr = new FileReader(file);
            BufferedReader bufrd = new BufferedReader(fr);

            String ch;
            while ((ch = bufrd.readLine()) != null) {
                FilePath = FilePath + ch;
            }

            bufrd.close();
            fr.close();
        }
        catch (Exception e) {
        }

        person = new Person();

        data1.add(new myString("이름", person.name));
        data1.add(new myString("성별", person.sex));
        data2.add(new myString("전화번호", person.phone_number1));
        data2.add(new myString("", person.phone_number2));
        data2.add(new myString("", person.phone_number3));
        data2.add(new myString("이메일", person.email));
        data2.add(new myString("소속", person.department));
    }

    public class ListViewAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        public ArrayList<myString> data;
        int layout;

        public ListViewAdapter(Context context, int layout, ArrayList<myString> data) {
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.data = data;
            this.layout = layout;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public myString getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(layout, parent, false);
            }

            myString item = data.get(position);

            TextView textview = (TextView) convertView.findViewById(R.id.textView);
            textview.setText(item.name);
            TextView textview2 = (TextView) convertView.findViewById(R.id.textView2);
            if((position >= 0 && position <= 2) && getCount()>=2) textview2.setText(format.StringToPhone(item.element));
            else textview2.setText(item.element);

            return convertView;
        }

        public void addItem(myString item) {
            data.add(item);
            dataChange();
        }

        public void dataChange() {
            this.notifyDataSetChanged();
        }
    }

    public void FileSave() {
        try {
            File file = new File(getActivity().getFilesDir(), FileName);
            FileWriter fw = new FileWriter(file);
            BufferedWriter bufwr = new BufferedWriter(fw);

            String save;
            save = gson.toJson(person, Person.class);
            bufwr.write(save);
            bufwr.close();
            fw.close();
        }
        catch (Exception e) {
        }

        try {
            File file = new File(getActivity().getFilesDir(), FileName2);
            FileWriter fw = new FileWriter(file);
            BufferedWriter bufwr = new BufferedWriter(fw);

            bufwr.write(FilePath);
            bufwr.close();
            fw.close();
        }
        catch (Exception e) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_third, container, false);

        listview1 = (ListView) view.findViewById(R.id._listView);
        adapter1 = new ListViewAdapter(getActivity(), R.layout.item, data1);
        listview1.setAdapter(adapter1);

        listview2 = (ListView) view.findViewById(R.id._listView2);
        adapter2 = new ListViewAdapter(getActivity(), R.layout.item, data2);
        listview2.setAdapter(adapter2);

        imageview = (ImageView) view.findViewById(R.id.imageView);

        if(!FilePath.equals(new String())) {
            int flag = 0;
            int permissionReadExtern = ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
            if (permissionReadExtern == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READEXTERN);
                permissionReadExtern = ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
                if (permissionReadExtern == PackageManager.PERMISSION_DENIED) flag = 1;
            }
            if (flag != 1) {
                ExifInterface exif = null;
                try {
                    exif = new ExifInterface(FilePath);
                    int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    int exifDegree = exifOrientationToDegrees(exifOrientation);
                    Bitmap bitmap = BitmapFactory.decodeFile(FilePath);//경로를 통해 비트맵으로 전환
                    imageview.setImageBitmap(rotate(bitmap, exifDegree));//이미지 뷰에 비트맵 넣기
                    imageview.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println(FilePath);
                }
            }
        }

        listview1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                show1(position);
            }
        });

        listview2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                show1(position + 2);
            }
        });

        imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int permissionReadExtern = ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
                if(permissionReadExtern == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READEXTERN);
                    permissionReadExtern = ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
                    if(permissionReadExtern == PackageManager.PERMISSION_DENIED) return;
                }

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_CODE);
            }
        });

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 0; i < permissions.length; i++) {
            String permission = permissions[i];
            int grantResult = grantResults[i];
            if (permission.equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                if(grantResult == PackageManager.PERMISSION_GRANTED) {
                    //resultText.setText("read/write storage permission authorized");
                }
                else {
                    //resultText.setText("read/write storage permission denied");
                }
            }
        }
    }

    @Override
     public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case GALLERY_CODE:
                    sendPicture(data.getData()); //갤러리에서 가져오기
                    break;
                case CAMERA_CODE:
                    //getPictureForPhoto(); //카메라에서 가져오기
                    break;
                default:
                    break;
            }
        }
    }

    private void sendPicture(Uri imgUri) {

        String imagePath = getRealPathFromURI(imgUri); // path 경로
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(imagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        int exifDegree = exifOrientationToDegrees(exifOrientation);

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);//경로를 통해 비트맵으로 전환
        imageview.setImageBitmap(rotate(bitmap, exifDegree));//이미지 뷰에 비트맵 넣기
        imageview.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        FilePath = new String(imagePath);
        FileSave();
    }

    private int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    private Bitmap rotate(Bitmap src, float degree) {
        Matrix matrix = new Matrix();

        matrix.postRotate(degree);

        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }

    private String getRealPathFromURI(Uri contentUri) {
        int column_index=0;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(contentUri, proj, null, null, null);
        if(cursor.moveToFirst()){
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        }
        return cursor.getString(column_index);
    }

    @Override
    public void onDestroy() {
        FileSave();
        super.onDestroy();
    }

    void show1(final int position) {
        final EditText edittext = new EditText(getActivity());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        String title;

        switch(position){
            case 0: title = "이름"; edittext.setInputType(0x00000001); break;
            case 1: title = "성별"; edittext.setInputType(0x00000001); break;
            case 2: title = "전화번호1"; edittext.setInputType(0x00000003); break;
            case 3: title = "전화번호2"; edittext.setInputType(0x00000003); break;
            case 4: title = "전화번호3"; edittext.setInputType(0x00000003); break;
            case 5: title = "이메일"; edittext.setInputType(0x00000021); break;
            case 6: title = "소속"; edittext.setInputType(0x00000001); break;
            default: title ="";
        }

        builder.setTitle("프로필 수정");
        builder.setMessage(title + "을(를) 입력하십시오");
        builder.setView(edittext);
        builder.setPositiveButton("입력",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch(position){
                            case 0: person.name = edittext.getText().toString(); data1.get(position).element = edittext.getText().toString(); break;
                            case 1: person.sex = edittext.getText().toString(); data1.get(position).element = edittext.getText().toString(); break;
                            case 2: person.phone_number1 = edittext.getText().toString(); data2.get(position - 2).element = edittext.getText().toString(); break;
                            case 3: person.phone_number2 = edittext.getText().toString(); data2.get(position - 2).element = edittext.getText().toString(); break;
                            case 4: person.phone_number3 = edittext.getText().toString(); data2.get(position - 2).element = edittext.getText().toString(); break;
                            case 5: person.email = edittext.getText().toString(); data2.get(position - 2).element = edittext.getText().toString(); break;
                            case 6: person.department = edittext.getText().toString(); data2.get(position - 2).element = edittext.getText().toString(); break;
                            default: break;
                        }
                        adapter1.dataChange();
                        adapter2.dataChange();
                    }
                });
        builder.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        builder.show();
    }

    public class myString {
        String name, element;

        public myString(){
            name = new String();
            element = new String();
        }

        public myString(String name, String element){
            this.name = name;
            this.element = element;
        }
    }
}
