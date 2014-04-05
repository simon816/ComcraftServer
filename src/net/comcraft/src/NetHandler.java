package net.comcraft.src;


public interface NetHandler {

    void handleBlockChange(Player player, PacketBlockChange packetBlockChange);

    void handleChunkData(PacketChunkData packetChunkData);

    void handleWorldInfo(PacketWorldInfo packetWorldInfo);

    void addPlayer(Player player);

    void deletePlayer(Player player);

    void handleLogin(Player player);

    void handlePlayerMove(Player player, Vec3D data);

    World getWorld();

}
