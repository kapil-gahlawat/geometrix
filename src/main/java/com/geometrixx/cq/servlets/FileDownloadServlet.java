package com.geometrixx.cq.servlets;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.felix.scr.annotations.*;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.dam.api.Asset;

@Component (metatype=true)
@Service
@Properties({
        @Property(name="sling.servlet.methods", value="GET", propertyPrivate = true),
        @Property(name="sling.servlet.paths", value="/bin/file/download", propertyPrivate = true),
        @Property(name="sling.servlet.prefix", intValue =-1, propertyPrivate = true)
})
public class FileDownloadServlet extends SlingAllMethodsServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1717861708967935344L;

	private final static Logger logger = LoggerFactory.getLogger(FileDownloadServlet.class);
	
	private final String FILE_BASE_PATH = "/content/dam/geometrixx/documents/";
	
	@Override
	protected void doGet(SlingHttpServletRequest request,
			SlingHttpServletResponse response) throws ServletException,
			IOException {
		
		String filePath = FILE_BASE_PATH + request.getParameter("file_name");
		ResourceResolver resourceResolver = request.getResourceResolver();
		Resource fileResource = resourceResolver.getResource(filePath);
		
		if(fileResource == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
		Asset asset = fileResource.adaptTo(Asset.class);
		
		if(asset.getOriginal() != null) {
			InputStream is = asset.getOriginal().getStream();
			String fileName = asset.getName();
			
			response.setContentType("application/octet-stream");
			response.addHeader("Content-Disposition", "attachment; filename=" + fileName);
			ServletOutputStream stream = null;
			stream = response.getOutputStream();
			
			//read from the file; write to the ServletOutputStream
			int readBytes = 0;
			BufferedInputStream buf = new BufferedInputStream(is);
		    while ((readBytes = buf.read()) != -1) {
		        stream.write(readBytes);
		    }
		    
		    logger.info("file sent");
		    return;
		}
	}
}