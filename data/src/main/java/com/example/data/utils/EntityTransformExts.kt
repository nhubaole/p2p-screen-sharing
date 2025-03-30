package com.example.data.utils

import com.example.data.model.ClientInfo
import com.example.data.model.FramePacket
import com.example.domain.entity.FrameEntity
import com.example.domain.entity.PeerEntity
import com.example.domain.entity.PeerRole

fun FramePacket.toEntity(): FrameEntity {
    return FrameEntity(
        payload = payload
    )
}

fun ClientInfo.toEntity(): PeerEntity {
    return PeerEntity(
        id = null,
        role = PeerRole.RECEIVER,
        ip = ip,
        port = port
    )
}