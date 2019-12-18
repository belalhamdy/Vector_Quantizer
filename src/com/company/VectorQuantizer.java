package com.company;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VectorQuantizer {
    class Node {
        List<Block> blocks;
        Block parent;

        Node(Block parent) {
            this.parent = parent;
        }


        public double[] getAverage() {
            return Block.getAverage(this.blocks);
        }

        public void addBlock(Block block) {
            if (this.blocks == null) this.blocks = new ArrayList<>();
            this.blocks.add(block);
        }

        public void setBlocks(List<Block> blocks) {
            this.blocks = blocks;
        }
    }

    private int maxBlocks, blockSize, extraRows = 0, extraColumns = 0;
    private int[][] image, oldImage;
    private int[] compressedImage;
    private List<Block> imageBlocks;
    private List<Node> leafs = new ArrayList<>(); // to store all leafs to help in splitting

    private int[][] decompressionDictionary; // blocks saved for decompression
    VectorQuantizer(int[][] image, int blockSize, int maxBlocks) {

        this.blockSize = blockSize;
        this.maxBlocks = getNearestPower(maxBlocks);

        this.image = image;
        this.oldImage = image;
    }

    VectorQuantizer(CompressionData compressionData) throws Exception {
        int width, height;
        try {
            blockSize = Integer.parseInt(compressionData.blockSize.trim());
            extraColumns = Integer.parseInt(compressionData.extraColumns.trim());
            extraRows = Integer.parseInt(compressionData.extraRows.trim());
            maxBlocks = Integer.parseInt(compressionData.dictionarySize.trim()); // dictionary size = max number of blocks
            width = Integer.parseInt(compressionData.imageWidth.trim());
            height = Integer.parseInt(compressionData.imageHeight.trim());
        } catch (NumberFormatException e) {
            e.printStackTrace();
            throw e;
        }
        stringDictionaryFill(compressionData.dictionary);
        buildDecompressedImage(stringCompressedImageToArray(compressionData.compressedImage),width,height);



    }
    private void generateBlocks() {
        int numberOfBlocks = (image.length * image[0].length) / (blockSize * blockSize); // number of blocks = image area/block area
        List<Block> ret = new ArrayList<>();

        for (int i = 0; i < numberOfBlocks; ++i) {
            ret.add(new Block(image, i, blockSize));
        }

        this.imageBlocks = ret;
    }

    // rounds the number to nearest power of 2 to ensure that max blocks is power of 2
    private int getNearestPower(int n) {
        return (1 << (int) (Math.log(n) / Math.log(2)));
    }

    /*private void distributeBlocks() {
        List<Node> ret = new ArrayList<>(); // old leafs

        for (Node leaf : leafs) {
            ret.add(new Node(new Block(leaf.parent.data, Block.BLOCK_TYPE.DEFAULT))); // leafs already contains blocks
        }

        for (Block currentBlock : ImageBlocks) {
            double minDistance = Double.MAX_VALUE;
            Node minLeaf = ret.get(0);
            for (Node currentLeaf : ret) {
                double distance = currentBlock.getDistance(currentLeaf.parent);
                if (Double.compare(distance, minDistance) < 0) {
                    minDistance = distance;
                    minLeaf = currentLeaf;
                }
            }

            minLeaf.addBlock(currentBlock);
        }
        leafs = ret;
    }*/
    private void distributeBlocks() {

        for (Node leaf : leafs) {
//            if (leaf.blocks != null)
            leaf.blocks.clear();
        }

        for (Block currentBlock : imageBlocks) {
            double minDistance = Double.MAX_VALUE;
            Node minLeaf = leafs.get(0);
            for (Node currentLeaf : leafs) {
                double distance = currentBlock.getDistance(currentLeaf.parent);
                if (Double.compare(distance, minDistance) < 0) {
                    minDistance = distance;
                    minLeaf = currentLeaf;
                }
            }

            minLeaf.addBlock(currentBlock);
        }
    }
    private void split() {
        //splits all leafs
        List<Node> ret = new ArrayList<>();
        for (Node currentLeaf : leafs) {

            double[] average = currentLeaf.getAverage();
            ;
            Block leftParent = new Block(average, Block.BLOCK_TYPE.ROUND);
            Block rightParent = new Block(average, Block.BLOCK_TYPE.CEIL);

            ret.add(new Node(leftParent));
            ret.add(new Node(rightParent));
        }
        leafs = ret;
    }
    private int compareAveragesArrays(double[] first, double[] second) {
        if (first == null || second == null) return Integer.MAX_VALUE;
        for (int i = 0; i < first.length; ++i) {
            if (first.length != second.length || first[i] != second[i])
                return Math.min(Double.compare(first[i], second[i]), 1);
        }
        return 0;
    }
    private boolean enhanceDistribution() {
        for (Node leaf : leafs) {
            leaf.parent.data = leaf.getAverage();
        }
        distributeBlocks();

        boolean ret = false;
        for (Node leaf : leafs) {
            if (compareAveragesArrays(leaf.getAverage(), leaf.parent.data) != 0) {
                ret = true;
            }
        }
        return ret;
    }

    private void buildCompressedImage() {
        compressedImage = new int[(image.length * image[0].length) / (blockSize * blockSize)]; // number of blocks = ImageArea / blocks area
        for (int i = 0; i < leafs.size(); ++i) {
            for (Block currentBlock : leafs.get(i).blocks) {
                compressedImage[currentBlock.index] = i;
            }
        }
    }

    private String dictionaryToString() {
        StringBuilder ret = new StringBuilder("");
        for (Node curr : leafs) {
            for (double val : curr.parent.data) {
                ret.append((int) val).append(" ");
            }
            ret = new StringBuilder(ret.toString().trim());
            ret.append("\n");
        }
        return ret.toString().trim();
    }

    private String compressedImageToString() {
        StringBuilder ret = new StringBuilder("");
        for (int i : compressedImage) ret.append(i).append(" ");
        return ret.toString().trim();
    }
    private CompressionData getCompressionData() {
        return new CompressionData(blockSize + "", maxBlocks + "", extraRows + "", extraColumns + "",
                image.length + "", image[0].length + "", dictionaryToString(), compressedImage.length + "",
                compressedImageToString());
    }
    private void resizeImage() {
        extraRows = image.length % blockSize;
        extraColumns = image.length % blockSize;
        if (extraRows != 0) extraRows = blockSize - extraRows; // 12 % 5 = 2 so i need to add 3
        if (extraColumns != 0) extraColumns = blockSize - extraColumns;

        int[][] resizedImage = new int[image.length + extraRows][image[0].length + extraColumns];

        /*for (int i = 0; i < resizedImage.length; ++i) {
            for (int j = 0; j < resizedImage[0].length; ++j) {
                resizedImage[i][j] = ((i >= image.length || j >= image[0].length) ? 0 : image[i][j]);
            }
        } */
        for (int i = 0; i < image.length; ++i) {
            System.arraycopy(image[i], 0, resizedImage[i], 0, image[i].length);
        }
        image = resizedImage;
    }

    private void trimImage() {
        int[][] resizedImage = new int[image.length - extraRows][image[0].length - extraColumns];
        for (int i = 0; i < resizedImage.length; ++i) {
            System.arraycopy(image[i], 0, resizedImage[i], 0, resizedImage[i].length);
        }
        image = resizedImage;
    }
    private int[] stringToIntArray(String array){
        return Arrays.stream(array.split("\\W+")).mapToInt(Integer::parseInt).toArray();
    }
    private int[] stringCompressedImageToArray(String compressedImage){
        return stringToIntArray(compressedImage);
    }
    private void stringDictionaryFill(String dictionary) {

        int[] dictionaryArray = stringToIntArray(dictionary);
        int elementSize = blockSize*blockSize;
        decompressionDictionary = new int[dictionaryArray.length][elementSize];

        int idx = -1;
        for (int i = 0 ; i<dictionaryArray.length ; ++i){
            if (i % elementSize == 0) ++idx;
            decompressionDictionary[idx][i % elementSize] = dictionaryArray[i];
        }
    }
    private void fillBlock(int[][] image, int index , int[] block){
        int width = image[0].length;
        int height = image.length;

        int blocksPerRow = width/blockSize;
        int blocksPerCol = height/blockSize;


        int startRow = (index / blocksPerRow) * blockSize ;
        int startCol = (index % blocksPerCol) * blockSize;

        int idx = 0;
        for (int i = startRow ; i < startRow + blockSize ; ++i){
            for (int j = startCol ; j < startCol + blockSize ; ++j ){
                image[i][j] = block[idx++];
            }
        }
    }
    private void buildDecompressedImage(int[] compressedImage , int width , int height){
        image = new int[width][height];
        int[] currentBlock;
        for (int i = 0 ; i<compressedImage.length ; ++i){
            currentBlock = decompressionDictionary[compressedImage[i]];
            fillBlock(image,i,currentBlock);
        }
        oldImage = image;
        trimImage();
    }

    public CompressionData compress() {
        resizeImage(); // Not tested
        generateBlocks(); // tested

        //tested to tested until here
        Node root = new Node(null);
        root.setBlocks(this.imageBlocks);
        leafs.add(root);

        while (leafs.size() < maxBlocks) {
            split();
            distributeBlocks();
        }

        int MAX_LOOP = 10;
        while (MAX_LOOP-- > 0) {
            if (!enhanceDistribution()) break;
        }
        // Tested until here
        buildCompressedImage();
        return getCompressionData();
    }

    public int[][] Decompress() {
        return image;
    }
}
