package cl.jonnattan.emulator.config;

import java.io.IOException;
import java.util.logging.Logger;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;


public class AllRequestFilter implements Filter {

	private static final Logger logger = Logger.getLogger(AllRequestFilter.class.getName());

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
