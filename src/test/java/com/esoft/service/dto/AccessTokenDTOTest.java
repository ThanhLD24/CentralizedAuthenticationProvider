package com.esoft.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.esoft.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AccessTokenDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AccessTokenDTO.class);
        AccessTokenDTO accessTokenDTO1 = new AccessTokenDTO();
        accessTokenDTO1.setId(1L);
        AccessTokenDTO accessTokenDTO2 = new AccessTokenDTO();
        assertThat(accessTokenDTO1).isNotEqualTo(accessTokenDTO2);
        accessTokenDTO2.setId(accessTokenDTO1.getId());
        assertThat(accessTokenDTO1).isEqualTo(accessTokenDTO2);
        accessTokenDTO2.setId(2L);
        assertThat(accessTokenDTO1).isNotEqualTo(accessTokenDTO2);
        accessTokenDTO1.setId(null);
        assertThat(accessTokenDTO1).isNotEqualTo(accessTokenDTO2);
    }
}
