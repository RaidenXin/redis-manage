package com.raiden.redis.net.handle;

import com.raiden.redis.net.exception.MovedException;
import com.raiden.redis.net.exception.RedisException;
import com.raiden.redis.net.exception.UnknownCommandException;
import com.raiden.redis.net.response.RedisDataResponse;
import com.raiden.redis.net.response.RedisResponse;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.*;
import io.netty.handler.codec.CodecException;
import io.netty.handler.codec.redis.*;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

@ChannelHandler.Sharable
public class RedisClientHandler extends ChannelDuplexHandler {

    private static final String MOVED = "MOVED";

    private static final String UNKNOWN_COMMAND = "ERR unknown command";

    private Lock lock;
    private Map<Channel, RedisResponse> response;
    private Map<Channel, Condition> conditions;

    public RedisClientHandler(){
        this.lock = new ReentrantLock();
        this.response = new ConcurrentHashMap<>();
        this.conditions = new ConcurrentHashMap<>();
    }


    public <T> T getResponse(Channel channel) {
        Condition condition = lock.newCondition();
        conditions.put(channel, condition);
        try {
            lock.lock();
            condition.await();
        } catch (InterruptedException e) {
        }finally {
            lock.unlock();
        }
        RedisResponse redisResponse = response.get(channel);
        if (redisResponse.isSuccess()){
            return redisResponse.getBody();
        }
        throw redisResponse.getError();
    }

    // 发送 redis 命令
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        if (msg instanceof String[]){
            String[] commands = (String[]) msg;
            List<RedisMessage> children = new ArrayList<>(commands.length);
            for (String cmdString : commands) {
                children.add(new FullBulkStringRedisMessage(ByteBufUtil.writeUtf8(ctx.alloc(), cmdString)));
            }
            RedisMessage request = new ArrayRedisMessage(children);
            ctx.write(request, promise);
        }
    }


    // 接收 redis 响应数据
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        RedisMessage redisMessage = (RedisMessage) msg;
        // 打印响应消息
        RedisResponse responseBody = parseMessage(redisMessage);
        // 是否资源
        ReferenceCountUtil.release(redisMessage);
        Channel channel = ctx.channel();
        this.response.put(channel, responseBody);
        Condition condition = this.conditions.get(channel);
        try {
            lock.lock();
            condition.signal();
        }finally {
            lock.unlock();
        }
    }


    //这个是入栈 读取的时候 报错处理方法
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        Channel channel = ctx.channel();
        this.caught(channel, cause);
    }

    public void caught(Channel channel, Throwable cause){
        this.response.put(channel, RedisDataResponse.build(cause, true));
        Condition condition = this.conditions.get(channel);
        try {
            lock.lock();
            condition.signal();
        }finally {
            lock.unlock();
        }
    }

    private RedisResponse parseMessage(RedisMessage message){
        if (message instanceof ArrayRedisMessage){
            return RedisDataResponse.build(message2Arr(message));
        }else {
            return RedisDataResponse.build(message2String(message));
        }
    }

    private static <T> T message2Arr(RedisMessage msg) {
        if (msg instanceof ArrayRedisMessage) {
            ArrayRedisMessage arr = (ArrayRedisMessage) msg;
            List<RedisMessage> children = arr.children();
            assert children != null;
            //是否存在数组
            boolean isExistArr = children.stream().filter(child -> child instanceof ArrayRedisMessage).findFirst().isPresent();
            //是否存在非数组
            boolean isExistNonArr = children.stream().filter(child -> !(child instanceof ArrayRedisMessage)).findFirst().isPresent();
            //数组和非数组同时存在
            if (isExistArr && isExistNonArr){
                return (T) children.stream()
                        .flatMap(child -> child instanceof ArrayRedisMessage ? Stream.of(messageToArr(child)) : Stream.of(message2String(child)))
                        .toArray((value -> new String[value]));
            }else {
                //不存在数组 只存在非数组
                if (!isExistArr){
                    return (T) messageToArr(msg);
                }else {
                    //不存在非数组 只存在数组
                    String[][] result = new String[children.size()][];
                    int index = 0;
                    for (RedisMessage child : children) {
                        result[index++] = messageToArr(child);
                    }
                    return (T) result;
                }
            }
        } else {
            throw new CodecException("unknown message type: " + msg);
        }
    }

    private static String[] messageToArr(RedisMessage msg) {
        if (msg instanceof ArrayRedisMessage) {
            ArrayRedisMessage arr = (ArrayRedisMessage) msg;
            List<RedisMessage> children = arr.children();
            assert children != null;
            String[] result = new String[children.size()];
            int index = 0;
            for (RedisMessage child : children) {
                result[index++] = message2String(child);
            }
            return result;
        } else {
            throw new CodecException("unknown message type: " + msg);
        }
    }


    private static String message2String(RedisMessage msg) {
        if (msg instanceof SimpleStringRedisMessage) {
            return ((SimpleStringRedisMessage) msg).content();
        } else if (msg instanceof ErrorRedisMessage) {
            handlErrorMessages((ErrorRedisMessage) msg);
            return StringUtils.EMPTY;
        } else if (msg instanceof IntegerRedisMessage) {
            return String.valueOf(((IntegerRedisMessage) msg).value());
        } else if (msg instanceof FullBulkStringRedisMessage) {
            return getString((FullBulkStringRedisMessage) msg);
        } else {
            throw new CodecException("unknown message type: " + msg);
        }
    }

    private static void handlErrorMessages(ErrorRedisMessage message){
        String errorMsg = message.content();
        if (StringUtils.isNotBlank(errorMsg)){
            if (errorMsg.startsWith(MOVED)){
                throw new MovedException(errorMsg);
            }
            if (errorMsg.startsWith(UNKNOWN_COMMAND)){
                throw new UnknownCommandException(errorMsg);
            }
        }
        throw new RedisException(errorMsg);
    }

    private static String getString(FullBulkStringRedisMessage msg) {
        if (msg.isNull()) {
            return null;
        }
        return msg.content().toString(CharsetUtil.UTF_8);
    }

}
