package id.co.dif.smj.utils

interface Downloader {
    fun downloadFile(url: String, title: String, mimeType: String): Long
}