package farn.dynamicLight.thing;

import java.lang.RuntimeException;

public class LightCache
{
    final static int cacheSize = 0x8000;
	
    public LightCache()
    {
        coords = new BlockCoord[cacheSize];
        lightValues = new float[cacheSize];
    }
    
    public void clear()
    {
        for(int i=0; i<cacheSize; ++i)
            coords[i] = null;
    }
    
    private int calcHash(int x, int y, int z)
    {
        int m = 0x5bd1e995;
        int r = 24;

        int h = 1234567890;
        int k;
        
        k = x;
        k *= m;
        k ^= k >>> r;
        k *= m;
        h *= m;
        h ^= k;
        
        k = y;
        k *= m;
        k ^= k >>> r;
        k *= m;
        h *= m;
        h ^= k;
        
        k = z;
        k *= m;
        k ^= k >>> r;
        k *= m;
        h *= m;
        h ^= k;
        
        h ^= h >>> 13;
        h *= m;
        h ^= h >>> 15;
        
        return h;
    }
    
    private int findEntry(int x, int y, int z)
    {
        int i = Math.abs(calcHash(x, y, z))%cacheSize;
        int h = i;
        int j = 0;
        
        while(coords[i] != null && !coords[i].isEqual(x, y, z))
        {
            i = (i+1)%cacheSize;
            if(j++>cacheSize)
            {
                throw new RuntimeException("Light cache full");
            }
        }
        
        return i;
    }
    
    public float getLightValue(int x, int y, int z)
    {
        int i = findEntry(x, y, z);
        if(coords[i] == null)
        {
            return -1;
        }
        
        return lightValues[i];
    }
    
    public void setLightValue(int x, int y, int z, float l)
    {
        int i = findEntry(x, y, z);
        coords[i] = BlockCoord.getFromPool(x, y, z);
        lightValues[i] = l;
    }
    
    BlockCoord coords[];
    float lightValues[];
    
    public static LightCache cache = new LightCache();
}