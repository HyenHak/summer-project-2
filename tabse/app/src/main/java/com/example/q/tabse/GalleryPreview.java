package com.example.q.tabse;

import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterInside;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;

import java.io.IOException;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import java.io.Closeable;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore.Images.ImageColumns;


/**
 * Created by SHAJIB on 25/12/2015.
 */
public class GalleryPreview extends AppCompatActivity {
    public ArrayList<String> imageList = new ArrayList<>();
    public int array_size;
    public ViewPager vp;
    public static String [] pt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.gallery_preview);

        int i;
        for(i=0;i<Second.imageList.size();i++){
            imageList.add(Second.imageList.get(i).file_name);
        }
        array_size = imageList.size();
        pt = new String[array_size];

        int position = getIntent().getIntExtra("pos", 0);

        vp = (ImageViewTouchViewPager) findViewById(R.id.tv_pager);

        for(i=0;i<array_size;i++){
            pt[i] = imageList.get(i);
        }

        vp.setAdapter(new PagerAdapterClass(this, array_size));
        vp.setCurrentItem(position);
    }
}

class ImageViewTouchViewPager extends ViewPager {
    private static final String TAG = "ImageViewTouchViewPager";
    public static final String VIEW_PAGER_OBJECT_TAG = "image#";

    private int previousPosition;

    private OnPageSelectedListener onPageSelectedListener;

    public ImageViewTouchViewPager(Context context) {
        super(context);
        init();
    }

    public ImageViewTouchViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setOnPageSelectedListener(OnPageSelectedListener listener) {
        onPageSelectedListener = listener;
    }

    @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        if (v instanceof ImageViewTouch) {
            if (((ImageViewTouch)v).getScale() == ((ImageViewTouch)v).getMinScale()) {
                return super.canScroll(v, checkV, dx, x, y);
            }
            return ((ImageViewTouch) v).canScroll(dx);
        } else {
            return super.canScroll(v, checkV, dx, x, y);
        }
    }

    public interface OnPageSelectedListener {

        public void onPageSelected(int position);

    }

    private void init() {
        previousPosition = getCurrentItem();
        setOnPageChangeListener(new SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (onPageSelectedListener != null) {
                    onPageSelectedListener.onPageSelected(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == SCROLL_STATE_SETTLING && previousPosition != getCurrentItem()) {
                    try {
                        ImageViewTouch imageViewTouch = (ImageViewTouch)
                                findViewWithTag(VIEW_PAGER_OBJECT_TAG + getCurrentItem());
                        if (imageViewTouch != null) {
                            imageViewTouch.zoomTo(1f, 300);
                        }

                        previousPosition = getCurrentItem();
                    } catch (ClassCastException ex) {
                        Log.e(TAG, "This view pager should have only ImageViewTouch as a children.", ex);
                    }
                }
            }
        });
    }
}

class PagerAdapterClass extends PagerAdapter{

    private LayoutInflater mInflater;
    private int size;
    private Context mcontext;

    public PagerAdapterClass(Context c, int size){
        super();
        mInflater = LayoutInflater.from(c);
        this.size = size;
        mcontext = c;
    }

    @Override
    public int getCount() {
        return size;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = mInflater.inflate(R.layout.image_view, null);
        ImageViewTouch ivt = view.findViewById(R.id.image);
        String imagePath = GalleryPreview.pt[position]; // path 경로
        ivt.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
        Bitmap bitmap = DecodeUtils.decode( mcontext, getUriFromPath(imagePath), 1024, 1024 );
        ivt.setImageBitmap(bitmap);//이미지 뷰에 비트맵 넣기
        container.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(View pager, int position, Object view) {
        ((ViewPager)pager).removeView((View)view);
    }

    @Override
    public boolean isViewFromObject(View pager, Object obj) {
        return pager == obj;
    }

    @Override public void restoreState(Parcelable arg0, ClassLoader arg1) {}
    @Override public Parcelable saveState() { return null; }

    public Uri getUriFromPath(String path) {
        Uri uri = Uri.fromFile(new File(path));
        return uri;
    }
}

class DecodeUtils {

