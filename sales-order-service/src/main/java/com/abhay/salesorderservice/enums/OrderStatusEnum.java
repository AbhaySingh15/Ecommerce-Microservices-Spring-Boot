package com.abhay.salesorderservice.enums;

public enum OrderStatusEnum implements CodeEnum {
//    NEW(0,"New Order"),
    ORDER_PLACED(1, "Order Successfully Placed"),
    ORDER_NOT_PLACED(2, "Order Not Placed"),
    ORDER_DELETED(3, "Order Successfully deleted");

    private final int code;
    private final String msg;

    OrderStatusEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }

}
