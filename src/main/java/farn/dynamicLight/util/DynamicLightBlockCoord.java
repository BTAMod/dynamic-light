package farn.dynamicLight.util;

import java.util.ArrayList;
import java.util.List;


public final class DynamicLightBlockCoord
{

    public DynamicLightBlockCoord(int i, int j, int k)
    {
        x = i;
        y = j;
        z = k;
    }
    
    public static DynamicLightBlockCoord getFromPool(int i, int j, int k)
    {
        if(numBlockCoordsInUse >= blockCoords.size())
        {
            blockCoords.add(new DynamicLightBlockCoord(i, j, k));
        }
        return (blockCoords.get(numBlockCoordsInUse++)).set(i, j, k);
    }
    
    public static void resetPool()
    {
        numBlockCoordsInUse = 0;
    }
    
    public DynamicLightBlockCoord set(int i, int j, int k)
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
        if(obj instanceof DynamicLightBlockCoord)
        {
        	DynamicLightBlockCoord otherCoord = (DynamicLightBlockCoord)obj;
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
    
    private static List<DynamicLightBlockCoord> blockCoords = new ArrayList<DynamicLightBlockCoord>();
    public static int numBlockCoordsInUse = 0;
}
