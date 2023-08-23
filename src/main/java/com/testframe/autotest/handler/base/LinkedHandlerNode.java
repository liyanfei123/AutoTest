package com.testframe.autotest.handler.base;

/**
 * 单节点处理对象
 */
public class LinkedHandlerNode<C extends Channel> {

    // 当前节点的处理器
    private final HandlerI<C> handler;

    // 下一个节点
    private LinkedHandlerNode<C> next = null;

    public LinkedHandlerNode(HandlerI<C> handler) {
        this.handler = handler;
    }

    public void setNext(LinkedHandlerNode<C> next) {
        this.next = next;
    }

    /**
     * 节点处理器处理
     * 若还存在下一个节点，则对下一个节点也进行处理
     * @param channel
     * @return
     */
    public Boolean entry(C channel) {
        if (this.handler != null) {
            Boolean isSuccess = this.handler.handle(channel);
            if (!isSuccess) {
                return false;
            }
        }
        return this.next == null ? true : this.next.entry(channel);

    }
}
