package com.example.data.model

import java.nio.ByteBuffer
import java.nio.ByteOrder

data class FramePacket(
    val payload: ByteArray
) {
    companion object {
        fun encode(packet: FramePacket): ByteArray {
            val payloadSize = packet.payload.size
            val buffer = ByteBuffer.allocate(payloadSize)
                .order(ByteOrder.BIG_ENDIAN)

            buffer.put(packet.payload)

            return buffer.array()
        }

        fun decode(data: ByteArray): FramePacket {
            val buffer = ByteBuffer.wrap(data).order(ByteOrder.BIG_ENDIAN)

            val payload = ByteArray(data.size)
            buffer.get(payload)

            return FramePacket(payload)
        }
    }
}

data class FrameChunkPacket(
    val frameId: Long,
    val chunkIndex: Int,
    val totalChunks: Int,
    val payload: ByteArray
) {
    companion object {
        const val HEADER_SIZE = 8 + 4 + 4

        fun encode(packet: FrameChunkPacket): ByteArray {
            val buffer = ByteBuffer.allocate(HEADER_SIZE + packet.payload.size)
                .order(ByteOrder.BIG_ENDIAN)

            buffer.putLong(packet.frameId)
            buffer.putInt(packet.chunkIndex)
            buffer.putInt(packet.totalChunks)
            buffer.put(packet.payload)

            return buffer.array()
        }

        fun decode(data: ByteArray): FrameChunkPacket? {
            if (data.size < HEADER_SIZE) return null

            val buffer = ByteBuffer.wrap(data).order(ByteOrder.BIG_ENDIAN)
            val frameId = buffer.long
            val chunkIndex = buffer.int
            val totalChunks = buffer.int

            val payload = ByteArray(data.size - HEADER_SIZE)
            buffer.get(payload)

            return FrameChunkPacket(frameId, chunkIndex, totalChunks, payload)
        }
    }
}

fun FramePacket.encodeToChunks(): List<ByteArray> {
    val chunkSize = 8192
    val frameData = this.payload
    val totalChunks = (frameData.size + chunkSize - 1) / chunkSize
    val frameId = System.currentTimeMillis()

    return (0 until totalChunks).map { index ->
        val start = index * chunkSize
        val end = minOf(start + chunkSize, frameData.size)
        val chunkData = frameData.copyOfRange(start, end)

        val chunkPacket = FrameChunkPacket(
            frameId = frameId,
            chunkIndex = index,
            totalChunks = totalChunks,
            payload = chunkData
        )

        FrameChunkPacket.encode(chunkPacket)
    }
}
