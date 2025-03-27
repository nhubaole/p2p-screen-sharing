package com.example.p2pscreensharing.data.model

import java.nio.ByteBuffer
import java.nio.ByteOrder

data class FramePacket(
//    val timestamp: Long,
//    val width: Int,
//    val height: Int,
    val payload: ByteArray
) {
    companion object {
        const val MAGIC_HEADER: Short = 0xCAFE.toShort()

        fun encode(packet: FramePacket): ByteArray {
            val payloadSize = packet.payload.size
            val buffer = ByteBuffer.allocate(payloadSize)
                .order(ByteOrder.BIG_ENDIAN)

//            buffer.putShort(MAGIC_HEADER)
//            buffer.putLong(packet.timestamp)
//            buffer.putShort(packet.width.toShort())
//            buffer.putShort(packet.height.toShort())
//            buffer.putInt(payloadSize)
            buffer.put(packet.payload)

            return buffer.array()
        }

        fun decode(data: ByteArray): FramePacket? {
            val buffer = ByteBuffer.wrap(data).order(ByteOrder.BIG_ENDIAN)

//            if (data.size < 18) return null

//            val magic = buffer.short
//            if (magic != MAGIC_HEADER) return null
//
//            val timestamp = buffer.long
//            val width = buffer.short.toInt()
//            val height = buffer.short.toInt()
//            val payloadSize = buffer.int

            val payload = ByteArray(data.size)
            buffer.get(payload)

            return FramePacket(payload)
        }
    }
}