package com.facebook.presto.serde;

import com.facebook.presto.nblock.BlockBuilder;
import com.facebook.presto.nblock.BlockIterable;
import com.facebook.presto.nblock.uncompressed.UncompressedBlock;
import com.facebook.presto.slice.DynamicSliceOutput;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.facebook.presto.TupleInfo.SINGLE_VARBINARY;
import static com.facebook.presto.nblock.BlockAssertions.assertBlocksEquals;
import static com.facebook.presto.nblock.BlockIterables.createBlockIterable;
import static com.facebook.presto.serde.UncompressedBlockSerde.UNCOMPRESSED_BLOCK_SERDE;

public class TestDictionaryEncodedBlockSerde
{
    private DictionaryEncodedBlocksSerde dictionarySerde;

    @BeforeMethod(alwaysRun = true)
    public void setUp()
    {
        dictionarySerde = new DictionaryEncodedBlocksSerde(UNCOMPRESSED_BLOCK_SERDE);
    }

    @Test
    public void testRoundTrip()
    {
        UncompressedBlock expectedBlock = new BlockBuilder(0, SINGLE_VARBINARY)
                .append("alice")
                .append("bob")
                .append("charlie")
                .append("dave")
                .build();

        DynamicSliceOutput sliceOutput = new DynamicSliceOutput(1024);
        dictionarySerde.writeBlocks(sliceOutput, expectedBlock, expectedBlock, expectedBlock);
        BlockIterable actualBlocks = dictionarySerde.readBlocks(sliceOutput.slice(), 0);
        assertBlocksEquals(actualBlocks, createBlockIterable(new BlockBuilder(0, SINGLE_VARBINARY)
                .append("alice")
                .append("bob")
                .append("charlie")
                .append("dave")
                .append("alice")
                .append("bob")
                .append("charlie")
                .append("dave")
                .append("alice")
                .append("bob")
                .append("charlie")
                .append("dave")
                .build()));
    }
}
