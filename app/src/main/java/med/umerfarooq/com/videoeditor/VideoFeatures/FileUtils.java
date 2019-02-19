package med.umerfarooq.com.videoeditor.VideoFeatures;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.OnScanCompletedListener;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class FileUtils {
    private static final String APP_DIR = "AddTextToVideo";
    private static FileUtils instance = null;
    private static Context mContext;

    static class C04651 implements OnScanCompletedListener {
        C04651() {
        }

        public void onScanCompleted(String path, Uri uri) {
            Log.i("myLog", "Finished scanning " + path + " New row: " + uri);
        }
    }

    public static FileUtils getInstance(Context context) {
        if (instance == null) {
            synchronized (FileUtils.class) {
                if (instance == null) {
                    mContext = context;
                    instance = new FileUtils();
                }
            }
        }
        return instance;
    }

    private FileUtils() {
        if (isSDCanWrite()) {
            creatSDDir(APP_DIR);
        }
    }

    public boolean isSDCanWrite() {
        if (Environment.getExternalStorageState().equals("mounted") && Environment.getExternalStorageDirectory().canWrite() && Environment.getExternalStorageDirectory().canRead()) {
            return true;
        }
        return false;
    }

    public File creatSDDir(String dirName) {
        File dir = new File(getLocalPath() + dirName);
        dir.mkdirs();
        return dir;
    }

    private static String getLocalPath() {
        return Environment.getExternalStorageDirectory() + "/";
    }

    public String getAppDirPath() {
        if (getLocalPath() != null) {
            return getLocalPath() + APP_DIR + "/";
        }
        return null;
    }

    public File createTempFile(String prefix, String extension) throws IOException {
        File file = new File(getAppDirPath() + "/" + prefix + System.currentTimeMillis() + extension);
        file.createNewFile();
        return file;
    }

    public File createFileWithName(String name, String extension) throws IOException {
        name = name.trim().replaceAll(" ", "_").replaceAll("[-+.^:,]", "");
        File file = new File(getAppDirPath() + "/" + name + extension);
        if (file.exists()) {
            return new File(getAppDirPath() + "/" + name + "_" + System.currentTimeMillis() + extension);
        }
        file.createNewFile();
        return file;
    }

    public static boolean deleteFile(String path) {
        return new File(path).delete();
    }

    public File[] getListFiles() {
        return new File(Environment.getExternalStorageDirectory() + File.separator + APP_DIR).listFiles();
    }

    public static String getFileName(String fullName) {
        return fullName.substring(0, fullName.lastIndexOf("."));
    }

    public static String getFileType(String fullName) {
        return fullName.substring(fullName.lastIndexOf(".") + 1, fullName.length());
    }

    public static Date getFileLastModified(String pathFile) {
        return new Date(new File(pathFile).lastModified());
    }

    public static void scanMedia(Context context, String filePath) {
        MediaScannerConnection.scanFile(context, new String[]{filePath}, null, new C04651());
    }

//    public static String getFilePath(Context context, Uri uri) throws URISyntaxException {
//        String selection = null;
//        String[] selectionArgs = null;
//        if (VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
//            if (isExternalStorageDocument(uri)) {
//                return Environment.getExternalStorageDirectory() + "/" + DocumentsContract.getDocumentId(uri).split(":")[1];
//            } else if (isDownloadsDocument(uri)) {
//                uri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(DocumentsContract.getDocumentId(uri)).longValue());
//            } else if (isMediaDocument(uri)) {
//                String type = DocumentsContract.getDocumentId(uri).split(":")[0];
//                if ("image".equals(type)) {
//                    uri = Media.EXTERNAL_CONTENT_URI;
//                } else if (MimeTypes.BASE_TYPE_VIDEO.equals(type)) {
//                    uri = Video.Media.EXTERNAL_CONTENT_URI;
//                } else if (MimeTypes.BASE_TYPE_AUDIO.equals(type)) {
//                    uri = Audio.Media.EXTERNAL_CONTENT_URI;
//                }
//                selection = "_id=?";
//                selectionArgs = new String[]{split[1]};
//            }
//        }
//        if ("content".equalsIgnoreCase(uri.getScheme())) {
//            try {
//                Cursor cursor = context.getContentResolver().query(uri, new String[]{"_data"}, selection, selectionArgs, null);
//                int column_index = cursor.getColumnIndexOrThrow("_data");
//                if (cursor.moveToFirst()) {
//                    return cursor.getString(column_index);
//                }
//            } catch (Exception e) {
//            }
//        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
//            return uri.getPath();
//        }
//        return null;
//    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}
