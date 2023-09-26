package farn.dynamicLight.thing;;

import java.util.ArrayList;
import java.util.List;


public final class BlockCoord
{

    public BlockCoord(int i, int j, int k)
    {
        x = i;
        y = j;
        z = k;
    }
    
    public static BlockCoord getFromPool(int i, int j, int k)
    {
        if(numBlockCoordsInUse >= blockCoords.size())
        {
            blockCoords.add(new BlockCoord(i, j, k));
        }
        return (blockCoords.get(numBlockCoordsInUse++)).set(i, j, k);
    }
    
    public static void resetPool()
    {
        numBlockCoordsInUse = 0;
    }
    
    public static void releaseLastOne()
    {
        numBlockCoordsInUse--;
    }
    
    public BlockCoord set(int i, int j, int k)
    {
        x = i;
        y = j;
        z = k;
        return this;
    }
    
    public boolean isEqual(int i, int j, int k)
    {
        return x == i && y == j && z == k;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(obj instanceof BlockCoord)
        {
        	BlockCoord otherCoord = (BlockCoord)obj;
            return x == otherCoord.x && y == otherCoord.y && z == otherCoord.z;
        } else
        {
            return false;
        }
    }

    @Override
    public int hashCode()
    {
        return (x << 16) ^ z ^(y<<24);
    }

    public int x;
    public int y;
    public int z;
    
    private static List<BlockCoord> blockCoords = new ArrayList<BlockCoord>();
    public static int numBlockCoordsInUse = 0;
}
