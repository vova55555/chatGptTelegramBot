package org.hoshta;

import org.hoshta.handler.UserRequestHandler;
import org.hoshta.model.UserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class Dispatcher {

    private final List<UserRequestHandler> handlers;

    /**
     * Pay attention at this constructor
     * Since we have some global handlers,
     * like command /start which can interrupt any conversation flow.
     * These global handlers should go first in the list
     */
    @Autowired
    public Dispatcher(List<UserRequestHandler> handlers) {
        this.handlers = handlers
                .stream()
                .sorted(Comparator
                        .comparing(UserRequestHandler::isGlobal)
                        .reversed())
                .collect(Collectors.toList());
    }

    public boolean dispatch(UserRequest userRequest) {
        for (UserRequestHandler userRequestHandler : handlers) {
            if(userRequestHandler.isApplicable(userRequest)){
                userRequestHandler.handle(userRequest);
                return true;
            }
        }
        return false;
    }
}

