package com.facebook.presto.nblock.dictionary;

import com.facebook.presto.Range;
import com.facebook.presto.Tuple;
import com.facebook.presto.TupleInfo;
import com.facebook.presto.nblock.BlockCursor;
import com.facebook.presto.slice.Slice;
import com.google.common.primitives.Ints;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkPositionIndex;

public class DictionaryEncodedBlockCursor implements BlockCursor
{
    private final Dictionary dictionary;
    private final BlockCursor sourceCursor;

    public DictionaryEncodedBlockCursor(Dictionary dictionary, BlockCursor sourceCursor)
    {
        checkNotNull(dictionary, "dictionary is null");
        checkNotNull(sourceCursor, "sourceCursor is null");

        this.dictionary = dictionary;
        this.sourceCursor = sourceCursor;
    }

    @Override
    public TupleInfo getTupleInfo()
    {
        return dictionary.getTupleInfo();
    }

    @Override
    public Range getRange()
    {
        return sourceCursor.getRange();
    }

    @Override
    public boolean isValid()
    {
        return sourceCursor.isValid();
    }

    @Override
    public boolean isFinished()
    {
        return sourceCursor.isFinished();
    }

    @Override
    public boolean advanceNextValue()
    {
        return sourceCursor.advanceNextValue();
    }

    @Override
    public boolean advanceNextPosition()
    {
        return sourceCursor.advanceNextPosition();
    }

    @Override
    public boolean advanceToPosition(long position)
    {
        return sourceCursor.advanceToPosition(position);
    }

    @Override
    public Tuple getTuple()
    {
        return dictionary.getTuple(getDictionaryKey());
    }

    @Override
    public long getLong(int field)
    {
        return dictionary.getLong(getDictionaryKey(), field);
    }

    @Override
    public double getDouble(int field)
    {
        return dictionary.getDouble(getDictionaryKey(), field);
    }

    @Override
    public Slice getSlice(int field)
    {
        return dictionary.getSlice(getDictionaryKey(), field);
    }

    @Override
    public long getPosition()
    {
        return sourceCursor.getPosition();
    }

    @Override
    public boolean currentTupleEquals(Tuple value)
    {
        return dictionary.tupleEquals(getDictionaryKey(), value);
    }

    @Override
    public long getCurrentValueEndPosition()
    {
        return sourceCursor.getCurrentValueEndPosition();
    }

    public int getDictionaryKey()
    {
        int dictionaryKey = Ints.checkedCast(sourceCursor.getLong(0));
        checkPositionIndex(dictionaryKey, dictionary.size(), "dictionaryKey does not exist");
        return dictionaryKey;
    }
}
