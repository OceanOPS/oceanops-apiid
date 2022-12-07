package org.oceanops.api.id.filters;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.cayenne.BaseContext;
import org.apache.cayenne.ashwood.WeightedAshwoodEntitySorter;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.cayenne.dba.PkGenerator;
import org.apache.cayenne.map.EntitySorter;
import org.oceanops.api.db.OraclePkGeneratorCustom;

public class CayenneFilter implements Filter {
	private String pathToBeIgnored;
	private String cayenneConfigurationLocation;
	private ServerRuntime cayenneRuntime;

	@Override
	public void destroy() {
		this.cayenneRuntime.shutdown();
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		String path = ((HttpServletRequest) request).getRequestURI();
		if(!path.endsWith(pathToBeIgnored)){
			BaseContext.bindThreadObjectContext(this.cayenneRuntime.newContext());
		}
		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		pathToBeIgnored = filterConfig.getInitParameter("pathToBeIgnored");
		cayenneConfigurationLocation = filterConfig.getInitParameter("configuration-location");
		this.initCayenneRuntime();
	}

	private void initCayenneRuntime(){
		OraclePkGeneratorCustom pkgen = new OraclePkGeneratorCustom();
		pkgen.setPkCacheSize(1);

    	this.cayenneRuntime = ServerRuntime.builder()
                .addConfig(cayenneConfigurationLocation)
				.addModule(b -> b
					.bind(PkGenerator.class).toInstance(pkgen)
				)
				.addModule(b -> b
					.bind(EntitySorter.class).to(WeightedAshwoodEntitySorter.class))
                .build();
	}

}