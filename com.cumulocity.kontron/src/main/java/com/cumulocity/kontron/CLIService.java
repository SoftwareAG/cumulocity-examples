/*
 * Copyright 2012 Cumulocity GmbH 
 */

package com.cumulocity.kontron;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class CLIService
{
  
    public String readFile (String filename)
    {
        BufferedReader reader;
        String line = null ;
               
        try
        {
            reader = new BufferedReader( new FileReader( filename ) );
            line = reader.readLine() ;
            reader.close();
        }
        catch( FileNotFoundException e )
        {
            System.err.println(e.getMessage()) ;
        }
        catch( IOException e )
        {
        	System.err.println(e.getMessage()) ;
        }    	
    	return line ;
    }
}
