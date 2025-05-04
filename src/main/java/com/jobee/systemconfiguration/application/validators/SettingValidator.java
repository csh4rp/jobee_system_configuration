package com.jobee.systemconfiguration.application.validators;

import com.jobee.systemconfiguration.application.ErrorCodes;
import com.jobee.systemconfiguration.contracts.SettingUpsertModel;
import lombok.NonNull;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class SettingValidator implements Validator {
    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return SettingUpsertModel.class.equals(clazz);
    }

    @Override
    public void validate(@NonNull Object target, @NonNull Errors errors) {
        ValidationUtils.rejectIfEmpty(errors, "value", ErrorCodes.REQUIRED);
    }
}
