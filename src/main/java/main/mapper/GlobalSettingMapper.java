package main.mapper;

import main.dto.GlobalSettingDTO;
import main.model.GlobalSetting;
import org.springframework.stereotype.Component;

@Component
public class GlobalSettingMapper extends AbstractMapper<GlobalSetting, GlobalSettingDTO> {

    public GlobalSettingMapper() {
        super(GlobalSetting.class, GlobalSettingDTO.class);
    }
}
