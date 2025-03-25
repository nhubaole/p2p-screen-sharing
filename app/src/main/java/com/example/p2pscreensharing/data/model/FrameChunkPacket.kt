package com.example.p2pscreensharing.data.model

import java.nio.ByteBuffer
import java.nio.ByteOrder

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
