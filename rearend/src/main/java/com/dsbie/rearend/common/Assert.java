package com.dsbie.rearend.common;

import com.dsbie.rearend.common.utils.StringUtil;
import com.dsbie.rearend.exception.KToolException;

/**
 * 断言工具类
 *
 * @author WCG
 */
public class Assert {

    public static <T> void notNull(T object, String errorMsg) throws KToolException {
        if (null == object) {
            throw new KToolException(errorMsg);
        }
    }

    public static <T extends CharSequence> void notBlank(T text, String errorMsg) throws KToolException {
        if (StringUtil.isBlank(text)) {
            throw new KToolException(errorMsg);
        }
    }

}
