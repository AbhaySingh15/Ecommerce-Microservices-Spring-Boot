package com.abhay.salesorderservice.exception;

import com.abhay.salesorderservice.enums.ResultEnum;

public class MyException extends RuntimeException{

    public MyException(ResultEnum resultEnum) {
        super(String.valueOf(resultEnum));
    }
}
