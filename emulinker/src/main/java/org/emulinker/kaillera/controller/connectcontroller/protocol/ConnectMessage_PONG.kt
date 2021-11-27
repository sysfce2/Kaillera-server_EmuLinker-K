package org.emulinker.kaillera.controller.connectcontroller.protocol

import java.net.InetSocketAddress
import java.nio.ByteBuffer
import kotlin.Throws
import org.emulinker.kaillera.controller.messaging.MessageFormatException

class ConnectMessage_PONG : ConnectMessage() {
  override val iD = ID
  override val description = DESC

  var clientSocketAddress: InetSocketAddress? = null
  override fun toString(): String {
    return DESC
  }

  override val length = ID.length + 1

  override fun writeTo(buffer: ByteBuffer?) {
    buffer!!.put(charset.encode(ID))
    buffer.put(0x00.toByte())
  }

  companion object {
    const val ID = "PONG"
    private const val DESC = "Server Pong"

    @Throws(MessageFormatException::class)
    fun parse(msg: String): ConnectMessage {
      if (msg.length != 5) throw MessageFormatException("Invalid message length!")
      if (!msg.startsWith(ID)) throw MessageFormatException("Invalid message identifier!")
      if (msg[msg.length - 1].code != 0x00)
          throw MessageFormatException("Invalid message stop byte!")
      return ConnectMessage_PONG()
    }
  }
}