    /**
     * Try to load a {@link Bitmap} from the passed {@link Uri} ( a file, a content or an url )
     *
     * @param context	the current app context
     * @param uri	the image source
     * @param maxW	the final image maximum width
     * @param maxH	the final image maximum height
     * @return	the loaded and resized bitmap, if success, or null if load was unsuccesful
     */

    public static Bitmap decode( Context context, Uri uri, int maxW, int maxH ) {
        InputStream stream = openInputStream( context, uri );
        if ( null == stream ) {
            return null;
        }

        int orientation = ExifUtils.getExifOrientation( context, uri );

        Bitmap bitmap = null;
        int[] imageSize = new int[2];
        final boolean decoded = decodeImageBounds( stream, imageSize );
        IOUtils.closeSilently( stream );

        if ( decoded ) {
            int sampleSize = computeSampleSize( imageSize[0], imageSize[1], (int) ( maxW * 1.2 ), (int) ( maxH * 1.2 ), orientation );

            BitmapFactory.Options options = getDefaultOptions();
            options.inSampleSize = sampleSize;

            bitmap = decodeBitmap( context, uri, options, maxW, maxH, orientation, 0 );
        }

        return bitmap;
    }

    static Bitmap decodeBitmap( Context context, Uri uri, BitmapFactory.Options options, int maxW, int maxH,
                                int orientation, int pass ) {

        Bitmap bitmap = null;
        Bitmap newBitmap = null;


        if ( pass > 20 ) {
            return null;
        }

        InputStream stream = openInputStream( context, uri );
        if( null == stream ) return null;

        try {
            // decode the bitmap via android BitmapFactory
            bitmap = BitmapFactory.decodeStream( stream, null, options );
            IOUtils.closeSilently( stream );

            if ( bitmap != null ) {
                newBitmap = BitmapUtils.resizeBitmap( bitmap, maxW, maxH, orientation );
                if ( bitmap != newBitmap ) {
                    bitmap.recycle();
                }
                bitmap = newBitmap;
            }

        } catch ( OutOfMemoryError error ) {
            IOUtils.closeSilently( stream );
            if ( null != bitmap ) {
                bitmap.recycle();
            }
            options.inSampleSize += 1;
            bitmap = decodeBitmap( context, uri, options, maxW, maxH, orientation, pass + 1 );
        }
        return bitmap;

    }

    /**
     * Return an {@link InputStream} from the given uri. ( can be a local content, a file path or an http url )
     *
     * @param context
     * @param uri
     * @return the {@link InputStream} from the given uri, null if uri cannot be opened
     */
    public static InputStream openInputStream( Context context, Uri uri ) {
        if ( null == uri ) return null;
        final String scheme = uri.getScheme();
        InputStream stream = null;
        if ( scheme == null || ContentResolver.SCHEME_FILE.equals( scheme ) ) {
            // from file
            stream = openFileInputStream( uri.getPath() );
        } else if ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {
            // from content
            stream = openContentInputStream( context, uri );
        } else if ( "http".equals( scheme ) || "https".equals( scheme ) ) {
            // from remote uri
            stream = openRemoteInputStream( uri );
        }
        return stream;
    }

    public static boolean decodeImageBounds( final InputStream stream, int[] outSize ) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream( stream, null, options );
        if ( options.outHeight > 0 && options.outWidth > 0 ) {
            outSize[0] = options.outWidth;
            outSize[1] = options.outHeight;
            return true;
        }
        return false;
    }

    private static int computeSampleSize( final int bitmapW, final int bitmapH, final int maxW, final int maxH,
                                          final int orientation ) {
        double w, h;

        if ( orientation == 0 || orientation == 180 ) {
            w = bitmapW;
            h = bitmapH;
        } else {
            w = bitmapH;
            h = bitmapW;
        }

        final int sampleSize = (int) Math.ceil( Math.max( w / maxW, h / maxH ) );
        return sampleSize;
    }

