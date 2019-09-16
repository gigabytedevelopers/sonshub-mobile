package com.gigabytedevelopersinc.apps.sonshub.Repository

import java.io.File
import java.util.Comparator

/**
 * Project - SonsHub
 * Created with Android Studio
 * Company: Gigabyte Developers
 * User: Emmanuel Nwokoma
 * Title: Founder and CEO
 * Day: Wednesday, 06
 * Month: February
 * Year: 2019
 * Date: 10 Feb, 2019
 * Time: 10:19 PM
 * Desc: FoldersRepository
 **/

interface FoldersRepository {

    fun getMediaFiles(dir: File, acceptDirs: Boolean): List<File>
}

class RealFoldersRepository : FoldersRepository {

    private companion object {
        val SUPPORTED_EXT = arrayOf("mp3", "mp4", "m4a", "aac", "ogg", "wav")
        const val NO_MEDIA = ".nomedia"
        const val GO_UP = ".."
    }

    override fun getMediaFiles(dir: File, acceptDirs: Boolean): List<File> {
        return mutableListOf<File>().apply {
            add(File(dir, GO_UP))
            if (dir.isDirectory) {
                val children = dir.listFiles { file ->
                    when {
                        file.isFile -> {
                            val name = file.name
                            NO_MEDIA != name && checkFileExt(name)
                        }
                        file.isDirectory -> acceptDirs && checkDir(file)
                        else -> false
                    }
                } ?: emptyArray()
                val childList = children.toMutableList().apply {
                    sortBy { it.name }
                    sortWith(DirFirstComparator())
                }
                addAll(childList)
            }
        }
    }

    private fun checkDir(dir: File): Boolean {
        val files = dir.listFiles { pathname ->
            val name = pathname.name
            "." != name && GO_UP != name && pathname.canRead() && (pathname.isDirectory || pathname.isFile && checkFileExt(name))
        } ?: emptyArray()
        return dir.exists() && dir.canRead() && "." != dir.name && files.isNotEmpty()
    }

    private fun checkFileExt(name: String): Boolean {
        if (name.isEmpty()) {
            return false
        }
        val p = name.lastIndexOf(".") + 1
        if (p < 1) {
            return false
        }
        val ext = name.substring(p).toLowerCase()
        for (o in SUPPORTED_EXT) {
            if (o == ext) {
                return true
            }
        }
        return false
    }

    private class DirFirstComparator : Comparator<File> {
        override fun compare(f1: File, f2: File): Int {
            return if (f1.isDirectory == f2.isDirectory)
                0
            else if (f1.isDirectory && !f2.isDirectory)
                -1
            else
                1
        }
    }
}
