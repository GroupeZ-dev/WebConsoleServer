# WebConsoleServer

A Minecraft plugin that exposes your server console through a WebSocket connection. Built for [groupez.dev](https://groupez.dev), a test server generator for paid plugins.

## Download

[![Modrinth](https://img.shields.io/modrinth/dt/webconsoleserver?logo=modrinth&label=Modrinth)](https://modrinth.com/project/webconsoleserver)

Download the plugin from [Modrinth](https://modrinth.com/project/webconsoleserver).

## Features

- Real-time console log streaming via WebSocket
- Password-protected authentication
- Log history persistence (loads previous logs on client connection)
- Support for both **Spigot/Paper** and **Velocity** platforms
- Included test web page for quick testing

## Supported Platforms

| Platform | Minecraft Version | Java Version |
|----------|-------------------|--------------|
| Spigot/Paper | 1.8.8+ | Java 8+ |
| Velocity | 3.3.0+ | Java 21+ |

## Test Web Page

A test web page is included in the `web/` folder. Open `web/index.html` in your browser to connect to the WebSocket server and view console logs in real-time.

## Installation

1. Download the appropriate JAR for your platform from [Modrinth](https://modrinth.com/project/webconsoleserver)
2. Place the JAR in your server's `plugins` folder
3. Restart your server
4. Configure the plugin in `plugins/WebConsoleServer/config.properties` (Velocity) or `plugins/WebConsoleServer/config.yml` (Spigot)

## Configuration

```properties
# IP address to bind the WebSocket server (use 0.0.0.0 for all interfaces)
websocket-host=0.0.0.0

# Port for the WebSocket server
websocket-port=8765

# Password for WebSocket authentication (leave empty for no authentication)
websocket-password=changeme

# Maximum number of log lines to keep in history
max-log-history=500
```

## WebSocket Protocol

### Authentication Flow

1. Client connects to `ws://host:port`
2. Server sends `{"type":"auth_required"}`
3. Client sends `{"type":"auth","password":"your_password"}`
4. Server responds with `{"type":"auth_success"}` or `{"type":"auth_failed"}`
5. On success, server sends log history followed by `{"type":"history_complete"}`

### Message Types

| Type | Direction | Description |
|------|-----------|-------------|
| `auth_required` | Server → Client | Authentication is required |
| `auth` | Client → Server | Authentication request with password |
| `auth_success` | Server → Client | Authentication successful |
| `auth_failed` | Server → Client | Authentication failed |
| `log` | Server → Client | Console log message |
| `history_complete` | Server → Client | All historical logs have been sent |

### Log Message Format

```json
{"type":"log","message":"[12:34:56] [INFO] [ServerName]: Your log message here"}
```

## Building from Source

```bash
# Build all modules
./gradlew build

# Output JARs are located in target/
# - WebConsoleServer-Spigot-1.0.jar
# - WebConsoleServer-Velocity-1.0.jar
```

On Windows, use `gradlew.bat` instead of `./gradlew`.

## License

MIT License

## Links

- [groupez.dev](https://groupez.dev) - Test server generator for paid plugins
- [Modrinth](https://modrinth.com/project/webconsoleserver) - Download page
