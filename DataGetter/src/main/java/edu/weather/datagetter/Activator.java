package edu.weather.datagetter;

import edu.weather.datagetter.impl.DataGetterServiceImpl;
import edu.weather.datagetter.service.DataGetterService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * DataGetter bundle activator.
 */
public class Activator implements BundleActivator {

    @Override
    public void start(BundleContext context) throws Exception {
        context.registerService(DataGetterService.class.getName(),
                new DataGetterServiceImpl(), null);
    }

    @Override
    public void stop(BundleContext context) throws Exception {

    }
}