    /**
     * Return a {@link FileInputStream} from the given path or null if file not found
     *
     * @param path
     *           the file path
     * @return the {@link FileInputStream} of the given path, null if {@link FileNotFoundException} is thrown
     */
    static InputStream openFileInputStream( String path ) {
        try {
            return new FileInputStream( path );
        } catch ( FileNotFoundException e ) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Return a {@link BufferedInputStream} from the given uri or null if an exception is thrown
     *
     * @param context
     * @param uri
     * @return the {@link InputStream} of the given path. null if file is not found
     */
    static InputStream openContentInputStream( Context context, Uri uri ) {
        try {
            return context.getContentResolver().openInputStream( uri );
        } catch ( FileNotFoundException e ) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Return an {@link InputStream} from the given url or null if failed to retrieve the content
     *
     * @param uri
     * @return
     */
    static InputStream openRemoteInputStream( Uri uri ) {
        java.net.URL finalUrl;
        try {
            finalUrl = new java.net.URL( uri.toString() );
        } catch ( MalformedURLException e ) {
            e.printStackTrace();
            return null;
        }

        HttpURLConnection connection;
        try {
            connection = (HttpURLConnection) finalUrl.openConnection();
        } catch ( IOException e ) {
            e.printStackTrace();
            return null;
        }

        connection.setInstanceFollowRedirects( false );
        int code;
        try {
            code = connection.getResponseCode();
        } catch ( IOException e ) {
            e.printStackTrace();
            return null;
        }

        // permanent redirection
        if ( code == HttpURLConnection.HTTP_MOVED_PERM || code == HttpURLConnection.HTTP_MOVED_TEMP
                || code == HttpURLConnection.HTTP_SEE_OTHER ) {
            String newLocation = connection.getHeaderField( "Location" );
            return openRemoteInputStream( Uri.parse( newLocation ) );
        }

        try {
            return (InputStream) finalUrl.getContent();
        } catch ( IOException e ) {
            e.printStackTrace();
            return null;
        }
    }

    static BitmapFactory.Options getDefaultOptions() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inDither = false;
        options.inJustDecodeBounds = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];
        return options;
    }
}

class ExifUtils {

    public static final String[] EXIF_TAGS = {
            "FNumber", ExifInterface.TAG_DATETIME, "ExposureTime", ExifInterface.TAG_FLASH, ExifInterface.TAG_FOCAL_LENGTH,
            "GPSAltitude", "GPSAltitudeRef", ExifInterface.TAG_GPS_DATESTAMP, ExifInterface.TAG_GPS_LATITUDE,
            ExifInterface.TAG_GPS_LATITUDE_REF, ExifInterface.TAG_GPS_LONGITUDE, ExifInterface.TAG_GPS_LONGITUDE_REF,
            ExifInterface.TAG_GPS_PROCESSING_METHOD, ExifInterface.TAG_GPS_TIMESTAMP, ExifInterface.TAG_IMAGE_LENGTH,
            ExifInterface.TAG_IMAGE_WIDTH, "ISOSpeedRatings", ExifInterface.TAG_MAKE, ExifInterface.TAG_MODEL,
            ExifInterface.TAG_WHITE_BALANCE, };

