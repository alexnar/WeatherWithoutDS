package edu.weather.filewriter;

import edu.weather.filewriter.impl.FileWriterServiceImpl;
import edu.weather.filewriter.parameters.FileProperties;
import edu.weather.filewriter.service.FileWriterService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import java.util.Hashtable;

public class Activator implements BundleActivator {
    public void start(BundleContext context) throws Exception {
        Hashtable<String, Object> properties = new Hashtable<>();
        properties.put("file-path", FileProperties.getPathToFile());
        context.registerService(FileWriterService.class.getName(), new FileWriterServiceImpl(), properties);
    }

    public void stop(BundleContext context) throws Exception {

    }
}
