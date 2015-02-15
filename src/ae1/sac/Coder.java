package ae1.sac;
public class Coder
{
  private static int seed;
  private static final int a = 16807;
  private static final int m = 2147483647;
  private static final int q = 127773;
  private static final int r = 2836;
  
  public static int encrypt(int key, int p)
  {
    return code(key, p);
  }
  
  public static int decrypt(int key, int c)
  {
    return code(key, c);
  }
  
  public static int code(int key, int pc)
  {
    seed = key;
    return dice(65536) ^ pc;
  }
  
  public static int dice(int n)
  {
    int hi = seed / 127773;
    int lo = seed % 127773;
    int t = 16807 * lo - 2836 * hi;
    seed = t > 0 ? t : t + 2147483647;
    
    return seed % n;
  }
}