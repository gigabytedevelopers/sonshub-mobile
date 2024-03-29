package com.gigabytedevelopersinc.apps.sonshub.downloader.fetch2.downloader

import com.gigabytedevelopersinc.apps.sonshub.downloader.fetch2.Download
import com.gigabytedevelopersinc.apps.sonshub.downloader.fetch2.Error
import com.gigabytedevelopersinc.apps.sonshub.downloader.fetch2.database.DownloadInfo
import com.gigabytedevelopersinc.apps.sonshub.downloader.fetch2core.DownloadBlock

interface FileDownloader : Runnable {

    var interrupted: Boolean
    var terminated: Boolean
    val completedDownload: Boolean
    var delegate: Delegate?
    val download: Download

    interface Delegate {

        val interrupted: Boolean

        fun onStarted(download: Download, downloadBlocks: List<DownloadBlock>, totalBlocks: Int)

        fun onDownloadBlockUpdated(download: Download, downloadBlock: DownloadBlock, totalBlocks: Int)

        fun onProgress(download: Download, etaInMilliSeconds: Long, downloadedBytesPerSecond: Long)

        fun onError(download: Download, error: Error, throwable: Throwable?)

        fun onComplete(download: Download)

        fun saveDownloadProgress(download: Download)

        fun getNewDownloadInfoInstance(): DownloadInfo

    }

}