package ld.ldhomework.crawler;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

public class DownloadedFile {
    private String content;
    private String contentType;

    public DownloadedFile(InputStream is, String contentType) {
	this.contentType = contentType;
	try {
	    this.content = IOUtils.toString(is);
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    public InputStream getContent() {
	return IOUtils.toInputStream(content);
    }

    public String getContentType() {
	return contentType;
    }

}
