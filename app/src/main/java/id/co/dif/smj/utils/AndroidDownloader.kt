package id.co.dif.smj.utils

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import androidx.core.net.toUri
import java.util.Calendar

class AndroidDownloader(
    private val context: Context
) :Downloader{
    private val downloadManager = context.getSystemService(DownloadManager::class.java)
    override fun downloadFile(url: String, title: String, mimeType: String): Long {
        val calendar = Calendar.getInstance()
        val time = calendar.timeInMillis.toString()
        val request = DownloadManager.Request(url.toUri())
            .setMimeType(mimeType)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setTitle(title)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "$title - $time.pdf")
        return downloadManager.enqueue(request)
    }

}