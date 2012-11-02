package com.marcschweikert;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


public class ParallelDownloader {

  public void download(final String urlString,
                       String destinationFile,
                       final int numChunks)
    throws InterruptedException, ExecutionException,
           MalformedURLException, IOException {

    // let's calculate the file size
    final URL sourceURL = new URL(urlString);
    URLConnection urlConnection = sourceURL.openConnection();
    final int fileSize = urlConnection.getContentLength();

    // if the remote site returns a bad size then bail out
    if(fileSize <= 0) {
      throw new IOException("Invalid file size returned from remote host:  " + fileSize);
    }

    final List<Callable<byte[]>> partitions = new ArrayList<Callable<byte[]>>();

    //create a partition for each thread in our thread pool
    for(int i = 0; i < numChunks; i++) {
      final int j = i;
      final int start = (int)(Math.floor((double)fileSize/numChunks))*(j);
      //if this is last chunk set end equal to file size, else just calculate end
      final int end = (i==numChunks-1)  ? fileSize : (int)((Math.floor((double)fileSize/numChunks))*(j+1)-1);
      
      partitions.add(new Callable <byte[]> () {
        public byte[]  call() throws Exception {
          return Downloader.downloadChunk(start, end, urlString, j);
        }
      });
    }


    final ExecutorService executorPool = Executors.newFixedThreadPool(numChunks);
    final List<Future<byte[]>> results = executorPool.invokeAll(partitions, 1000, TimeUnit.SECONDS);

    byte [] finalByteArray = new byte [0];

    //when the threads finish combine their results
    //using fixed thread pool we are guaranteed to iterate 
    //through finals in order so we can just add them sequentially
    for(final Future<byte[]> result : results) {
      final byte[] newByteArray = new byte[result.get().length];
      System.arraycopy(result.get(), 0, newByteArray, 0, result.get().length);
      final byte[] finalTempByteArray = new byte[finalByteArray.length];

      System.arraycopy(finalByteArray, 0, finalTempByteArray, 0, finalByteArray.length);

      finalByteArray = new byte[newByteArray.length + finalTempByteArray.length];

      System.arraycopy(finalTempByteArray, 0, finalByteArray, 0, finalTempByteArray.length);
      System.arraycopy(newByteArray, 0, finalByteArray, finalTempByteArray.length, newByteArray.length);
      executorPool.shutdown();
    }

    try
    {
      //if they just selected a folder call the new file output and put it in that folder
      File file = new File(destinationFile);
      if(file.isDirectory()) {
      	  destinationFile = destinationFile + "/" + urlString.substring(urlString.lastIndexOf('/'));
      }
      
      final FileOutputStream fos = new FileOutputStream(destinationFile);

      fos.write(finalByteArray);
      fos.close();
    }
    catch(final FileNotFoundException ex)
    {
      System.out.println("FileNotFoundException : " + ex);
    }
    catch(final IOException ioe)
    {
      System.out.println("IOException : " + ioe);
    }
  }
 
}
