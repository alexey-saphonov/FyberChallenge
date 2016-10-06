package com.alexeysafonov.fyberchallenge.sha1

import java.security.MessageDigest

fun ByteArray.convertToHex(): String {
    val builder = StringBuilder();
    for (b in this) {
        builder.append(String.format("%02x", b).toLowerCase())
    }
    return builder.toString()
}

fun String.sha1(): String {
    val md = MessageDigest.getInstance("SHA-1")
    val textBytes = toByteArray(Charsets.ISO_8859_1)
    md.update(textBytes, 0, textBytes.size)
    val sha1hash = md.digest()
    return sha1hash.convertToHex()
}