package org.emulinker.kaillera.controller.connectcontroller.protocol

import java.nio.ByteBuffer
import kotlin.Throws
import org.emulinker.kaillera.controller.messaging.MessageFormatException

class ConnectMessage_TOO : ConnectMessage() {
  override val iD = ID
  override val description = DESC

  override fun toString(): String {
    return DESC
  }

  override val length: Int
    get() = ID.length + 1

  override fun writeTo(buffer: ByteBuffer?) {
    buffer!!.put(charset.encode(ID))
    buffer.put(0x00.toByte())
  }

  companion object {
    const val ID = "TOO"
    private const val DESC = "Server Full Response"

    @Throws(MessageFormatException::class)
    fun parse(msg: String): ConnectMessage {
      if (msg.length != ID.length + 1) throw MessageFormatException("Invalid message length!")
      if (!msg.startsWith(ID)) throw MessageFormatException("Invalid message identifier!")
      if (msg[msg.length - 1].code != 0x00)
          throw MessageFormatException("Invalid message stop byte!")
      return ConnectMessage_TOO()
    }
  }
}