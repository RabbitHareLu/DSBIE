package com.dsbie.frontend.utils;

import com.alexandriasoftware.swing.JInputValidator;
import com.alexandriasoftware.swing.JInputValidatorPreferences;
import com.alexandriasoftware.swing.Validation;
import com.dsbie.rearend.common.utils.StringUtil;

import javax.swing.*;

/**
 * @author lsl
 * @version 1.0
 * @date 2024年04月01日 11:17
 */
public class ComponentVerifierUtil {

    public static void notBlank(JComponent jComponent) {
        jComponent.setInputVerifier(new JInputValidator(jComponent, true, false) {
            @Override
            protected Validation getValidation(JComponent jComponent, JInputValidatorPreferences jInputValidatorPreferences) {
                JTextField jTextField = (JTextField) jComponent;
                if (StringUtil.isBlank(jTextField.getText())) {
                    return new Validation(Validation.Type.WARNING, "不能为空", jInputValidatorPreferences);
                }
                return new Validation(Validation.Type.NONE, "", jInputValidatorPreferences);
            }
        });
    }
}