    /**
     * Return the rotation of the passed image file
     *
     * @param filepath
     *           image absolute file path
     * @return image orientation
     */
    public static int getExifOrientation( final String filepath ) {
        if ( null == filepath ) return 0;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface( filepath );
        } catch ( IOException e ) {
            return 0;
        }
        return getExifOrientation( exif );
    }

    public static int getExifOrientation( final ExifInterface exif ) {
        int degree = 0;

        if ( exif != null ) {
            final int orientation = exif.getAttributeInt( ExifInterface.TAG_ORIENTATION, -1 );
            if ( orientation != -1 ) {
                switch ( orientation ) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                }
            }
        }
        return degree;
    }

    /**
     * Load the exif tags into the passed Bundle
     *
     * @param filepath
     * @param out
     * @return true if exif tags are loaded correctly
     */
    public static boolean loadAttributes( final String filepath, Bundle out ) {
        ExifInterface e;
        try {
            e = new ExifInterface( filepath );
        } catch ( IOException e1 ) {
            e1.printStackTrace();
            return false;
        }

        for ( String tag : EXIF_TAGS ) {
            out.putString( tag, e.getAttribute( tag ) );
        }
        return true;
    }

    /**
     * Store the exif attributes in the passed image file using the TAGS stored in the passed bundle
     *
     * @param filepath
     * @param bundle
     * @return true if success
     */
    public static boolean saveAttributes( final String filepath, Bundle bundle ) {
        ExifInterface exif;
        try {
            exif = new ExifInterface( filepath );
        } catch ( IOException e ) {
            e.printStackTrace();
            return false;
        }

        for ( String tag : EXIF_TAGS ) {
            if ( bundle.containsKey( tag ) ) {
                exif.setAttribute( tag, bundle.getString( tag ) );
            }
        }
        try {
            exif.saveAttributes();
        } catch ( IOException e ) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Return the string representation of the given orientation
     *
     * @param orientation
     * @return
     */
    public static String getExifOrientation( int orientation ) {
        switch ( orientation ) {
            case 0:
                return String.valueOf( ExifInterface.ORIENTATION_NORMAL );
            case 90:
                return String.valueOf( ExifInterface.ORIENTATION_ROTATE_90 );
            case 180:
                return String.valueOf( ExifInterface.ORIENTATION_ROTATE_180 );
            case 270:
                return String.valueOf( ExifInterface.ORIENTATION_ROTATE_270 );
            default:
                throw new AssertionError( "invalid: " + orientation );
        }
    }

    /**
     * Try to get the exif orientation of the passed image uri
     *
     * @param context
     * @param uri
     * @return
     */
    public static int getExifOrientation( Context context, Uri uri ) {

        final String scheme = uri.getScheme();

        ContentProviderClient provider = null;
        if ( scheme == null || ContentResolver.SCHEME_FILE.equals( scheme ) ) {
            return getExifOrientation( uri.getPath() );
        } else if ( scheme.equals( ContentResolver.SCHEME_CONTENT ) ) {
            try {
                provider = context.getContentResolver().acquireContentProviderClient( uri );
            } catch ( SecurityException e ) {
                return 0;
            }

            if ( provider != null ) {
                Cursor result;
                try {
                    result = provider.query( uri, new String[] { MediaStore.Images.ImageColumns.ORIENTATION, MediaStore.Images.ImageColumns.DATA }, null,
                            null, null );
                } catch ( Exception e ) {
                    e.printStackTrace();
                    return 0;
                }

                if ( result == null ) {
                    return 0;
                }

                int orientationColumnIndex = result.getColumnIndex( MediaStore.Images.ImageColumns.ORIENTATION );
                int dataColumnIndex = result.getColumnIndex( MediaStore.Images.ImageColumns.DATA );

                try {
                    if ( result.getCount() > 0 ) {
                        result.moveToFirst();

                        int rotation = 0;

                        if ( orientationColumnIndex > -1 ) {
                            rotation = result.getInt( orientationColumnIndex );
                        }

                        if ( dataColumnIndex > -1 ) {
                            String path = result.getString( dataColumnIndex );
                            rotation |= getExifOrientation( path );
                        }
                        return rotation;
                    }
                } finally {
                    result.close();
                }
            }
        }
        return 0;
    }
}

/**
 * Various bitmap utilities
 *
 * @author alessandro
 *
 */

class BitmapUtils {

