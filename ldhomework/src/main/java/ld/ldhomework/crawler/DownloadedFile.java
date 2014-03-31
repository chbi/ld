/*
 * Copyright 2014 Christian Bitschnau, Wolfgang Rohregger

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

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
