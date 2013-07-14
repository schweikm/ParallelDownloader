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

/**
 * Class that farms out the work of downloading chunks in parallel.
 * 
 * @author Chris Bubernak, Marc Schweikert
 * @version 1.0
 * 
 */
public final class ParallelDownloader {

	/**
	 * Unique serialization ID.
	 */
	private static final long serialVersionUID = 5555555555555555L;

	/**
	 * Download the file.
	 * 
	 * @param urlString
	 *            remote file to download
	 * @param destinationFile
	 *            local file to store
	 * @param numChunks
	 *            Number of parallel chunks to download
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws IOException
	 */
	public void download(final String urlString, final String destinationFile, final int numChunks)
			throws InterruptedException, ExecutionException, IOException {
		// let's calculate the file size
		final URL sourceURL = new URL(urlString);
		final URLConnection urlConnection = sourceURL.openConnection();
		final int fileSize = urlConnection.getContentLength();

		// if the remote site returns a bad size then bail out
		if (fileSize <= 0) {
			throw new IOException("Invalid file size returned from remote " + "host:  " + fileSize);
		}

		final List<Callable<byte[]>> partitions = new ArrayList<Callable<byte[]>>();

		// create a partition for each thread in our thread pool
		for (int i = 0; i < numChunks; i++) {
			final int finalI = i;
			final int start = (int) (Math.floor((double) fileSize / numChunks)) * finalI;

			// if this is last chunk set end equal to file size,
			// else just calculate end
			final int end = (i == (numChunks - 1)) ? fileSize - 1 : (int) ((Math.floor((double) fileSize / numChunks))
					* (finalI + 1) - 1);

			partitions.add(new Callable<byte[]>() {
				public byte[] call() {
					byte[] bytes = null;
					try {
						bytes = Downloader.downloadChunk(start, end, urlString, finalI);
					} catch (final IOException ioex) {
						ioex.printStackTrace(System.err);
					}
					return bytes;
				}
			});
		}

		final ExecutorService executorPool = Executors.newFixedThreadPool(numChunks);
		final List<Future<byte[]>> results = executorPool.invokeAll(partitions, 1000, TimeUnit.SECONDS);

		byte[] finalByteArray = new byte[0];

		// when the threads finish combine their results
		// using fixed thread pool we are guaranteed to iterate
		// through finals in order so we can just add them sequentially
		for (final Future<byte[]> result : results) {
			final byte[] newByteArray = new byte[result.get().length];
			System.arraycopy(result.get(), 0, newByteArray, 0, result.get().length);
			final byte[] tempByteArray = new byte[finalByteArray.length];

			System.arraycopy(finalByteArray, 0, tempByteArray, 0, finalByteArray.length);

			finalByteArray = new byte[newByteArray.length + tempByteArray.length];

			System.arraycopy(tempByteArray, 0, finalByteArray, 0, tempByteArray.length);

			System.arraycopy(newByteArray, 0, finalByteArray, tempByteArray.length, newByteArray.length);

			executorPool.shutdown();
		}

		try {
			// if they just selected a folder call the new file output
			// and put it in that folder
			final File file = new File(destinationFile);
			String destFile = destinationFile;
			if (file.isDirectory()) {
				destFile += "/" + urlString.substring(urlString.lastIndexOf('/'));
			}

			final FileOutputStream fos = new FileOutputStream(destFile);

			fos.write(finalByteArray);
			fos.close();
		} catch (final FileNotFoundException ex) {
			System.out.println("FileNotFoundException : " + ex);
		} catch (final IOException ioe) {
			System.out.println("IOException : " + ioe);
		}
	}
}
