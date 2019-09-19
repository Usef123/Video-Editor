package med.umerfarooq.com.videoeditor.VideoFeatures

import android.content.Context
import android.media.MediaScannerConnection
import android.media.MediaScannerConnection.OnScanCompletedListener
import android.net.Uri
import android.os.Environment
import android.util.Log

import java.io.File
import java.io.IOException
import java.util.Date

class FileUtils private constructor() {

    val isSDCanWrite: Boolean
        get() = if (Environment.getExternalStorageState() == "mounted" && Environment.getExternalStorageDirectory().canWrite() && Environment.getExternalStorageDirectory().canRead()) {
            true
        } else false

    val appDirPath: String
        get() = (if (localPath != null) {
            "$localPath$APP_DIR/"
        } else null) as String

    val listFiles: Array<File>
        get() = File(Environment.getExternalStorageDirectory().toString() + File.separator + APP_DIR).listFiles()

    internal class C04651 : OnScanCompletedListener {

        override fun onScanCompleted(path: String, uri: Uri) {
            Log.i("myLog", "Finished scanning $path New row: $uri")
        }
    }

    init {
        if (isSDCanWrite) {
            creatSDDir(APP_DIR)
        }
    }

    fun creatSDDir(dirName: String): File {
        val dir = File(localPath + dirName)
        dir.mkdirs()
        return dir
    }

    @Throws(IOException::class)
    fun createTempFile(prefix: String, extension: String): File {
        val file = File(appDirPath + "/" + prefix + System.currentTimeMillis() + extension)
        file.createNewFile()
        return file
    }

    @Throws(IOException::class)
    fun createFileWithName(name: String, extension: String): File {
        var name = name
        name = name.trim { it <= ' ' }.replace(" ".toRegex(), "_").replace("[-+.^:,]".toRegex(), "")
        val file = File("$appDirPath/$name$extension")
        if (file.exists()) {
            return File(appDirPath + "/" + name + "_" + System.currentTimeMillis() + extension)
        }
        file.createNewFile()
        return file
    }

    companion object {
        private val APP_DIR = "AddTextToVideo"
        private var instance: FileUtils? = null
        private var mContext: Context? = null

        fun getInstance(context: Context): FileUtils? {
            if (instance == null) {
                synchronized(FileUtils::class.java) {
                    if (instance == null) {
                        mContext = context
                        instance = FileUtils()
                    }
                }
            }
            return instance
        }

        private val localPath: String
            get() = Environment.getExternalStorageDirectory().toString() + "/"

        fun deleteFile(path: String): Boolean {
            return File(path).delete()
        }

        fun getFileName(fullName: String): String {
            return fullName.substring(0, fullName.lastIndexOf("."))
        }

        fun getFileType(fullName: String): String {
            return fullName.substring(fullName.lastIndexOf(".") + 1, fullName.length)
        }

        fun getFileLastModified(pathFile: String): Date {
            return Date(File(pathFile).lastModified())
        }

        fun scanMedia(context: Context, filePath: String) {
            MediaScannerConnection.scanFile(context, arrayOf(filePath), null, C04651())
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

        fun isExternalStorageDocument(uri: Uri): Boolean {
            return "com.android.externalstorage.documents" == uri.authority
        }

        fun isDownloadsDocument(uri: Uri): Boolean {
            return "com.android.providers.downloads.documents" == uri.authority
        }

        fun isMediaDocument(uri: Uri): Boolean {
            return "com.android.providers.media.documents" == uri.authority
        }
    }
}