    /**
     * Resize a bitmap
     *
     * @param input
     * @param destWidth
     * @param destHeight
     * @return
     * @throws OutOfMemoryError
     */
    public static Bitmap resizeBitmap( final Bitmap input, int destWidth, int destHeight ) throws OutOfMemoryError {
        return resizeBitmap( input, destWidth, destHeight, 0 );
    }

    /**
     * Resize a bitmap object to fit the passed width and height
     *
     * @param input
     *           The bitmap to be resized
     * @param destWidth
     *           Desired maximum width of the result bitmap
     * @param destHeight
     *           Desired maximum height of the result bitmap
     * @return A new resized bitmap
     * @throws OutOfMemoryError
     *            if the operation exceeds the available vm memory
     */
    public static Bitmap resizeBitmap( final Bitmap input, int destWidth, int destHeight, int rotation ) throws OutOfMemoryError {

        int dstWidth = destWidth;
        int dstHeight = destHeight;
        final int srcWidth = input.getWidth();
        final int srcHeight = input.getHeight();

        if ( rotation == 90 || rotation == 270 ) {
            dstWidth = destHeight;
            dstHeight = destWidth;
        }

        boolean needsResize = false;
        float p;
        if ( ( srcWidth > dstWidth ) || ( srcHeight > dstHeight ) ) {
            needsResize = true;
            if ( ( srcWidth > srcHeight ) && ( srcWidth > dstWidth ) ) {
                p = (float) dstWidth / (float) srcWidth;
                dstHeight = (int) ( srcHeight * p );
            } else {
                p = (float) dstHeight / (float) srcHeight;
                dstWidth = (int) ( srcWidth * p );
            }
        } else {
            dstWidth = srcWidth;
            dstHeight = srcHeight;
        }

        if ( needsResize || rotation != 0 ) {
            Bitmap output;

            if ( rotation == 0 ) {
                output = Bitmap.createScaledBitmap( input, dstWidth, dstHeight, true );
            } else {
                Matrix matrix = new Matrix();
                matrix.postScale( (float) dstWidth / srcWidth, (float) dstHeight / srcHeight );
                matrix.postRotate( rotation );
                output = Bitmap.createBitmap( input, 0, 0, srcWidth, srcHeight, matrix, true );
            }
            return output;
        } else
            return input;
    }

}

/**
 * Various I/O utilities
 *
 * @author alessandro
 *
 */
class IOUtils {

    /**
     * Close a {@link Closeable} stream without throwing any exception
     *
     * @param c
     */
    public static void closeSilently( final Closeable c ) {
        if ( c == null ) return;
        try {
            c.close();
        } catch ( final Throwable t ) {}
    }

    public static void closeSilently( final ParcelFileDescriptor c ) {
        if ( c == null ) return;
        try {
            c.close();
        } catch ( final Throwable t ) {}
    }

    public static void closeSilently( Cursor cursor ) {
        if ( cursor == null ) return;
        try {
            if ( cursor != null ) cursor.close();
        } catch ( Throwable t ) {}
    }

    /**
     * Try to return the absolute file path from the given Uri
     *
     * @param context
     * @param uri
     * @return the file path or null
     */
    public static String getRealFilePath( final Context context, final Uri uri ) {

        if ( null == uri ) return null;

        final String scheme = uri.getScheme();
        String data = null;

        if ( scheme == null )
            data = uri.getPath();
        else if ( ContentResolver.SCHEME_FILE.equals( scheme ) ) {
            data = uri.getPath();
        } else if ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {
            Cursor cursor = context.getContentResolver().query( uri, new String[] { Images.ImageColumns.DATA }, null, null, null );
            if ( null != cursor ) {
                if ( cursor.moveToFirst() ) {
                    int index = cursor.getColumnIndex( Images.ImageColumns.DATA );
                    if ( index > -1 ) {
                        data = cursor.getString( index );
                    }
                }
                cursor.close();
            }
        }
        return data;
    }
}

