package net.comcraft.src;

public class Block extends Object {

    public static final Block[] blocksList = new Block[256];

    public final int blockID;

    private String blockName;

    protected Block(int id) {
        blockID = id;
        if (id == 0) {
            System.out.println("Cannot create block 0 because 0 is air!");
            return;
        }
        if (blocksList[id] != null) {
            System.out.println("Block ID is already in use! Id: " + id);
            return;
        }
        blocksList[id] = this;
    }

    public String getBlockName() {
        return blockName;
    }

    protected void setBlockName(String name) {
        blockName = name;
    }

    public static Block getBlock(String name) {
        for (int i = 0; i < blocksList.length; i++) {
            if (blocksList[i] == null) {
                continue;
            }
            if (blocksList[i].getBlockName().equals(name)) {
                return blocksList[i];
            }
        }
        return null;
    }

    static {
        new Block(1).setBlockName("stone");
        new Block(2).setBlockName("grass");
        new Block(3).setBlockName("dirt");
        new Block(4).setBlockName("glass");
        new Block(5).setBlockName("leaves");
        new Block(6).setBlockName("sand");
        new Block(7).setBlockName("planks");
        new Block(8).setBlockName("wood");
        new Block(9).setBlockName("brick");
        new Block(10).setBlockName("woolWhite");
        new Block(11).setBlockName("woolBlack");
        new Block(12).setBlockName("woolRed");
        new Block(13).setBlockName("woolBlue");
        new Block(14).setBlockName("woolYellow");
        new Block(15).setBlockName("woolGreen");
        new Block(16).setBlockName("cobblestone");
        new Block(17).setBlockName("obsidian");
        new Block(18).setBlockName("bookshelve");
        new Block(19).setBlockName("pumpkin");
        new Block(20).setBlockName("ice");
        new Block(21).setBlockName("stoneBrick");
        new Block(22).setBlockName("mossStone");
        new Block(23).setBlockName("iron");
        new Block(24).setBlockName("gold");
        new Block(25).setBlockName("diamond");
        new Block(26).setBlockName("bedrock");
        new Block(27).setBlockName("stoneSlab");
        new Block(28).setBlockName("plankSlab");
        new Block(29).setBlockName("doubleStoneSlab");
        new Block(30).setBlockName("cobblestoneSlab");
        new Block(31).setBlockName("cactus");
        new Block(32).setBlockName("woolLightGray");
        new Block(33).setBlockName("woolGray");
        new Block(34).setBlockName("woolOrange");
        new Block(35).setBlockName("woolLime");
        new Block(36).setBlockName("woolCyan");
        new Block(37).setBlockName("woolLightBlue");
        new Block(38).setBlockName("woolPurple");
        new Block(39).setBlockName("woolMagenta");
        new Block(40).setBlockName("woolPink");
        new Block(41).setBlockName("woolBrown");
        new Block(42).setBlockName("water");
        new Block(43).setBlockName("redFlower");
        new Block(44).setBlockName("yellowFlower");
        new Block(45).setBlockName("treePlant");
        new Block(46).setBlockName("toadstool");
        new Block(47).setBlockName("mushroom");
        new Block(48).setBlockName("lava");
        new Block(49).setBlockName("snow");
        new Block(50).setBlockName("snowBlock");
        new Block(51).setBlockName("sandStone");
        new Block(52).setBlockName("lapisLazuli");
        new Block(53).setBlockName("craftingTable");
        new Block(54).setBlockName("furnace");
        new Block(55).setBlockName("tnt");
        new Block(56).setBlockName("netherrack");
        new Block(57).setBlockName("netherBrick");
        new Block(58).setBlockName("soulSand");
        new Block(60).setBlockName("plankTitle");
        new Block(61).setBlockName("planks1");
        new Block(62).setBlockName("planks2");
        new Block(63).setBlockName("planks3");
        new Block(64).setBlockName("plankSlab1");
        new Block(65).setBlockName("plankSlab2");
        new Block(66).setBlockName("plankSlab3");
        new Block(67).setBlockName("netherBrickSlab");
        new Block(68).setBlockName("brickSlab");
        new Block(69).setBlockName("wood1");
        new Block(70).setBlockName("wood2");
        new Block(72).setBlockName("plankTitle1");
        new Block(73).setBlockName("plankTitle2");
        new Block(74).setBlockName("plankTitle3");
        new Block(75).setBlockName("brickTitle");
        new Block(76).setBlockName("netherBrickTitle");
        new Block(77).setBlockName("stoneTitle");
        new Block(78).setBlockName("torch");
        new Block(79).setBlockName("woodenDoor");
        new Block(80).setBlockName("ironDoor");
        new Block(81).setBlockName("cobblestoneTitle");
        new Block(82).setBlockName("plankStairs");
        new Block(83).setBlockName("plankStairs1");
        new Block(84).setBlockName("plankStairs2");
        new Block(85).setBlockName("plankStairs3");
        new Block(86).setBlockName("brickStairs");
        new Block(87).setBlockName("netherBrickStairs");
        new Block(88).setBlockName("stoneStairs");
        new Block(89).setBlockName("cobblestoneStairs");
        new Block(90).setBlockName("whiteWoolStairs");
        new Block(91).setBlockName("blackWoolStairs");
        new Block(92).setBlockName("redWoolStairs");
        new Block(93).setBlockName("blueWoolStairs");
        new Block(94).setBlockName("yellowWoolStairs");
        new Block(95).setBlockName("greenWoolStairs");
        new Block(96).setBlockName("lightGreenWoolStairs");
        new Block(97).setBlockName("orangeWoolStairs");
        new Block(98).setBlockName("pinkWoolStairs");
        new Block(99).setBlockName("whiteWoolSlab");
        new Block(100).setBlockName("blackWoolSlab");
        new Block(101).setBlockName("redWoolSlab");
        new Block(102).setBlockName("blueWoolSlab");
        new Block(103).setBlockName("yellowWoolSlab");
        new Block(104).setBlockName("greenWoolSlab");
        new Block(105).setBlockName("lightGreenWoolSlab");
        new Block(106).setBlockName("orangeWoolSlab");
        new Block(107).setBlockName("pinkWoolSlab");
        new Block(108).setBlockName("tntWeak");
        new Block(109).setBlockName("tntStrong");
        new Block(110).setBlockName("chest");
        new Block(111).setBlockName("emoticon");
        new Block(112).setBlockName("alphabet1");
        new Block(113).setBlockName("alphabet2");
        new Block(114).setBlockName("numbers");
        new Block(115).setBlockName("fencePlank");
        new Block(116).setBlockName("fencePlank2");
        new Block(117).setBlockName("fencePlank3");
        new Block(118).setBlockName("fencePlank4");
        new Block(119).setBlockName("wheat");
        new Block(120).setBlockName("fenceNetherbrick");
        new Block(121).setBlockName("fenceStone");
        new Block(122).setBlockName("fenceBrick");
        new Block(123).setBlockName("animalSheep");
        new Block(124).setBlockName("bed");
        new Block(125).setBlockName("animalChicken");
        new Block(126).setBlockName("animalCow");
        new Block(127).setBlockName("animalPig");
        new Block(255).setBlockName("Player");
    }
}
