package org.moose.operator.web.filter;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * @author taohua
 */

public class CheckHeaderTokenRequestWrapper extends HttpServletRequestWrapper {

  private Map<String, String> headerMap = new HashMap<>();

  /**
   * Constructs a request object wrapping the given request.
   *
   * @param request The request to wrap
   * @throws IllegalArgumentException if the request is null
   */
  public CheckHeaderTokenRequestWrapper(HttpServletRequest request) {
    super(request);
  }

  /**
   * add a header with given name and value
   *
   * @param name
   * @param value
   */
  public void addHeader(String name, String value) {
    headerMap.put(name, value);
  }

  @Override
  public String getHeader(String name) {
    String headerValue = super.getHeader(name);
    if (headerMap.containsKey(name)) {
      headerValue = headerMap.get(name);
    }
    return headerValue;
  }

  /**
   * get the Header names
   */
  @Override
  public Enumeration<String> getHeaderNames() {
    List<String> names = Collections.list(super.getHeaderNames());
    names.addAll(headerMap.keySet());
    return Collections.enumeration(names);
  }

  @Override
  public Enumeration<String> getHeaders(String name) {
    List<String> values = Collections.list(super.getHeaders(name));
    if (headerMap.containsKey(name)) {
      values = Collections.singletonList(headerMap.get(name));
    }
    return Collections.enumeration(values);
  }
}
