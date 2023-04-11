package cl.jonnattan.emulator.config;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class AllRequestFilter implements Filter {

	private final static Logger logger = Logger.getLogger(AllRequestFilter.class.getName());

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		logger.info("====================== INIT ===========================");
		logger.info("URI           :" + httpRequest.getRequestURI());
		logger.info("RemoteAddr    :" + httpRequest.getRemoteAddr());
		logger.info("RemotePort    :" + httpRequest.getRemotePort());
		logger.info("Protocol HTTP :" + httpRequest.getProtocol());
		logger.info("Real IP :" + httpRequest.getHeader("X-Real-IP"));
		chain.doFilter(httpRequest, response);
		logger.info("====================== END ===========================");
	}

}
