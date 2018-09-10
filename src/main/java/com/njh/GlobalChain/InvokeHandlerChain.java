package com.njh.GlobalChain;

import com.njh.GlobalChain.Interface.AbstractIInvokeHandlerChain;
import com.njh.GlobalChain.Interface.IInvokeHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * ¹ýÂËÆ÷Á´
 */
public class InvokeHandlerChain implements AbstractIInvokeHandlerChain {
    List<IInvokeHandler> list = new ArrayList<IInvokeHandler>();

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response) {
        for (IInvokeHandler iInvokeHandler : list) {
            iInvokeHandler.invoke(request, response, this);
            iInvokeHandler.destory();
        }
    }

    public void addFilter(IInvokeHandler handler) {
        list.add(handler);
    }
}
