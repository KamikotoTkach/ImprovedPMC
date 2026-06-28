# ImprovedPMC

ImprovedPMC is a small Java library for typed communication over Minecraft plugin message channels. It hides raw byte-array handling, maps packets to logical channels, and adds a simple request/response layer.

Supported transports:

- Paper: `PaperIPMC`
- BungeeCord: `BungeeIPMC`
- Velocity: `VelocityIPMC`
- Common API: `Share`
- Shaded Paper/BungeeCord/Velocity jar: `IPMC`

## Installation

Add the module for your platform:

```xml
<dependency>
  <groupId>ru.cwcode.tkach.ipmc</groupId>
  <artifactId>PaperIPMC</artifactId>
  <version>1.6.0</version>
  <scope>provided</scope>
</dependency>
```

Use `BungeeIPMC` or `VelocityIPMC` instead on proxy platforms. Install the matching IPMC plugin jar and declare it as a plugin dependency, for example on Paper:

```yaml
depend: [IPMC]
```

## Usage

Create a packet by implementing `Packet`. Incoming packets are created reflectively, so the class must have a public no-argument constructor.

```java
public class NoticePacket implements Packet {
  private String message;

  public NoticePacket() {
  }

  public NoticePacket(String message) {
    this.message = message;
  }

  @Override
  public String channel() {
    return "example:notice";
  }

  @Override
  public void read(ByteArrayDataInput input) {
    message = input.readUTF();
  }

  @Override
  public void write(ByteArrayDataOutput output) {
    output.writeUTF(message);
  }
}
```

Register and send through the platform packet manager:

```java
IPMC.packetManager().registerIncomingPacket(NoticePacket.class, (player, packet) -> {
  // handle packet
});

IPMC.packetManager().send(new NoticePacket("Hello"), player);
```

For request/response calls:

```java
IPMC.packetManager().call(new PingPacket(), PongPacket.class, player)
    .thenAccept(response -> {
      if (response == null) return;
      // handle response
    });

IPMC.packetManager().registerIncomingPacket(PingPacket.class, (player, packet) -> {
  if (IPMC.packetManager().isAwaitingResponse(packet)) {
    IPMC.packetManager().sendResponse(packet, new PongPacket());
  }
});
```

Use `IPMC.clientPacketManager()` for the client-facing channel. Its default incoming packet size limit is 4096 bytes.

## SerializablePacket

`SerializablePacket<T extends Serializable>` is a base class for packets that carry one Java-serializable object. It stores the object in the packet body using `ObjectOutputStream` and reads it back with `ObjectInputStream`.

Example:

```java
public class UserDataPacket extends SerializablePacket<UserData> {
  public UserDataPacket() {
  }

  public UserDataPacket(UserData data) {
    super(data);
  }

  @Override
  public String channel() {
    return "example:user_data";
  }
}
```

Use `SerializablePacket` only when both sides are trusted and share compatible classes. For public, long-lived, or cross-version protocols, prefer implementing `Packet` directly and writing a stable explicit binary format.

## Plugin message protocol

The Minecraft plugin message channel is only the outer transport. Default outer channels are:

- `ipmc:internal` for server/proxy messages.
- `ipmc:client` for client-facing messages.

Payloads are encoded with Guava `ByteArrayDataOutput` and decoded with `ByteArrayDataInput`, using Java `DataOutput` conventions: `UTF` is `writeUTF`, integers are 4-byte big-endian, and longs are 8-byte big-endian.

Normal packet payload:

```text
UTF packetChannel
bytes packetBody
```

- `packetChannel` is `packet.channel()`. By default it is the packet class name.
- `packetBody` is written by `Packet.write(ByteArrayDataOutput)` and read by `Packet.read(ByteArrayDataInput)`.
- Incoming handlers read `packetChannel`, find the registered packet class, then parse the same payload while skipping the leading channel string.

`SerializablePacket` body:

```text
int serializedLength
byte[serializedLength] javaSerializedObject
```

Request wrapper, channel `ipmc:call`:

```text
UTF "ipmc:call"
long uid
UTF targetChannel
int nestedPacketLength
byte[nestedPacketLength] nestedPacket
```

Response wrapper, channel `ipmc:response`:

```text
UTF "ipmc:response"
long uid
UTF targetChannel
int nestedPacketLength
byte[nestedPacketLength] nestedPacket
```

`nestedPacket` is a complete normal ImprovedPMC packet payload, including its own leading `UTF packetChannel`. `nestedPacketLength = -1` means that no nested packet bytes are present.
