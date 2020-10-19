package com.cetcbigdata.varanus.utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.mozilla.intl.chardet.nsDetector;
import org.mozilla.intl.chardet.nsICharsetDetectionObserver;
import org.mozilla.intl.chardet.nsPSMDetector;

public class CharsetDetector {
	private static boolean found = false;
	private static String result;
	private static int lang;

	public static String[] detectChineseCharset(InputStream in) throws IOException {
		lang = nsPSMDetector.CHINESE;
		String[] prob;
		// Initalize the nsDetector() ;
		nsDetector det = new nsDetector(lang);
		// Set an observer...
		// The Notify() will be called when a matching charset is found.

		det.Init(new nsICharsetDetectionObserver() {
			@Override
			public void Notify(String charset) {
				found = true;
				result = charset;
			}
		});
		BufferedInputStream imp = new BufferedInputStream(in);
		byte[] buf = new byte[1024];
		int len;
		boolean isAscii = true;
		while ((len = imp.read(buf, 0, buf.length)) != -1) {
			// Check if the stream is only ascii.
			if (isAscii) {
				isAscii = det.isAscii(buf, len);
			}
			// DoIt if non-ascii and not done yet.
			if (!isAscii) {
				if (det.DoIt(buf, len, false)) {
					break;
				}
					
			}
		}
		imp.close();
		in.close();
		det.DataEnd();
		if (isAscii) {
			found = true;
			prob = new String[] { "ASCII" };
		} else if (found) {
			prob = new String[] { result };
		} else {
			prob = det.getProbableCharsets();
		}
		return prob;
	}

	public static String[] detectAllCharset(InputStream in) throws IOException {
		try {
			lang = nsPSMDetector.ALL;
			return detectChineseCharset(in);
		} catch (IOException e) {
			throw e;
		}
	}
	
	 public static void main(String[] args)throws IOException
	    {
	        URL url = new URL("http://www.cstc.gov.cn/View.aspx?id=24131");
	        String[] probableSet = detectChineseCharset(url.openStream());
	        for (String charset : probableSet)
	        {
	            System.out.println(charset);
	        }
	        
	        url = new URL("https://blog.csdn.net/qq_36691683/article/details/81608244");
	        probableSet = detectChineseCharset(url.openStream());
	        for (String charset : probableSet)
	        {
	            System.out.println(charset);
	        }
	    }
}
