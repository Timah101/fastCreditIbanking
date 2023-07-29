package com.iBanking.iBanking.utils;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;

import org.springframework.stereotype.Component;

@Component
public class SessionAttributeListener implements HttpSessionAttributeListener {
    @Override
    public void attributeAdded(HttpSessionBindingEvent event) {
        if (event.getName().equals("lastActivityTime")) {
            return; // No need to update if "lastActivityTime" is added
        }
        updateLastActivityTime(event.getSession());
    }

    @Override
    public void attributeReplaced(HttpSessionBindingEvent event) {
        if (event.getName().equals("lastActivityTime")) {
            return; // No need to update if "lastActivityTime" is replaced
        }
        updateLastActivityTime(event.getSession());
    }

    private void updateLastActivityTime(HttpSession session) {
        session.setAttribute("lastActivityTime", System.currentTimeMillis());
    }
}
