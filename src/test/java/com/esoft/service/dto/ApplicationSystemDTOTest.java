package com.esoft.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.esoft.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ApplicationSystemDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ApplicationSystemDTO.class);
        ApplicationSystemDTO applicationSystemDTO1 = new ApplicationSystemDTO();
        applicationSystemDTO1.setId(1L);
        ApplicationSystemDTO applicationSystemDTO2 = new ApplicationSystemDTO();
        assertThat(applicationSystemDTO1).isNotEqualTo(applicationSystemDTO2);
        applicationSystemDTO2.setId(applicationSystemDTO1.getId());
        assertThat(applicationSystemDTO1).isEqualTo(applicationSystemDTO2);
        applicationSystemDTO2.setId(2L);
        assertThat(applicationSystemDTO1).isNotEqualTo(applicationSystemDTO2);
        applicationSystemDTO1.setId(null);
        assertThat(applicationSystemDTO1).isNotEqualTo(applicationSystemDTO2);
    }
}
