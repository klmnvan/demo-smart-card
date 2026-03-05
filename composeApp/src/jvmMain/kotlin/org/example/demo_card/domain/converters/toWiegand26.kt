package org.example.demo_card.domain.converters

fun ByteArray.toWiegand26(): String {
    val u = map { it.toInt() and 0xFF }
    val fc = u[1]
    val cn = (u[2] shl 8) or u[3]
    return "$fc,$cn"
}