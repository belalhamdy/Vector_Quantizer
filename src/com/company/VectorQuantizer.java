package com.company;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VectorQuantizer {
    private static class Node {
        List<Block> blocks;
        Block parent;

        Node(Block parent) {
            this.blocks = new ArrayList<>();
            this.parent = parent;
        }

        public double[] getAverages() {
            return Block.getAverages(this.blocks);
        }

        public void addBlock(Block block) {
            this.blocks.add(block);
        }

        public void clearBlocks() {

            this.blocks.clear();
        }
    }

    private int maxBlocks, blockSize, extraHeight = 0, extraWidth = 0;
    private int[][] image;
    private int[] compressedImage;
    private List<Block> imageBlocks;
    private List<Node> leafs = new ArrayList<>(); // to store all leafs to help in splitting

    private int[][] decompressionDictionary; // blocks saved for decompression

    VectorQuantizer(int[][] image, int blockSize, int maxBlocks) {

        this.blockSize = blockSize;

        this.maxBlocks = (1 << (int) (Math.log(maxBlocks) / Math.log(2)));

        this.image = image;
    }

    VectorQuantizer(CompressionData compressionData) {
        int width, height;

        blockSize = compressionData.blockSize;
        extraWidth = compressionData.extraWidth;
        extraHeight = compressionData.extraHeight;
        maxBlocks = compressionData.dictionarySize; // dictionary size = max number of blocks
        width = compressionData.imageWidth;
        height = compressionData.imageHeight;

        stringDictionaryFill(compressionData.dictionary);
        buildDecompressedImage(stringCompressedImageToArray(compressionData.compressedImage), width, height);
    }


    private void extendImage() {
        extraHeight = (blockSize - image.length % blockSize) % blockSize;
        extraWidth = (blockSize - image[0].length % blockSize) % blockSize;

        if (extraHeight == 0 && extraWidth == 0) return;

        int[][] resizedImage = new int[image.length + extraHeight][image[0].length + extraWidth];

        //duplicate the last pixel for the extra width and height
        for (int i = 0; i < resizedImage.length; ++i) {
            for (int j = 0; j < resizedImage[0].length; ++j) {
                resizedImage[i][j] = image[Math.min(i, image.length - 1)][Math.min(j, image[0].length - 1)];
            }
        }
        image = resizedImage;
    }

    private void generateBlocks() {
        int numberOfBlocks = (image.length * image[0].length) / (blockSize * blockSize); // number of blocks = image area/block area
        List<Block> ret = new ArrayList<>();

        for (int i = 0; i < numberOfBlocks; ++i) {
            ret.add(new Block(image, i, blockSize));
        }

        this.imageBlocks = ret;
    }

    private void splitLeafs() {
        List<Node> ret = new ArrayList<>();
        for (Node currentLeaf : leafs) {

            double[] average = currentLeaf.getAverages();
            if (average == null) average = new double[blockSize * blockSize];

            Block leftParent = new Block(average, Block.BlockType.FLOOR);
            Block rightParent = new Block(average, Block.BlockType.CEIL);

            ret.add(new Node(leftParent));
            ret.add(new Node(rightParent));
        }
        leafs = ret;
    }

    private void distributeBlocks() {
        for (Block currentBlock : imageBlocks) {
            double minDistance = Double.MAX_VALUE;
            Node minLeaf = leafs.get(0);
            for (Node currentLeaf : leafs) {
                double distance = currentBlock.getDistanceTo(currentLeaf.parent);
                if (Double.compare(distance, minDistance) < 0) {
                    minDistance = distance;
                    minLeaf = currentLeaf;
                }
            }
            minLeaf.addBlock(currentBlock);
        }
    }

    private boolean enhanceDistribution() {
        if (leafs.size() == 1) return true;

        for (Node leaf : leafs) {
            leaf.parent.data = leaf.getAverages();
            if (leaf.parent.data == null) leaf.parent.data = new double[blockSize * blockSize];
            leaf.clearBlocks();
        }
        distributeBlocks();

        boolean jumpsaround = false;
        for (Node leaf : leafs) {
            if (!Arrays.equals(leaf.getAverages(), leaf.parent.data)) {
                jumpsaround = true;
                break;
            }
        }
        return jumpsaround;
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
        StringBuilder ret = new StringBuilder();
        for (Node curr : leafs) {
            for (double val : curr.parent.data) {
                ret.append((int) val).append(" ");
            }
            ret = new StringBuilder(ret.toString().trim());
            ret.append(" ");
        }
        return ret.toString().trim();
    }

    private String compressedImageToString() {
        StringBuilder ret = new StringBuilder();
        for (int i : compressedImage) ret.append(i).append(" ");
        return ret.toString().trim();
    }

    private CompressionData getCompressionData() {
        return new CompressionData(blockSize, maxBlocks, extraWidth, extraHeight,
                image.length, image[0].length, dictionaryToString(), compressedImage.length,
                compressedImageToString());
    }

    private void trimImage() {
        if (extraHeight == 0 && extraWidth == 0) return;

        int[][] resizedImage = new int[image.length - extraHeight][image[0].length - extraWidth];
        for (int i = 0; i < resizedImage.length; ++i) {
            System.arraycopy(image[i], 0, resizedImage[i], 0, resizedImage[i].length);
        }
        image = resizedImage;
    }

    private int[] stringToIntArray(String array) {
        return Arrays.stream(array.split("\\W+")).mapToInt(Integer::parseInt).toArray();
    }

    private int[] stringCompressedImageToArray(String compressedImage) {
        return stringToIntArray(compressedImage);
    }

    private void stringDictionaryFill(String dictionary) {
        int[] dictionaryArray = stringToIntArray(dictionary);
        int elementSize = blockSize * blockSize;
        decompressionDictionary = new int[dictionaryArray.length][elementSize];

        int idx = -1;
        for (int i = 0; i < dictionaryArray.length; ++i) {
            if (i % elementSize == 0) ++idx;
            decompressionDictionary[idx][i % elementSize] = dictionaryArray[i];
        }
    }

    private void fillBlock(int[][] image, int index, int[] block) {
        int width = image[0].length;

        int blocksPerRow = width / blockSize;

        int si = (index / blocksPerRow) * blockSize;
        int sj = (index % blocksPerRow) * blockSize;

        int idx = 0;
        for (int i = si; i < si + blockSize; ++i) {
            for (int j = sj; j < sj + blockSize; ++j) {
                image[i][j] = block[idx++];

            }
        }
    }

    private void buildDecompressedImage(int[] compressedImage, int width, int height) {
        image = new int[width][height];
        int[] currentBlock;
        for (int i = 0; i < compressedImage.length; ++i) {
            currentBlock = decompressionDictionary[compressedImage[i]];
            fillBlock(image, i, currentBlock);
        }
        trimImage();
    }

    public CompressionData compress() {
        extendImage();
        generateBlocks();
        Node root = new Node(new Block(Block.getAverages(this.imageBlocks)));
        for (Block v : this.imageBlocks)
            root.addBlock(v);
        leafs.add(root);

        while (leafs.size() < maxBlocks) {
            splitLeafs();
            distributeBlocks();
        }

        int MAX_LOOP = 10;
        while (MAX_LOOP-- > 0) {
            if (!enhanceDistribution()) break;
        }

        buildCompressedImage();
        return getCompressionData();
    }

    public int[][] decompress() {
        return image;
    }
}
