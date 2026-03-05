package org.example.demo_card.domain.converters

fun ByteArray.toCardDecimal(): String =
    fold(0L) { acc, byte -> acc * 256 + (byte.toInt() and 0xFF) }.toString()

