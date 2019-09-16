package com.gigabytedevelopersinc.apps.sonshub.downloader.fetch2core.server

interface FileResourceTransporterWriter {

    fun sendFileRequest(fileRequest: FileRequest)

    fun sendFileResponse(fileResponse: FileResponse)

    fun sendRawBytes(byteArray: ByteArray, offset: Int, length: Int)

}