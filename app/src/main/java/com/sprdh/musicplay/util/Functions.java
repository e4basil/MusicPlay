package com.sprdh.musicplay.util;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;

import com.sprdh.musicplay.R;


import java.io.FileDescriptor;
import java.util.ArrayList;

public class Functions {
    private static String TAG = "Functions";

    /**
     * Check if service is running or not
     *
     * @param serviceName
     * @param context
     * @return
     */
    public static boolean isServiceRunning(String serviceName, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceName.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Read the songs present in external storage
     *
     * @param context
     * @return
     */
    public static ArrayList<MediaItem> listOfSongs(Context context) {
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        // Filter only mp3s, only those marked by the MediaStore to be music and longer than 1 minute
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
//                + " AND " + MediaStore.Audio.Media.MIME_TYPE + "= 'audio/mpeg'"
//                + " AND " + MediaStore.Audio.Media.DURATION + " > 60000";
        String sortOrder = MediaStore.Audio.AudioColumns.TITLE
                + " COLLATE LOCALIZED ASC";
        Cursor c = context.getContentResolver().query(uri, null, selection, null, sortOrder);
        ArrayList<MediaItem> listOfSongs = new ArrayList<MediaItem>();
        try{
            c.moveToFirst();
            while (c.moveToNext()) {
                MediaItem songData = new MediaItem();

                String title = c.getString(c.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String artist = c.getString(c.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String album = c.getString(c.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                long duration = c.getLong(c.getColumnIndex(MediaStore.Audio.Media.DURATION));
                String data = c.getString(c.getColumnIndex(MediaStore.Audio.Media.DATA));
                long albumId = c.getLong(c.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                String composer = c.getString(c.getColumnIndex(MediaStore.Audio.Media.COMPOSER));

                songData.setTitle(title);
                songData.setAlbum(album);
                songData.setArtist(artist);
                songData.setDuration(duration);
                songData.setPath(data);
                songData.setAlbumId(albumId);
                songData.setComposer(composer);
                listOfSongs.add(songData);
            }
            c.close();
            Log.d("SIZE", "SIZE: " + listOfSongs.size());

        }catch (Exception e){
            Log.d(TAG,"listOfSongs() "+e.getMessage());
        }

        return listOfSongs;
    }


    public static Bitmap getAlbumart(Context context, Long album_id) {
        Bitmap bm = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        try {
            final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
            Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);
            ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
            if (pfd != null) {
                FileDescriptor fd = pfd.getFileDescriptor();
                bm = BitmapFactory.decodeFileDescriptor(fd, null, options);
                pfd = null;
                fd = null;
            }
        } catch (Error ee) {
        } catch (Exception e) {
        }
        return bm;
    }

    /**
     * @param context
     * @return
     */
    public static Bitmap getDefaultAlbumArt(Context context) {
        Bitmap bm = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        try {
            bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_album_art, options);
        } catch (Error ee) {
        } catch (Exception e) {
        }
        return bm;
    }

    /**
     * Convert milliseconds into time hh:mm:ss
     *
     * @param milliseconds
     * @return time in String
     */
    public static String getDuration(long milliseconds) {
        long sec = (milliseconds / 1000) % 60;
        long min = (milliseconds / (60 * 1000)) % 60;
        long hour = milliseconds / (60 * 60 * 1000);

        String s = (sec < 10) ? "0" + sec : "" + sec;
        String m = (min < 10) ? "0" + min : "" + min;
        String h = "" + hour;

        String time = "";
        if (hour > 0) {
            time = h + ":" + m + ":" + s;
        } else {
            time = m + ":" + s;
        }
        return time;
    }

    public static boolean currentVersionSupportBigNotification() {
        int sdkVersion = android.os.Build.VERSION.SDK_INT;
        if (sdkVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            return true;
        }
        return false;
    }

    public static boolean currentVersionSupportLockScreenControls() {
        int sdkVersion = android.os.Build.VERSION.SDK_INT;
        if (sdkVersion >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            return true;
        }
        return false;
    }
}
