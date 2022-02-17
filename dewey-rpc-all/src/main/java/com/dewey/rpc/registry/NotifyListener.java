package com.dewey.rpc.registry;

import java.net.URI;
import java.util.Set;

/**
 * @auther dewey
 * @date 2022/2/16 23:16
 */
public interface NotifyListener {
    void notify(Set<URI> uriSet);
}
