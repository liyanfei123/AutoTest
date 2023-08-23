package com.testframe.autotest.handler.base;

public class DefaultHandlerChain<C extends Channel> {

    // lambda表达式表示一个实现了HandlerI接口的函数对象
    private LinkedHandlerNode<C> header = new LinkedHandlerNode<>((h) -> {
        return true;
    } );

    private LinkedHandlerNode<C> tail;

    public DefaultHandlerChain() {
        this.tail = this.header;
    }

    public DefaultHandlerChain<C> setNext(HandlerI handler) {
        LinkedHandlerNode<C> handlerNode = new LinkedHandlerNode<>(handler);
        this.tail.setNext(handlerNode);
        this.tail = handlerNode;
        return this;
    }

    public Boolean process(C channel) {
        return this.header.entry(channel);
    }
}
