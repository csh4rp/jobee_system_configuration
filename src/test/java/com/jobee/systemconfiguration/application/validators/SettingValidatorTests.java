package com.jobee.systemconfiguration.application.validators;

import com.jobee.systemconfiguration.contracts.SettingUpsertModel;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

public class SettingValidatorTests {

    SettingValidator validator = new SettingValidator();

    @Test
    void shouldContainErrors_WhenObjectIsInvalid() {

        // given
        SettingUpsertModel model = new SettingUpsertModel(null);
        Errors errors = new BeanPropertyBindingResult(model, "request");

        // when
        validator.validate(model, errors);

        // then
        assert (errors.hasErrors());
        assert (errors.getFieldError("value") != null);
        assert (errors.getFieldError("value").getCode().equals("VALUE_IS_REQUIRED"));
    }

    @Test
    void shouldNotContainAnyErrors_WhenObjectIsValid() {

        // given
        SettingUpsertModel model = new SettingUpsertModel("value");
        Errors errors = new BeanPropertyBindingResult(model, "request");

        // when
        validator.validate(model, errors);

        // then
        assert (!errors.hasErrors());
    }
}
