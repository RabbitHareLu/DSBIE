package com.dsbie.rearend.exception;

/**
 * K-Tools业务异常
 *
 * @author WCG
 */
public class KToolException extends RuntimeException {

    public KToolException(String msg) {
        super(msg);
    }

    public KToolException(String msg, Exception e) {
        super(msg, e);
    }

}
