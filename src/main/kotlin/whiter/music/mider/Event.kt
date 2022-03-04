package whiter.music.mider

import java.nio.ByteBuffer
import kotlin.experimental.or

interface IEvent : HasByteSize, HexData {

}

enum class EventType(val operateCode: Byte) {
    note_on(0x90.toByte()), note_off(0x80.toByte()), program_change(0xc0.toByte()),
    control_change(0xb0.toByte()), aftertouch(0xd0.toByte()), glide(0xe0.toByte()),
    keyaftertouch(0xa0.toByte()), sysex(0xf0.toByte())
}

class Event(val type: EventType, val args: ByteArray, val track: Byte = 0) : IEvent {

    constructor(type: EventType, arg: Byte, track: Byte = 0) : this(type, byteArrayOf(arg), track)
    constructor(type: EventType, track: Byte = 0) : this(type, 0, track)

    override fun getOccupiedBytes() = 1 + args.size

    override fun getHexDataAsByteBuffer(): ByteBuffer {
        val occupied = getOccupiedBytes()
        val buffer = ByteBuffer.allocate(occupied)
        val actualOperateCode = type.operateCode or track
        with(buffer) {
            put(actualOperateCode)
            if (args.isNotEmpty()) put(args)
            flip()
        }

        return buffer
    }

}

enum class MetaEventType(val operateCode: Byte) {
    META_TEMPO(0x51), META_END_OF_TRACK(0x2f)
}

class MetaEvent(val type: MetaEventType = MetaEventType.META_END_OF_TRACK, val args: ByteArray = HexConst.emptyData) : IEvent {

    override fun getHexDataAsByteBuffer(): ByteBuffer {
        val occupied = getOccupiedBytes()
        val buffer = ByteBuffer.allocate(occupied)
        with(buffer) {
            put(type.operateCode)
            if (args.isNotEmpty()) put(args)
            flip()
        }

        return buffer
    }

    override fun getOccupiedBytes() = 1 + args.size
}