package com.dewey.rpc.rpc.protocol.trpc;

import com.dewey.rpc.common.tools.SpiUtils;
import com.dewey.rpc.common.tools.URIUtils;
import com.dewey.rpc.remoting.Transporter;
import com.dewey.rpc.rpc.protocol.Protocol;

import java.net.URI;

/**
 * @auther dewey
 * @date 2022/2/8 22:49
 */
public class TrpcProtocol implements Protocol {
    @Override
    public void export(URI exportUrl) {
//        String transporterName = URIUtils.getParam(exportUrl, "transporter");
//        Transporter transporter = (Transporter) SpiUtils.getServiceImpl(transporterName, Transporter.class);
//        transporter.start(exportUrl,)
    }
}
