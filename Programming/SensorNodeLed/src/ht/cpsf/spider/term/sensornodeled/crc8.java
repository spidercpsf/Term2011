/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ht.cpsf.spider.term.sensornodeled;

/**
 *
 * @author cpsf
 */
public class crc8
{
   //--------
   //-------- Variables
   //--------

   /**
    * CRC 8 lookup table
    */
   private static byte dscrc_table [];

   /*
    * Create the lookup table
    */
   static
   {

      //Translated from the assembly code in iButton Standards, page 129.
      dscrc_table = new byte [256];

      int acc;
      int crc;

      for (int i = 0; i < 256; i++)
      {
         acc = i;
         crc = 0;

         for (int j = 0; j < 8; j++)
         {
            if (((acc ^ crc) & 0x01) == 0x01)
            {
               crc = ((crc ^ 0x18) >> 1) | 0x80;
            }
            else
               crc = crc >> 1;

            acc = acc >> 1;
         }

         dscrc_table [i] = ( byte ) crc;
      }
   }

   //--------
   //-------- Constructor
   //--------


   //--------
   //-------- Methods
   //--------

   /**
    * Perform the CRC8 on the data element based on the provided seed.
    * <p>
    * CRC8 is based on the polynomial = X^8 + X^5 + X^4 + 1.
    *
    * @param   dataToCrc   data element on which to perform the CRC8
    * @param   seed        seed the CRC8 with this value
    * @return  CRC8 value
    */
   public static int compute (int dataToCRC, int seed)
   {
      return (dscrc_table [(seed ^ dataToCRC) & 0x0FF] & 0x0FF);
   }

   /**
    * Perform the CRC8 on the data element based on a zero seed.
    * <p>
    * CRC8 is based on the polynomial = X^8 + X^5 + X^4 + 1.
    *
    * @param   dataToCrc   data element on which to perform the CRC8
    * @return  CRC8 value
    */
   public static int compute (int dataToCRC)
   {
      return (dscrc_table [dataToCRC & 0x0FF] & 0x0FF);
   }

   /**
    * Perform the CRC8 on an array of data elements based on a
    * zero seed.
    * <p>
    * CRC8 is based on the polynomial = X^8 + X^5 + X^4 + 1.
    *
    * @param   dataToCrc   array of data elements on which to perform the CRC8
    * @return  CRC8 value
    */
   public static int compute (byte dataToCrc [])
   {
      return compute(dataToCrc, 0, dataToCrc.length);
   }

   /**
    * Perform the CRC8 on an array of data elements based on a
    * zero seed.
    * <p>
    * CRC8 is based on the polynomial = X^8 + X^5 + X^4 + 1.
    *
    * @param   dataToCrc   array of data elements on which to perform the CRC8
    * @param   off         offset into array
    * @param   len         length of data to crc
    * @return  CRC8 value
    */
   public static int compute (byte dataToCrc [], int off, int len)
   {
      return compute(dataToCrc, off, len, 0);
   }

   /**
    * Perform the CRC8 on an array of data elements based on the
    * provided seed.
    * <p>
    * CRC8 is based on the polynomial = X^8 + X^5 + X^4 + 1.
    *
    * @param   dataToCrc   array of data elements on which to perform the CRC8
    * @param   off         offset into array
    * @param   len         length of data to crc
    * @param   seed        seed to use for CRC8
    * @return  CRC8 value
    */
   public static int compute (byte dataToCrc [], int off, int len, int seed)
   {

      // loop to do the crc on each data element
      int CRC8 = seed;

      for (int i = 0; i < len; i++)
         CRC8 = dscrc_table [(CRC8 ^ dataToCrc [i + off]) & 0x0FF];

      return (CRC8 & 0x0FF);
   }

   /**
    * Perform the CRC8 on an array of data elements based on the
    * provided seed.
    * <p>
    * CRC8 is based on the polynomial = X^8 + X^5 + X^4 + 1.
    *
    * @param   dataToCrc   array of data elements on which to perform the CRC8
    * @param   seed        seed to use for CRC8
    * @return  CRC8 value
    */
   public static int compute (byte dataToCrc [], int seed)
   {
      return compute(dataToCrc, 0, dataToCrc.length, seed);
   }
   private static final long POLY64REV = 0xd800000000000000L;

    private static final long[] LOOKUPTABLE;

    static {
        LOOKUPTABLE = new long[0x100];
        for (int i = 0; i < 0x100; i++) {
            long v = i;
            for (int j = 0; j < 8; j++) {
                if ((v & 1) == 1) {
                    v = (v >>> 1) ^ POLY64REV;
                } else {
                    v = (v >>> 1);
                }
            }
            LOOKUPTABLE[i] = v;
        }
    }

    /**
     * Calculates the CRC64 checksum for the given data array.
     *
     * @param data
     *            data to calculate checksum for
     * @return checksum value
     */
    public static long checksum(final byte[] data) {
        long sum = 0;
        for (int i = 0; i < data.length; i++) {
            final int lookupidx = ((int) sum ^ data[i]) & 0xff;
            sum = (sum >>> 8) ^ LOOKUPTABLE[lookupidx];
        }
        return sum;
    }
    public static long checksum(final byte[] data,int size) {
        long sum = 0;
        for (int i = 0; i < size; i++) {
            final int lookupidx = ((int) sum ^ data[i]) & 0xff;
            sum = (sum >>> 8) ^ LOOKUPTABLE[lookupidx];
        }
        return sum;
    }
}