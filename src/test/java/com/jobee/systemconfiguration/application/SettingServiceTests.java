package com.jobee.systemconfiguration.application;

import com.jobee.systemconfiguration.application.caching.SettingCacheService;
import com.jobee.systemconfiguration.application.exceptions.ConflictException;
import com.jobee.systemconfiguration.application.exceptions.EntityNotFoundException;
import com.jobee.systemconfiguration.application.messaging.MessagingService;
import com.jobee.systemconfiguration.contracts.SettingModel;
import com.jobee.systemconfiguration.domain.Setting;
import com.jobee.systemconfiguration.domain.SettingRepository;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.OngoingStubbing;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class SettingServiceTests {

    SettingRepository settingRepository = mock(SettingRepository.class);
    SettingCacheService settingCacheService = mock(SettingCacheService.class);
    MessagingService messagingService = mock(MessagingService.class);

    SettingService settingService = new SettingService(settingRepository, settingCacheService, messagingService);

    @Test
    void shouldCreateSetting_WhenSettingDoesNotExist(){

        // given
        SettingModel model = aModel();
        whenSettingRepositoryIsAskedToFindByContextAndName(model.context(), model.name()).
                thenReturn(Optional.empty());

        // when
        settingService.create(model);

        // then
        verifyThatSettingWasSaved();
        verifyThatSettingWasSetInCache(model);
    }

    @Test
    void shouldThrowException_WhenCreatingSettingAndSettingDoesExist(){

        // given
        Setting existingSetting = aSetting();
        SettingModel model = new SettingModel(existingSetting.getContext(), existingSetting.getName(), existingSetting.getValue());

        whenSettingRepositoryIsAskedToFindByContextAndName(model.context(), model.name())
                .thenReturn(Optional.of(existingSetting));

        // when & then
        assertThrows(ConflictException.class, () -> settingService.create(model));
    }

    @Test
    void shouldUpdateSetting_WhenSettingExists(){

        // given
        Setting existingSetting = aSetting();
        SettingModel model = new SettingModel(existingSetting.getContext(), existingSetting.getName(), "New_Value");

        whenSettingRepositoryIsAskedToFindByContextAndName(model.context(), model.name())
                .thenReturn(Optional.of(existingSetting));

        // when
        settingService.update(model);

        // then
        verifyThatSettingWasUpdated(model);
        verifyThatSettingWasSetInCache(model);
    }

    @Test
    void shouldThrowException_WhenUpdatingSettingAndSettingDoesNotExist(){

        // given
        Setting existingSetting = aSetting();
        SettingModel model = new SettingModel(existingSetting.getContext(), existingSetting.getName(), "New_Value");

        when(settingRepository.findByContextAndName(any(), any())).thenReturn(Optional.empty());

        // when & then
        assertThrows(EntityNotFoundException.class, () -> settingService.update(model));
    }

    private OngoingStubbing<Optional<Setting>> whenSettingRepositoryIsAskedToFindByContextAndName(String context, String name) {
        return when(settingRepository.findByContextAndName(argThat(a -> a.equals(context)), argThat(a -> a.equals(name))));
    }

    private static SettingModel aModel() {
        return new SettingModel("context", "name", "value");
    }

    private static Setting aSetting() {
        return new Setting("context", "name", "value", "");
    }

    private void verifyThatSettingWasSetInCache(SettingModel model) {
        verify(settingCacheService, times(1)).setSetting(model.context(), model.name(), model.value());
    }

    private void verifyThatSettingWasUpdated(SettingModel model) {
        verify(settingRepository, times(1)).save((argThat(setting -> setting.getValue().equals(model.value()))));
    }

    private void verifyThatSettingWasSaved() {
        verify(settingRepository, times(1)).save(any(Setting.class));
    }
}
