package com.dewey.rpc.rpc.protocol.trpc.codec;

import com.dewey.rpc.common.serialize.Serialization;
import com.dewey.rpc.common.tools.ByteUtil;
import com.dewey.rpc.remoting.Codec;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @auther dewey
 * @date 2022/2/8 23:01
 */
@Slf4j
public class TrpcCodec implements Codec {
    public static final byte[] MAGIC = new byte[]{(byte) 0xda,(byte) 0xbb};
    public static final int HEADER_LEN = 6;
    //用来临时保留没有处理过的请求报文
    ByteBuf tempMsg = Unpooled.buffer();
    @Override
    public byte[] encode(Object msg) throws Exception {
        byte[] responseBody = (byte[]) msg;
        //构建header
        ByteBuf requestBuffer = Unpooled.buffer();
        requestBuffer.writeByte(0xda);
        requestBuffer.writeByte(0xbb);
        requestBuffer.writeBytes(ByteUtil.int2bytes(responseBody.length));
        requestBuffer.writeBytes(responseBody);
        byte[] result = new byte[requestBuffer.readableBytes()];
        requestBuffer.readBytes(result);
        return result;
    }

    /**
     * 解码的结果是 RpcInvocation 对象集合
     * @param data
     * @return
     * @throws Exception
     */
    @Override
    public List<Object> decode(byte[] data) throws Exception {
        List<Object> out = new ArrayList<>();
        //1、解析（解析头部，去除数据，封装成invocation ）
        //合并报文
        ByteBuf message = Unpooled.buffer();
        int tmpMsgSize = tempMsg.readableBytes();
        if (tmpMsgSize > 0) {
            message.writeBytes(tempMsg);
            message.writeBytes(data);
            log.info("合并：上一数据包余下的长度为：{}，合并后长度为：{}",tmpMsgSize,message.readableBytes());
        }else {
            message.writeBytes(data);
        }
        for (;;){
            //如果数据太少，不够一个头部，待会处理
            if (HEADER_LEN >= message.readableBytes()) {
                tempMsg.clear();
                tempMsg.writeBytes(message);
                return out;
            }
            //1.2 解析数据
            //1.2.1检查关键字
            byte[] magic = new byte[2];
            message.readBytes(magic);
            for (;;){
                if (magic[0] != MAGIC[0] || magic[1] != MAGIC[1]){
                    if (message.readableBytes() == 0){
                        //所有数据读完都没发现正确的头，算了..等下次数据
                        tempMsg.clear();
                        tempMsg.writeByte(magic[1]);
                        return out;
                    }
                    magic[0] = magic[1];
                    magic[1] = message.readByte();
                }else {
                    break;
                }
            }
            byte[] lengthBytes = new byte[4];
            message.readBytes(lengthBytes);
            int length = ByteUtil.bytes2Int_BE(lengthBytes);
            //1.2.2 读取body
            //如果body没传输完，先不处理
            if (message.readableBytes() < length) {
                tempMsg.clear();
                tempMsg.writeBytes(magic);
                tempMsg.writeBytes(lengthBytes);
                tempMsg.writeBytes(message);
                return out;
            }
            byte[] body = new byte[length];
            message.readBytes(body);
            //序列化
            Object o = getSerialization().deserialize(body, decodeType);
            out.add(o);

        }
    }

    @Override
    public Codec createInstance() {
        TrpcCodec trpcCodec = new TrpcCodec();
        trpcCodec.setDecodeType(this.decodeType);
        trpcCodec.setSerialization(this.serialization);
        return trpcCodec;
    }
    private Serialization serialization;

    private Class decodeType;

    public Class getDecodeType() {
        return decodeType;
    }

    public void setDecodeType(Class decodeType) {
        this.decodeType = decodeType;
    }

    public Serialization getSerialization() {
        return serialization;
    }

    public void setSerialization(Serialization serialization) {
        this.serialization = serialization;
    }
}
