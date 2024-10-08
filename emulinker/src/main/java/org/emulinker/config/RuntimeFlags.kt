package org.emulinker.config

import java.nio.charset.Charset
import javax.inject.Singleton
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import org.apache.commons.configuration.Configuration

/** Configuration flags that are set at startup and do not change until the job is restarted. */
@Singleton
data class RuntimeFlags(
  val allowMultipleConnections: Boolean,
  val allowSinglePlayer: Boolean,
  val charset: Charset,
  val chatFloodTime: Int,
  // tbh I have no idea what this does.
  val clientTypes: List<String>,
  val connectionTypes: List<String>,
  val coreThreadPoolSize: Int,
  val createGameFloodTime: Int,
  val gameAutoFireSensitivity: Int,
  val gameBufferSize: Int,
  val gameDesynchTimeouts: Int,
  val gameTimeout: Duration,
  val idleTimeout: Duration,
  val keepAliveTimeout: Duration,
  val lagstatDuration: Duration,
  val maxChatLength: Int,
  val maxClientNameLength: Int,
  val maxGameChatLength: Int,
  val maxGameNameLength: Int,
  val maxGames: Int,
  val maxPing: Duration,
  val maxQuitMessageLength: Int,
  val maxUserNameLength: Int,
  val maxUsers: Int,
  val metricsEnabled: Boolean,
  val metricsLoggingFrequency: Duration,
  val nettyFlags: Int,
  val serverAddress: String,
  val serverLocation: String,
  val serverName: String,
  val serverPort: Int,
  val serverWebsite: String,
  val touchEmulinker: Boolean,
  val touchKaillera: Boolean,
  val twitterBroadcastDelay: Duration,
  val twitterDeletePostOnClose: Boolean,
  val twitterEnabled: Boolean,
  val twitterOAuthAccessToken: String,
  val twitterOAuthAccessTokenSecret: String,
  val twitterOAuthConsumerKey: String,
  val twitterOAuthConsumerSecret: String,
  val twitterPreventBroadcastNameSuffixes: List<String>,
  val v086BufferSize: Int,
) {

  init {
    // Note: this used to be max 30, but for some reason we had 31 set as the default in the config.
    // Setting this to max 31 so we don't break existing users.
    // TODO(nue): Just remove this restriction as it seems unhelpful?
    require(maxUserNameLength <= 31) { "server.maxUserNameLength must be <= 31" }
    require(maxGameNameLength <= 127) { "server.maxGameNameLength must be <= 127" }
    require(maxClientNameLength <= 127) { "server.maxClientNameLength must be <= 127" }
    require(maxPing in 1.milliseconds..1000.milliseconds) { "server.maxPing must be in 1..1000" }
    require(keepAliveTimeout.isPositive()) {
      "server.keepAliveTimeout must be > 0 (190 is recommended)"
    }
    require(lagstatDuration.isPositive()) { "server.lagstatDurationSeconds must be positive" }
    require(gameBufferSize > 0) { "game.bufferSize can not be <= 0" }
    require(gameTimeout.isPositive()) { "game.timeoutMillis can not be <= 0" }
    require(gameAutoFireSensitivity in 0..5) { "game.defaultAutoFireSensitivity must be 0-5" }
    for (s in connectionTypes) {
      try {
        val ct = s.toInt()
        require(ct in 1..6) { "Invalid connectionType: $s" }
      } catch (e: NumberFormatException) {
        throw IllegalStateException("Invalid connectionType: $s", e)
      }
    }
  }

  companion object {

    fun loadFromApacheConfiguration(config: Configuration): RuntimeFlags {
      @Suppress("UNCHECKED_CAST") // TODO(nue): Replace commons-configurations.
      return RuntimeFlags(
        allowMultipleConnections = config.getBoolean("server.allowMultipleConnections"),
        allowSinglePlayer = config.getBoolean("server.allowSinglePlayer", true),
        charset = Charset.forName(config.getString("emulinker.charset")),
        chatFloodTime = config.getInt("server.chatFloodTime"),
        clientTypes = config.getStringArray("controllers.v086.clientTypes.clientType").toList(),
        connectionTypes = config.getList("server.allowedConnectionTypes") as List<String>,
        coreThreadPoolSize = config.getInt("server.coreThreadpoolSize", 5),
        createGameFloodTime = config.getInt("server.createGameFloodTime"),
        gameAutoFireSensitivity = config.getInt("game.defaultAutoFireSensitivity"),
        gameBufferSize = config.getInt("game.bufferSize"),
        gameDesynchTimeouts = config.getInt("game.desynchTimeouts"),
        gameTimeout = config.getInt("game.timeoutMillis").milliseconds,
        idleTimeout = config.getInt("server.idleTimeout").seconds,
        keepAliveTimeout = config.getInt("server.keepAliveTimeout").seconds,
        lagstatDuration =
          config.getInt("server.lagstatDurationSeconds", 3.minutes.inWholeSeconds.toInt()).seconds,
        maxChatLength = config.getInt("server.maxChatLength"),
        maxClientNameLength = config.getInt("server.maxClientNameLength"),
        maxGameChatLength = config.getInt("server.maxGameChatLength"),
        maxGameNameLength = config.getInt("server.maxGameNameLength"),
        maxGames = config.getInt("server.maxGames"),
        maxPing = config.getInt("server.maxPing").milliseconds,
        maxQuitMessageLength = config.getInt("server.maxQuitMessageLength"),
        maxUserNameLength = config.getInt("server.maxUserNameLength"),
        maxUsers = config.getInt("server.maxUsers"),
        metricsEnabled = config.getBoolean("metrics.enabled", false),
        metricsLoggingFrequency = config.getInt("metrics.loggingFrequencySeconds", 30).seconds,
        // TODO(nue): This default works well, but maybe we can experiment further.
        nettyFlags = config.getInt("server.nettyThreadpoolSize", 30),
        serverAddress = config.getString("masterList.serverConnectAddress", ""),
        serverLocation = config.getString("masterList.serverLocation", "Unknown"),
        serverName = config.getString("masterList.serverName", "Emulinker Server"),
        serverPort = config.getInt("controllers.connect.port"),
        serverWebsite = config.getString("masterList.serverWebsite", ""),
        touchEmulinker = config.getBoolean("masterList.touchEmulinker", false),
        touchKaillera = config.getBoolean("masterList.touchKaillera", false),
        twitterBroadcastDelay = config.getInt("twitter.broadcastDelaySeconds", 15).seconds,
        twitterDeletePostOnClose = config.getBoolean("twitter.deletePostOnClose", false),
        twitterEnabled = config.getBoolean("twitter.enabled", false),
        twitterOAuthAccessToken = config.getString("twitter.auth.oAuthAccessToken", ""),
        twitterOAuthAccessTokenSecret = config.getString("twitter.auth.oAuthAccessTokenSecret", ""),
        twitterOAuthConsumerKey = config.getString("twitter.auth.oAuthConsumerKey", ""),
        twitterOAuthConsumerSecret = config.getString("twitter.auth.oAuthConsumerSecret", ""),
        twitterPreventBroadcastNameSuffixes =
          config.getStringArray("twitter.preventBroadcastNameSuffixes").toList(),
        v086BufferSize = config.getInt("controllers.v086.bufferSize", 4096),
      )
    }
  }
}
