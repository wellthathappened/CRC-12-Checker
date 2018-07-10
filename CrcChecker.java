//------------------------------------------------------------------

// University of Central Florida

// CIS3360 - Summer 2015

// Program Author: Ian Lewis

//------------------------------------------------------------------

import java.math.BigInteger;

public class CrcChecker
{

    public static void main(String[] args) throws InterruptedException
	{
        
        char mode = args[0].charAt(0);
        String hexInput = args[1].toUpperCase();
        String binInput = hexToBin(hexInput);
        String observedCrc = "";
        String poly = "101001010100";
        
        //Begin header output
        System.out.println("CRC Checker by Ian Lewis\n");
        
        //Print mode for operation selected in arguments
        if (mode == 'c')
            System.out.println("Mode of operation: calculate");
		
        else if (mode == 'v')
            System.out.println("Mode of operation: verify");
        
        //Print the hexadecimal input and the binary representation of the input
        System.out.println("The input string (hex): " + hexInput);
        System.out.print("The input string (bin): ");
        
        //Print the binary representation with spaces between each 4th bit
        binPrint(binInput);
        
        //Print the polynomial used for the operation in binary form
        //(x^12)+(x^10)+(x^7)+(x^5)+(x^3) = 101001010100
        System.out.println("\n");
        System.out.println("The polynomial that was used (binary bit string)"
                            + ": 1010 0101 0100 0");
        
        //If calculate mode is chosen, apply 12 zeroes to binary form of input
        if (mode == 'c')
		{
            System.out.println("We will append 12 zeroes at the end of the "
                                + "binary input.\n");
            
            //Append 12 zeroes to the right of the binary form of the input
            binInput+="000000000000";
            
            System.out.println("The binary string difference after each XOR "
                                + "step of the CRC calculation:\n");
            
            //Run CRC calculation
            CRCcalc(binInput,poly,observedCrc,mode);
        }
        
        //If verify mode is chosen, set Observed CRC, print CRC in binary & hex
        else if (mode == 'v')
		{
            
            //Set Observed CRC as last 12 bits of input
            for(int i=12;i<24;i++)
                observedCrc+=binInput.charAt(i);
            
            System.out.print("The 12-bit CRC observed at the end of the input: ");
            
            //Print binary form of observed CRC
            binPrint(observedCrc);
            
            //Print hex form of observed CRC
            System.out.println(" (bin) = " + hexInput.charAt(hexInput.length()-3)
                                           + hexInput.charAt(hexInput.length()-2)
                                           + hexInput.charAt(hexInput.length()-1)
                                           + "(hex)\n");
            
            System.out.println("The binary string difference after each XOR "
                                + "step of the CRC calculation:\n");
            
            //Establish new String Input to be used in calculations
            String newInput = "";
            
            //Only use the first 12 bits from the original input
            for(int i=0;i<12;i++)
                newInput+=binInput.charAt(i);
            
            //Append 12 zeroes to the right of NEW binary input for easier calc
            newInput+="000000000000";
            
            //Run CRC calculation
            CRCcalc(newInput,poly,observedCrc,mode);
        } 
    }
    
    public static void CRCcalc(String input, String poly, String obsCrc, char m)
	{
        
        //Establish result string and CRC string
        String result = input;
        String CRC = "";
        
        //Print initial input
        binPrint(input);
        System.out.print("\n");
        
        //While there is a 1 in the first 12 bits
        //Cycle through the XOR computation and print the result from each cycle
        while(result.indexOf('1')<=11)
		{
            result=Compute(result,poly);
            
            binPrint(result);
			
            System.out.print("\n");
        }
        
        //Establish final CRC result
        for(int i=12;i<24;i++)
            CRC += result.charAt(i);
        
        //Print CRC result in binary and hex form
        System.out.print("\nThe Computed CRC for the input is: ");
        
        binPrint(CRC);
        
        System.out.println(" (bin) = " + binToHex(CRC).toUpperCase() 
                            + " (hex)\n");
        
        
        //If verify mode is chosen, compare the Observed CRC to the final CRC
        if(m=='v')
		{
            System.out.print("Did the CRC check pass? (Yes or No): ");
        
            if(obsCrc.compareTo(CRC)==0)
                System.out.print("Yes\n");
        
            else System.out.print("No\n");
        }
    }
    
    //XOR Computation
    public static String Compute(String input, String poly)
	{
        
        //Establish index where the first "1" bit is located
        //As well as establish temporary string storage
        int startPoint = input.indexOf('1');
        String temp1 = "";
        String tempResult = "";
        String finalResult = "";
        
        //Establish the dividen bits used in the current XOR cycle
        for(int i=startPoint;i<(startPoint+12);i++)
            temp1+=input.charAt(i);
        
        //XOR bit operation
        for(int i=0;i<12;i++)
		{
            
            //If ("1" and "1") or ("0" and "0), apply a "0" bit
            if(((temp1.charAt(i)=='1')&&(poly.charAt(i)=='1'))||
               ((temp1.charAt(i)=='0')&&(poly.charAt(i)=='0')))
                tempResult+='0';
            
            //If ("0" and "1") or ("1" and "0), apply a "1" bit
            else if(((temp1.charAt(i)=='1')&&(poly.charAt(i)=='0'))||
                    ((temp1.charAt(i)=='0')&&(poly.charAt(i)=='1')))
                tempResult+='1';
        }
        
        //Establish final result for XOR cycle
        for(int i=0;i<24;i++)
		{
            //If before the start point, apply a "0"
            if(i<startPoint)
                finalResult+='0';
            
            //If at start point, apply the result from the XOR opperation
            else if(i==startPoint)
			{
                finalResult+=tempResult;
                i=startPoint+11;
            }
            
            //If XOR operation result is applied, apply rest of original input
            else if(i>=startPoint+11)
                finalResult+=input.charAt(i);
        }
        
        //Return the final result for this cycle
        return finalResult;
    }
    
    //Binary print statement with spaces after every 4th bit
    public static void binPrint(String input)
	{
        for(int i=0;i<input.length();i++)
		{
            if ((i != 0) && (i % 4)==0)
                System.out.print(" ");
            
            System.out.print(input.charAt(i));
        }
    }
    
    //Hex to binary conversion
    static String hexToBin(String input)
	{
        return new BigInteger(input, 16).toString(2);
    }
    
    //Binary to hex conversion
    static String binToHex(String input)
	{
        return new BigInteger(input, 2).toString(16);
    }
}
